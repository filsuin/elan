# Historique des modifications

## 0.3.1 - 9 octobre 2026

### Mises à jour de l'application

L'application vérifie `https://elan.maxremonnay.fr/api/v1/app-update` à l'ouverture/reprise et une fois par jour en arrière-plan. Si le backend annonce un `versionCode` supérieur, une notification ouvre `https://elan.maxremonnay.fr/app/download`, où l'APK est servi par le backend. Android doit autoriser les notifications. La vérification ne transmet aucune donnée du journal.

Version Android : `versionCode = 4`, `versionName = 0.3.1`. Pour annoncer une version suivante, remplacer l'APK sous `backend/updates/elan.apk` et mettre à jour `APP_UPDATE_VERSION_CODE` et `APP_UPDATE_VERSION_NAME` dans la configuration du serveur.

## 0.3.0 - 9 octobre 2026

### Yoga

Passage de 6 à 48 fiches : 42 ajouts (postures, variantes avec appui et mouvements de mobilité). Chaque fiche expose quatre étapes, matériel, précautions, adaptation, niveau, catégorie et zones associées. Les six identifiants d'origine sont conservés. Schémas vectoriels propres à chaque fiche, sans image distante.

Recherche par nom français, nom sanskrit, zone ou matériel ; normalisation accents/casse ; filtres de niveau/catégorie et favoris persistants. Dix parcours préconfigurés. Séances personnalisées : création, copie, ordre des étapes, durées et suppression. Jusqu'à 30 séances de 40 étapes ; les postures bilatérales ajoutent deux côtés explicites.

### Pratique, respiration et suivi

Lecteur commun à horloge monotone, installation, transitions réglables, pause/reprise, passage d'étape, enregistrement complet ou partiel. Sons/annonces vocales facultatifs. Écran maintenu allumé pendant la lecture. Pause en arrière-plan, au verrouillage et après rotation.

Quatre rythmes respiratoires sans rétention : 3/3, 4/4, 4/6 et 5/5 secondes. Choix 1/3/5/10 minutes, ajusté aux cycles complets avec durée réelle affichée. Pauses silencieuses de 2/5/10/15 minutes.

Nouvel espace Suivi : historique 7/30/90 jours/tout, type de pratique, temps effectif, jours actifs, série de jours, objectif hebdomadaire et histogramme lundi-dimanche. Aucune attribution de temps aux transitions, pauses ou parties sautées. Enregistrement idempotent, suppression par entrée.

### Plateforme et données

Rappel quotidien facultatif à heure locale indicative, permission contextuelle Android 13+, reprogrammation au redémarrage/changement d'heure, reprise de programmation après réouverture. Aucun besoin d'alarme exacte ni service de premier plan.

Schéma JSON v2 avec lecture v1 et valeurs par défaut ; format borné à 10 Mo ; validations explicites ; import avec confirmation et rappel désactivé après restauration. Nom du fichier privé et applicationId inchangés.

Rétablissement des fichiers absents du ZIP source : catalogue Gradle, propriétés du wrapper, lanceur de préparation Windows. Versions 3 / 0.3.0. Guide de déploiement, tests complémentaires et rapport de validation actuel. Le backend reste distinct et non connecté au client.

### Validation

200 contrôles métier exécutés ; analyse syntaxique Kotlin et contrôles structurels effectués. Tests Gradle/JSON et tests Compose fournis mais non exécutés ici. Aucun APK ni résultat de test Android 0.3.0 n'est revendiqué.

