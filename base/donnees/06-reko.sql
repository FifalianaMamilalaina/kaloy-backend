BEGIN;

WITH new_user AS (
    INSERT INTO users (email, phone, password_hash, role_id, status_id, created_at, updated_at)
    VALUES (
        'rekoband@yopmail.com',
        NULL,
        '$2a$10$NB28uz78pQD2hUr2sv8q0ez2s/xIn3zeJh4GzDTWQXf90dIoCYqgm',
        2,
        1,
        NOW(),
        NOW()
    )
    RETURNING id
),

new_user_info AS (
    INSERT INTO users_infos (name, last_name, user_name, id_user)
    SELECT 'Fy', 'RASOLOFONIAINA', 'fy_rasolofo', id
    FROM new_user
    RETURNING id_user
),

new_artist AS (
    INSERT INTO artists (
        active_since_year, bio, is_certified, stage_name,
        artist_type_id, verification_status_id, user_id, created_at
    )
    SELECT
        2015,
        'Te hisolo vava ny fo mihira . Te ihira izay ilainao renesina . Te hiteny izay irianao ho lazaina',
        false,
        'Reko Band',
        2,
        2,
        id,
        NOW()
    FROM new_user
    RETURNING id
),

new_album_misia AS (
    INSERT INTO albums (title, release_date, artist_id, created_at)
    SELECT 'Misia', CURRENT_DATE, id, NOW()
    FROM new_artist
    RETURNING id
),

new_album_topic AS (
    INSERT INTO albums (title, release_date, artist_id, created_at)
    SELECT 'REKO BAND - Topic', CURRENT_DATE, id, NOW()
    FROM new_artist
    RETURNING id
)

INSERT INTO songs (
    title, audio_url, artist_id, album_id,
    created_at, is_downloadable, language, storage_type_id
)
SELECT
    s.title,
    s.audio_url,
    a.id,
    CASE WHEN s.album = 'Misia' THEN m.id ELSE t.id END,
    NOW(),
    true,
    'mg',
    2
FROM (VALUES
    ('Gasy Tsara',   'Gasy Tsara.mp3',   'Misia'),
    ('Hafa Mihitsy', 'Hafa Mihitsy.mp3', 'Misia'),
    ('Io Aho Io',    'Io Aho Io.mp3',    'Misia'),
    ('Izay',         'Izay.mp3',         'Misia'),
    ('Mahaleova',    'Mahaleova.mp3',    'Topic'),
    ('Mamiko',       'Mamiko.mp3',       'Topic'),
    ('Misia',        'Misia.mp3',        'Topic'),
    ('Ny Anaranao',  'Ny Anaranao.mp3',  'Topic'),
    ('One Thing',    'One Thing.mp3',    'Topic'),
    ('Portion',      'Portion.mp3',      'Topic'),
    ('Such Things',  'Such Things.mp3',  'Topic'),
    ('Teach Us',     'Teach Us.mp3',     'Topic'),
    ('Tongasoa',     'Tongasoa.mp3',     'Topic'),
    ('Zanako',       'Zanako.mp3',       'Topic')
) AS s(title, audio_url, album)
CROSS JOIN new_artist a
CROSS JOIN new_album_misia m
CROSS JOIN new_album_topic t;

COMMIT;

UPDATE songs
SET audio_url = 'Reko/' || audio_url
WHERE artist_id = (SELECT id FROM artists WHERE stage_name = 'Reko Band')
  AND audio_url NOT LIKE 'Reko/%';