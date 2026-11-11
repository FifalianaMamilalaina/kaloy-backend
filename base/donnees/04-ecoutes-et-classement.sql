-- =====================================================================
-- Jeu de donnees : historique d'ecoutes pour les classements (Sprint 4)
-- =====================================================================
-- A lancer APRES seed_contenu_kaloy.sql.
--
--   psql -U postgres -d musical_app -f seed_ecoutes_classement.sql
--
-- Objectif : rendre le classement VERIFIABLE. Avec six ecoutes, on voit que
-- l'ecran s'affiche ; on ne voit pas si le tri est juste. Il faut des ecarts
-- nets entre les chansons, et des ecoutes reparties dans le temps.
--
-- Ce que le script produit :
--   - une popularite volontairement inegale, decroissante par chanson ;
--   - des ecoutes etalees sur 20 jours, pour que la fenetre glissante de
--     7 jours en exclue reellement une partie ;
--   - plusieurs auditeurs, l'historique n'etant pas rattache a un seul compte.
--
-- Rejouable : le script efface d'abord les ecoutes qu'il a lui-meme creees,
-- reconnaissables a leur plage d'identifiants (100000 et au-dela).
-- =====================================================================

DELETE FROM listening_history WHERE id >= 100000;

-- ---------------------------------------------------------------------
-- Generation
-- ---------------------------------------------------------------------
-- Chaque chanson recoit un nombre d'ecoutes qui decroit avec son rang, et
-- chaque ecoute est datee aleatoirement dans les 20 derniers jours.
--
-- La formule du nombre d'ecoutes (42 - rang, minimum 2) donne un classement
-- lisible : le premier titre est nettement devant, sans que les derniers
-- soient a zero.

INSERT INTO listening_history (id, user_id, song_id, play_mode_id, listened_at)
SELECT
    100000 + row_number() OVER ()                       AS id,
    auditeurs.user_id,
    classement.song_id,
    (SELECT id FROM play_modes WHERE name = 'AUDIO')    AS play_mode_id,
    NOW() - (random() * INTERVAL '20 days')             AS listened_at
FROM (
    SELECT s.id AS song_id,
           GREATEST(42 - row_number() OVER (ORDER BY s.id), 2) AS nombre
    FROM songs s
    WHERE s.id BETWEEN 301 AND 330
) AS classement
CROSS JOIN LATERAL generate_series(1, classement.nombre::int) AS n
CROSS JOIN LATERAL (
    -- Auditeur tire parmi les comptes existants, pour que l'historique ne
    -- soit pas celui d'une seule personne.
    SELECT id AS user_id
    FROM users
    ORDER BY random()
    LIMIT 1
) AS auditeurs;

SELECT setval(pg_get_serial_sequence('listening_history', 'id'),
              GREATEST((SELECT MAX(id) FROM listening_history), 1));

-- ---------------------------------------------------------------------
-- Recapitulatif : le classement attendu sur 7 jours
-- ---------------------------------------------------------------------
-- C'est exactement ce que l'application doit afficher, dans cet ordre.

SELECT s.id,
       s.title,
       a.stage_name                              AS artiste,
       count(lh.id)                              AS ecoutes_7j,
       (SELECT count(*) FROM listening_history l WHERE l.song_id = s.id) AS ecoutes_total
FROM songs s
JOIN listening_history lh ON lh.song_id = s.id
JOIN artists a ON a.id = s.artist_id
WHERE lh.listened_at >= NOW() - INTERVAL '7 days'
GROUP BY s.id, s.title, a.stage_name
ORDER BY count(lh.id) DESC, max(lh.listened_at) DESC, s.id DESC
LIMIT 10;
