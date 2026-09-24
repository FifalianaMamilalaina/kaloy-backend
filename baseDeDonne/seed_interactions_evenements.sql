-- =====================================================================
-- Jeu de donnees : likes et commentaires sur les evenements (Sprint 3)
-- =====================================================================
-- A lancer APRES seed_contenu_kaloy.sql (comptes 1 et 101-106,
-- evenements 401-404).
--
--   psql -U postgres -d musical_app -f seed_interactions_evenements.sql
--
-- Rejouable : identifiants explicites + ON CONFLICT DO NOTHING.
--
-- Choix pour le test :
--   - la plupart des commentaires viennent des comptes artistes, pour que
--     client@dev.com puisse tester « Signaler » ;
--   - un commentaire vient de client@dev.com, pour tester « Supprimer » ;
--   - un commentaire est masque (is_hidden), il ne doit PAS s'afficher ;
--   - client@dev.com n'a aime aucun evenement, pour tester le like.
--
-- Plages reservees : commentaires 901-912   likes 951-960
-- =====================================================================


-- ---------------------------------------------------------------------
-- 1. Commentaires
-- ---------------------------------------------------------------------
INSERT INTO comments (id, author_user_id, target_type_id, target_id, content, is_hidden, created_at)
SELECT v.id,
       (SELECT id FROM users WHERE email = v.auteur),
       (SELECT id FROM interaction_targets WHERE name = 'EVENT'),
       v.evenement, v.contenu, v.masque, v.quand
FROM (VALUES
  -- Nuit du Salegy (401)
  (901, 'jaojoby@kaloy.mg',   401, 'Merci a tous d''etre venus, quelle energie ce soir-la !',          FALSE, TIMESTAMP '2026-06-15 10:00'),
  (902, 'tencemena@kaloy.mg', 401, 'Partager la scene avec Jaojoby, un reve de gamine.',               FALSE, TIMESTAMP '2026-06-15 11:30'),
  (903, 'client@dev.com',     401, 'Meilleure soiree de l''annee, vivement la prochaine edition.',     FALSE, TIMESTAMP '2026-06-16 09:15'),

  -- Gasy Festival (402)
  (904, 'rossy@kaloy.mg',     402, 'Trois jours incroyables. Misaotra betsaka Antananarivo !',         FALSE, TIMESTAMP '2026-08-24 09:00'),
  (905, 'mage4@kaloy.mg',     402, 'La scene principale le samedi soir, on s''en souviendra.',         FALSE, TIMESTAMP '2026-08-24 12:40'),
  (906, 'samoela@kaloy.mg',   402, 'Petite scene, grande ambiance. Merci au public du club.',          FALSE, TIMESTAMP '2026-08-24 18:05'),
  -- Masque par la moderation : ne doit jamais apparaitre dans l'app.
  (907, 'ambondrona@kaloy.mg',402, '[commentaire masque par la moderation]',                           TRUE,  TIMESTAMP '2026-08-25 08:00')
) AS v(id, auteur, evenement, contenu, masque, quand)
ON CONFLICT (id) DO NOTHING;


-- ---------------------------------------------------------------------
-- 2. Likes
-- ---------------------------------------------------------------------
INSERT INTO likes (id, user_id, target_type_id, target_id, created_at)
SELECT v.id,
       (SELECT id FROM users WHERE email = v.qui),
       (SELECT id FROM interaction_targets WHERE name = 'EVENT'),
       v.evenement, v.quand
FROM (VALUES
  (951, 'rossy@kaloy.mg',      401, TIMESTAMP '2026-06-15 08:00'),
  (952, 'tencemena@kaloy.mg',  401, TIMESTAMP '2026-06-15 08:30'),
  (953, 'jaojoby@kaloy.mg',    402, TIMESTAMP '2026-08-24 08:00'),
  (954, 'mage4@kaloy.mg',      402, TIMESTAMP '2026-08-24 08:10'),
  (955, 'samoela@kaloy.mg',    402, TIMESTAMP '2026-08-24 08:20'),
  (956, 'ambondrona@kaloy.mg', 402, TIMESTAMP '2026-08-24 08:30')
) AS v(id, qui, evenement, quand)
ON CONFLICT (id) DO NOTHING;


-- ---------------------------------------------------------------------
-- 3. Realignement des sequences
-- ---------------------------------------------------------------------
SELECT setval(pg_get_serial_sequence('comments', 'id'), GREATEST((SELECT MAX(id) FROM comments), 1));
SELECT setval(pg_get_serial_sequence('likes',    'id'), GREATEST((SELECT MAX(id) FROM likes),    1));


-- ---------------------------------------------------------------------
-- 4. Recapitulatif
-- ---------------------------------------------------------------------
SELECT e.name AS evenement,
       (SELECT count(*) FROM likes l
          WHERE l.target_id = e.id
            AND l.target_type_id = (SELECT id FROM interaction_targets WHERE name = 'EVENT')) AS likes,
       (SELECT count(*) FROM comments c
          WHERE c.target_id = e.id AND NOT c.is_hidden
            AND c.target_type_id = (SELECT id FROM interaction_targets WHERE name = 'EVENT')) AS commentaires_visibles
FROM events e
WHERE e.id IN (401, 402)
ORDER BY e.id;
