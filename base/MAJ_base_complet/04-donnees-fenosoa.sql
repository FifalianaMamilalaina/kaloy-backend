-- psql -U postgres -d musical_app -f "D:/ITU/STAGE_MEMOIRE_SOUTENANCE/kaloy-backend/base/MAJ_base_complet/04-donnees-fenosoa.sql"
-- ================================================================
-- NOUVELLES CHANSONS + DONNÉES FENOSOA — Kaloy Musical App
-- ================================================================
--
-- Nouveaux albums, chansons (Fiadanana Lavorary, Eny Ampifitia,
-- Vali-teny Ampifitia, TMI/Kalvary) et interactions pour
-- fenosoarandrianarisoa@yopmail.com (user_id=42).
--
-- Usage :
--   psql -U postgres -d musical_app -f "D:/ITU/STAGE_MEMOIRE_SOUTENANCE/kaloy-backend/base/MAJ_base_complet/04-donnees-fenosoa.sql"
--
-- Généré le 18/11/2026
-- ================================================================

BEGIN;

-- ================================================================
-- 1. NOUVEAUX ALBUMS
-- ================================================================

INSERT INTO albums (id, artist_id, title, cover_url, release_date, created_at) VALUES
  (4, 6,  'Feno Fiderana',       '', '2024-01-01', NOW()),
  (5, 14, 'Eny Ampifitia',       '', '2024-01-01', NOW()),
  (6, 8,  'Vali-teny Ampifitia', '', '2024-01-01', NOW()),
  (7, 14, 'Kalvary',             '', '2024-01-01', NOW());

-- ================================================================
-- 2. NOUVEAU GENRE
-- ================================================================

INSERT INTO genres (id, name) VALUES (6, 'Gospel');

-- ================================================================
-- 3. NOUVELLES CHANSONS
-- ================================================================

-- ---- Groupe 1 : Fiadanana Lavorary (artiste 6 / Vetsonkira, album 4) ----
INSERT INTO songs (
  id, artist_id, album_id, title,
  audio_url,
  karaoke_audio_url,
  playback_url,
  author_composer,
  language, is_downloadable, storage_type_id,
  duration_seconds, release_date, created_at
) VALUES (
  16, 6, 4,
  'Fiadanana Lavorary',
  'Fiadanana Lavorary/Fiadanana Lavorary - Vetsonkira(M4A_128K).m4a',
  'Fiadanana Lavorary/Karaoke Vetsonkira Fiadanana lavorary by naharisetra image(720P_HD).mp4',
  'Fiadanana Lavorary/Karaoke Vetsonkira Fiadanana lavorary by naharisetra image(M4A_128K) playback.m4a',
  'Vetsonkira',
  'mg', true, 2,
  240, '2023-06-01', NOW()
);

-- ---- Groupe 2 : Eny Ampifitia Instrumental (artiste 14, album 5) ----
INSERT INTO songs (
  id, artist_id, album_id, title,
  audio_url,
  karaoke_audio_url,
  author_composer,
  language, is_downloadable, storage_type_id,
  duration_seconds, release_date, created_at
) VALUES (
  17, 14, 5,
  'Eny Ampifitia Instrumental',
  'Eny Ampifitia/ENY - Ampifitia - Instrumental(M4A_128K).m4a',
  'Eny Ampifitia/ENY - Ampifitia - Instrumental(720P_HD) Karaoke.mp4',
  'Eny Ampifitia',
  'mg', true, 2,
  200, '2023-06-01', NOW()
);

-- ---- Groupe 2 : FJKM Isotry Fitiavana (artiste 14, album 5) ----
INSERT INTO songs (
  id, artist_id, album_id, title,
  audio_url,
  author_composer,
  language, is_downloadable, storage_type_id,
  duration_seconds, release_date, created_at
) VALUES (
  18, 14, 5,
  'FJKM Isotry Fitiavana',
  'Eny Ampifitia/ENY Ampifitia FJKM Isotry Fitiavana(M4A_128K)-audio.m4a',
  'Eny Ampifitia',
  'mg', true, 2,
  180, '2023-06-01', NOW()
);

-- ---- Groupe 3 : Vali-teny Ampifitia (artiste 8 / Reko Band, album 6) ----
INSERT INTO songs (
  id, artist_id, album_id, title,
  audio_url,
  karaoke_audio_url,
  playback_url,
  language, is_downloadable, storage_type_id,
  duration_seconds, release_date, created_at
) VALUES (
  19, 8, 6,
  'Vali-teny Ampifitia',
  'Vali-teny Ampifitia/AMPIFITIA-Valinteny(M4A_128K)-audio.m4a',
  'Vali-teny Ampifitia/VALIN-TENY Ampifitia Karaoke(720P_HD).mp4',
  'Vali-teny Ampifitia/VALIN-TENY Ampifitia Instrumental(M4A_128K).m4a',
  'mg', true, 2,
  220, '2023-06-01', NOW()
);

-- ---- Groupe 4 : Kalvary - Tanora Masina (artiste 14 / TMI, album 7) ----
INSERT INTO songs (
  id, artist_id, album_id, title,
  audio_url,
  playback_url,
  solfa_url,
  author_composer,
  language, is_downloadable, storage_type_id,
  duration_seconds, release_date, created_at
) VALUES (
  20, 14, 7,
  'Kalvary - Tanora Masina',
  'TMI/Kalvary - Tanora Masina Playback Parole Audio.mp3',
  'TMI/Kalvary - Tanora Masina Playback.mp3',
  'TMI/Kalvary - Tanora Masina Playback Solfa .pdf',
  'Tanora Masina Itaosy',
  'mg', true, 2,
  250, '2023-06-01', NOW()
);

-- ================================================================
-- 4. GENRES DES NOUVELLES CHANSONS (Gospel = id 6)
-- ================================================================

INSERT INTO song_genres (id, genre_id, song_id) VALUES
  (20, 6, 16),
  (21, 6, 17),
  (22, 6, 18),
  (23, 6, 19),
  (24, 6, 20);

-- ================================================================
-- 5. INTERACTIONS fenosoarandrianarisoa (user_id=42)
-- ================================================================

-- ---- Artistes suivis (follows) ----
INSERT INTO follows (client_user_id, artist_id, created_at) VALUES
  (42,  6, NOW()),
  (42,  8, NOW()),
  (42, 14, NOW());

-- ---- Historique d'écoute (9 nouvelles entrées) ----
-- play_mode_id=1 (AUDIO)
INSERT INTO listening_history (id, user_id, song_id, play_mode_id, listened_at, completed, duration_listened_seconds, created_at) VALUES
  (32, 42,  1, 1, '2026-11-18 08:00:00', true,  215, '2026-11-18 08:00:00'),
  (33, 42,  2, 1, '2026-11-18 08:05:00', true,  210, '2026-11-18 08:05:00'),
  (34, 42,  3, 1, '2026-11-18 08:10:00', false,  90, '2026-11-18 08:10:00'),
  (35, 42,  8, 1, '2026-11-18 08:15:00', true,  230, '2026-11-18 08:15:00'),
  (36, 42, 16, 1, '2026-11-18 08:20:00', true,  240, '2026-11-18 08:20:00'),
  (37, 42, 17, 1, '2026-11-18 08:25:00', true,  200, '2026-11-18 08:25:00'),
  (38, 42, 18, 1, '2026-11-18 08:30:00', false, 100, '2026-11-18 08:30:00'),
  (39, 42, 19, 1, '2026-11-18 08:35:00', true,  220, '2026-11-18 08:35:00'),
  (40, 42, 20, 1, '2026-11-18 08:40:00', true,  250, '2026-11-18 08:40:00');

-- ---- Likes (songs, albums, artistes) ----
-- interaction_targets : 1=ALBUM · 2=ARTIST · 3=SONG
INSERT INTO likes (id, user_id, target_type_id, target_id, created_at) VALUES
  -- Songs aimées
  (29, 42, 3,  1, NOW()),
  (30, 42, 3, 16, NOW()),
  (31, 42, 3, 17, NOW()),
  (32, 42, 3, 19, NOW()),
  (33, 42, 3, 20, NOW()),
  -- Albums aimés
  (34, 42, 1,  1, NOW()),
  (35, 42, 1,  4, NOW()),
  -- Artistes aimés
  (36, 42, 2,  6, NOW()),
  (37, 42, 2,  8, NOW()),
  (38, 42, 2, 14, NOW());

-- ---- Nouvelle playlist (sans génération ni fusion ni téléchargement) ----
INSERT INTO playlists (id, owner_user_id, name, visibility_id, created_at, updated_at)
VALUES (8, 42, 'Gospel Préféré', 1, NOW(), NOW());

INSERT INTO playlist_songs (id, playlist_id, song_id, position, added_at) VALUES
  (25, 8,  1, 1, NOW()),
  (26, 8, 16, 2, NOW()),
  (27, 8, 17, 3, NOW()),
  (28, 8, 20, 4, NOW());

-- ================================================================
-- 6. RESYNCHRONISATION DES SÉQUENCES
-- Nécessaire car les IDs ont été insérés explicitement
-- ================================================================

SELECT setval(pg_get_serial_sequence('albums',          'id'), (SELECT MAX(id) FROM albums));
SELECT setval(pg_get_serial_sequence('genres',          'id'), (SELECT MAX(id) FROM genres));
SELECT setval(pg_get_serial_sequence('songs',           'id'), (SELECT MAX(id) FROM songs));
SELECT setval(pg_get_serial_sequence('song_genres',     'id'), (SELECT MAX(id) FROM song_genres));
SELECT setval(pg_get_serial_sequence('follows',         'id'), (SELECT MAX(id) FROM follows));
SELECT setval(pg_get_serial_sequence('listening_history','id'), (SELECT MAX(id) FROM listening_history));
SELECT setval(pg_get_serial_sequence('likes',           'id'), (SELECT MAX(id) FROM likes));
SELECT setval(pg_get_serial_sequence('playlists',       'id'), (SELECT MAX(id) FROM playlists));
SELECT setval(pg_get_serial_sequence('playlist_songs',  'id'), (SELECT MAX(id) FROM playlist_songs));

COMMIT;

-- ================================================================
-- VÉRIFICATION
-- ================================================================
-- SELECT COUNT(*) FROM songs;                                      -- 20
-- SELECT COUNT(*) FROM albums;                                     -- 7
-- SELECT COUNT(*) FROM follows WHERE client_user_id = 42;         -- 3
-- SELECT COUNT(*) FROM listening_history WHERE user_id = 42;      -- 13 (4+9)
-- SELECT COUNT(*) FROM likes WHERE user_id = 42;                  -- 11 (1+10)
-- SELECT COUNT(*) FROM playlists WHERE owner_user_id = 42;        -- 2
-- ================================================================


SELECT 
    COUNT(id) AS total_ecoutes,
    SUM(duration_listened_seconds) AS temps_total_sec,
    COUNT(CASE WHEN completed = true THEN 1 END) AS chansons_completees
FROM listening_history 
WHERE user_id = 42 AND DATE(listened_at) = '2026-11-18';