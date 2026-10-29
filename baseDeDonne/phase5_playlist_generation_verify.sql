-- Requetes de validation a executer APRES la generation et la conservation
-- des playlists depuis l'application mobile.
--
-- Pendant le test mobile, nommer les playlists avec le prefixe :
--   PHASE5 - ...
-- Remplacer l'adresse email ci-dessous par celle du compte connecte sur mobile.
-- Ces requetes sont en lecture seule.

-- 1. Une ligne par playlist generee conservee : criteres, titres, duree totale.
SELECT
    p.id AS playlist_id,
    p.name AS playlist_name,
    p.created_at,
    criteria.target_duration_seconds,
    criteria.included_genres,
    criteria.excluded_genres,
    criteria.included_artist_ids,
    criteria.excluded_artist_ids,
    COUNT(playlist_song.id) AS song_count,
    COALESCE(SUM(song.duration_seconds), 0) AS actual_duration_seconds,
    COALESCE(SUM(song.duration_seconds), 0) <= criteria.target_duration_seconds AS within_target
FROM playlists AS p
JOIN users AS owner ON owner.id = p.owner_user_id
JOIN playlist_generation_criteria AS criteria ON criteria.playlist_id = p.id
LEFT JOIN playlist_songs AS playlist_song ON playlist_song.playlist_id = p.id
LEFT JOIN songs AS song ON song.id = playlist_song.song_id
WHERE owner.email = 'REMPLACER_PAR_EMAIL_DU_COMPTE'
  AND p.name LIKE 'PHASE5 - %'
GROUP BY p.id, p.name, p.created_at, criteria.id
ORDER BY p.created_at DESC, p.id DESC;

-- 2. Details des titres et de leur ordre dans chaque playlist conservee.
SELECT
    p.id AS playlist_id,
    p.name AS playlist_name,
    playlist_song.position,
    song.id AS song_id,
    song.title,
    artist.id AS artist_id,
    artist.stage_name,
    song.duration_seconds,
    COALESCE(
        STRING_AGG(DISTINCT genre.name, ', ' ORDER BY genre.name),
        '(aucun genre)'
    ) AS genres
FROM playlists AS p
JOIN users AS owner ON owner.id = p.owner_user_id
JOIN playlist_generation_criteria AS criteria ON criteria.playlist_id = p.id
JOIN playlist_songs AS playlist_song ON playlist_song.playlist_id = p.id
JOIN songs AS song ON song.id = playlist_song.song_id
JOIN artists AS artist ON artist.id = song.artist_id
LEFT JOIN song_genres AS song_genre ON song_genre.song_id = song.id
LEFT JOIN genres AS genre ON genre.id = song_genre.genre_id
WHERE owner.email = 'antonio@yopmail.com'
  AND p.name LIKE 'PHASE5 - %'
GROUP BY p.id, p.name, playlist_song.position, song.id, artist.id, artist.stage_name
ORDER BY p.created_at DESC, p.id DESC, playlist_song.position;

-- 3. Contrôle rapide du jeu de départ prepare avant le test mobile.
SELECT
    song.id AS song_id,
    song.title,
    artist.id AS artist_id,
    artist.stage_name,
    song.duration_seconds,
    ARRAY_AGG(DISTINCT genre.name ORDER BY genre.name) AS genres
FROM songs AS song
JOIN artists AS artist ON artist.id = song.artist_id
JOIN song_genres AS song_genre ON song_genre.song_id = song.id
JOIN genres AS genre ON genre.id = song_genre.genre_id
WHERE song.id BETWEEN 1 AND 15
  AND genre.name IN ('Rock', 'Jazz')
GROUP BY song.id, song.title, artist.id, artist.stage_name, song.duration_seconds
ORDER BY song.id;
