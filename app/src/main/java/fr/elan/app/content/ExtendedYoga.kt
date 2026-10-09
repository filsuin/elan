package fr.elan.app.content

/** Original editorial content; not a therapeutic prescription. */
internal object ExtendedYoga {
    val poses = listOf(
        YogaPose(
            id = "mountain", title = "Montagne", subtitle = "Ancrage / Posture",
            seconds = 45, equipment = "Sol stable ; mur accessible",
            steps = listOf(
                "Place les pieds parallèles, à une largeur confortable, et répartis les appuis.",
                "Garde les genoux souples, le bassin neutre et les bras relâchés.",
                "Laisse la tête s'élever sans relever le menton ; respire naturellement.",
                "Pour sortir, bouge les doigts et fais un petit pas en gardant un appui proche."
            ),
            caution = "En cas de vertige ou d'instabilité, assieds-toi. Ne ferme pas les yeux pour chercher l'équilibre.", adaptation = "Pratique le dos près d'un mur, ou reste assis sur une chaise.",
            sanskrit = "Tadasana", level = YogaLevel.BEGINNER, category = YogaCategory.STANDING,
            focus = listOf("Ancrage", "Posture"), bilateral = false,
        ),
        YogaPose(
            id = "upward_salute", title = "Bras vers le ciel", subtitle = "Épaules / Posture",
            seconds = 30, equipment = "Sol stable",
            steps = listOf(
                "Pars de la montagne, les pieds bien posés et les genoux non verrouillés.",
                "Monte lentement les bras devant toi jusqu'à une hauteur confortable.",
                "Garde les côtes au-dessus du bassin, sans cambrer pour monter plus haut.",
                "Redescends les bras lentement et reviens à une respiration naturelle."
            ),
            caution = "Ne traverse pas une douleur d'épaule. Arrête si lever les bras provoque des fourmillements.", adaptation = "Garde les mains à hauteur de poitrine, ou effectue le mouvement assis.",
            sanskrit = "Urdhva Hastasana", level = YogaLevel.BEGINNER, category = YogaCategory.STANDING,
            focus = listOf("Épaules", "Posture"), bilateral = false,
        ),
        YogaPose(
            id = "standing_side", title = "Inclinaison debout", subtitle = "Flancs / Épaules",
            seconds = 30, equipment = "Mur ou chaise stable",
            steps = listOf(
                "Debout, garde les pieds au sol et un appui accessible.",
                "Pour le côté indiqué, lève ce bras sans forcer ; l'autre main reste sur la hanche.",
                "Incline très peu le buste du côté opposé au bras levé, sans tourner ni cambrer.",
                "Reviens au centre avant de baisser le bras. L'autre côté se pratique séparément."
            ),
            caution = "Réduis l'amplitude si le dos ou l'épaule tire. Aucune compression lombaire n'est recherchée.", adaptation = "Pose les deux mains sur les hanches, ou choisis l'inclinaison sur chaise.",
            sanskrit = "Parsva Tadasana", level = YogaLevel.BEGINNER, category = YogaCategory.STANDING,
            focus = listOf("Flancs", "Épaules"), bilateral = true,
        ),
        YogaPose(
            id = "half_fold", title = "Demi-flexion avec appui", subtitle = "Dos / Jambes",
            seconds = 45, equipment = "Chaise lourde calée contre un mur",
            steps = listOf(
                "Place les mains sur le dossier d'une chaise immobilisée, face à toi.",
                "Recule d'un petit pas et plie les genoux pour incliner le buste depuis les hanches.",
                "Allonge le dos sans baisser la tête sous le niveau du buste ; respire librement.",
                "Rapproche les pieds de la chaise avant de te redresser lentement."
            ),
            caution = "Ne tire pas sur un meuble mobile. Évite la flexion si elle provoque douleur ou vertige.", adaptation = "Garde le buste plus vertical et les genoux davantage pliés.",
            sanskrit = "Ardha Uttanasana adaptée", level = YogaLevel.BEGINNER, category = YogaCategory.STANDING,
            focus = listOf("Dos", "Jambes"), bilateral = false,
        ),
        YogaPose(
            id = "chair_wall", title = "Chaise au mur", subtitle = "Jambes / Ancrage",
            seconds = 20, equipment = "Mur dégagé et sol non glissant",
            steps = listOf(
                "Appuie doucement le dos au mur et avance un peu les pieds.",
                "Plie légèrement les genoux en glissant de quelques centimètres seulement.",
                "Garde les genoux dans la direction des pieds et une respiration non bloquée.",
                "Remonte avec les jambes, sans rebond, puis fais une pause debout."
            ),
            caution = "Sollicite les genoux et les cuisses. Évite en cas de douleur et ne cherche pas l'angle droit.", adaptation = "Reste presque debout ; une assise sur chaise peut remplacer toute la posture.",
            sanskrit = "Utkatasana adaptée", level = YogaLevel.INTERMEDIATE, category = YogaCategory.STANDING,
            focus = listOf("Jambes", "Ancrage"), bilateral = false,
        ),
        YogaPose(
            id = "warrior1", title = "Guerrier I, pas court", subtitle = "Jambes / Hanches",
            seconds = 30, equipment = "Chaise stable accessible",
            steps = listOf(
                "Avance le pied du côté indiqué et conserve un écart latéral entre les pieds.",
                "Pose le talon arrière avec le pied légèrement tourné vers l'extérieur ; raccourcis le pas si nécessaire.",
                "Plie peu le genou avant dans la direction du pied. Les mains peuvent rester aux hanches.",
                "Redresse la jambe avant et ramène les pieds ensemble, avec l'aide de la chaise."
            ),
            caution = "Ne force pas les deux hanches à regarder devant. Évite toute torsion douloureuse du genou arrière.", adaptation = "Garde un pas très court et une main sur le dossier ; les bras ne sont pas obligatoirement levés.",
            sanskrit = "Virabhadrasana I adaptée", level = YogaLevel.INTERMEDIATE, category = YogaCategory.STANDING,
            focus = listOf("Jambes", "Hanches"), bilateral = true,
        ),
        YogaPose(
            id = "warrior2", title = "Guerrier II", subtitle = "Jambes / Ancrage",
            seconds = 30, equipment = "Tapis antiglisse ; chaise accessible",
            steps = listOf(
                "Écarte modérément les pieds et tourne le pied du côté indiqué vers l'extérieur.",
                "Plie peu ce genou en le gardant aligné avec les orteils ; garde l'autre jambe souple.",
                "Ouvre les bras à une hauteur confortable et garde le buste vertical, sans pencher sur la jambe avant.",
                "Redresse la jambe pliée, baisse les bras, puis ramène les pieds parallèles."
            ),
            caution = "Raccourcis l'écart en cas de tension aux hanches. Ne laisse pas le genou avant tomber vers l'intérieur.", adaptation = "Pose une main sur une chaise et conserve une petite flexion du genou.",
            sanskrit = "Virabhadrasana II", level = YogaLevel.INTERMEDIATE, category = YogaCategory.STANDING,
            focus = listOf("Jambes", "Ancrage"), bilateral = true,
        ),
        YogaPose(
            id = "reverse_warrior", title = "Guerrier inversé doux", subtitle = "Flancs / Jambes",
            seconds = 20, equipment = "Tapis antiglisse",
            steps = listOf(
                "Installe un guerrier II peu profond avec le genou du côté indiqué fléchi.",
                "Pose la main arrière sur la hanche, sans pousser sur le genou.",
                "Lève le bras avant et incline très légèrement le buste vers l'arrière, regard devant.",
                "Reviens au guerrier II, baisse les bras puis redresse la jambe avant."
            ),
            caution = "Cette variante demande de la stabilité. Ne cambre pas le bas du dos et ne renverse pas la tête.", adaptation = "Garde les mains aux hanches ; le guerrier II seul est une alternative.",
            sanskrit = "Viparita Virabhadrasana", level = YogaLevel.INTERMEDIATE, category = YogaCategory.STANDING,
            focus = listOf("Flancs", "Jambes"), bilateral = true,
        ),
        YogaPose(
            id = "triangle_supported", title = "Triangle avec support", subtitle = "Flancs / Jambes",
            seconds = 30, equipment = "Chaise stable placée du côté avant",
            steps = listOf(
                "Écarte les pieds modérément ; le pied du côté indiqué se tourne vers la chaise.",
                "Garde les deux genoux souples et allonge le buste vers ce pied.",
                "Pose la main du même côté sur le dossier, l'autre sur la hanche ; regarde devant ou vers le sol.",
                "Appuie dans les pieds et redresse le buste avant de changer l'orientation des pieds."
            ),
            caution = "Ne cherche pas à toucher le sol. Évite de verrouiller le genou ou de tourner la nuque.", adaptation = "Utilise un support haut et une inclinaison minime.",
            sanskrit = "Trikonasana adaptée", level = YogaLevel.INTERMEDIATE, category = YogaCategory.STANDING,
            focus = listOf("Flancs", "Jambes"), bilateral = true,
        ),
        YogaPose(
            id = "side_angle", title = "Angle latéral avec appui", subtitle = "Flancs / Jambes",
            seconds = 30, equipment = "Chaise stable accessible",
            steps = listOf(
                "Pars d'un guerrier II peu profond, genou du côté indiqué fléchi.",
                "Pose l'avant-bras du même côté sur la cuisse, sans comprimer le genou.",
                "Garde l'autre main sur la hanche et le cou dans le prolongement du dos.",
                "Redresse le buste avec les jambes, puis tends doucement la jambe avant."
            ),
            caution = "L'appui ne doit pas porter sur le genou. Reste dans une faible amplitude si le dos est sensible.", adaptation = "Pose la main sur un dossier haut et garde le buste plus vertical.",
            sanskrit = "Utthita Parsvakonasana adaptée", level = YogaLevel.INTERMEDIATE, category = YogaCategory.STANDING,
            focus = listOf("Flancs", "Jambes"), bilateral = true,
        ),
        YogaPose(
            id = "wide_fold_supported", title = "Flexion large avec support", subtitle = "Jambes / Hanches",
            seconds = 30, equipment = "Chaise lourde contre un mur",
            steps = listOf(
                "Debout face à la chaise, écarte les pieds sans aller à ton maximum.",
                "Pose les mains sur le dossier et fléchis les genoux.",
                "Incline le buste depuis les hanches en gardant le dos long et la tête soutenue par son alignement.",
                "Redresse-toi lentement en te rapprochant du support, puis réduis l'écart des pieds."
            ),
            caution = "Ne suspends pas la tête vers le sol. Évite toute douleur à l'aine ou tout vertige.", adaptation = "Rapproche les pieds et utilise un support plus haut.",
            sanskrit = "Prasarita Padottanasana adaptée", level = YogaLevel.INTERMEDIATE, category = YogaCategory.STANDING,
            focus = listOf("Jambes", "Hanches"), bilateral = false,
        ),
        YogaPose(
            id = "tree_supported", title = "Arbre avec appui", subtitle = "Équilibre / Ancrage",
            seconds = 30, equipment = "Mur ou chaise lourde",
            steps = listOf(
                "Tiens le support et transfère progressivement le poids sur la jambe opposée au côté indiqué.",
                "Pose le talon du côté indiqué contre la cheville d'appui en gardant les orteils au sol.",
                "Garde le bassin stable et le regard sur un point fixe. L'appui de la main reste disponible.",
                "Repose le pied à plat et répartis le poids avant d'essayer l'autre côté."
            ),
            caution = "Ne place jamais le pied contre le genou. Pratique près d'un support, sans fermer les yeux.", adaptation = "Les orteils du pied mobile restent au sol pendant toute la posture.",
            sanskrit = "Vrksasana adaptée", level = YogaLevel.BEGINNER, category = YogaCategory.BALANCE,
            focus = listOf("Équilibre", "Ancrage"), bilateral = true,
        ),
        YogaPose(
            id = "heel_raise_supported", title = "Montée sur pointes avec appui", subtitle = "Chevilles / Équilibre",
            seconds = 30, equipment = "Chaise stable ou plan de travail fixe",
            steps = listOf(
                "Tiens le support des deux mains, les pieds parallèles et les genoux souples.",
                "Monte légèrement les talons, sans basculer les chevilles vers l'extérieur.",
                "Redescends lentement les talons au sol ; répète seulement si cela reste confortable.",
                "Termine les deux pieds bien à plat avant de lâcher le support."
            ),
            caution = "Ne pratique pas sans appui en cas d'instabilité. Évite si le pied, la cheville ou le tendon est douloureux.", adaptation = "Fais le mouvement assis, pieds au sol, avec une très petite amplitude.",
            sanskrit = "Mobilité debout", level = YogaLevel.BEGINNER, category = YogaCategory.BALANCE,
            focus = listOf("Chevilles", "Équilibre"), bilateral = false,
        ),
        YogaPose(
            id = "star", title = "Étoile debout", subtitle = "Posture / Épaules",
            seconds = 30, equipment = "Sol non glissant",
            steps = listOf(
                "Écarte confortablement les pieds, sans aller jusqu'à un grand écart.",
                "Ouvre les bras sur les côtés, sous le niveau des épaules si nécessaire.",
                "Reste vertical, genoux souples et regard devant toi, en respirant naturellement.",
                "Baisse les bras avant de rapprocher progressivement les pieds."
            ),
            caution = "Ne maintiens pas les bras levés en cas de douleur ou de fourmillement.", adaptation = "Réduis l'écart des pieds et garde les mains sur les hanches.",
            sanskrit = "Utthita Tadasana adaptée", level = YogaLevel.BEGINNER, category = YogaCategory.STANDING,
            focus = listOf("Posture", "Épaules"), bilateral = false,
        ),
        YogaPose(
            id = "easy_seat", title = "Tailleur soutenu", subtitle = "Hanches / Posture",
            seconds = 60, equipment = "Coussin ferme ; couverture",
            steps = listOf(
                "Assieds-toi sur un coussin assez haut pour que le bassin reste confortable.",
                "Croise librement les jambes sans tirer les pieds vers toi ni pousser les genoux.",
                "Pose les mains sur les cuisses et garde le dos long sans te raidir.",
                "Décroise les jambes lentement et bouge les pieds avant de te relever."
            ),
            caution = "Ne force pas les genoux au sol ; change de position en cas d'engourdissement.", adaptation = "Assieds-toi sur une chaise ou place des coussins sous les cuisses.",
            sanskrit = "Sukhasana", level = YogaLevel.GENTLE, category = YogaCategory.SEATED,
            focus = listOf("Hanches", "Posture"), bilateral = false,
        ),
        YogaPose(
            id = "seated_side", title = "Inclinaison assise", subtitle = "Flancs / Épaules",
            seconds = 30, equipment = "Coussin ferme",
            steps = listOf(
                "Installe-toi en tailleur soutenu, ou les jambes dans une position libre.",
                "Lève le bras du côté indiqué et pose l'autre main près du bassin.",
                "Incline peu le buste à l'opposé du bras levé, en gardant les deux côtés du bassin soutenus.",
                "Reviens au centre avant de descendre le bras et de changer de côté."
            ),
            caution = "Ne porte pas tout le poids sur le poignet d'appui. Évite la compression du bas du dos.", adaptation = "Garde le bras bas ou réalise la variante sur chaise.",
            sanskrit = "Parsva Sukhasana", level = YogaLevel.BEGINNER, category = YogaCategory.SEATED,
            focus = listOf("Flancs", "Épaules"), bilateral = true,
        ),
        YogaPose(
            id = "seated_twist", title = "Rotation assise douce", subtitle = "Dos / Mobilité",
            seconds = 30, equipment = "Coussin ferme",
            steps = listOf(
                "Assieds-toi confortablement, bassin bien soutenu et dos long.",
                "Tourne doucement le buste vers le côté indiqué, sans chercher l'amplitude maximale.",
                "Les mains se posent sans tirer sur les jambes ; la tête suit seulement si c'est confortable.",
                "Reviens lentement au centre et marque une pause avant l'autre côté."
            ),
            caution = "Pas de torsion forcée, notamment en cas de problème de dos ou de grossesse : demande une adaptation.", adaptation = "Reste face à toi ou choisis une rotation très petite sur chaise.",
            sanskrit = "Parivrtta Sukhasana adaptée", level = YogaLevel.BEGINNER, category = YogaCategory.SEATED,
            focus = listOf("Dos", "Mobilité"), bilateral = true,
        ),
        YogaPose(
            id = "butterfly", title = "Papillon soutenu", subtitle = "Hanches / Jambes",
            seconds = 45, equipment = "Coussin et deux supports",
            steps = listOf(
                "Assieds-toi sur un coussin et rapproche les plantes des pieds sans tirer sur les chevilles.",
                "Laisse les genoux s'ouvrir seulement dans leur amplitude confortable.",
                "Soutiens les cuisses avec des coussins et garde le buste vertical.",
                "Rapproche les genoux avec l'aide des mains avant d'allonger les jambes."
            ),
            caution = "N'appuie jamais sur les genoux pour les abaisser. Évite si l'aine ou le genou est sensible.", adaptation = "Éloigne les pieds du bassin et augmente la hauteur des supports.",
            sanskrit = "Baddha Konasana adaptée", level = YogaLevel.BEGINNER, category = YogaCategory.SEATED,
            focus = listOf("Hanches", "Jambes"), bilateral = false,
        ),
        YogaPose(
            id = "seated_staff", title = "Bâton assis", subtitle = "Dos / Jambes",
            seconds = 30, equipment = "Coussin ferme ; mur facultatif",
            steps = listOf(
                "Assieds-toi sur un coussin avec les jambes devant toi et les genoux un peu pliés.",
                "Pose les mains à côté du bassin, sans te suspendre aux poignets.",
                "Allonge le dos doucement ; les pieds restent dans une position naturelle.",
                "Relâche puis plie davantage les genoux avant de changer de position."
            ),
            caution = "Ne cherche pas à plaquer les genoux au sol. Un dos qui s'arrondit fortement indique de réduire l'effort.", adaptation = "Adosse-toi au mur et soutiens les genoux avec une couverture roulée.",
            sanskrit = "Dandasana adaptée", level = YogaLevel.BEGINNER, category = YogaCategory.SEATED,
            focus = listOf("Dos", "Jambes"), bilateral = false,
        ),
        YogaPose(
            id = "seated_forward", title = "Flexion assise modérée", subtitle = "Jambes / Hanches",
            seconds = 30, equipment = "Coussin ; couverture sous les genoux",
            steps = listOf(
                "Assieds-toi sur un support, jambes devant et genoux franchement souples.",
                "Pose les mains sur les cuisses et incline un peu le buste depuis les hanches.",
                "Arrête-toi avant d'arrondir fortement le dos ; il n'est pas nécessaire de toucher les pieds.",
                "Redresse-toi avec les mains en appui puis replie les jambes confortablement."
            ),
            caution = "Évite toute traction derrière le genou, douleur lombaire ou fourmillement. Ne tire pas le buste avec une sangle.", adaptation = "Reste vertical dans le bâton assis, genoux soutenus.",
            sanskrit = "Paschimottanasana adaptée", level = YogaLevel.INTERMEDIATE, category = YogaCategory.SEATED,
            focus = listOf("Jambes", "Hanches"), bilateral = false,
        ),
        YogaPose(
            id = "single_leg_fold", title = "Flexion sur une jambe, adaptée", subtitle = "Jambes / Hanches",
            seconds = 30, equipment = "Coussin et support sous le genou",
            steps = listOf(
                "Allonge la jambe du côté indiqué, genou souple. Plie l'autre jambe sans forcer sa hanche.",
                "Soutiens le genou plié et oriente le buste vers la jambe allongée.",
                "Incline très peu le buste, mains sur les cuisses, sans tirer le pied.",
                "Redresse-toi puis rapproche les genoux avant d'inverser les jambes."
            ),
            caution = "La jambe pliée ne doit pas provoquer de douleur au genou. Évite une flexion qui arrondit fortement le dos.", adaptation = "Garde les deux jambes parallèles et les genoux pliés.",
            sanskrit = "Janu Sirsasana adaptée", level = YogaLevel.INTERMEDIATE, category = YogaCategory.SEATED,
            focus = listOf("Jambes", "Hanches"), bilateral = true,
        ),
        YogaPose(
            id = "wide_seat", title = "Assise jambes ouvertes", subtitle = "Hanches / Jambes",
            seconds = 30, equipment = "Coussin haut et couvertures",
            steps = listOf(
                "Assieds-toi sur un support ferme, puis ouvre modérément les jambes.",
                "Conserve les genoux souples et soutiens-les si nécessaire.",
                "Reste vertical, mains derrière le bassin pour un appui léger, sans avancer le buste.",
                "Rapproche les jambes une à une avec les mains et marque une pause."
            ),
            caution = "Ne force pas l'ouverture des hanches et ne rebondis pas. Arrête en cas de douleur à l'aine.", adaptation = "Réduis largement l'écart ou reprends une assise sur chaise.",
            sanskrit = "Upavistha Konasana adaptée", level = YogaLevel.INTERMEDIATE, category = YogaCategory.SEATED,
            focus = listOf("Hanches", "Jambes"), bilateral = false,
        ),
        YogaPose(
            id = "chair_cat", title = "Chat-vache sur chaise", subtitle = "Dos / Posture",
            seconds = 60, equipment = "Chaise stable sans roulettes",
            steps = listOf(
                "Assieds-toi les pieds soutenus, les mains sur les cuisses.",
                "Arrondis légèrement le haut du dos, sans laisser tomber la tête.",
                "Reviens au centre puis ouvre doucement la poitrine, sans grande cambrure.",
                "Alterne lentement à ton rythme puis retrouve une position neutre."
            ),
            caution = "Ne force pas la nuque ni le bas du dos ; le mouvement peut rester presque imperceptible.", adaptation = "Reste adossé et observe simplement la respiration naturelle.",
            sanskrit = "Mobilité assise", level = YogaLevel.GENTLE, category = YogaCategory.MOBILITY,
            focus = listOf("Dos", "Posture"), bilateral = false,
        ),
        YogaPose(
            id = "chair_twist", title = "Rotation sur chaise", subtitle = "Dos / Mobilité",
            seconds = 30, equipment = "Chaise stable sans roulettes",
            steps = listOf(
                "Pose les deux pieds au sol et garde le bassin face à l'avant.",
                "Tourne un peu le buste vers le côté indiqué, les mains posées librement.",
                "Ne tire pas sur le dossier ; le regard peut rester presque devant.",
                "Reviens au centre avant de laisser l'autre côté prendre sa place."
            ),
            caution = "Évite les torsions forcées. Fais adapter l'exercice en cas de grossesse, de blessure ou de trouble du dos.", adaptation = "Tourne seulement de quelques degrés, ou reste dans l'assise confortable.",
            sanskrit = "Rotation assise adaptée", level = YogaLevel.GENTLE, category = YogaCategory.SEATED,
            focus = listOf("Dos", "Mobilité"), bilateral = true,
        ),
        YogaPose(
            id = "chair_side", title = "Inclinaison sur chaise", subtitle = "Flancs / Épaules",
            seconds = 30, equipment = "Chaise stable sans roulettes",
            steps = listOf(
                "Assieds-toi, pieds soutenus et bassin stable sur l'assise.",
                "Lève le bras du côté indiqué seulement si l'épaule le permet.",
                "Incline très peu le buste à l'opposé de ce bras, sans décoller le bassin.",
                "Reviens au centre puis baisse le bras en gardant une respiration libre."
            ),
            caution = "Ne te suspends pas au bord de la chaise. Arrête si le bas du dos ou l'épaule se pince.", adaptation = "Pose les deux mains sur les cuisses et réduis l'inclinaison.",
            sanskrit = "Étirement latéral assis", level = YogaLevel.GENTLE, category = YogaCategory.SEATED,
            focus = listOf("Flancs", "Épaules"), bilateral = true,
        ),
        YogaPose(
            id = "chair_ankle", title = "Chevilles en mouvement", subtitle = "Chevilles / Jambes",
            seconds = 45, equipment = "Chaise stable",
            steps = listOf(
                "Assieds-toi confortablement et garde le pied opposé au côté indiqué au sol.",
                "Avance légèrement le pied du côté indiqué, talon soutenu au sol.",
                "Relève puis abaisse doucement les orteils, sans forcer l'amplitude.",
                "Repose tout le pied et attends un instant avant de changer de jambe."
            ),
            caution = "Évite en cas de douleur aiguë ou de consigne d'immobilisation de la cheville.", adaptation = "Réalise un mouvement très petit en gardant tout le pied près du sol.",
            sanskrit = "Mobilité sur chaise", level = YogaLevel.GENTLE, category = YogaCategory.MOBILITY,
            focus = listOf("Chevilles", "Jambes"), bilateral = true,
        ),
        YogaPose(
            id = "tabletop", title = "Table à quatre pattes", subtitle = "Posture / Appuis",
            seconds = 30, equipment = "Tapis et couverture pour les genoux",
            steps = listOf(
                "Place les mains sous les épaules et les genoux sous les hanches sur un tapis.",
                "Répartis l'appui sur les mains, sans bloquer les coudes.",
                "Garde un dos neutre, le regard vers le tapis et la respiration naturelle.",
                "Recule doucement le bassin puis reviens assis avec l'aide des mains."
            ),
            caution = "Évite les appuis au sol si les poignets ou les genoux sont douloureux.", adaptation = "Choisis le chat-vache sur chaise, ou protège les genoux avec une couverture.",
            sanskrit = "Bharmanasana", level = YogaLevel.BEGINNER, category = YogaCategory.FLOOR,
            focus = listOf("Posture", "Appuis"), bilateral = false,
        ),
        YogaPose(
            id = "bird_dog", title = "Équilibre à quatre pattes", subtitle = "Équilibre / Dos",
            seconds = 20, equipment = "Tapis et couverture",
            steps = listOf(
                "Pars d'une table stable ; le côté indiqué désigne le bras qui s'allonge.",
                "Fais glisser la jambe opposée vers l'arrière, orteils au sol pour commencer.",
                "Allonge le bras indiqué devant toi si le bassin reste stable, sans creuser le dos.",
                "Repose la main puis ramène le genou sous le bassin avant d'inverser."
            ),
            caution = "Ne recherche pas la hauteur de la jambe. Arrête si les poignets, les genoux ou le bas du dos sont sensibles.", adaptation = "Garde les deux mains au sol et ne fais glisser qu'une jambe, sans la lever.",
            sanskrit = "Dandayamana Bharmanasana adaptée", level = YogaLevel.INTERMEDIATE, category = YogaCategory.FLOOR,
            focus = listOf("Équilibre", "Dos"), bilateral = true,
        ),
        YogaPose(
            id = "puppy_supported", title = "Chiot avec support", subtitle = "Épaules / Dos",
            seconds = 30, equipment = "Tapis et coussin haut",
            steps = listOf(
                "Depuis la table, avance les mains de quelques centimètres seulement.",
                "Place un coussin haut sous la poitrine ou le front pour éviter de t'affaisser.",
                "Garde le bassin proche de la verticale des genoux et les épaules dans une amplitude douce.",
                "Ramène les mains sous les épaules avant de te redresser."
            ),
            caution = "Aucune pression sur la gorge ni grande cambrure. Évite si les épaules ou les genoux sont douloureux.", adaptation = "Pratique plutôt le chien au mur, avec le buste peu incliné.",
            sanskrit = "Uttana Shishosana adaptée", level = YogaLevel.INTERMEDIATE, category = YogaCategory.FLOOR,
            focus = listOf("Épaules", "Dos"), bilateral = false,
        ),
        YogaPose(
            id = "down_dog_wall", title = "Chien au mur", subtitle = "Épaules / Dos",
            seconds = 45, equipment = "Mur dégagé ; sol antiglisse",
            steps = listOf(
                "Place les mains contre le mur à hauteur de poitrine, les doigts confortablement ouverts.",
                "Recule un peu les pieds et plie les genoux pour incliner le buste.",
                "Allonge doucement le dos, sans enfoncer la poitrine ni verrouiller les coudes.",
                "Rapproche-toi du mur avant de baisser les bras et de te redresser."
            ),
            caution = "Les mains ne doivent pas glisser. Reste plus vertical en cas de vertige ou de gêne aux épaules.", adaptation = "Garde les mains plus hautes et le buste presque debout.",
            sanskrit = "Adho Mukha Svanasana au mur", level = YogaLevel.BEGINNER, category = YogaCategory.STANDING,
            focus = listOf("Épaules", "Dos"), bilateral = false,
        ),
        YogaPose(
            id = "low_lunge", title = "Fente basse soutenue", subtitle = "Hanches / Jambes",
            seconds = 30, equipment = "Tapis ; couverture ; chaise lourde",
            steps = listOf(
                "Mets le pied du côté indiqué devant, genou plié ; pose le genou arrière sur une couverture.",
                "Pose les mains sur le dossier d'une chaise stable pour garder le buste soutenu.",
                "Laisse le bassin avancer très peu, en gardant le genou avant dirigé vers les orteils.",
                "Ramène le bassin puis le pied avant avec l'aide du support."
            ),
            caution = "Cette posture charge le genou arrière. Évite en cas de douleur et ne creuse pas le bas du dos.", adaptation = "Pratique un pas court debout, les deux mains sur un support.",
            sanskrit = "Anjaneyasana adaptée", level = YogaLevel.INTERMEDIATE, category = YogaCategory.FLOOR,
            focus = listOf("Hanches", "Jambes"), bilateral = true,
        ),
        YogaPose(
            id = "half_split", title = "Demi-grand écart adapté", subtitle = "Jambes / Hanches",
            seconds = 25, equipment = "Couverture et deux supports hauts",
            steps = listOf(
                "Depuis une fente basse, garde la jambe du côté indiqué devant et les mains soutenues.",
                "Recule un peu le bassin pour diminuer la flexion du genou avant, sans le verrouiller.",
                "Garde le dos long et le buste haut ; il n'est pas nécessaire de descendre vers la jambe.",
                "Replie doucement le genou avant avant de quitter l'appui au sol."
            ),
            caution = "Évite toute traction derrière le genou ou sensation nerveuse. Ne recherche pas un grand écart.", adaptation = "Conserve le genou avant très plié, ou remplace par une demi-flexion debout avec appui.",
            sanskrit = "Ardha Hanumanasana adaptée", level = YogaLevel.INTERMEDIATE, category = YogaCategory.FLOOR,
            focus = listOf("Jambes", "Hanches"), bilateral = true,
        ),
        YogaPose(
            id = "sphinx", title = "Sphinx doux", subtitle = "Dos / Poitrine",
            seconds = 30, equipment = "Tapis ; couverture fine",
            steps = listOf(
                "Allonge-toi sur le ventre uniquement si cette position est confortable.",
                "Pose les avant-bras au sol, coudes légèrement devant les épaules.",
                "Laisse le haut du buste se soulever peu, ventre soutenu et nuque longue.",
                "Redescends sur les mains empilées puis roule lentement sur le côté."
            ),
            caution = "Évite la compression lombaire et la position sur le ventre pendant la grossesse ; demande une adaptation.", adaptation = "Avance les coudes davantage, ou remplace par le repos sur le côté.",
            sanskrit = "Salamba Bhujangasana", level = YogaLevel.BEGINNER, category = YogaCategory.FLOOR,
            focus = listOf("Dos", "Poitrine"), bilateral = false,
        ),
        YogaPose(
            id = "low_cobra", title = "Petit cobra", subtitle = "Dos / Poitrine",
            seconds = 20, equipment = "Tapis",
            steps = listOf(
                "Allonge-toi sur le ventre, mains près des côtes et coudes dirigés vers l'arrière.",
                "Garde le bassin au sol et soulève très peu la poitrine, sans pousser fort sur les mains.",
                "Regarde le tapis devant toi, nuque longue, en respirant naturellement.",
                "Redépose doucement le buste et repose la tête sur les mains."
            ),
            caution = "Pas d'extension forcée. Évite en cas de douleur de dos, de poignet ou de gêne à plat ventre ; grossesse : fais adapter.", adaptation = "Garde le front posé ou choisis le sphinx très bas.",
            sanskrit = "Bhujangasana basse", level = YogaLevel.INTERMEDIATE, category = YogaCategory.FLOOR,
            focus = listOf("Dos", "Poitrine"), bilateral = false,
        ),
        YogaPose(
            id = "bridge", title = "Petit pont", subtitle = "Hanches / Jambes",
            seconds = 25, equipment = "Tapis antiglisse",
            steps = listOf(
                "Allonge-toi sur le dos, genoux pliés et pieds au sol à une largeur confortable.",
                "Appuie doucement dans les pieds et soulève le bassin de quelques centimètres.",
                "Garde les genoux dans la direction des pieds, les bras au sol et la tête immobile.",
                "Redescends le bassin lentement puis repose-toi, sans tourner la tête pendant l'effort."
            ),
            caution = "Ne prends pas appui sur la nuque. Arrête en cas de douleur ou de crampe ; fais adapter si rester sur le dos est déconseillé.", adaptation = "Laisse le bassin au sol et choisis le repos, genoux pliés.",
            sanskrit = "Setu Bandha Sarvangasana adaptée", level = YogaLevel.INTERMEDIATE, category = YogaCategory.FLOOR,
            focus = listOf("Hanches", "Jambes"), bilateral = false,
        ),
        YogaPose(
            id = "wind_relief", title = "Un genou vers le buste", subtitle = "Hanches / Dos",
            seconds = 30, equipment = "Tapis et coussin pour la tête",
            steps = listOf(
                "Allonge-toi sur le dos, les deux genoux pliés et les pieds au sol.",
                "Rapproche un peu le genou du côté indiqué et tiens l'arrière de la cuisse.",
                "Garde la tête soutenue ; ne comprime pas le ventre et ne tire pas sur la rotule.",
                "Repose le pied au sol lentement avant de changer de côté."
            ),
            caution = "Évite toute compression du ventre ou douleur de hanche ; grossesse et position sur le dos : demande une adaptation.", adaptation = "Garde les deux pieds au sol et laisse les mains sur le ventre ou le long du corps.",
            sanskrit = "Apanasana unilatérale", level = YogaLevel.GENTLE, category = YogaCategory.FLOOR,
            focus = listOf("Hanches", "Dos"), bilateral = true,
        ),
        YogaPose(
            id = "supine_twist", title = "Rotation allongée soutenue", subtitle = "Dos / Mobilité",
            seconds = 30, equipment = "Tapis et coussins hauts",
            steps = listOf(
                "Allonge-toi sur le dos, genoux pliés, puis place des coussins du côté indiqué.",
                "Laisse les genoux s'incliner légèrement vers ces coussins ; aucune descente au sol n'est exigée.",
                "Garde les bras confortables et la tête neutre ; ne force pas les épaules à rester plaquées.",
                "Ramène les genoux au centre avec l'aide des mains si nécessaire."
            ),
            caution = "Ne force pas une torsion du dos. Fais adapter en cas de grossesse, de blessure ou d'inconfort allongé.", adaptation = "Fais seulement basculer les genoux de quelques centimètres.",
            sanskrit = "Supta Matsyendrasana adaptée", level = YogaLevel.BEGINNER, category = YogaCategory.FLOOR,
            focus = listOf("Dos", "Mobilité"), bilateral = true,
        ),
        YogaPose(
            id = "reclined_butterfly", title = "Papillon allongé soutenu", subtitle = "Hanches / Détente",
            seconds = 60, equipment = "Tapis et deux coussins sous les cuisses",
            steps = listOf(
                "Allonge-toi confortablement, genoux pliés et tête soutenue.",
                "Rapproche les plantes des pieds sans les presser, puis laisse les cuisses reposer sur les coussins.",
                "Garde une ouverture modérée ; respire naturellement sans chercher d'étirement intense.",
                "Rapproche les genoux avec les mains, puis roule sur le côté pour te relever."
            ),
            caution = "Ne laisse pas les genoux suspendus si cela tire. Fais adapter si la position sur le dos est inconfortable ou déconseillée.", adaptation = "Garde les pieds à plat et les genoux orientés vers le plafond.",
            sanskrit = "Supta Baddha Konasana adaptée", level = YogaLevel.GENTLE, category = YogaCategory.REST,
            focus = listOf("Hanches", "Détente"), bilateral = false,
        ),
        YogaPose(
            id = "figure_four", title = "Cheville sur cuisse, au sol", subtitle = "Hanches / Jambes",
            seconds = 30, equipment = "Tapis et coussin pour la tête",
            steps = listOf(
                "Allonge-toi sur le dos, genoux pliés et pieds au sol.",
                "Pose la cheville du côté indiqué sur la cuisse opposée, au-dessus du genou.",
                "Laisse le pied d'appui au sol et ne pousse pas sur le genou ouvert.",
                "Décroise doucement et repose le pied avant de passer à l'autre côté."
            ),
            caution = "Évite si cela tord ou comprime le genou. Aucune traction vers la poitrine n'est nécessaire.", adaptation = "Garde les deux pieds au sol ou demande une adaptation sur chaise.",
            sanskrit = "Sucirandhrasana adaptée", level = YogaLevel.BEGINNER, category = YogaCategory.FLOOR,
            focus = listOf("Hanches", "Jambes"), bilateral = true,
        ),
        YogaPose(
            id = "hamstring_strap", title = "Jambe soutenue par une sangle", subtitle = "Jambes / Hanches",
            seconds = 30, equipment = "Tapis et sangle non élastique",
            steps = listOf(
                "Allonge-toi sur le dos, l'autre pied au sol ; place la sangle autour du pied du côté indiqué.",
                "Lève modérément cette jambe en gardant son genou plié.",
                "Tiens la sangle sans tirer la jambe vers la tête et sans lever les épaules.",
                "Replie davantage le genou puis repose le pied avant de retirer la sangle."
            ),
            caution = "Arrête en cas de fourmillement, traction derrière le genou ou douleur. Ne bloque pas le genou.", adaptation = "Tiens l'arrière de la cuisse avec les mains et garde la jambe plus basse.",
            sanskrit = "Supta Padangusthasana adaptée", level = YogaLevel.BEGINNER, category = YogaCategory.FLOOR,
            focus = listOf("Jambes", "Hanches"), bilateral = true,
        ),
        YogaPose(
            id = "happy_baby", title = "Bébé heureux adapté", subtitle = "Hanches / Détente",
            seconds = 30, equipment = "Tapis et coussin",
            steps = listOf(
                "Allonge-toi sur le dos et rapproche modérément les genoux, sans comprimer le ventre.",
                "Tiens l'arrière des cuisses plutôt que les pieds ; laisse les jambes ouvertes sans forcer.",
                "Garde la tête soutenue et le bassin au sol, sans tirer les genoux vers les aisselles.",
                "Relâche une jambe puis l'autre et repose les pieds au sol."
            ),
            caution = "Évite la compression abdominale et toute douleur de hanche ou de dos ; grossesse : demande une adaptation.", adaptation = "Rapproche une seule cuisse à la fois, ou garde les pieds posés.",
            sanskrit = "Ananda Balasana adaptée", level = YogaLevel.BEGINNER, category = YogaCategory.FLOOR,
            focus = listOf("Hanches", "Détente"), bilateral = false,
        ),
        YogaPose(
            id = "savasana", title = "Repos allongé soutenu", subtitle = "Détente / Repos",
            seconds = 120, equipment = "Tapis ; coussin sous les genoux",
            steps = listOf(
                "Allonge-toi seulement si tu peux t'installer et te relever confortablement.",
                "Soutiens les genoux avec un coussin et ajuste l'appui de la tête.",
                "Laisse les bras reposer, les yeux ouverts ou fermés selon ton confort, sans modifier le souffle.",
                "Bouge doucement les mains et les pieds, roule sur le côté puis redresse-toi avec les bras."
            ),
            caution = "Ne reste pas sur le dos si cela te gêne. Pendant la grossesse, demande un positionnement adapté.", adaptation = "Choisis le repos sur le côté ou une assise avec dossier.",
            sanskrit = "Savasana adaptée", level = YogaLevel.GENTLE, category = YogaCategory.REST,
            focus = listOf("Détente", "Repos"), bilateral = false,
        ),
    )
}

