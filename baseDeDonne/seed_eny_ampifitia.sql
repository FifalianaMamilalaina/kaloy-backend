-- ---------------------------------------------------------------------
-- Donnees reelles : « Eny Ampifitia », FJKM Isotry Fitiavana
--
-- POURQUOI CE JEU EXISTE
--
-- Jusqu'ici aucune chanson n'avait de vrai fichier : audio_url, karaoke_audio_url
-- et playback_url etaient vides sur les 30 chansons, et video_url pointait sur un
-- lien de remplissage. On ne pouvait donc verifier ni la lecture audio, ni le
-- karaoke, ni le lecteur embarque — seulement que les ecrans s'affichaient.
--
-- Ce jeu cree une chanson avec ses quatre medias reels, un par mode du lecteur :
--
--   AUDIO    audio_url          le chant enregistre
--   KARAOKE  karaoke_audio_url  l'instrumental en video
--   PLAYBACK playback_url       l'instrumental en audio seul
--   VIDEO    video_url          le clip, sur YouTube
--
-- La colonne karaoke_audio_url porte un fichier VIDEO malgre son nom : c'est
-- voulu. Cote mobile, le mode KARAOKE est rendu par le lecteur video, pas par
-- le lecteur audio. Le nom de la colonne est trompeur, pas le contenu.
--
-- OU SONT LES FICHIERS
--
-- Dans Mozika/uploads/, servis par le backend lui-meme. Ce n'est pas MinIO :
-- MinIO est prevu par l'architecture mais n'a jamais servi — zero ligne en base
-- l'utilise — alors que /uploads sert deja les photos d'evenement et recoit les
-- fichiers du pipeline d'ingestion. getSongPlayerDetails reconnait desormais les
-- deux : une URL absolue est renvoyee telle quelle, un nom d'objet passe par
-- MinIO.
--
-- LES CHEMINS SONT RELATIFS, et c'est voulu. Une adresse absolue figerait en
-- base le nom d'hote du moment : l'adresse Wi-Fi du poste change, et tous les
-- medias deja enregistres deviennent injoignables. Pire, 10.0.2.2 ne designe
-- rien sur un telephone reel. L'application prefixe ces chemins avec sa propre
-- adresse de base, qu'elle connait par construction.
--
-- PREREQUIS : les trois fichiers doivent etre presents dans Mozika/uploads/.
-- ---------------------------------------------------------------------

-- Compte de l'artiste. Le mot de passe est repris d'un compte existant plutot
-- que recopie en clair : tous les comptes de test partagent le meme.
INSERT INTO users (id, email, password_hash, role_id, status_id, email_verified_at, created_at, updated_at)
SELECT 107,
       'isotry@kaloy.mg',
       (SELECT password_hash FROM users WHERE email = 'jaojoby@kaloy.mg'),
       (SELECT id FROM user_roles WHERE name = 'ARTIST'),
       (SELECT id FROM user_statuses WHERE name = 'ACTIVE'),
       NOW(), NOW(), NOW()
WHERE EXISTS (SELECT 1 FROM users WHERE email = 'jaojoby@kaloy.mg')
ON CONFLICT (id) DO NOTHING;

-- GROUP et non SOLO : c'est un choeur.
INSERT INTO artists (id, user_id, artist_type_id, stage_name, active_since_year,
                     photo_url, bio, verification_status_id, verified_at, is_certified, created_at)
SELECT 107, 107,
       (SELECT id FROM artist_types WHERE name = 'GROUP'),
       'FJKM Isotry Fitiavana',
       NULL,
       NULL,
       'Choeur de la paroisse FJKM Isotry Fitiavana, a Antananarivo.',
       (SELECT id FROM verification_statuses WHERE name = 'VERIFIED'),
       NOW(), TRUE, NOW()
WHERE EXISTS (SELECT 1 FROM users WHERE id = 107)
ON CONFLICT (id) DO NOTHING;

-- La duree est deduite du debit annonce par le nom du fichier (128 kbit/s),
-- pas lue dans le conteneur : c'est une valeur d'affichage, le lecteur
-- determine lui-meme la duree reelle au moment de la lecture.
INSERT INTO songs (id, artist_id, album_id, title, duration_seconds, release_date,
                   language, author_composer, storage_type_id,
                   audio_url, video_url, karaoke_audio_url, playback_url,
                   is_downloadable, created_at)
SELECT 331, 107, NULL,
       'Eny Ampifitia',
       316,
       NULL,
       'MG',
       NULL,
       (SELECT id FROM audio_storage_types WHERE name = 'EXTERNAL_LINK'),
       '/uploads/eny-ampifitia-chant.m4a',
       'https://youtu.be/hysXgQivtyY',
       '/uploads/eny-ampifitia-karaoke.mp4',
       '/uploads/eny-ampifitia-playback.m4a',
       FALSE, NOW()
WHERE EXISTS (SELECT 1 FROM artists WHERE id = 107)
ON CONFLICT (id) DO NOTHING;


-- ---------------------------------------------------------------------
-- Controle
-- ---------------------------------------------------------------------
SELECT s.id, s.title, a.stage_name,
       s.audio_url IS NOT NULL AS a_audio,
       s.karaoke_audio_url IS NOT NULL AS a_karaoke,
       s.playback_url IS NOT NULL AS a_playback,
       s.video_url IS NOT NULL AS a_clip
FROM songs s JOIN artists a ON a.id = s.artist_id
WHERE s.id = 331;
