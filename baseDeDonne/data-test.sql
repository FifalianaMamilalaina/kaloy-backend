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