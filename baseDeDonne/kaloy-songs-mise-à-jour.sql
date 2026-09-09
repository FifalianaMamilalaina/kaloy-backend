-- Active: 1764824681200@@127.0.0.1@5432@musical_app
-- 1. Supprimer les colonnes binaires (BYTEA) qui ne servent plus
ALTER TABLE "songs" DROP COLUMN IF EXISTS "audio_file";
ALTER TABLE "songs" DROP COLUMN IF EXISTS "karaoke_audio";
ALTER TABLE "songs" DROP COLUMN IF EXISTS "playback";

-- 2. Renommer 'solfa' en 'solfa_url' pour la cohérence des noms
ALTER TABLE "songs" RENAME COLUMN "solfa" TO "solfa_url";

-- 3. Ajouter les nouvelles colonnes d'URL pour les médias manquants
ALTER TABLE "songs" ADD COLUMN IF NOT EXISTS "video_url" TEXT;
ALTER TABLE "songs" ADD COLUMN IF NOT EXISTS "karaoke_audio_url" TEXT;
ALTER TABLE "songs" ADD COLUMN IF NOT EXISTS "playback_url" TEXT;

-- 4. S'assurer que audio_url est bien NOT NULL (car une chanson doit avoir au moins un audio)
ALTER TABLE "songs" ALTER COLUMN "audio_url" SET NOT NULL;

UPDATE songs SET audio_url = 'Izay.mp3' WHERE id = 1;
SELECT id, title, audio_url FROM songs WHERE id = 1;