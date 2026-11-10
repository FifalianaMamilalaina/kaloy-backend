-- Ajout des types de cible manquants pour le module Favoris
-- Les types SONG et EVENT existent déjà ; on ajoute ALBUM et ARTIST.
INSERT INTO interaction_targets (name) VALUES ('ALBUM')  ON CONFLICT (name) DO NOTHING;
INSERT INTO interaction_targets (name) VALUES ('ARTIST') ON CONFLICT (name) DO NOTHING;
