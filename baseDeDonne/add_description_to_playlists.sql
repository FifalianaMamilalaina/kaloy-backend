-- Migration : ajout du champ description sur la table playlists
-- À exécuter une seule fois sur la base PostgreSQL avant de démarrer le backend.
ALTER TABLE playlists ADD COLUMN IF NOT EXISTS description TEXT;
