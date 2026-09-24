-- Jeu de données de test pour Kaloy
-- Le mot de passe crypté correspond à "password123" (BCrypt)
-- Insérer les statuts et rôles si la DB est vide
INSERT INTO "user_roles" ("name") VALUES ('CLIENT'), ('ARTIST'), ('ADMIN') ON CONFLICT DO NOTHING;
INSERT INTO "user_statuses" ("name") VALUES ('ACTIVE'), ('INACTIVE') ON CONFLICT DO NOTHING;
INSERT INTO "artist_types" ("name") VALUES ('SOLO'), ('GROUP') ON CONFLICT DO NOTHING;
INSERT INTO "verification_statuses" ("name") VALUES ('VERIFIED'), ('PENDING') ON CONFLICT DO NOTHING;

-- Création d'un compte CLIENT (email: client@dev.com / mdp: password123)
WITH client_role AS (SELECT id FROM user_roles WHERE name = 'CLIENT'),
     status_active AS (SELECT id FROM user_statuses WHERE name = 'ACTIVE')
INSERT INTO users (email, password_hash, role_id, status_id, created_at, updated_at)
VALUES (
    'client@dev.com', 
    '$2a$10$XN1YhP9B1J9.fR9B1J9.fO9B1J9.fR9B1J9.fR9B1J9.fR9B1J9.fO', 
    (SELECT id FROM client_role), 
    (SELECT id FROM status_active), 
    NOW(), NOW()
) ON CONFLICT DO NOTHING;

-- Création d'un compte ARTIST (email: artist@dev.com / mdp: password123)
WITH artist_role AS (SELECT id FROM user_roles WHERE name = 'ARTIST'),
     status_active AS (SELECT id FROM user_statuses WHERE name = 'ACTIVE')
INSERT INTO users (email, password_hash, role_id, status_id, created_at, updated_at)
VALUES (
    'artist@dev.com', 
    '$2a$10$XN1YhP9B1J9.fR9B1J9.fO9B1J9.fR9B1J9.fR9B1J9.fR9B1J9.fO', 
    (SELECT id FROM artist_role), 
    (SELECT id FROM status_active), 
    NOW(), NOW()
) ON CONFLICT DO NOTHING;
