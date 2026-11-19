-- ================================================================
-- NETTOYAGE DES USERS DE TEST — Kaloy Musical App
-- ================================================================
--
-- Conservés : antonio (25), artistetest (31), rekoband (34),
--             fenosoarandrianarisoa (42), njakatianaraharisoa (43)
--
-- Supprime tous les autres users ainsi que TOUTES les données
-- liées : playlists, historique, likes, et contenu artiste
-- (songs, albums, editorial_playlists).
--
-- Usage :
--   psql -U postgres -d musical_app -f 03-cleanup-users-test.sql
--
-- Généré le 17/11/2026
-- ================================================================
-- psql -U postgres -d musical_app -f "D:/ITU/STAGE_MEMOIRE_SOUTENANCE/kaloy-backend/base/MAJ_base_complet/03-cleanup-users-test.sql"

BEGIN;

-- ---- DONNÉES DIRECTEMENT LIÉES AUX USERS SUPPRIMÉS ----

-- Likes des users supprimés
DELETE FROM likes
WHERE user_id NOT IN (25, 31, 34, 42, 43);

-- Historique d'écoute des users supprimés
DELETE FROM listening_history
WHERE user_id NOT IN (25, 31, 34, 42, 43);

-- Téléchargements offline des users supprimés (FK CASCADE, mais explicite)
DELETE FROM playlist_downloads
WHERE user_id NOT IN (25, 31, 34, 42, 43);

-- Codes de vérification des users supprimés
DELETE FROM verification_codes
WHERE user_id NOT IN (25, 31, 34, 42, 43);

-- Historique de statuts des users supprimés
DELETE FROM user_status_history
WHERE user_id NOT IN (25, 31, 34, 42, 43);

-- Nullifier changed_by pour les lignes des users conservés
UPDATE user_status_history
SET changed_by_user_id = NULL
WHERE changed_by_user_id IS NOT NULL
  AND changed_by_user_id NOT IN (25, 31, 34, 42, 43);

-- Infos profil des users supprimés
DELETE FROM users_infos
WHERE id_user NOT IN (25, 31, 34, 42, 43);

-- File d'attente de lecture des users supprimés
DELETE FROM up_next_queue
WHERE user_id NOT IN (25, 31, 34, 42, 43);

-- Appareils enregistrés des users supprimés
DELETE FROM user_devices
WHERE user_id NOT IN (25, 31, 34, 42, 43);

-- Préférences de notification des users supprimés
DELETE FROM notification_preferences
WHERE user_id NOT IN (25, 31, 34, 42, 43);

-- Notifications des users supprimés
DELETE FROM notifications
WHERE user_id NOT IN (25, 31, 34, 42, 43);

-- Historique de recherche des users supprimés
DELETE FROM search_history
WHERE user_id NOT IN (25, 31, 34, 42, 43);

-- Commentaires des users supprimés
DELETE FROM comments
WHERE author_user_id NOT IN (25, 31, 34, 42, 43);

-- Signalements des users supprimés
DELETE FROM reports
WHERE reporter_user_id NOT IN (25, 31, 34, 42, 43);

-- Médias d'événements uploadés par les users supprimés
DELETE FROM event_media
WHERE uploader_user_id NOT IN (25, 31, 34, 42, 43);

-- Tokens JWT des users supprimés
DELETE FROM auth_refresh_tokens
WHERE user_id NOT IN (25, 31, 34, 42, 43);

-- Follows des users supprimés (en tant que suiveur)
DELETE FROM follows
WHERE client_user_id NOT IN (25, 31, 34, 42, 43);

-- ---- PLAYLISTS DES USERS SUPPRIMÉS ----

-- downloads (table legacy) référençant playlists ou users supprimés
DELETE FROM downloads
WHERE user_id NOT IN (25, 31, 34, 42, 43)
   OR playlist_id IN (
     SELECT id FROM playlists WHERE owner_user_id NOT IN (25, 31, 34, 42, 43)
   );

-- Songs dans les playlists des users supprimés
DELETE FROM playlist_songs
WHERE playlist_id IN (
  SELECT id FROM playlists WHERE owner_user_id NOT IN (25, 31, 34, 42, 43)
);

-- Playlists des users supprimés (CASCADE : criteria, fusions, playlist_downloads)
DELETE FROM playlists
WHERE owner_user_id NOT IN (25, 31, 34, 42, 43);

-- ---- CONTENU DES ARTISTES SUPPRIMÉS ----

-- Follows des users conservés vers artistes supprimés (FK artist_id -> artists.id)
DELETE FROM follows
WHERE artist_id IN (
  SELECT id FROM artists WHERE user_id NOT IN (25, 31, 34, 42, 43)
);

-- up_next_queue des users conservés vers songs supprimées (FK song_id -> songs.id)
DELETE FROM up_next_queue
WHERE song_id IN (
  SELECT id FROM songs
  WHERE artist_id IN (SELECT id FROM artists WHERE user_id NOT IN (25, 31, 34, 42, 43))
);

-- editorial_playlist_songs référençant des songs OU des editorial_playlists supprimées
DELETE FROM editorial_playlist_songs
WHERE song_id IN (
  SELECT id FROM songs
  WHERE artist_id IN (SELECT id FROM artists WHERE user_id NOT IN (25, 31, 34, 42, 43))
)
OR editorial_playlist_id IN (
  SELECT id FROM editorial_playlists
  WHERE artist_id IN (SELECT id FROM artists WHERE user_id NOT IN (25, 31, 34, 42, 43))
);

-- Editorial playlists des artistes supprimés
DELETE FROM editorial_playlists
WHERE artist_id IN (
  SELECT id FROM artists WHERE user_id NOT IN (25, 31, 34, 42, 43)
);

-- Likes orphelins pointant vers songs des artistes supprimés
DELETE FROM likes
WHERE target_type_id IN (SELECT id FROM interaction_targets WHERE UPPER(name) = 'SONG')
  AND target_id IN (
    SELECT id FROM songs
    WHERE artist_id IN (SELECT id FROM artists WHERE user_id NOT IN (25, 31, 34, 42, 43))
  );

-- Likes orphelins pointant vers albums des artistes supprimés
DELETE FROM likes
WHERE target_type_id IN (SELECT id FROM interaction_targets WHERE UPPER(name) = 'ALBUM')
  AND target_id IN (
    SELECT id FROM albums
    WHERE artist_id IN (SELECT id FROM artists WHERE user_id NOT IN (25, 31, 34, 42, 43))
  );

-- Likes orphelins pointant vers artistes supprimés
DELETE FROM likes
WHERE target_type_id IN (SELECT id FROM interaction_targets WHERE UPPER(name) IN ('ARTIST', 'ARTISTE'))
  AND target_id IN (
    SELECT id FROM artists WHERE user_id NOT IN (25, 31, 34, 42, 43)
  );

-- playlist_songs des users conservés pointant vers songs supprimées
DELETE FROM playlist_songs
WHERE song_id IN (
  SELECT id FROM songs
  WHERE artist_id IN (SELECT id FROM artists WHERE user_id NOT IN (25, 31, 34, 42, 43))
);

-- Historique d'écoute des users conservés sur songs supprimées
DELETE FROM listening_history
WHERE song_id IN (
  SELECT id FROM songs
  WHERE artist_id IN (SELECT id FROM artists WHERE user_id NOT IN (25, 31, 34, 42, 43))
);

-- Genres des songs supprimées
DELETE FROM song_genres
WHERE song_id IN (
  SELECT id FROM songs
  WHERE artist_id IN (SELECT id FROM artists WHERE user_id NOT IN (25, 31, 34, 42, 43))
);

-- Songs des artistes supprimés
DELETE FROM songs
WHERE artist_id IN (
  SELECT id FROM artists WHERE user_id NOT IN (25, 31, 34, 42, 43)
);

-- Albums des artistes supprimés
DELETE FROM albums
WHERE artist_id IN (
  SELECT id FROM artists WHERE user_id NOT IN (25, 31, 34, 42, 43)
);

-- ---- PROFILS ET SUPPRESSION FINALE ----

-- Profils clients des users supprimés
DELETE FROM clients
WHERE user_id NOT IN (25, 31, 34, 42, 43);

-- Profils artistes des users supprimés
DELETE FROM artists
WHERE user_id NOT IN (25, 31, 34, 42, 43);

-- Suppression finale des users
DELETE FROM users
WHERE id NOT IN (25, 31, 34, 42, 43);

COMMIT;

-- ================================================================
-- VÉRIFICATION (à exécuter après le script)
-- ================================================================
-- SELECT id, email, role_id FROM users ORDER BY id;
-- -- Doit retourner exactement 5 lignes : 25, 31, 34, 42, 43
--
-- SELECT COUNT(*) FROM likes WHERE user_id NOT IN (25, 31, 34, 42, 43);
-- SELECT COUNT(*) FROM listening_history WHERE user_id NOT IN (25, 31, 34, 42, 43);
-- SELECT COUNT(*) FROM playlists WHERE owner_user_id NOT IN (25, 31, 34, 42, 43);
-- SELECT COUNT(*) FROM artists WHERE user_id NOT IN (25, 31, 34, 42, 43);
-- -- Toutes ces requêtes doivent retourner 0
-- ================================================================
