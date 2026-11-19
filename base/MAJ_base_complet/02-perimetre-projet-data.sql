-- ============================================================
-- KALOY — DONNÉES DE RÉFÉRENCE (Sprints 0 à 10)
-- ============================================================
--
-- Ce fichier contient les insertions initiales (seed data) pour 
-- les tables de référence et de configuration du projet KALOY.
-- Les instructions ON CONFLICT DO NOTHING garantissent l'idempotence
-- du script en cas de réexécution.
--
-- Usage :
--   psql -U postgres -d kaloy_perimetre -f 02-donnees-reference.sql
--
-- Consolidé le 15/11/2026
-- ============================================================

-- ============================================================
-- SPRINT 0 — Tables de référence (lookups)
-- ============================================================

INSERT INTO "user_roles" ("name") VALUES
('CLIENT'),
('ARTIST'),
('ADMIN')
ON CONFLICT (name) DO NOTHING;

INSERT INTO "user_statuses" ("name") VALUES
('ACTIVE'),
('INACTIVE'),
('BANNED'),
('SUSPENDED')
ON CONFLICT (name) DO NOTHING;

INSERT INTO "audio_storage_types" ("name") VALUES
('DATABASE'),
('EXTERNAL_LINK')
ON CONFLICT (name) DO NOTHING;

INSERT INTO "artist_types" ("name") VALUES
('SOLO'),
('GROUP')
ON CONFLICT (name) DO NOTHING;

INSERT INTO "verification_channels" ("name") VALUES
('EMAIL'),
('SMS')
ON CONFLICT (name) DO NOTHING;

INSERT INTO "verification_statuses" ("name") VALUES
('PENDING'),
('VALIDATED'),
('REJECTED')
ON CONFLICT (name) DO NOTHING;

INSERT INTO "verification_statuses_users" ("name") VALUES
('NOT_VERIFIED'),
('VERIFIED')
ON CONFLICT (name) DO NOTHING;

INSERT INTO "play_modes" ("name") VALUES
('AUDIO'),
('VIDEO'),
('PLAYBACK'),
('KARAOKE'),
('SOLFA'),
('RADIO')
ON CONFLICT (name) DO NOTHING;

INSERT INTO "playlist_visibilities" ("name") VALUES
('PRIVATE'),
('PUBLIC')
ON CONFLICT (name) DO NOTHING;

INSERT INTO "interaction_targets" ("name") VALUES
('SONG'),
('ALBUM'),
('ARTIST')
ON CONFLICT (name) DO NOTHING;

-- ============================================================
-- SPRINT 2 — Catalogue musical (Genres)
-- ============================================================

INSERT INTO "genres" ("name") VALUES
('Folk progressif'),
('Rock'),
('Jazz')
ON CONFLICT (name) DO NOTHING;

-- ============================================================
-- FIN DU SCRIPT DE DONNÉES
-- ============================================================