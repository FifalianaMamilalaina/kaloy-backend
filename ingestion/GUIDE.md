# Guide du pipeline d'ingestion Kaloy

Ce document s'adresse à la personne qui va brancher ce pipeline sur la partie
web. Aucune connaissance préalable de RabbitMQ n'est nécessaire : tout ce qu'il
faut savoir est expliqué ici.

---

## 1. À quoi sert ce programme

Les artistes envoient leurs contenus sous forme d'**archive `.zip`** : des
fichiers MP3, une pochette, parfois un clip, parfois les paroles en PDF, parfois
un tableur Excel décrivant le tout.

Ce pipeline **transforme automatiquement cette archive en contenu publié** dans
le catalogue Kaloy. Il lit les fichiers, en extrait les informations, retrouve
l'artiste concerné, envoie le tout à l'API, puis fait le ménage.

En une phrase : **tu déposes un `.zip` dans un dossier, et la chanson apparaît
dans l'application.**

Le pipeline n'a **aucune interface**. Il tourne en arrière-plan et surveille un
dossier. Côté web, ton travail consistera à y déposer les archives reçues, puis
à lire l'état du traitement en base.

---

## 2. Comment ça marche, en images

Le traitement est découpé en quatre programmes qui se passent le relais :

```
   depot/Rossy_TsyVery.zip
            │
            ▼
   ┌──────────────────┐
   │  PROGRAMME 1     │  Repère la nouvelle archive, la décompresse,
   │  Surveillance    │  trie les fichiers (audio, vidéo, image, PDF, Excel)
   └────────┬─────────┘
            │  file d'attente : kaloy.ingestion.extraits
            ▼
   ┌──────────────────┐
   │  PROGRAMME 2     │  Lit les tags du MP3, le tableur Excel,
   │  Métadonnées     │  le PDF des paroles, et croise les trois
   └────────┬─────────┘
            │  file d'attente : kaloy.ingestion.metadonnees
            ▼
   ┌──────────────────┐
   │  PROGRAMME 3     │  Retrouve l'artiste, envoie les fichiers,
   │  Publication     │  crée l'album et la chanson via l'API
   └────────┬─────────┘
            │  file d'attente : kaloy.ingestion.publies
            ▼
   ┌──────────────────┐
   │  PROGRAMME 4     │  Supprime l'archive du dépôt,
   │  Nettoyage       │  UNIQUEMENT si tout a réussi
   └──────────────────┘
```

### Pourquoi des « files d'attente » ?

Une file d'attente, c'est une boîte aux lettres entre deux programmes. Le
Programme 1 y dépose un message, le Programme 2 vient le chercher quand il est
prêt.

L'intérêt est concret : **si un programme s'arrête, les messages ne sont pas
perdus**. Ils attendent dans la file. Au redémarrage, le traitement reprend là
où il s'était interrompu, sans repartir du début.

C'est **RabbitMQ** qui gère ces files. Tu n'auras jamais à le manipuler : il
tourne comme un service et le pipeline s'y connecte tout seul.

---

## 3. Ce qu'il faut avoir avant de lancer

Quatre choses doivent tourner. Vérifie-les dans cet ordre.

### 3.1 PostgreSQL

La base `musical_app` sur le port 5432. C'est là que finiront les chansons.

### 3.2 RabbitMQ

Installé comme service Windows. Pour vérifier qu'il tourne :

```powershell
Get-Service RabbitMQ
```

Tu dois voir `Running`. Si ce n'est pas le cas :

```powershell
Start-Service RabbitMQ
```

### 3.3 L'API Kaloy

C'est elle qui écrit réellement en base. Le pipeline ne fait que l'appeler.

```powershell
cd kaloy-backend\Mozika
.\mvnw.cmd spring-boot:run
```

Attends la ligne `Started MozikaApplication`. L'API répond alors sur
`http://localhost:8087/mozika`.

### 3.4 Le pipeline

```powershell
cd kaloy-backend\ingestion
.\mvnw.cmd spring-boot:run
```

Attends `Started IngestionApplication`. Il surveille dès lors le dossier de
dépôt, **toutes les 5 secondes**.

> **Ordre important** : lance l'API *avant* le pipeline. Le pipeline a besoin de
> se connecter à l'API pour publier.

---

## 4. Comment nommer une archive

C'est le point le plus important du guide, parce que c'est **le nom de
l'archive qui détermine à quel artiste le contenu sera rattaché**.

### La règle

> Tout ce qui précède le premier tiret bas (`_`) est le **nom de scène** de
> l'artiste.

| Nom de l'archive | Artiste identifié |
|---|---|
| `Rossy_TsyVery.zip` | Rossy |
| `Mage 4_Tantara.zip` | Mage 4 |
| `Jaojoby_Album2026.zip` | Jaojoby |
| `Tence Mena_MilaAnao.zip` | Tence Mena |

Ce qui suit le `_` n'a pas d'importance pour le programme : mets-y ce qui t'aide
à t'y retrouver.

### Ce qui est toléré

La comparaison **ignore les majuscules et les accents**. `rossy_album.zip`,
`ROSSY_album.zip` et `Rossy_album.zip` désignent donc le même artiste.

### Ce qui échoue

- **Pas de `_` du tout** → `album2026.zip` : le programme cherchera un artiste
  nommé « album2026 », qui n'existe pas.
- **Nom de scène inconnu** → `Inconnu_Test.zip` : aucun artiste ne correspond.
- **Deux artistes portent le même nom de scène** : le programme refuse plutôt
  que de choisir au hasard.

Dans tous ces cas, **l'archive reste dans le dossier de dépôt**. Rien n'est
perdu : corrige le nom, et elle sera reprise au scan suivant.

---

## 5. Ce que contient l'archive

Mets les fichiers **directement dans le zip**, sans sous-dossier obligatoire.

| Type | Extensions | Rôle |
|---|---|---|
| Audio | `.mp3` | La chanson. **Obligatoire** : sans MP3, rien n'est publié |
| Vidéo | `.mp4` | Le clip, facultatif |
| Image | `.jpg`, `.jpeg`, `.png` | La pochette, facultative |
| Paroles | `.pdf` | Les paroles, facultatives |
| Métadonnées | `.xlsx`, `.xls` | Le tableur, facultatif |

Les fichiers d'un autre type sont ignorés sans faire échouer l'import.

### Le nommage du PDF de paroles

Pour que les paroles soient rattachées à la bonne chanson :

```
NomArtiste_TitreChanson_paroles.pdf
```

Exemple : pour la chanson « Tsy Very » de Rossy →
`Rossy_TsyVery_paroles.pdf`.

Là encore, accents et majuscules sont ignorés lors du rapprochement.

### Le tableur Excel

Il est **facultatif**, mais recommandé : ce que l'artiste y écrit est considéré
comme plus fiable que les tags du MP3.

La **première ligne doit contenir les titres des colonnes**. Les colonnes
reconnues :

| Colonne | Contenu |
|---|---|
| `Titre` | Le titre de la chanson |
| `Album` | Le nom de l'album |
| `Genre` | Le genre musical |
| `Date de sortie` | Format `AAAA-MM-JJ`, par exemple `2026-08-21` |
| `Artiste` | Le nom de scène |

L'ordre des colonnes n'a **aucune importance**, et tu peux en ajouter d'autres :
elles seront ignorées. Les libellés sont reconnus quelle que soit leur écriture
(`Date de sortie`, `date_de_sortie`, `DATE DE SORTIE` fonctionnent tous).

### D'où viennent les informations, au juste

Quand plusieurs sources donnent la même information, le pipeline applique cet
ordre de priorité :

```
1. Le tableur Excel      (une saisie volontaire de l'artiste)
2. Les tags du MP3       (souvent générés par un logiciel)
3. Le nom du fichier     (en dernier recours)
```

---

## 6. Comment savoir si ça a marché

Deux endroits à regarder. Ils sont complémentaires.

### 6.1 Le dossier de dépôt

C'est l'indicateur le plus simple :

- **L'archive a disparu** → tout s'est bien passé, le contenu est en base.
- **L'archive est toujours là** → quelque chose a échoué.

Cette règle est volontaire : une archive n'est supprimée **qu'après**
confirmation que le contenu est bien enregistré. Rien ne peut être perdu
silencieusement.

### 6.2 La table `content_submissions`

C'est la table d'état du pipeline, celle que tu interrogeras depuis le web.

| Colonne | Contenu |
|---|---|
| `source_filename` | Le nom du fichier traité |
| `file_type` | `AUDIO` ou `ARCHIVE` |
| `status_id` | Le statut, à joindre avec `submission_statuses` |
| `error_message` | Le motif, quand il y a eu échec |
| `submitted_at` | Date de prise en charge |
| `processed_at` | Date de fin de traitement |

Les statuts possibles : `PENDING`, `PROCESSING`, `SUCCESS`, `ERROR`.

Une requête pour voir les derniers traitements :

```sql
SELECT cs.source_filename,
       cs.file_type,
       st.name   AS statut,
       cs.error_message,
       cs.processed_at
FROM content_submissions cs
JOIN submission_statuses st ON st.id = cs.status_id
ORDER BY cs.id DESC
LIMIT 20;
```

### 6.3 Les journaux du pipeline

La fenêtre où tourne le pipeline affiche le déroulement en clair :

```
Nouvelle archive detectee : Rossy_TsyVery.zip
Rossy_TsyVery.zip — 3 fichier(s) extrait(s), transmis au programme 2
Rossy_TsyVery.zip — 1 chanson(s) decrite(s), transmises au programme 3
Rossy_TsyVery.zip — publication de 1 chanson(s)
Rossy_TsyVery.zip — 1 publiee(s), 0 en echec
Rossy_TsyVery.zip — 1 chanson(s) publiee(s), archive retiree du depot
```

---

## 7. Les messages d'erreur, et quoi faire

| Message | Ce qui s'est passé | Correction |
|---|---|---|
| `Aucun artiste ne correspond a « X »` | Le nom avant le `_` ne correspond à aucun artiste en base | Vérifie l'orthographe du nom de scène, ou crée l'artiste |
| `Plusieurs artistes portent le nom « X »` | Deux comptes ont le même nom de scène | À trancher manuellement : le programme refuse de choisir |
| `Nom d'archive invalide` | Il n'y a pas de `_` dans le nom | Renomme en `NomArtiste_QuelqueChose.zip` |
| `Archive vide ou ne contenant aucun fichier exploitable` | Le zip ne contient aucun type reconnu | Vérifie que les MP3 sont bien à l'intérieur |
| `Aucun fichier audio ni tableur de metadonnees` | Pas de MP3 dans l'archive | Ajoute au moins un MP3 |
| `Format non supporte` | Un fichier dépasse les types acceptés à l'envoi | Convertis en MP3, MP4, JPEG, PNG ou PDF |
| `Fichier trop volumineux` | Plus de 200 Mo | Réduis la taille du fichier |

Dans **tous** ces cas, l'archive reste dans le dépôt. Corrige et elle sera
reprise automatiquement — ou redépose-la.

---

## 8. La configuration

Tout se règle dans `src/main/resources/application.yml`.

```yaml
kaloy:
  ingestion:
    depot: ${user.dir}/depot          # dossier surveillé
    travail: ${user.dir}/travail      # dossier temporaire de décompression
    intervalle-scan-ms: 5000          # fréquence de surveillance

    api:
      base-url: http://localhost:8087/mozika
      email: artist@dev.com           # compte de service
      mot-de-passe: password123

    notifications:
      actives: false                  # envoi d'emails en cas d'échec
      destinataire: ...
```

### Les réglages à connaître

**`depot`** — le dossier surveillé. `${user.dir}` désigne le dossier depuis
lequel tu lances le programme : par défaut, `kaloy-backend/ingestion/depot`.
C'est là que la partie web devra écrire les archives. Tu peux indiquer un chemin
absolu, par exemple `D:/kaloy/depot`.

**`api.email` et `api.mot-de-passe`** — le compte utilisé pour publier. Il doit
exister en base **et être vérifié** (statut `VERIFIED`), sinon la connexion est
refusée. En production, crée un compte de service dédié plutôt que d'utiliser un
compte de test.

**`notifications.actives`** — laissé à `false`. Quand c'est désactivé, le
message d'échec est simplement écrit dans les journaux, ce qui permet de voir ce
qui *aurait* été envoyé. Passe à `true` seulement quand tu veux réellement
envoyer des emails.

---

## 9. Brancher la partie web

Le pipeline est volontairement indépendant : tu n'as pas à l'appeler, ni à
connaître RabbitMQ.

**Pour importer un contenu**, la partie web a deux choses à faire :

1. **Écrire l'archive dans le dossier de dépôt**, en respectant la convention de
   nommage (`NomArtiste_QuelqueChose.zip`).
2. **Suivre l'état** en interrogeant `content_submissions`, ou via l'endpoint
   REST existant `POST /contentsubmissions/search`.

Un conseil : **écris d'abord le fichier sous un nom temporaire, puis renomme-le
en `.zip`** une fois l'écriture terminée. Le pipeline attend certes qu'une
archive cesse de grossir avant de l'ouvrir, mais le renommage est plus sûr.

C'est tout. Le reste est automatique.

---

## 10. Petit glossaire

**RabbitMQ** — un logiciel qui transporte des messages entre programmes. Il
fait office de boîte aux lettres : un programme dépose, un autre relève.

**File d'attente (queue)** — une boîte aux lettres nommée. Ici, trois :
`kaloy.ingestion.extraits`, `kaloy.ingestion.metadonnees`,
`kaloy.ingestion.publies`.

**AMQP** — le langage que parlent les programmes avec RabbitMQ, sur le port
5672. Tu n'as pas à t'en occuper.

**Tags ID3** — des informations enregistrées à l'intérieur même d'un fichier
MP3 (titre, artiste, album, genre). C'est ce qu'affiche un lecteur audio.

**Zip Slip** — une attaque où une archive contient un fichier nommé
`../../../quelque_chose`, afin d'écrire en dehors du dossier prévu. Le
Programme 1 vérifie chaque chemin et rejette ces entrées.

**Compte de service** — un compte utilisateur qui n'appartient à personne, créé
pour qu'un programme puisse s'authentifier auprès de l'API.

---

## 11. En cas de problème au démarrage

| Symptôme | Cause probable | Solution |
|---|---|---|
| `Connection refused: localhost:5672` | RabbitMQ n'est pas démarré | `Start-Service RabbitMQ` |
| `Connexion au compte de service impossible` | Identifiants faux, ou compte non vérifié | Vérifie `kaloy.ingestion.api` |
| `Connection refused: localhost:8087` | L'API n'est pas lancée | Démarre `Mozika` d'abord |
| Rien ne se passe après un dépôt | Mauvais dossier surveillé | Compare le chemin affiché au démarrage avec l'emplacement du zip |
| L'archive reste sans message d'erreur | Elle a déjà été traitée dans cette session | Redémarre le pipeline : il la reprendra |

---

## 12. Limites actuelles

Trois points à connaître, pour ne pas chercher un bug là où il n'y en a pas :

- **Les métadonnées des fichiers MP4 ne sont pas lues.** La vidéo est bien
  envoyée et rattachée à la chanson, mais ses tags internes sont ignorés.
- **Un échec très précoce ne laisse pas de trace en base.** Si l'artiste n'a pas
  pu être identifié, la ligne `content_submissions` ne peut pas être écrite, car
  elle exige un artiste. Le motif reste alors visible dans les journaux.
- **Une archive invalide est retentée à chaque redémarrage** du pipeline, tant
  qu'elle reste dans le dépôt. C'est voulu : cela permet de corriger puis de
  relancer. Mais une archive définitivement invalide doit être retirée à la main.
