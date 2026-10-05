-- Ajouter des colonnes pour enrichir l'historique
ALTER TABLE listening_history 
ADD COLUMN IF NOT EXISTS duration_listened_seconds INTEGER DEFAULT 0,
ADD COLUMN IF NOT EXISTS completed BOOLEAN DEFAULT FALSE,
ADD COLUMN IF NOT EXISTS created_at TIMESTAMP DEFAULT NOW();

-- Ajouter un index pour optimiser les requêtes par utilisateur et date
CREATE INDEX IF NOT EXISTS idx_listening_history_user_date 
ON listening_history(user_id, listened_at DESC);

-- Ajouter un index pour la recherche par chanson
CREATE INDEX IF NOT EXISTS idx_listening_history_song 
ON listening_history(song_id);