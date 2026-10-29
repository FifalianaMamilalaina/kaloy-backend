-- Reprise : une notification par invitation deja en attente.
--
-- Le centre de notifications du mobile lisait /invitations et affichait des
-- lignes de la table concerts. Il lit desormais /notifications/me. Les
-- invitations emises avant cette bascule n'ont donc aucune notification, et
-- l'ecran apparaitrait vide — ce qui se lirait comme une regression alors que
-- les invitations sont toujours la.
--
-- A lancer une seule fois, apres le deploiement. Relancable sans risque :
-- le NOT EXISTS empeche tout doublon.
--
--   psql -U postgres -d musical_app -f reprise_notifications_invitations.sql

INSERT INTO notifications (user_id, type_id, content, related_entity_type_id,
                           related_entity_id, is_read, created_at)
SELECT
    a.user_id,
    (SELECT id FROM notification_types WHERE name = 'CONCERT_INVITATION'),
    -- Le titre d'un concert est facultatif en base : le concert 756 n'en a
    -- pas. Une simple concatenation donnerait NULL sur toute la chaine, et
    -- content est NOT NULL — d'ou les reculs successifs sur le nom de
    -- l'evenement, puis sur un libelle generique.
    CASE
        WHEN NULLIF(c.title, '') IS NOT NULL AND e.name IS NOT NULL
            THEN 'Invitation a jouer : ' || c.title || ' (' || e.name || ')'
        WHEN NULLIF(c.title, '') IS NOT NULL
            THEN 'Invitation a jouer : ' || c.title
        WHEN e.name IS NOT NULL
            THEN 'Invitation a jouer a ' || e.name
        ELSE 'Invitation a jouer'
    END
        || ' - le ' || to_char(c.start_time, 'DD/MM/YYYY')
        || ' a ' || to_char(c.start_time, 'HH24:MI'),
    (SELECT id FROM interaction_targets WHERE name = 'CONCERT'),
    c.id,
    FALSE,
    -- La date de creation du concert, et non now() : sinon les invitations
    -- apparaitraient toutes a la meme seconde et l'ordre d'affichage ne
    -- voudrait plus rien dire.
    COALESCE(c.created_at, now())
FROM concerts c
JOIN participation_statuses ps ON ps.id = c.status_id
JOIN artists a ON a.id = c.artist_id
LEFT JOIN events e ON e.id = c.event_id
WHERE ps.name = 'PENDING'
  AND c.start_time > now()
  AND NOT EXISTS (
        SELECT 1
        FROM notifications n
        WHERE n.user_id = a.user_id
          AND n.related_entity_id = c.id
          AND n.related_entity_type_id = (SELECT id FROM interaction_targets
                                          WHERE name = 'CONCERT')
          AND n.type_id = (SELECT id FROM notification_types
                           WHERE name = 'CONCERT_INVITATION')
  );

-- Controle : doit renvoyer autant de lignes qu'il y a d'invitations en attente.
SELECT u.email, n.content, n.related_entity_id AS concert, n.created_at
FROM notifications n
JOIN users u ON u.id = n.user_id
JOIN notification_types nt ON nt.id = n.type_id
WHERE nt.name = 'CONCERT_INVITATION'
ORDER BY n.created_at;
