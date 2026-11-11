-- ---------------------------------------------------------------------
-- Invitations a participer a un evenement, en attente de reponse
--
-- Une invitation n'est pas une entite distincte : c'est une ligne de la table
-- concerts dont le statut vaut PENDING. L'artiste invite repond, et sa reponse
-- met le statut a CONFIRMED (le concert entre dans son calendrier) ou a
-- DECLINED (il n'apparait nulle part).
--
-- Le jeu existant ne contenait qu'une seule invitation exploitable, ce qui ne
-- permettait de tester ni l'acceptation ni le refus sans tout remettre a zero
-- entre les deux. On en ajoute quatre, reparties pour couvrir trois cas :
--
--   * plusieurs invitations pour UN MEME artiste (Jaojoby, 102), afin de
--     pouvoir en accepter une, en refuser une autre, et voir la liste se vider ;
--   * des invitations adressees a D'AUTRES artistes (106 et 104), qui ne
--     doivent jamais apparaitre dans la liste de Jaojoby — c'est le controle
--     d'isolation ;
--   * toutes a des dates futures, puisque le serveur refuse desormais de
--     repondre a une invitation perimee. Celle de Tence Mena du 23/08, deja en
--     base, sert justement de cas perime.
--
-- Les identifiants 718 a 721 prolongent ceux de seed_concerts_medias.sql, qui
-- s'arrete a 717.
-- ---------------------------------------------------------------------

INSERT INTO concerts (
    id, event_id, title, description, artist_id, venue_id,
    start_time, end_time, status_id, responded_at,
    created_by_artist_id, moderation_status_id, created_at
)
SELECT v.id, v.event_id, v.titre, v.description, v.artist_id, v.venue_id,
       v.debut, v.fin,
       (SELECT id FROM participation_statuses WHERE name = 'PENDING'),
       NULL,
       v.invite_par,
       (SELECT id FROM event_moderation_statuses WHERE name = 'APPROVED'),
       NOW()
FROM (VALUES
  -- Concert Ambondrona (403), le 07/11/2026 — deux artistes sollicites
  (718, 403, 'Invite surprise', 'En attente de reponse de l''artiste.',
         102, 501, TIMESTAMP '2026-11-07 20:30', TIMESTAMP '2026-11-07 21:15', 104),
  (720, 403, 'Cloture de soiree', 'En attente de reponse de l''artiste.',
         106, 501, TIMESTAMP '2026-11-07 22:30', TIMESTAMP '2026-11-07 23:30', 104),

  -- Tananarive Live (404), les 19 et 20/12/2026
  (719, 404, 'Scene J1', 'En attente de reponse de l''artiste.',
         102, 502, TIMESTAMP '2026-12-19 21:00', TIMESTAMP '2026-12-19 22:30', 103),
  (721, 404, 'Scene J2', 'En attente de reponse de l''artiste.',
         104, 503, TIMESTAMP '2026-12-20 20:00', TIMESTAMP '2026-12-20 21:30', 103)
) AS v(id, event_id, titre, description, artist_id, venue_id, debut, fin, invite_par)
ON CONFLICT (id) DO NOTHING;


-- ---------------------------------------------------------------------
-- Controle : ce que l'on doit obtenir apres insertion
-- ---------------------------------------------------------------------
SELECT a.stage_name AS artiste,
       count(*) AS invitations_en_attente,
       min(to_char(c.start_time, 'YYYY-MM-DD')) AS premiere
FROM concerts c
JOIN artists a ON a.id = c.artist_id
JOIN participation_statuses ps ON ps.id = c.status_id
WHERE ps.name = 'PENDING'
  AND c.start_time > NOW()
GROUP BY a.stage_name
ORDER BY a.stage_name;
