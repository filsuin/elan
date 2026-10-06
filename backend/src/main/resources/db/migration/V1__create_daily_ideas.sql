CREATE TABLE IF NOT EXISTS daily_ideas (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(160) NOT NULL,
    text VARCHAR(2000) NOT NULL,
    language VARCHAR(10) NOT NULL DEFAULT 'fr',
    source VARCHAR(80) NOT NULL DEFAULT 'editorial',
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS daily_ideas_active_language_idx
    ON daily_ideas (active, language, id);

INSERT INTO daily_ideas (title, text, language, source)
SELECT seed.title, seed.text, 'fr', 'editorial'
FROM (VALUES
    ('Commencer petit', 'Choisis une chose réalisable aujourd''hui. Une journée n''a pas besoin d''être parfaite pour avoir de la valeur.'),
    ('Te parler avec respect', 'Quand une pensée dure arrive, essaie de la reformuler comme tu parlerais à une personne qui compte pour toi.'),
    ('Garder de la marge', 'Laisse un espace libre dans ta journée. Tu as le droit de ne pas remplir chaque minute.'),
    ('Chercher un lien', 'Un message, un bonjour, une conversation : choisis un contact qui te fait te sentir à l''aise.'),
    ('Remarquer l''ordinaire', 'Prends un instant pour observer une couleur, un son ou une sensation agréable, sans chercher un moment exceptionnel.'),
    ('Une pause sans performance', 'Pose ce que tu fais un instant. Relâche les épaules et laisse ta respiration suivre son rythme naturel.'),
    ('Dire ce dont tu as besoin', 'Tu peux exprimer une limite simplement : « Aujourd''hui, je ne peux pas m''en charger. »'),
    ('Faire de la place au repos', 'Le repos n''a pas à se mériter. Choisis une pause qui correspond à ton énergie du moment.'),
    ('Voir ce qui est déjà là', 'Note une chose que tu sais faire ou une qualité que tu as montrée récemment, avec un exemple concret.'),
    ('Revenir à une étape', 'Quand une tâche semble trop grande, cherche seulement sa première petite étape.'),
    ('T''accorder du plaisir', 'Prévois un moment simple qui te plaît : quelques pages, une chanson, cuisiner ou regarder dehors.'),
    ('Changer le rythme', 'Selon tes possibilités, change de position ou fais quelques pas. Rien à compter, rien à prouver.'),
    ('Accepter une journée mitigée', 'Plusieurs émotions peuvent coexister. Tu n''as pas besoin de transformer tout ce que tu ressens en positif.'),
    ('Finir avec douceur', 'Avant de clore ta journée, repère une chose faite, tentée ou simplement traversée.')
) AS seed(title, text)
WHERE NOT EXISTS (
    SELECT 1 FROM daily_ideas existing
    WHERE existing.title = seed.title AND existing.language = 'fr'
);
