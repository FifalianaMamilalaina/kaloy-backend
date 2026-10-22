-- Donnees de test pour le scenario de generation de playlists - Phase 5.

BEGIN;

DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM songs WHERE id = 1 AND artist_id = 6
    ) OR (
        SELECT COUNT(*) FROM songs WHERE id BETWEEN 2 AND 15 AND artist_id = 8
    ) <> 14 THEN
        RAISE EXCEPTION
            'Le jeu de donnees attendu est absent : attendu song 1/artist 6 et songs 2-15/artist 8.';
    END IF;
END
$$;

INSERT INTO genres (name)
VALUES ('Rock'), ('Jazz')
ON CONFLICT (name) DO NOTHING;

-- Durees factices pour tester la generation : la source fournie ne contient
-- pas de duree pour les chansons 2 a 15. Les chansons deja chronometrees ne
-- sont pas modifiees. Valeurs exprimees en secondes.
UPDATE songs AS song
SET duration_seconds = fixture.duration_seconds
FROM (VALUES
    (2::BIGINT, 210),
    (3::BIGINT, 195),
    (4::BIGINT, 225),
    (5::BIGINT, 180),
    (6::BIGINT, 240),
    (7::BIGINT, 205),
    (8::BIGINT, 230),
    (9::BIGINT, 215),
    (10::BIGINT, 190),
    (11::BIGINT, 220),
    (12::BIGINT, 200),
    (13::BIGINT, 235),
    (14::BIGINT, 185),
    (15::BIGINT, 210)
) AS fixture(song_id, duration_seconds)
WHERE song.id = fixture.song_id
  AND song.artist_id = 8
  AND song.duration_seconds IS NULL;

-- Rock : chansons existantes de Mamitiana et Reko Band.
-- Jazz : chansons existantes de Reko Band.
-- Les jointures verifient aussi l'artiste attendu avant de creer les liens.
INSERT INTO song_genres (song_id, genre_id)
SELECT mapping.song_id, genre.id
FROM (VALUES
    (1::BIGINT, 6::BIGINT, 'Rock'::VARCHAR),
    (2::BIGINT, 8::BIGINT, 'Rock'::VARCHAR),
    (3::BIGINT, 8::BIGINT, 'Rock'::VARCHAR),
    (4::BIGINT, 8::BIGINT, 'Rock'::VARCHAR),
    (5::BIGINT, 8::BIGINT, 'Rock'::VARCHAR),
    (6::BIGINT, 8::BIGINT, 'Rock'::VARCHAR),
    (7::BIGINT, 8::BIGINT, 'Rock'::VARCHAR),
    (8::BIGINT, 8::BIGINT, 'Rock'::VARCHAR),
    (9::BIGINT, 8::BIGINT, 'Jazz'::VARCHAR),
    (10::BIGINT, 8::BIGINT, 'Jazz'::VARCHAR),
    (11::BIGINT, 8::BIGINT, 'Jazz'::VARCHAR),
    (12::BIGINT, 8::BIGINT, 'Jazz'::VARCHAR),
    (13::BIGINT, 8::BIGINT, 'Jazz'::VARCHAR),
    (14::BIGINT, 8::BIGINT, 'Jazz'::VARCHAR),
    (15::BIGINT, 8::BIGINT, 'Jazz'::VARCHAR)
) AS mapping(song_id, expected_artist_id, genre_name)
JOIN songs AS song
  ON song.id = mapping.song_id
 AND song.artist_id = mapping.expected_artist_id
JOIN genres AS genre
  ON genre.name = mapping.genre_name
WHERE NOT EXISTS (
    SELECT 1
    FROM song_genres AS existing
    WHERE existing.song_id = mapping.song_id
      AND existing.genre_id = genre.id
);

COMMIT;

-- Verification avant le test mobile : genres et titres disponibles.
SELECT
    song.id AS song_id,
    song.title,
    artist.stage_name,
    song.duration_seconds,
    genre.name AS test_genre
FROM songs AS song
JOIN artists AS artist ON artist.id = song.artist_id
JOIN song_genres AS song_genre ON song_genre.song_id = song.id
JOIN genres AS genre ON genre.id = song_genre.genre_id
WHERE genre.name IN ('Rock', 'Jazz')
  AND song.id BETWEEN 1 AND 15
ORDER BY genre.name, song.id;

-- Relever les identifiants a utiliser dans les filtres de l'application.
SELECT id, name
FROM genres
WHERE name IN ('Rock', 'Jazz')
ORDER BY name;

SELECT id, stage_name
FROM artists
WHERE id IN (6, 8)
ORDER BY id;
