-- Seed data for events

-- 1. Insert event moderation statuses
INSERT INTO event_moderation_statuses (name) VALUES
  ('Approuvé'),
  ('En attente'),
  ('Refusé')
ON CONFLICT (name) DO NOTHING;

-- 2. Insert sample events (using artist id=1 "Bijoux", moderation_status_id=1 "Approuvé")
INSERT INTO events (name, description, start_date, end_date, created_by_artist_id, moderation_status_id, created_at) VALUES
  (
    'Festival Mozika 2026',
    'Le grand festival annuel de musique malgache. Une célébration des arts avec des artistes locaux et internationaux. Au programme : concerts en plein air, ateliers musicaux, expositions artistiques et bien plus encore.',
    '2026-11-15',
    '2026-11-17',
    1,
    1,
    NOW()
  ),
  (
    'Soirée Jazz & Blues',
    'Une soirée intimiste dédiée au jazz et au blues. Venez découvrir les talents de la scène malgache dans une ambiance chaleureuse et conviviale. Réservation recommandée.',
    '2026-10-25',
    '2026-10-25',
    1,
    1,
    NOW()
  ),
  (
    'Concert de Noël',
    'Célébrez la saison des fêtes avec un concert exceptionnel réunissant les meilleurs artistes du moment. Une soirée magique en famille ou entre amis avec des chants traditionnels et modernes.',
    '2026-12-24',
    '2026-12-24',
    1,
    1,
    NOW()
  ),
  (
    'Showcase Jeunes Talents',
    'Découvrez la nouvelle génération des artistes malgaches. Ce showcase est une occasion unique de voir les stars de demain sur scène. Entrée libre pour encourager les jeunes talents.',
    '2026-10-20',
    '2026-10-20',
    1,
    1,
    NOW()
  ),
  (
    'Nuit Acoustique',
    'Une nuit magique dédiée aux performances acoustiques. Dans un cadre intimiste, vivez une expérience musicale authentique et émouvante. Nombre de places limité.',
    '2026-11-05',
    '2026-11-05',
    1,
    1,
    NOW()
  )
ON CONFLICT DO NOTHING;

SELECT 'Seed data inserted successfully' AS message;
SELECT id, name, start_date, end_date FROM events;
