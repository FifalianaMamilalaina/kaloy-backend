-- =====================================================================
-- Jeu de donnees de contenu pour Kaloy
-- =====================================================================
-- A lancer une fois sur la base "musical_app" :
--   psql -U postgres -d musical_app -f seed_contenu_kaloy.sql
--
-- Le script est REJOUABLE : les identifiants sont explicites et chaque
-- insertion est protegee par ON CONFLICT DO NOTHING. Le relancer ne cree
-- donc pas de doublons.
--
-- Il fait deux choses :
--   1. Repare les deux comptes de developpement, qui ne pouvaient pas se
--      connecter (hash BCrypt factice + statut de verification absent).
--   2. Insere artistes, albums, chansons, genres, playlists et evenements.
--
-- Plages d'identifiants reservees au jeu de test, pour ne jamais entrer en
-- collision avec les donnees creees par l'application :
--   utilisateurs artistes 101-106   artistes    101-106
--   albums                201-210   chansons    301-330
--   evenements            401-404   lieux       501-503
--   playlists editoriales 601-604
-- =====================================================================


-- ---------------------------------------------------------------------
-- 1. Comptes de developpement
-- ---------------------------------------------------------------------
-- Le hash present dans insert_test_data.sql etait un motif repete, pas un
-- vrai hash BCrypt : la connexion echouait en 401. Celui-ci est un vrai
-- hash de "password123", verifie avec BCryptPasswordEncoder.
--
-- De plus login() exige verification_statuses_users = VERIFIED (sinon 403
-- « Compte non verifie »), or les deux comptes avaient la colonne a NULL.
--
--   client@dev.com / password123   (role CLIENT)
--   artist@dev.com / password123   (role ARTIST)

UPDATE users
SET password_hash            = '$2a$10$/LJzCEMdUsedkJ8qUbdRweJtacnT86C5AHv8SraglJeeoDVCttDY2',
    verification_status_user_id = (SELECT id FROM verification_statuses_users WHERE name = 'VERIFIED'),
    email_verified_at        = COALESCE(email_verified_at, NOW()),
    status_id                = (SELECT id FROM user_statuses WHERE name = 'ACTIVE'),
    updated_at               = NOW()
WHERE email IN ('client@dev.com', 'artist@dev.com');

-- Le compte client doit exister dans "clients" pour que le profil fonctionne.
INSERT INTO clients (user_id)
SELECT id FROM users WHERE email = 'client@dev.com'
ON CONFLICT DO NOTHING;

INSERT INTO users_infos (name, last_name, user_name, id_user)
SELECT 'Client', 'Test', 'client_test', id FROM users WHERE email = 'client@dev.com'
ON CONFLICT DO NOTHING;


-- ---------------------------------------------------------------------
-- 2. Genres
-- ---------------------------------------------------------------------
INSERT INTO genres (id, name) VALUES
  (1,  'Salegy'),
  (2,  'Tsapiky'),
  (3,  'Hira Gasy'),
  (4,  'Kilalaky'),
  (5,  'Basesa'),
  (6,  'Afindrafindrao'),
  (7,  'Gospel'),
  (8,  'Pop'),
  (9,  'Jazz'),
  (10, 'Slam')
ON CONFLICT (id) DO NOTHING;


-- ---------------------------------------------------------------------
-- 3. Comptes utilisateurs portant les artistes
-- ---------------------------------------------------------------------
-- La table "artists" exige un user_id unique : chaque artiste a donc son
-- propre compte. Ils partagent le mot de passe password123 et sont marques
-- VERIFIED, ce qui permet de se connecter en tant qu'artiste pour tester.

INSERT INTO users (id, email, password_hash, role_id, status_id, verification_status_user_id, email_verified_at, created_at, updated_at)
SELECT v.id,
       v.email,
       '$2a$10$/LJzCEMdUsedkJ8qUbdRweJtacnT86C5AHv8SraglJeeoDVCttDY2',
       (SELECT id FROM user_roles WHERE name = 'ARTIST'),
       (SELECT id FROM user_statuses WHERE name = 'ACTIVE'),
       (SELECT id FROM verification_statuses_users WHERE name = 'VERIFIED'),
       NOW(), NOW(), NOW()
FROM (VALUES
  (101, 'rossy@kaloy.mg'),
  (102, 'jaojoby@kaloy.mg'),
  (103, 'mage4@kaloy.mg'),
  (104, 'ambondrona@kaloy.mg'),
  (105, 'samoela@kaloy.mg'),
  (106, 'tencemena@kaloy.mg')
) AS v(id, email)
ON CONFLICT (id) DO NOTHING;


-- ---------------------------------------------------------------------
-- 4. Artistes
-- ---------------------------------------------------------------------
INSERT INTO artists (id, user_id, artist_type_id, stage_name, active_since_year, photo_url, bio, verification_status_id, verified_at, is_certified, created_at)
SELECT v.id,
       v.user_id,
       (SELECT id FROM artist_types WHERE name = v.type),
       v.stage_name,
       v.annee,
       v.photo,
       v.bio,
       (SELECT id FROM verification_statuses WHERE name = 'VALIDATED'),
       NOW(),
       v.certifie,
       NOW()
FROM (VALUES
  (101, 101, 'SOLO',  'Rossy',       1980,
   'https://picsum.photos/seed/rossy/400/400',
   'Figure majeure de la musique malgache, Rossy melange salegy, vakisaova et influences occidentales depuis le debut des annees 1980. Son groupe a porte la musique de Madagascar sur les scenes internationales.',
   TRUE),
  (102, 102, 'SOLO',  'Jaojoby',     1970,
   'https://picsum.photos/seed/jaojoby/400/400',
   'Surnomme le roi du salegy, Jaojoby a transforme un rythme traditionnel du nord de Madagascar en un genre populaire dans tout le pays. Sa voix rauque et ses cuivres sont immediatement reconnaissables.',
   TRUE),
  (103, 103, 'GROUP', 'Mage 4',      1996,
   'https://picsum.photos/seed/mage4/400/400',
   'Groupe emblematique de la scene pop malgache, Mage 4 s''est impose avec des melodies accrocheuses et des textes en malgache qui parlent du quotidien.',
   TRUE),
  (104, 104, 'GROUP', 'Ambondrona',  1999,
   'https://picsum.photos/seed/ambondrona/400/400',
   'Groupe de pop-rock d''Antananarivo, Ambondrona est connu pour ses refrains rassembleurs et ses concerts au Palais des Sports.',
   FALSE),
  (105, 105, 'SOLO',  'Samoela',     1995,
   'https://picsum.photos/seed/samoela/400/400',
   'Auteur-compositeur a la plume acerbe, Samoela porte une parole sociale sur des arrangements acoustiques depouilles.',
   FALSE),
  (106, 106, 'SOLO',  'Tence Mena',  2008,
   'https://picsum.photos/seed/tencemena/400/400',
   'Voix feminine majeure du salegy moderne, Tence Mena mele rythmes traditionnels du nord et production contemporaine.',
   TRUE)
) AS v(id, user_id, type, stage_name, annee, photo, bio, certifie)
ON CONFLICT (id) DO NOTHING;

INSERT INTO users_infos (name, last_name, user_name, id_user)
SELECT v.prenom, v.nom, v.pseudo, v.user_id
FROM (VALUES
  ('Paul',      'Bert',      'rossy_officiel',      101),
  ('Eusebe',    'Jaojoby',   'jaojoby_officiel',    102),
  ('Groupe',    'Mage 4',    'mage4_officiel',      103),
  ('Groupe',    'Ambondrona','ambondrona_officiel', 104),
  ('Samoela',   'Rakoto',    'samoela_officiel',    105),
  ('Tence',     'Mena',      'tencemena_officiel',  106)
) AS v(prenom, nom, pseudo, user_id)
ON CONFLICT DO NOTHING;


-- ---------------------------------------------------------------------
-- 5. Albums
-- ---------------------------------------------------------------------
INSERT INTO albums (id, artist_id, title, cover_url, release_date, created_at) VALUES
  (201, 101, 'Tsy Very',          'https://picsum.photos/seed/alb201/400/400', '1993-05-12', NOW()),
  (202, 101, 'Island of Ghosts',  'https://picsum.photos/seed/alb202/400/400', '1991-09-01', NOW()),
  (203, 102, 'Salegy !',          'https://picsum.photos/seed/alb203/400/400', '1992-03-20', NOW()),
  (204, 102, 'Malagasy',          'https://picsum.photos/seed/alb204/400/400', '2004-11-08', NOW()),
  (205, 103, 'Mifankatiava',      'https://picsum.photos/seed/alb205/400/400', '2001-07-15', NOW()),
  (206, 103, 'Tantara',           'https://picsum.photos/seed/alb206/400/400', '2008-02-29', NOW()),
  (207, 104, 'Ambondrona',        'https://picsum.photos/seed/alb207/400/400', '2003-06-10', NOW()),
  (208, 104, 'Vazaha',            'https://picsum.photos/seed/alb208/400/400', '2010-10-01', NOW()),
  (209, 105, 'Tsy Misy Fady',     'https://picsum.photos/seed/alb209/400/400', '1998-04-18', NOW()),
  (210, 106, 'Mila Anao',         'https://picsum.photos/seed/alb210/400/400', '2015-12-05', NOW())
ON CONFLICT (id) DO NOTHING;


-- ---------------------------------------------------------------------
-- 6. Chansons
-- ---------------------------------------------------------------------
-- storage_type = EXTERNAL_LINK et une video_url YouTube, car l'ecran de
-- detail chanson sait deja lire une video externe.

INSERT INTO songs (id, artist_id, album_id, title, duration_seconds, release_date, language, author_composer, storage_type_id, video_url, lyrics, is_downloadable, created_at)
SELECT v.id, v.artist_id, v.album_id, v.title, v.duree, v.sortie, 'mg', v.auteur,
       (SELECT id FROM audio_storage_types WHERE name = 'EXTERNAL_LINK'),
       v.video, v.paroles, TRUE, NOW()
FROM (VALUES
  -- Rossy / Tsy Very
  (301, 101, 201, 'Tsy Very',            245, DATE '1993-05-12', 'Rossy',      'https://www.youtube.com/watch?v=dQw4w9WgXcQ', 'Tsy very ny fitiavana...'),
  (302, 101, 201, 'Lalan-kizorana',      198, DATE '1993-05-12', 'Rossy',      'https://www.youtube.com/watch?v=dQw4w9WgXcQ', NULL),
  (303, 101, 201, 'Mandeha ny fotoana',  312, DATE '1993-05-12', 'Rossy',      NULL, NULL),
  -- Rossy / Island of Ghosts
  (304, 101, 202, 'Island of Ghosts',    276, DATE '1991-09-01', 'Rossy',      'https://www.youtube.com/watch?v=dQw4w9WgXcQ', NULL),
  (305, 101, 202, 'Vakisaova',           224, DATE '1991-09-01', 'Rossy',      NULL, NULL),
  (306, 101, 202, 'Zaza Gasy',           189, DATE '1991-09-01', 'Rossy',      NULL, NULL),
  -- Jaojoby / Salegy !
  (307, 102, 203, 'Samy Mandeha',        301, DATE '1992-03-20', 'E. Jaojoby', 'https://www.youtube.com/watch?v=dQw4w9WgXcQ', 'Samy mandeha samy mitady...'),
  (308, 102, 203, 'Malemilemy',          267, DATE '1992-03-20', 'E. Jaojoby', NULL, NULL),
  (309, 102, 203, 'Fitiavana Tsy Mifidy',290, DATE '1992-03-20', 'E. Jaojoby', NULL, NULL),
  -- Jaojoby / Malagasy
  (310, 102, 204, 'Malagasy',            258, DATE '2004-11-08', 'E. Jaojoby', 'https://www.youtube.com/watch?v=dQw4w9WgXcQ', NULL),
  (311, 102, 204, 'Aza Mandeha',         233, DATE '2004-11-08', 'E. Jaojoby', NULL, NULL),
  (312, 102, 204, 'Vetson-tanana',       279, DATE '2004-11-08', 'E. Jaojoby', NULL, NULL),
  -- Mage 4 / Mifankatiava
  (313, 103, 205, 'Mifankatiava',        212, DATE '2001-07-15', 'Mage 4',     'https://www.youtube.com/watch?v=dQw4w9WgXcQ', 'Mifankatiava isika...'),
  (314, 103, 205, 'Ny Alina',            195, DATE '2001-07-15', 'Mage 4',     NULL, NULL),
  (315, 103, 205, 'Tiako Ianao',         241, DATE '2001-07-15', 'Mage 4',     NULL, NULL),
  -- Mage 4 / Tantara
  (316, 103, 206, 'Tantara',             228, DATE '2008-02-29', 'Mage 4',     NULL, NULL),
  (317, 103, 206, 'Indray Andro',        254, DATE '2008-02-29', 'Mage 4',     NULL, NULL),
  (318, 103, 206, 'Veloma',              203, DATE '2008-02-29', 'Mage 4',     NULL, NULL),
  -- Ambondrona / Ambondrona
  (319, 104, 207, 'Mitady Anao',         236, DATE '2003-06-10', 'Ambondrona', 'https://www.youtube.com/watch?v=dQw4w9WgXcQ', NULL),
  (320, 104, 207, 'Anaty Rahona',        271, DATE '2003-06-10', 'Ambondrona', NULL, NULL),
  (321, 104, 207, 'Tsiky',               188, DATE '2003-06-10', 'Ambondrona', NULL, NULL),
  -- Ambondrona / Vazaha
  (322, 104, 208, 'Vazaha',              249, DATE '2010-10-01', 'Ambondrona', NULL, NULL),
  (323, 104, 208, 'Lavitra Anao',        263, DATE '2010-10-01', 'Ambondrona', NULL, NULL),
  (324, 104, 208, 'Mamiratra',           215, DATE '2010-10-01', 'Ambondrona', NULL, NULL),
  -- Samoela / Tsy Misy Fady
  (325, 105, 209, 'Tsy Misy Fady',       294, DATE '1998-04-18', 'Samoela',    'https://www.youtube.com/watch?v=dQw4w9WgXcQ', 'Tsy misy fady eto...'),
  (326, 105, 209, 'Fahantrana',          318, DATE '1998-04-18', 'Samoela',    NULL, NULL),
  (327, 105, 209, 'Rariny',              247, DATE '1998-04-18', 'Samoela',    NULL, NULL),
  -- Tence Mena / Mila Anao
  (328, 106, 210, 'Mila Anao',           222, DATE '2015-12-05', 'Tence Mena', 'https://www.youtube.com/watch?v=dQw4w9WgXcQ', NULL),
  (329, 106, 210, 'Tsy Hadino',          256, DATE '2015-12-05', 'Tence Mena', NULL, NULL),
  (330, 106, 210, 'Mahery Fo',           238, DATE '2015-12-05', 'Tence Mena', NULL, NULL)
) AS v(id, artist_id, album_id, title, duree, sortie, auteur, video, paroles)
ON CONFLICT (id) DO NOTHING;


-- ---------------------------------------------------------------------
-- 7. Genres des chansons
-- ---------------------------------------------------------------------
-- Chaque genre a au moins deux chansons, pour que l'ecran genre ne soit
-- jamais vide quel que soit le genre ouvert.

INSERT INTO song_genres (song_id, genre_id)
SELECT v.song_id, v.genre_id
FROM (VALUES
  (301,1),(302,6),(303,1),(304,1),(305,6),(306,8),
  (307,1),(308,1),(309,1),(310,1),(311,5),(312,5),
  (313,8),(314,8),(315,8),(316,8),(317,9),(318,8),
  (319,8),(320,8),(321,8),(322,8),(323,9),(324,7),
  (325,10),(326,10),(327,3),(328,1),(329,2),(330,4),
  (303,3),(305,2),(312,4),(326,3),(330,2),(327,10)
) AS v(song_id, genre_id)
WHERE NOT EXISTS (
  SELECT 1 FROM song_genres sg
  WHERE sg.song_id = v.song_id AND sg.genre_id = v.genre_id
);


-- ---------------------------------------------------------------------
-- 8. Playlists editoriales (section « A la une » de l'accueil)
-- ---------------------------------------------------------------------
INSERT INTO editorial_playlists (id, artist_id, title, description, cover_url, is_featured, created_at) VALUES
  (601, 102, 'Salegy Essentiels',   'Les rythmes du nord qui font danser Madagascar.',        'https://picsum.photos/seed/pl601/400/400', TRUE,  NOW()),
  (602, 103, 'Pop Malagasy',        'La nouvelle scene pop d''Antananarivo.',                 'https://picsum.photos/seed/pl602/400/400', TRUE,  NOW()),
  (603, 105, 'Textes et Engagement', 'Quand la musique malgache prend la parole.',            'https://picsum.photos/seed/pl603/400/400', TRUE,  NOW()),
  (604, 101, 'Classiques Gasy',     'Les titres qui ont marque trois decennies.',             'https://picsum.photos/seed/pl604/400/400', FALSE, NOW())
ON CONFLICT (id) DO NOTHING;

INSERT INTO editorial_playlist_songs (editorial_playlist_id, song_id, position)
SELECT v.pl, v.song, v.pos
FROM (VALUES
  (601,307,1),(601,308,2),(601,310,3),(601,301,4),(601,328,5),
  (602,313,1),(602,315,2),(602,319,3),(602,322,4),(602,324,5),
  (603,325,1),(603,326,2),(603,327,3),
  (604,301,1),(604,304,2),(604,307,3),(604,313,4)
) AS v(pl, song, pos)
WHERE NOT EXISTS (
  SELECT 1 FROM editorial_playlist_songs e
  WHERE e.editorial_playlist_id = v.pl AND e.song_id = v.song
);


-- ---------------------------------------------------------------------
-- 9. Lieux et evenements
-- ---------------------------------------------------------------------
-- Prepare le terrain pour les Sprints 4 et 5 : deux evenements passes,
-- deux a venir, tous APPROVED pour qu'ils soient visibles.

INSERT INTO venues (id, name, location) VALUES
  (501, 'Palais des Sports Mahamasina', 'Antananarivo'),
  (502, 'Stade Barea Mahamasina',       'Antananarivo'),
  (503, 'CCI Ivato',                    'Ivato, Antananarivo')
ON CONFLICT (id) DO NOTHING;

INSERT INTO events (id, name, description, start_date, end_date, created_by_artist_id, moderation_status_id, reviewed_at, created_at)
SELECT v.id, v.nom, v.descr, v.debut, v.fin, v.artist_id,
       (SELECT id FROM event_moderation_statuses WHERE name = 'APPROVED'),
       NOW(), NOW()
FROM (VALUES
  (401, 'Nuit du Salegy',        'Une nuit entiere consacree au salegy, avec Jaojoby en tete d''affiche.',      DATE '2026-06-14', DATE '2026-06-14', 102),
  (402, 'Gasy Festival 2026',    'Trois jours de musique malgache toutes generations confondues.',              DATE '2026-08-21', DATE '2026-08-23', 101),
  (403, 'Concert Ambondrona',    'Le retour d''Ambondrona sur la scene du Palais des Sports.',                  DATE '2026-11-07', DATE '2026-11-07', 104),
  (404, 'Tananarive Live',       'Plateau partage entre Mage 4, Tence Mena et Samoela.',                        DATE '2026-12-19', DATE '2026-12-20', 103)
) AS v(id, nom, descr, debut, fin, artist_id)
ON CONFLICT (id) DO NOTHING;


-- ---------------------------------------------------------------------
-- 10. Historique d'ecoute du compte client
-- ---------------------------------------------------------------------
-- Alimente la section « Recemment ecoute » de l'accueil. Les dates sont
-- reculees pour que l'ordre d'affichage soit lisible.

INSERT INTO listening_history (user_id, song_id, play_mode_id, listened_at)
SELECT (SELECT id FROM users WHERE email = 'client@dev.com'),
       v.song,
       (SELECT id FROM play_modes WHERE name = 'AUDIO'),
       NOW() - (v.heures || ' hours')::interval
FROM (VALUES
  (307, 2), (313, 5), (301, 9), (325, 26), (319, 31), (328, 50)
) AS v(song, heures)
WHERE EXISTS (SELECT 1 FROM users WHERE email = 'client@dev.com')
  AND NOT EXISTS (
    SELECT 1 FROM listening_history lh
    WHERE lh.song_id = v.song
      AND lh.user_id = (SELECT id FROM users WHERE email = 'client@dev.com')
  );


-- ---------------------------------------------------------------------
-- 11. Realignement des sequences
-- ---------------------------------------------------------------------
-- Les identifiants ont ete fournis explicitement : sans ce realignement,
-- la prochaine insertion applicative repartirait de 1 et violerait la
-- contrainte de cle primaire.

SELECT setval(pg_get_serial_sequence('users',               'id'), GREATEST((SELECT MAX(id) FROM users),               1));
SELECT setval(pg_get_serial_sequence('artists',             'id'), GREATEST((SELECT MAX(id) FROM artists),             1));
SELECT setval(pg_get_serial_sequence('albums',              'id'), GREATEST((SELECT MAX(id) FROM albums),              1));
SELECT setval(pg_get_serial_sequence('songs',               'id'), GREATEST((SELECT MAX(id) FROM songs),               1));
SELECT setval(pg_get_serial_sequence('genres',              'id'), GREATEST((SELECT MAX(id) FROM genres),              1));
SELECT setval(pg_get_serial_sequence('events',              'id'), GREATEST((SELECT MAX(id) FROM events),              1));
SELECT setval(pg_get_serial_sequence('venues',              'id'), GREATEST((SELECT MAX(id) FROM venues),              1));
SELECT setval(pg_get_serial_sequence('editorial_playlists', 'id'), GREATEST((SELECT MAX(id) FROM editorial_playlists), 1));
SELECT setval(pg_get_serial_sequence('song_genres',         'id'), GREATEST((SELECT MAX(id) FROM song_genres),         1));
SELECT setval(pg_get_serial_sequence('editorial_playlist_songs', 'id'), GREATEST((SELECT MAX(id) FROM editorial_playlist_songs), 1));
SELECT setval(pg_get_serial_sequence('listening_history',   'id'), GREATEST((SELECT MAX(id) FROM listening_history),   1));
SELECT setval(pg_get_serial_sequence('users_infos',         'id'), GREATEST((SELECT MAX(id) FROM users_infos),         1));
SELECT setval(pg_get_serial_sequence('clients',             'id'), GREATEST((SELECT MAX(id) FROM clients),             1));


-- ---------------------------------------------------------------------
-- 12. Recapitulatif
-- ---------------------------------------------------------------------
SELECT 'artistes'   AS table_, count(*) FROM artists
UNION ALL SELECT 'albums',              count(*) FROM albums
UNION ALL SELECT 'chansons',            count(*) FROM songs
UNION ALL SELECT 'genres',              count(*) FROM genres
UNION ALL SELECT 'liens song_genres',   count(*) FROM song_genres
UNION ALL SELECT 'playlists',           count(*) FROM editorial_playlists
UNION ALL SELECT 'evenements',          count(*) FROM events
UNION ALL SELECT 'lieux',               count(*) FROM venues
UNION ALL SELECT 'ecoutes recentes',    count(*) FROM listening_history;
