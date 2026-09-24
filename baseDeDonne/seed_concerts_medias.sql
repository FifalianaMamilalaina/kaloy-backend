-- =====================================================================
-- Jeu de donnees : concerts et medias d'evenement (Sprint 4)
-- =====================================================================
-- A lancer APRES seed_contenu_kaloy.sql, qui cree les artistes (101-106),
-- les evenements (401-404) et les lieux (501-503) dont ce script depend.
--
--   psql -U postgres -d musical_app -f seed_concerts_medias.sql
--
-- Rejouable : identifiants explicites + ON CONFLICT DO NOTHING.
--
-- RAPPEL DU MODELE
--   Un evenement est declare par un artiste et peut durer plusieurs jours.
--   Il est compose de plusieurs concerts, chacun etant le creneau d'UN
--   artiste dans un lieu donne. Le concert porte aussi le statut de la
--   demande de participation : l'organisateur invite (PENDING), l'artiste
--   repond (CONFIRMED ou DECLINED). Seuls les CONFIRMED apparaissent dans
--   le calendrier de l'artiste.
--
-- Plages reservees :  concerts 701-720   medias 801-812
-- =====================================================================


-- ---------------------------------------------------------------------
-- 1. Concerts
-- ---------------------------------------------------------------------
-- On melange volontairement les trois statuts pour verifier le filtrage :
-- un calendrier qui afficherait un PENDING ou un DECLINED serait faux.
--
-- Evenements de reference (crees par seed_contenu_kaloy.sql) :
--   401 Nuit du Salegy      14/06/2026  (passe)    organise par Jaojoby
--   402 Gasy Festival 2026  21-23/08/26 (passe)    organise par Rossy
--   403 Concert Ambondrona  07/11/2026  (a venir)  organise par Ambondrona
--   404 Tananarive Live     19-20/12/26 (a venir)  organise par Mage 4

INSERT INTO concerts (
    id, event_id, title, description, artist_id, venue_id,
    start_time, end_time, status_id, responded_at,
    created_by_artist_id, moderation_status_id, reviewed_at, created_at
)
SELECT v.id, v.event_id, v.title, v.descr, v.artist_id, v.venue_id,
       v.debut, v.fin,
       (SELECT id FROM participation_statuses WHERE name = v.statut),
       CASE WHEN v.statut = 'PENDING' THEN NULL ELSE NOW() END,
       v.organisateur,
       (SELECT id FROM event_moderation_statuses WHERE name = 'APPROVED'),
       NOW(), NOW()
FROM (VALUES
  -- --- 401 Nuit du Salegy (passe) — Palais des Sports ---
  (701, 401, 'Ouverture salegy',      'Le set d''ouverture de la soiree.',        102, 501, TIMESTAMP '2026-06-14 20:00', TIMESTAMP '2026-06-14 21:30', 'CONFIRMED', 102),
  (702, 401, 'Salegy du nord',        'Tence Mena revisite les classiques.',      106, 501, TIMESTAMP '2026-06-14 21:45', TIMESTAMP '2026-06-14 23:00', 'CONFIRMED', 102),
  (703, 401, 'Cloture',               'Rossy en invite surprise.',                101, 501, TIMESTAMP '2026-06-14 23:15', TIMESTAMP '2026-06-15 01:00', 'CONFIRMED', 102),
  (704, 401, 'Set acoustique',        'Invitation declinee, agenda incompatible.',105, 501, TIMESTAMP '2026-06-14 19:00', TIMESTAMP '2026-06-14 19:45', 'DECLINED',  102),

  -- --- 402 Gasy Festival (passe, 3 jours, 2 lieux en parallele) ---
  (705, 402, 'Scene principale J1',   'Rossy ouvre le festival.',                 101, 502, TIMESTAMP '2026-08-21 18:00', TIMESTAMP '2026-08-21 20:00', 'CONFIRMED', 101),
  (706, 402, 'Scene club J1',         'Au meme moment, en petit comite.',         105, 503, TIMESTAMP '2026-08-21 18:30', TIMESTAMP '2026-08-21 20:00', 'CONFIRMED', 101),
  (707, 402, 'Scene principale J2',   'La soiree pop du festival.',               103, 502, TIMESTAMP '2026-08-22 19:00', TIMESTAMP '2026-08-22 21:00', 'CONFIRMED', 101),
  (708, 402, 'Scene club J2',         'Ambiance salegy jusqu''au bout de la nuit.',102, 503, TIMESTAMP '2026-08-22 21:30', TIMESTAMP '2026-08-23 00:00', 'CONFIRMED', 101),
  (709, 402, 'Scene principale J3',   'Cloture avec Ambondrona.',                 104, 502, TIMESTAMP '2026-08-23 19:00', TIMESTAMP '2026-08-23 21:30', 'CONFIRMED', 101),
  (710, 402, 'Scene club J3',         'Invitation restee sans reponse.',          106, 503, TIMESTAMP '2026-08-23 20:00', TIMESTAMP '2026-08-23 22:00', 'PENDING',   101),

  -- --- 403 Concert Ambondrona (a venir) ---
  (711, 403, 'Ambondrona en concert', 'Le groupe au complet.',                    104, 501, TIMESTAMP '2026-11-07 20:00', TIMESTAMP '2026-11-07 22:30', 'CONFIRMED', 104),
  (712, 403, 'Premiere partie',       'Samoela ouvre la soiree.',                 105, 501, TIMESTAMP '2026-11-07 19:00', TIMESTAMP '2026-11-07 19:45', 'CONFIRMED', 104),

  -- --- 404 Tananarive Live (a venir, 2 jours) ---
  (713, 404, 'Mage 4 live',           'Le groupe presente son nouveau repertoire.',103, 502, TIMESTAMP '2026-12-19 20:00', TIMESTAMP '2026-12-19 22:00', 'CONFIRMED', 103),
  (714, 404, 'Tence Mena live',       'Salegy moderne.',                          106, 502, TIMESTAMP '2026-12-19 22:15', TIMESTAMP '2026-12-20 00:00', 'CONFIRMED', 103),
  (715, 404, 'Samoela live',          'Textes et guitare.',                       105, 503, TIMESTAMP '2026-12-20 19:00', TIMESTAMP '2026-12-20 20:30', 'CONFIRMED', 103),
  (716, 404, 'Jaojoby live',          'En attente de reponse de l''artiste.',     102, 502, TIMESTAMP '2026-12-20 21:00', TIMESTAMP '2026-12-20 23:00', 'PENDING',   103),
  (717, 404, 'Rossy live',            'Refuse : deja en tournee a cette date.',   101, 503, TIMESTAMP '2026-12-20 21:00', TIMESTAMP '2026-12-20 23:00', 'DECLINED',  103)
) AS v(id, event_id, title, descr, artist_id, venue_id, debut, fin, statut, organisateur)
ON CONFLICT (id) DO NOTHING;


-- ---------------------------------------------------------------------
-- 2. Medias post-evenement
-- ---------------------------------------------------------------------
-- Uniquement sur les deux evenements PASSES (401 et 402) : une galerie
-- post-evenement n'a pas de sens sur un evenement a venir.
--
-- L'uploader est le compte client de developpement, faute de mieux.
-- Les videos pointent vers YouTube, que SmartVideoPlayerComposable sait lire.

INSERT INTO event_media (id, event_id, uploader_user_id, media_type_id, url, created_at)
SELECT v.id, v.event_id,
       (SELECT id FROM users WHERE email = 'client@dev.com'),
       (SELECT id FROM media_types WHERE name = v.type),
       v.url,
       v.quand
FROM (VALUES
  -- Nuit du Salegy
  (801, 401, 'PHOTO', 'https://picsum.photos/seed/ev401a/800/600', TIMESTAMP '2026-06-15 02:10'),
  (802, 401, 'PHOTO', 'https://picsum.photos/seed/ev401b/800/600', TIMESTAMP '2026-06-15 02:12'),
  (803, 401, 'PHOTO', 'https://picsum.photos/seed/ev401c/800/600', TIMESTAMP '2026-06-15 02:15'),
  (804, 401, 'VIDEO', 'https://www.youtube.com/watch?v=dQw4w9WgXcQ', TIMESTAMP '2026-06-15 09:00'),

  -- Gasy Festival
  (805, 402, 'PHOTO', 'https://picsum.photos/seed/ev402a/800/600', TIMESTAMP '2026-08-21 22:00'),
  (806, 402, 'PHOTO', 'https://picsum.photos/seed/ev402b/800/600', TIMESTAMP '2026-08-22 21:30'),
  (807, 402, 'PHOTO', 'https://picsum.photos/seed/ev402c/800/600', TIMESTAMP '2026-08-23 22:00'),
  (808, 402, 'PHOTO', 'https://picsum.photos/seed/ev402d/800/600', TIMESTAMP '2026-08-24 10:00'),
  (809, 402, 'VIDEO', 'https://www.youtube.com/watch?v=dQw4w9WgXcQ', TIMESTAMP '2026-08-24 11:00'),
  (810, 402, 'VIDEO', 'https://www.youtube.com/watch?v=dQw4w9WgXcQ', TIMESTAMP '2026-08-24 11:30')
) AS v(id, event_id, type, url, quand)
WHERE EXISTS (SELECT 1 FROM users WHERE email = 'client@dev.com')
ON CONFLICT (id) DO NOTHING;


-- ---------------------------------------------------------------------
-- 3. Realignement des sequences
-- ---------------------------------------------------------------------
SELECT setval(pg_get_serial_sequence('concerts',    'id'), GREATEST((SELECT MAX(id) FROM concerts),    1));
SELECT setval(pg_get_serial_sequence('event_media', 'id'), GREATEST((SELECT MAX(id) FROM event_media), 1));


-- ---------------------------------------------------------------------
-- 4. Recapitulatif
-- ---------------------------------------------------------------------
SELECT 'concerts CONFIRMED' AS quoi, count(*) FROM concerts c
  JOIN participation_statuses p ON p.id = c.status_id WHERE p.name = 'CONFIRMED'
UNION ALL SELECT 'concerts PENDING',  count(*) FROM concerts c
  JOIN participation_statuses p ON p.id = c.status_id WHERE p.name = 'PENDING'
UNION ALL SELECT 'concerts DECLINED', count(*) FROM concerts c
  JOIN participation_statuses p ON p.id = c.status_id WHERE p.name = 'DECLINED'
UNION ALL SELECT 'medias PHOTO',      count(*) FROM event_media m
  JOIN media_types t ON t.id = m.media_type_id WHERE t.name = 'PHOTO'
UNION ALL SELECT 'medias VIDEO',      count(*) FROM event_media m
  JOIN media_types t ON t.id = m.media_type_id WHERE t.name = 'VIDEO';
