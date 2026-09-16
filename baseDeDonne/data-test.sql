-- 1. CRÉATION DE L'UTILISATEUR (avec dates explicites)
INSERT INTO "users" (email, password_hash, role_id, status_id, created_at, updated_at)
VALUES (
    'artistetest@yopmail.com', 
    '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 
    2, 
    1, 
    NOW(), 
    NOW()
)
ON CONFLICT (email) DO NOTHING;

-- 2. CRÉATION DU PROFIL ARTISTE (avec date explicite)
INSERT INTO "artists" (user_id, artist_type_id, stage_name, verification_status_id, is_certified, created_at)
VALUES (
    (SELECT id FROM "users" WHERE email = 'artistetest@yopmail.com'), 
    1, 
    'Artiste Test Kaloy', 
    2, 
    false,
    NOW()
);

-- 3. CRÉATION DE LA CHANSON DE TEST (avec date explicite)
INSERT INTO "songs" (
    artist_id, title, duration_seconds, language, storage_type_id, 
    audio_url, is_downloadable, created_at
)
VALUES (
    (SELECT id FROM "artists" WHERE stage_name = 'Artiste Test Kaloy'), 
    'Mon Premier Hit', 
    215, 
    'fr', 
    2, 
    'Izay.mp3', 
    true,
    NOW()
);

-- 4. VÉRIFICATION FINALE
SELECT 
    u.email, 
    a.stage_name, 
    s.title, 
    s.audio_url 
FROM "songs" s
JOIN "artists" a ON s.artist_id = a.id
JOIN "users" u ON a.user_id = u.id
WHERE s.title = 'Mon Premier Hit';

--------------------------------

-- On passe le statut de vérification à 2 (Vérifié)
UPDATE "users"
SET verification_status_user_id = 2
WHERE email = 'artistetest@yopmail.com';

-- Vérification
SELECT email, email_verified_at, verification_status_user_id 
FROM "users" 
WHERE email = 'artistetest@yopmail.com';

UPDATE "songs"
SET 
    "audio_url" = 'Eh sambatra sy tretrika - Vetsonkira(M4A_128K)-audio.m4a',
    "video_url" = 'https://youtu.be/xqFnnCB6vIo?si=fJPWsBvhEQyW4enb',
    "karaoke_audio_url" = 'Eh sambatra sy tretrika-Vetsonkira( Karaoké)(720P_HD).mp4',
    "playback_url" = 'Eh sambatra sy tretrika-Vetsonkira Playback(M4A_128K).m4a',
    "solfa_url" = 'solfa.pdf',
    "title" = 'Eh sambatra sy tretrika',
    "author_composer" = 'Vetsonkira',
    "duration_seconds" = 215,
    "language" = 'mg'
WHERE id = 1;

-- Vérification
SELECT id, title, audio_url, video_url, karaoke_audio_url, playback_url, solfa_url 
FROM songs WHERE id = 1;