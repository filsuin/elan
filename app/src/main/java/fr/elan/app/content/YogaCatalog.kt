package fr.elan.app.content

data class YogaPose(
    val id: String,
    val title: String,
    val subtitle: String,
    val seconds: Int,
    val equipment: String,
    val steps: List<String>,
    val caution: String,
    val sanskrit: String = "Variante douce",
    val level: YogaLevel = YogaLevel.GENTLE,
    val category: YogaCategory = YogaCategory.REST,
    val focus: List<String> = listOf("Repos"),
    val bilateral: Boolean = false,
    val adaptation: String = "Raccourcis la durée ou choisis une assise soutenue.",
)

enum class YogaLevel(val label: String) { GENTLE("Doux"), BEGINNER("Débutant"), INTERMEDIATE("Intermédiaire") }
enum class YogaCategory(val label: String) {
    STANDING("Debout"), SEATED("Assis"), FLOOR("Au sol"), BALANCE("Équilibre"), REST("Repos"), MOBILITY("Mobilité")
}

object YogaCatalog {
    const val safety = "Reste dans une amplitude confortable et respire naturellement, sans retenir ton souffle. Arrête en cas de douleur, vertige ou gêne. En cas de blessure, de problème de santé ou de grossesse, demande un avis adapté avant de pratiquer. Ces repères ne remplacent pas un cours avec une personne qualifiée."
    private val original = listOf(
        YogaPose("seated", "Assise confortable", "S'installer, sans effort", 90, "Une chaise stable, sans roulettes", listOf(
            "Assieds-toi sur une chaise stable, les pieds soutenus par le sol ou un support.",
            "Appuie ton dos contre le dossier si cela est plus confortable. Pose les mains sur les cuisses.",
            "Laisse les épaules se relâcher. Garde les yeux ouverts ou le regard abaissé.",
            "Observe simplement quelques respirations naturelles, puis reprends doucement tes activités."
        ), "Ajuste la hauteur de l'assise. Change de position si une zone devient inconfortable."),
        YogaPose("child", "Enfant soutenu", "Une variante avec coussins", 60, "Tapis et coussin ferme", listOf(
            "Mets-toi à genoux sur un support confortable, uniquement si tes genoux le permettent.",
            "Place un coussin haut devant toi. Recule doucement le bassin vers les talons, sans chercher à les toucher.",
            "Incline le buste vers le coussin et soutiens la tête. Garde le nez et la bouche dégagés.",
            "Ne cherche pas à descendre davantage. Pour sortir, aide-toi des mains et redresse-toi lentement."
        ), "Évite cette posture si elle comprime le ventre ou provoque une gêne aux genoux, aux hanches ou au dos. Choisis l'assise confortable à la place."),
        YogaPose("cat", "Chat-vache doux", "Un mouvement de petite amplitude", 60, "Un tapis, éventuellement une couverture", listOf(
            "Installe-toi à quatre pattes, les mains sous les épaules et les genoux sous les hanches.",
            "En respirant naturellement, arrondis très légèrement le dos.",
            "Reviens à un dos neutre, puis laisse-le se creuser légèrement, sans forcer la nuque.",
            "Alterne lentement, sans amplitude imposée. Reviens au neutre avant de te relever."
        ), "Ne pratique pas en appui si les poignets ou les genoux sont douloureux. Une assise simple est une alternative ; aucun mouvement ne doit faire mal."),
        YogaPose("rest", "Repos, genoux pliés", "Laisser le sol te soutenir", 120, "Un tapis et un petit coussin", listOf(
            "Allonge-toi sur le dos si cette position t'est confortable.",
            "Plie les genoux et pose les pieds au sol, sans imposer une largeur précise.",
            "Soutiens la tête si nécessaire. Laisse les bras dans une position naturelle.",
            "Pour sortir, roule sur le côté, marque une pause, puis aide-toi des bras."
        ), "Ne reste pas sur le dos si cela te gêne. Pendant la grossesse, fais adapter les postures par un professionnel ; le repos sur le côté peut être une option à discuter."),
        YogaPose("chair", "Jambes sur une chaise", "Un repos avec support", 120, "Tapis et chaise stable, sans roulettes", listOf(
            "Place la chaise de manière à ce qu'elle ne puisse pas glisser.",
            "Allonge-toi sur le dos et pose les mollets sur l'assise, sans chercher un angle exact.",
            "Ajuste la distance pour ne ressentir aucune traction. Soutiens la tête si besoin.",
            "Pour sortir, enlève une jambe puis l'autre, roule sur le côté et redresse-toi lentement."
        ), "Choisis une autre position si tu ne peux pas t'installer ou te relever facilement, ou si rester sur le dos est inconfortable."),
        YogaPose("side", "Repos sur le côté", "Trouver ta position de repos", 120, "Un tapis et deux coussins", listOf(
            "Allonge-toi sur le côté qui t'est confortable.",
            "Soutiens la tête pour garder le cou dans le prolongement du dos.",
            "Plie les jambes sans les serrer. Un coussin entre les genoux peut rendre la position plus confortable.",
            "Laisse la respiration naturelle. Pour te relever, prends appui sur les bras et marque une pause assise."
        ), "Ajuste les supports si une épaule, une hanche ou le cou devient inconfortable. La durée proposée reste facultative."),
    )
    val poses: List<YogaPose> = original.map { pose ->
        when (pose.id) {
            "seated" -> pose.copy(category = YogaCategory.SEATED, focus = listOf("Posture", "Repos"))
            "cat" -> pose.copy(category = YogaCategory.MOBILITY, focus = listOf("Dos"), sanskrit = "Marjaryasana / Bitilasana")
            "child" -> pose.copy(sanskrit = "Balasana avec support", focus = listOf("Repos", "Dos"))
            else -> pose
        }
    } + ExtendedYoga.poses
    private val byId = poses.associateBy { it.id }
    fun find(id: String): YogaPose? = byId[id]

    fun search(query: String, level: YogaLevel? = null, category: YogaCategory? = null,
        favorites: Set<String>? = null): List<YogaPose> {
        val terms = normalize(query).split(" ").filter { it.isNotBlank() }
        return poses.filter { pose ->
            (level == null || pose.level == level) && (category == null || pose.category == category) &&
                (favorites == null || pose.id in favorites) && terms.all { term ->
                term in normalize("${pose.title} ${pose.sanskrit} ${pose.focus.joinToString(" ")} ${pose.equipment}")
            }
        }
    }
    fun normalize(text: String): String = java.text.Normalizer.normalize(text, java.text.Normalizer.Form.NFD)
        .replace(Regex("\\p{M}+"), "").lowercase(java.util.Locale.FRENCH).trim()

}
