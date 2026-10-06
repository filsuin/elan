package fr.elan.app.content

import java.time.LocalDate

data class DailyTip(val title: String, val text: String)

/** Editorial suggestions, not personalised predictions or treatment advice. */
object DailyContent {
    val tips = listOf(
        DailyTip("Commencer petit", "Choisis une chose réalisable aujourd'hui. Une journée n'a pas besoin d'être parfaite pour avoir de la valeur."),
        DailyTip("Te parler avec respect", "Quand une pensée dure arrive, essaie de la reformuler comme tu parlerais à une personne qui compte pour toi."),
        DailyTip("Garder de la marge", "Laisse un espace libre dans ta journée. Tu as le droit de ne pas remplir chaque minute."),
        DailyTip("Chercher un lien", "Un message, un bonjour, une conversation : choisis un contact qui te fait te sentir à l'aise."),
        DailyTip("Remarquer l'ordinaire", "Prends un instant pour observer une couleur, un son ou une sensation agréable, sans chercher un moment exceptionnel."),
        DailyTip("Une pause sans performance", "Pose ce que tu fais un instant. Relâche les épaules et laisse ta respiration suivre son rythme naturel."),
        DailyTip("Dire ce dont tu as besoin", "Tu peux exprimer une limite simplement : « Aujourd'hui, je ne peux pas m'en charger. »"),
        DailyTip("Faire de la place au repos", "Le repos n'a pas à se mériter. Choisis une pause qui correspond à ton énergie du moment."),
        DailyTip("Voir ce qui est déjà là", "Note une chose que tu sais faire ou une qualité que tu as montrée récemment, avec un exemple concret."),
        DailyTip("Revenir à une étape", "Quand une tâche semble trop grande, cherche seulement sa première petite étape."),
        DailyTip("T'accorder du plaisir", "Prévois un moment simple qui te plaît : quelques pages, une chanson, cuisiner ou regarder dehors."),
        DailyTip("Changer le rythme", "Selon tes possibilités, change de position ou fais quelques pas. Rien à compter, rien à prouver."),
        DailyTip("Accepter une journée mitigée", "Plusieurs émotions peuvent coexister. Tu n'as pas besoin de transformer tout ce que tu ressens en positif."),
        DailyTip("Finir avec douceur", "Avant de clore ta journée, repère une chose faite, tentée ou simplement traversée."),
    )
    val challenges = listOf(
        "Note une petite réussite et ce que tu as fait pour y contribuer.",
        "Remplace un « je dois tout faire » par une priorité réaliste.",
        "Reconnais un effort récent, même si le résultat n'est pas parfait.",
        "Exprime une préférence dans une situation simple.",
        "Écris une qualité que tu apprécies chez toi et un exemple.",
        "Accorde-toi le droit de demander une précision ou de l'aide.",
        "Essaie une petite chose nouvelle, dans une situation où tu te sens en sécurité.",
        "Réponds à une pensée autocritique avec un fait plus nuancé.",
        "Garde une trace d'un retour positif reçu récemment.",
        "Choisis une limite que tu souhaites respecter aujourd'hui.",
        "Remercie-toi d'avoir pris soin de quelque chose d'important pour toi.",
        "Fais un petit pas vers un projet qui te ressemble.",
        "Repère une comparaison inutile et reviens à ton propre chemin.",
        "Note ce que tu aimerais dire à un proche qui vivrait ta journée.",
    )
    fun index(date: LocalDate, size: Int): Int {
        require(size > 0)
        return Math.floorMod(date.toEpochDay(), size.toLong()).toInt()
    }
    fun tip(date: LocalDate) = tips[index(date, tips.size)]
    fun challenge(date: LocalDate) = challenges[index(date.plusDays(3), challenges.size)]
}
