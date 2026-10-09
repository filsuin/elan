package fr.elan.app.content

import fr.elan.app.domain.*
import kotlin.math.roundToInt

/** Bilateral positions always expand into explicitly labelled left AND right steps. */
object PracticeCatalog {
    fun stepsFor(poseId: String, seconds: Int? = null): List<YogaStep> {
        val pose = requireNotNull(YogaCatalog.find(poseId)) { "Posture inconnue" }
        val duration = seconds ?: pose.seconds
        return if (pose.bilateral) listOf(YogaStep(poseId, duration, PoseSide.LEFT), YogaStep(poseId, duration, PoseSide.RIGHT))
            else listOf(YogaStep(poseId, duration))
    }
    private fun session(id: String, title: String, description: String, vararg poses: String): YogaSession =
        YogaSession(id, title, description, poses.flatMap { stepsFor(it) })
    val sessions = listOf(
        session("morning", "Réveil en douceur", "Une mise en mouvement progressive, principalement debout.",
            "seated", "mountain", "upward_salute", "standing_side", "down_dog_wall", "mountain"),
        session("desk", "Pause au bureau", "Tout se pratique sur une chaise stable, sans tapis.",
            "seated", "chair_cat", "chair_side", "chair_twist", "chair_ankle", "seated"),
        session("evening", "Retour au calme", "Des appuis soutenus et des changements de position tranquilles.",
            "easy_seat", "child", "rest", "reclined_butterfly", "side"),
        session("discover", "Premiers pas", "Découvrir des positions simples sans recherche de performance.",
            "seated", "mountain", "star", "half_fold", "seated"),
        session("standing", "Mobilité debout", "Une séquence sans passage au sol ; garde un appui proche.",
            "mountain", "upward_salute", "standing_side", "down_dog_wall", "heel_raise_supported", "mountain"),
        session("balance", "Équilibre avec appui", "Mur ou chaise lourde obligatoire ; garde les yeux ouverts.",
            "mountain", "heel_raise_supported", "tree_supported", "star", "mountain"),
        session("ground", "Prendre le temps au sol", "Un tapis, des coussins et des amplitudes modestes.",
            "easy_seat", "cat", "child", "rest", "wind_relief", "side"),
        session("shoulders", "Bouger les épaules", "Du mouvement doux, sans chercher à lever les bras au maximum.",
            "seated", "upward_salute", "star", "down_dog_wall", "chair_side", "seated"),
        session("flow", "Enchaînement intermédiaire", "Familiarité avec les postures requise ; adapte ou passe une étape.",
            "mountain", "warrior1", "warrior2", "side_angle", "half_fold", "seated"),
        session("restful", "Repos soutenu", "Des positions de repos ; les durées sont facultatives.",
            "seated", "rest", "chair", "side"),
    )
    fun yogaPlan(session: YogaSession, transitionSeconds: Int = 10, durationPercent: Int = 100): PracticePlan {
        require(transitionSeconds in 5..30)
        require(durationPercent in listOf(50, 100, 150, 200))
        require(session.steps.isNotEmpty() && session.steps.size <= MAX_SESSION_STEPS)
        val result = mutableListOf(TimedStep("Installation", "Installe ton matériel. Tu peux mettre en pause pour changer de position.",
            10, StepKind.PREPARATION))
        session.steps.forEachIndexed { i, step ->
            val pose = requireNotNull(YogaCatalog.find(step.poseId))
            val title = pose.title + if (step.side == PoseSide.NONE) "" else " · ${step.side.label}"
            if (i > 0) result += TimedStep("Transition", "Prends ton temps pour : $title. Mets en pause si nécessaire.",
                transitionSeconds, StepKind.TRANSITION, pose.id, step.side)
            val seconds = (step.seconds * durationPercent / 100f).roundToInt().coerceIn(15, 300)
            result += TimedStep(title, "Respire naturellement. Quitte la posture à tout moment en cas de gêne.",
                seconds, StepKind.POSE, pose.id, step.side)
        }
        return PracticePlan(session.title, PracticeKind.YOGA, result)
    }
}

data class BreathingPattern(val id: String, val title: String, val inhale: Int, val exhale: Int, val description: String)
object BreathingCatalog {
    const val safety = "Sans apnée et sans respirations forcées. Le rythme est une option, pas un objectif. Reste assis, ne pratique pas en conduisant. Arrête et retrouve ton souffle naturel en cas de vertige, d'inconfort ou d'essoufflement. Demande un avis adapté en cas de maladie respiratoire ou cardiovasculaire."
    val patterns = listOf(
        BreathingPattern("gentle", "Rythme court", 3, 3, "3 secondes pour inspirer, 3 pour expirer."),
        BreathingPattern("equal", "Rythme égal", 4, 4, "4 secondes pour inspirer, 4 pour expirer."),
        BreathingPattern("long_exhale", "Expiration un peu plus longue", 4, 6, "4 secondes pour inspirer, 6 pour expirer, seulement si c'est confortable."),
        BreathingPattern("slow", "Rythme lent", 5, 5, "5 secondes pour inspirer, 5 pour expirer, sans forcer."),
    )
    fun plan(pattern: BreathingPattern, minutes: Int): PracticePlan {
        require(pattern in patterns && minutes in listOf(1, 3, 5, 10))
        val cycles = (minutes * 60 / (pattern.inhale + pattern.exhale)).coerceAtLeast(1)
        val steps = mutableListOf(TimedStep("Installation", "Assieds-toi confortablement. Garde un souffle léger et naturel.", 10, StepKind.PREPARATION))
        repeat(cycles) {
            steps += TimedStep("Inspire doucement", "Sans remplir les poumons au maximum.", pattern.inhale, StepKind.INHALE)
            steps += TimedStep("Expire doucement", "Sans vider les poumons de force.", pattern.exhale, StepKind.EXHALE)
        }
        return PracticePlan(pattern.title, PracticeKind.BREATHING, steps)
    }
    fun meditation(minutes: Int): PracticePlan {
        require(minutes in listOf(2, 5, 10, 15))
        return PracticePlan("Pause silencieuse", PracticeKind.MEDITATION, listOf(
            TimedStep("Installation", "Choisis une assise confortable, regard ouvert ou abaissé.", 10, StepKind.PREPARATION),
            TimedStep("Un moment pour toi", "Remarque les points d'appui et les sons autour de toi. Rien à réussir. Reviens à ton souffle naturel quand tu le souhaites.",
                minutes * 60, StepKind.SILENCE),
        ))
    }
}

