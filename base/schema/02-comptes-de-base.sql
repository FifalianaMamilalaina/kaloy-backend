-- ============================================================
-- KALOY - COMPTES DE BASE
-- ============================================================
--
-- A lancer juste apres 01-schema.sql :
--
--   psql -U postgres -d musical_app -f 02-comptes-de-base.sql
--
-- Ce fichier est dans schema/ et non dans donnees/ parce que sans lui
-- PERSONNE ne peut se connecter a une installation neuve. L'inscription
-- par l'application ne suffit pas : elle cree un compte NOT_VERIFIED et
-- envoie un code par courriel ou par SMS, donc elle demande un serveur
-- de courriel et un compte MAPI deja configures et joignables.
--
-- Il remplace l'ancien insert_test_data.sql, qui creait ces deux comptes
-- INUTILISABLES de deux facons :
--
--   - son hash de mot de passe etait un motif repete et non un vrai hash
--     BCrypt, donc login() repondait 401 ;
--   - il laissait verification_status_user_id a NULL, et login() refuse
--     par un 403 « Compte non verifie » tout compte dont ce champ ne vaut
--     pas VERIFIED.
--
-- Les deux defauts etaient repares par un UPDATE enfoui dans
-- seed_contenu_kaloy.sql, un fichier de contenu de demonstration : qui
-- n'installait que la structure se retrouvait sans aucun acces.
--
-- Relancable sans risque, et il repare au passage une base ou l'ancien
-- fichier a tourne : le ON CONFLICT reecrit le hash et le statut.
-- ============================================================


-- ------------------------------------------------------------
-- 1. Les deux comptes
-- ------------------------------------------------------------
-- Hash BCrypt reel de « password123 ».
--
-- Aucun identifiant explicite : sur une base neuve ils prennent 1 et 2,
-- comme dans la base de developpement, et la sequence reste alignee.

INSERT INTO users (email, password_hash, role_id, status_id,
                   verification_status_user_id, email_verified_at,
                   created_at, updated_at)
SELECT v.email,
       '$2a$10$/LJzCEMdUsedkJ8qUbdRweJtacnT86C5AHv8SraglJeeoDVCttDY2',
       (SELECT id FROM user_roles    WHERE name = v.role),
       (SELECT id FROM user_statuses WHERE name = 'ACTIVE'),
       (SELECT id FROM verification_statuses_users WHERE name = 'VERIFIED'),
       NOW(), NOW(), NOW()
FROM (VALUES
        ('client@dev.com', 'CLIENT'),
        ('artist@dev.com', 'ARTIST')
     ) AS v(email, role)
ON CONFLICT (email) DO UPDATE
SET password_hash               = EXCLUDED.password_hash,
    verification_status_user_id = EXCLUDED.verification_status_user_id,
    status_id                   = EXCLUDED.status_id,
    email_verified_at           = COALESCE(users.email_verified_at, NOW()),
    updated_at                  = NOW();


-- ------------------------------------------------------------
-- 2. Le profil client
-- ------------------------------------------------------------
-- Sans la ligne dans "clients", l'ecran de profil ne trouve rien.

INSERT INTO clients (user_id)
SELECT id FROM users WHERE email = 'client@dev.com'
ON CONFLICT (user_id) DO NOTHING;


-- ------------------------------------------------------------
-- 3. Le profil artiste
-- ------------------------------------------------------------
-- Sans la ligne dans "artists", le compte se connecte mais ne peut rien
-- faire en tant qu'artiste : ServiceArtisteConnecte.resoudre() repond
-- 403 « Ce compte n'est rattache a aucun profil artiste » sur toute
-- operation de « Mes chansons ».
--
-- L'ancien fichier ne la creait pas, et elle manque encore dans la base
-- de developpement : artist@dev.com y est un compte ARTIST sans profil.
--
-- VALIDATED et non VERIFIED : artists.verification_status_id pointe vers
-- verification_statuses, qui est une autre table que
-- verification_statuses_users utilisee pour les comptes. C'est le statut
-- que portent les artistes du jeu de donnees.

INSERT INTO artists (user_id, artist_type_id, stage_name,
                     verification_status_id, verified_at, is_certified,
                     created_at)
SELECT u.id,
       (SELECT id FROM artist_types WHERE name = 'SOLO'),
       'Artiste Dev',
       (SELECT id FROM verification_statuses WHERE name = 'VALIDATED'),
       NOW(), FALSE, NOW()
FROM users u
WHERE u.email = 'artist@dev.com'
ON CONFLICT (user_id) DO NOTHING;


-- ------------------------------------------------------------
-- 4. Les noms affiches
-- ------------------------------------------------------------
-- users_infos.user_name est UNIQUE mais id_user ne l'est pas : un
-- ON CONFLICT ne protegerait pas du doublon par utilisateur, d'ou le
-- NOT EXISTS.

INSERT INTO users_infos (name, last_name, user_name, id_user)
SELECT v.prenom, v.nom, v.pseudo, u.id
FROM (VALUES
        ('Client',  'Test', 'client_test', 'client@dev.com'),
        ('Artiste', 'Dev',  'artiste_dev', 'artist@dev.com')
     ) AS v(prenom, nom, pseudo, email)
JOIN users u ON u.email = v.email
WHERE NOT EXISTS (
        SELECT 1 FROM users_infos ui WHERE ui.id_user = u.id);


-- ------------------------------------------------------------
-- Controle
-- ------------------------------------------------------------
-- Les deux lignes doivent afficher « oui » partout, sans quoi la
-- connexion echouera.

SELECT u.email,
       r.name AS role,
       CASE WHEN v.name = 'VERIFIED' THEN 'oui' ELSE 'NON' END AS peut_se_connecter,
       CASE WHEN c.user_id IS NOT NULL OR a.id IS NOT NULL
            THEN 'oui' ELSE 'NON' END AS profil,
       CASE WHEN ui.id_user IS NOT NULL THEN 'oui' ELSE 'NON' END AS nom_affiche
FROM users u
LEFT JOIN user_roles r                  ON r.id  = u.role_id
LEFT JOIN verification_statuses_users v ON v.id  = u.verification_status_user_id
LEFT JOIN clients c                     ON c.user_id = u.id
LEFT JOIN artists a                     ON a.user_id = u.id
LEFT JOIN users_infos ui                ON ui.id_user = u.id
WHERE u.email IN ('client@dev.com', 'artist@dev.com')
ORDER BY u.email;
