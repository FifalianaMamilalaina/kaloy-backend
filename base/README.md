# Base de données Kaloy — installation sur un poste neuf

Tout ce qu'il faut pour amener une machine vierge jusqu'à une application qui
démarre et qui a du contenu. Les fichiers sont à jour au **09/11/2026** et
vérifiés : la séquence ci-dessous a été lancée sur une base vide, sans une
seule erreur, et le résultat comparé à la base de développement.

```
base/
  schema/    01-schema.sql            la structure et les tables de référence
             02-comptes-de-base.sql   les deux comptes qui permettent de se connecter
  donnees/   01 à 08 puis 99          le contenu de démonstration
```

**`schema/` suffit à faire tourner l'application.** `donnees/` ne fait que la
remplir : sans lui l'application démarre, on s'y connecte et on peut déclarer
ses propres chansons, mais le catalogue est vide.

## La séquence

```bash
createdb -U postgres musical_app

# Indispensable : la structure, puis les comptes.
psql -U postgres -d musical_app -f schema/01-schema.sql
psql -U postgres -d musical_app -f schema/02-comptes-de-base.sql

# Facultatif : le contenu de démonstration, dans l'ordre des numéros.
for f in donnees/*.sql; do psql -U postgres -d musical_app -f "$f"; done
```

Sous PowerShell :

```powershell
Get-ChildItem schema\*.sql, donnees\*.sql | ForEach-Object {
    psql -U postgres -d musical_app -f $_.FullName
}
```

L'utilisateur est `postgres`, et le mot de passe n'est pas écrit ici — il est
dans `Mozika/src/main/resources/application.yml`, avec le reste de la
configuration. Le nom `musical_app` n'est pas libre : c'est celui que porte
`spring.datasource.url`.

**L'ordre des numéros n'est pas décoratif.** Chaque fichier s'appuie sur les
identifiants créés par les précédents. `99` arrive en dernier parce qu'il
réaligne les séquences sur le plus grand identifiant inséré ; lancé plus tôt,
la première chanson créée par l'application entrerait en collision avec une
chanson du jeu de données.

## Ce que l'on doit obtenir

| Mesure | Attendu |
| --- | --- |
| Tables | 49 |
| Clés étrangères | 67 |
| Utilisateurs | 10 |
| dont comptes capables de se connecter | 9 |
| Artistes | 9 |
| Albums | 12 |
| Chansons | 45 |
| Genres | 10 |
| Liens chanson / genre | 36 |
| Événements | 4 |
| Concerts | 21 |
| Lieux | 3 |
| Lignes d'historique d'écoute | 801 |
| Commentaires | 7 |
| Mentions j'aime | 6 |
| Listes éditoriales | 4 |

Contrôle rapide :

```sql
SELECT count(*) AS tables FROM information_schema.tables WHERE table_schema = 'public';
SELECT count(*) AS chansons FROM songs;
```

## Les comptes

Tous ont le même mot de passe : **`password123`**.

Les deux premiers viennent de `schema/`, les autres de `donnees/`.

| Compte | Rôle | Vient de | À quoi il sert |
| --- | --- | --- | --- |
| `client@dev.com` | CLIENT | schéma | parcourir, écouter, commenter |
| `artist@dev.com` | ARTIST | schéma | déclarer des chansons, avec un profil artiste |
| `rossy@kaloy.mg` | ARTIST | données | porte des chansons et des concerts |
| `jaojoby@kaloy.mg` | ARTIST | données | porte des invitations en attente |
| `mage4@kaloy.mg`, `ambondrona@kaloy.mg`, `samoela@kaloy.mg`, `tencemena@kaloy.mg` | ARTIST | données | le reste du catalogue |

### Pourquoi les comptes sont dans `schema/` et non dans `donnees/`

Sans eux, **personne ne peut se connecter à une installation neuve**.
S'inscrire depuis l'application ne remplace pas : l'inscription crée un compte
`NOT_VERIFIED` et envoie un code par courriel ou par SMS, donc elle réclame un
serveur de courriel et un compte MAPI déjà configurés et joignables.

L'ancien `insert_test_data.sql` créait bien ces deux comptes, mais
**inutilisables** :

- son hash de mot de passe était un motif répété et non un vrai hash BCrypt,
  donc `login()` répondait **401** ;
- il laissait `verification_status_user_id` à `NULL`, et `login()` refuse par un
  **403** tout compte dont ce champ ne vaut pas `VERIFIED` ;
- il ne créait aucune ligne dans `artists`, si bien que `artist@dev.com` se
  connectait sans pouvoir rien faire en tant qu'artiste :
  `ServiceArtisteConnecte.resoudre()` répond **403 « Ce compte n'est rattaché à
  aucun profil artiste »** sur toute opération de « Mes chansons ».

Les deux premiers défauts étaient réparés par un `UPDATE` enfoui dans
`seed_contenu_kaloy.sql`, un fichier de contenu de démonstration. Qui
n'installait que la structure se retrouvait donc sans aucun accès. Le troisième
n'était réparé nulle part, et manque encore dans la base de développement.

`schema/02-comptes-de-base.sql` crée les deux comptes corrects du premier coup
et se relance sans risque : son `ON CONFLICT` réécrit le hash et le statut, ce
qui répare aussi une base où l'ancien fichier a tourné.

## Ce que le schéma contient, et ce qu'il ne contient plus

`01-schema.sql` part du script réaligné le 04/11/2026 et **absorbe quatre
scripts de migration**. Les originaux restent dans `../baseDeDonne/` parce
qu'ils servent encore aux bases déjà en service, mais il ne faut **pas** les
lancer après le schéma :

| Fichier | Pourquoi il est inutile ici |
| --- | --- |
| `user_devices.sql` | la table est créée par le schéma |
| `MAJ_listening_history.sql` | ses colonnes et ses deux index sont dans le schéma |
| `kaloy-songs-mise-à-jour.sql` | voir l'avertissement ci-dessous |
| `reprise_notifications_invitations.sql` | il rattrape des invitations émises avant une bascule ; une base neuve n'en a aucune |

**Avertissement sur `kaloy-songs-mise-à-jour.sql`.** Il finit par
`ALTER TABLE "songs" ALTER COLUMN "audio_url" SET NOT NULL`. Le lancer
annulerait la correction du 04/11 : `audio_url` est nullable **exprès**, parce
qu'une chanson peut être déclarée avec seulement des paroles ou un solfa, le
serveur déduisant le type de stockage de la présence d'un audio.

**Trois colonnes ont été retirées** par rapport au script du 04/11 :
`songs.audio_file`, `songs.karaoke_audio` et `songs.playback`, en `BYTEA`.
C'était le dessin d'origine, qui rangeait les fichiers dans la base même.
Aucune entité ne les mappe plus, et comme Hibernate tourne en
`ddl-auto: update` — qui ajoute une colonne mais n'en supprime jamais — elles
seraient restées à vie sur chaque poste neuf.

C'est le seul écart avec la base de développement : **248 colonnes sur 251 sont
identiques**, et les trois manquantes sont précisément celles-là.

**Deux index ont été ajoutés**, repris de `MAJ_listening_history.sql` :
`idx_listening_history_user_date` et `idx_listening_history_song`. L'historique
est toujours lu par utilisateur et du plus récent au plus ancien, donc l'ordre
`DESC` fait partie de l'index.

## Deux fichiers volontairement laissés de côté

`baseDeDonne/insert_test_data.sql` **n'est pas repris** : il est remplacé par
`schema/02-comptes-de-base.sql`, pour les trois raisons détaillées plus haut.
Ses quatre `INSERT` de tables de référence n'y manquent pas non plus — le
schéma les sème déjà, et de façon plus complète : l'ancien fichier ne listait
que deux valeurs pour `user_statuses` et pour `verification_statuses`, là où le
schéma en pose quatre de chaque.

`baseDeDonne/song_genres_data.sql` n'est pas repris non plus, et il ne faut pas
le lancer : il rattache aux genres des chansons d'identifiants 2, 3, 4 et 5. Or les
chansons commencent à 301, aussi bien dans une base neuve que dans la base de
développement, qui n'en contient aucune entre 2 et 5. Le fichier visait une base
antérieure et échoue aujourd'hui sur une violation de clé étrangère, partout.

Ce qu'il apportait est déjà là : les 10 genres viennent du schéma, et les 36
liens chanson / genre de `02-contenu-kaloy.sql`.

## Hibernate rétrécit 17 colonnes au premier démarrage

Mesuré le 09/11/2026, et contraire à ce qui était écrit jusque-là.

Une base créée par ce dossier déclare 17 colonnes en `TEXT`. **Au premier
démarrage de l'application, Hibernate les ramène toutes à `varchar(255)`.**
Vérifié en comparant les colonnes avant et après un unique lancement sur une
base neuve : 17 `ALTER` silencieux, sans une ligne d'erreur.

| Colonne | Déclarée | Après un démarrage |
| --- | --- | --- |
| `comments.content` | `TEXT` | `varchar(255)` |
| `notifications.content` | `TEXT` | `varchar(255)` |
| `songs.lyrics_sync_data` | `JSONB` | `varchar(255)` |
| `songs.audio_url`, `songs.video_url` | `TEXT` | `varchar(255)` |
| `artists.bio`, `artists.photo_url` | `TEXT` | `varchar(255)` |
| `events.description`, `concerts.description` | `TEXT` | `varchar(255)` |
| `albums.cover_url`, `event_media.url` | `TEXT` | `varchar(255)` |
| `editorial_playlists.cover_url`, `.description` | `TEXT` | `varchar(255)` |
| `artist_group_members.photo_url` | `TEXT` | `varchar(255)` |
| `content_submissions.error_message` | `TEXT` | `varchar(255)` |
| `reports.reason`, `user_status_history.reason` | `TEXT` | `varchar(255)` |

**Ce que l'on croyait, et qui est faux.** Il était écrit que `ddl-auto: update`
ajoute une colonne mais n'altère jamais une colonne existante, et que l'état de
la base de développement venait d'une dérive historique. Non : Hibernate altère
bel et bien, dans le sens du rétrécissement, et il le refait à chaque
démarrage. La base de développement est le **résultat** de ce rétrécissement,
pas d'un accident.

**Ce que cela implique.** Les `TEXT` de ce schéma ne tiennent pas tout seuls.
Deux conséquences concrètes :

- `comments.content` retombe à 255 caractères alors que le mobile en autorise
  1000 : un commentaire long est refusé par la base ;
- `songs.lyrics_sync_data` cesse d'être du `JSONB` alors que `Song.java`
  l'écrit avec un `CAST(? as jsonb)`. Latent, rien ne l'écrit encore.

La correction appartient aux entités, par un `columnDefinition = "TEXT"` sur
les 17 champs concernés, et non à ce dossier : tant qu'elle n'est pas faite,
relancer le schéma ne sert à rien, le premier démarrage annule le travail.

## Après la base

L'application a aussi besoin de deux choses hors PostgreSQL :

- `Mozika/src/main/resources/firebase-service-account.json`, qui n'est **pas**
  dans le dépôt — il est dans `.gitignore` — et doit être récupéré à part ;
- MinIO en service sur `localhost:9000`, aux clés indiquées dans
  `application.yml`, avec le bucket `kaloy`.

Le contexte est `/mozika` : une fois lancé, le serveur répond sur
`http://localhost:8087/mozika`.

## Ce que ce dossier ne peut pas apporter

**Les fichiers audio ne sont pas dedans, et ne peuvent pas l'être.** Sur les
45 chansons du jeu de données, 30 n'ont aucun audio et les 15 autres portent un
chemin du genre `Reko/<titre>.mp3`, qui est le nom d'un objet dans le bucket
MinIO `kaloy`. Un poste neuf a un bucket vide : la base sera juste, les écrans
se rempliront, et la lecture ne donnera rien.

Deux façons de s'en sortir, selon le besoin :

- déposer les fichiers dans le bucket `kaloy` en conservant exactement ces
  noms, par la console MinIO sur `http://localhost:9001` ;
- ou déclarer une chanson depuis l'application, ce qui envoie le fichier et
  écrit un chemin `/uploads/<nom>` servi par le serveur lui-même. C'est le
  chemin que suivent les chansons déclarées depuis le 03/11.

Les deux formes cohabitent en base sans problème : le serveur distingue un
objet MinIO d'un fichier local au moment de résoudre l'adresse de lecture.
