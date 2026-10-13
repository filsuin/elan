# Élan 0.3.2 - yoga, respiration et journal

Application Android native en français (Kotlin, Jetpack Compose, Material 3). Cette livraison enrichit le projet `elan.zip` fourni : **48 fiches au lieu de 6, 10 parcours, séances personnalisables, respiration, suivi et rappel quotidien facultatif**. Le journal, les routines et les thèmes existants sont conservés.

**Statut : version 0.3.3 en préparation.** Les notifications d'annonces utilisent Firebase Cloud Messaging uniquement après activation volontaire dans les réglages ; le serveur doit être configuré avec les identifiants Firebase avant l'envoi des pushes.

## Ouvrir et déployer

Guide complet : **[GUIDE_DEPLOIEMENT.md](GUIDE_DEPLOIEMENT.md)**. Version lisible dans un navigateur : **[GUIDE_DEPLOIEMENT.html](GUIDE_DEPLOIEMENT.html)**.

Sur Windows : extraire le projet, lancer `PREPARER_WINDOWS.cmd`, ouvrir le dossier contenant `settings.gradle.kts` dans Android Studio, installer le SDK 36 / Build-Tools 35.0.0, choisir un JDK 17 ou 21, synchroniser, puis lancer :

```powershell
.\gradlew.bat :app:testDebugUnitTest :app:lintDebug :app:assembleDebug
```

L'APK de test sera créé **après réussite** dans `app\build\outputs\apk\debug\app-debug.apk`. Les scripts ne remplacent ni l'installation d'Android Studio/SDK, ni la signature de diffusion.

## Les cinq espaces

| Espace | Contenu |
| --- | --- |
| Accueil | Conseils locaux, routines personnelles, petite victoire, objectif de pratique et raccourcis. |
| Yoga | 48 fiches de postures, variantes et mobilité ; schémas ; recherche ; niveaux ; catégories ; favoris ; 10 parcours ; éditeur de séances. |
| Respirer | Quatre rythmes sans apnée, guidage visuel et pauses silencieuses minutées. |
| Humeur | Saisie et historique des ressentis, notes, thèmes et victoires, comme dans le projet d'origine. |
| Suivi | Temps effectivement pratiqué, objectif hebdomadaire, histogramme, jours actifs, historique et filtres. |

Le lecteur de pratique propose des transitions, les côtés gauche/droit, pause/reprise, passage d'étape et enregistrement d'un parcours complet ou partiel. Sons et annonces vocales sont facultatifs ; une voix française déclarée hors ligne par le moteur Android doit être disponible pour parler.

## Données, confidentialité et limites

L'app ne crée aucun compte, ne contient aucune publicité et ne transmet pas le journal. Le stockage local reste dans l'espace privé Android de l'application. La permission Internet sert à vérifier les métadonnées publiques de version et, seulement si l'utilisateur active **Annonces de nouvelles versions**, à s'abonner au sujet Firebase Cloud Messaging `elan-app-updates`. Firebase peut alors traiter les identifiants techniques nécessaires à l'abonnement et à la livraison des notifications ; ni le journal, ni les sauvegardes, ni le prénom ne sont envoyés. La désactivation retire l'abonnement au sujet. Les notifications Android peuvent également être bloquées par le système.

Le stockage privé `elan-journal-v1.json` conserve son nom pour reprendre les données existantes. Le codec lit les sauvegardes v1 et v2 ; les écritures et exports utilisent v2. Une restauration remplace les données après confirmation et désactive le rappel. Le JSON exporté est en clair. Aucun mot de passe ou chiffrement applicatif n'est ajouté.

Avant de partager une sauvegarde, vérifie l'emplacement choisi : un fournisseur cloud, un dossier partagé ou une messagerie peut synchroniser et exposer le fichier. Élan ne contrôle ni la conservation ni les accès de ces fournisseurs. Pour supprimer les données locales, utilise **Effacer toutes mes données** dans les réglages ; les fichiers déjà exportés doivent être supprimés séparément.

Les séances et pratiques doivent être enregistrées explicitement. Un brouillon ou un lecteur non enregistré survit à la rotation via le ViewModel, mais pas à la destruction du processus. Le lecteur se met en pause en arrière-plan ; il n'est pas un lecteur audio en service de fond.

Le rappel est désactivé par défaut et son horaire est indicatif. Les schémas sont des repères simplifiés, pas des démonstrations anatomiques validées. Faire relire les contenus par une personne qualifiée avant diffusion.

## Documentation et tests

- [Nouveautés](CHANGELOG.md), [utilisation](docs/UTILISATION.md), [validation](docs/VALIDATION.md).
- [Sources techniques et précautions](docs/SOURCES.md), [liste des fichiers modifiés](FICHIERS_MODIFIES.md).
- Tests sans SDK : `sh tools/test-domain.sh` avec Kotlin CLI 1.9+ et JDK 17+.
- Journal des 200 contrôles : `docs/domain-test-results.txt`.

Version : `versionCode = 6`, `versionName = "0.3.3"`, identifiant conservé : `fr.elan.app`.

## Publier le dépôt

Avant de rendre ce dépôt public :

1. Restreindre la clé API Firebase dans Google Cloud au package `fr.elan.app`, aux certificats de signature utilisés et aux seules API nécessaires.
2. Vérifier les règles Firebase et activer les alertes de quota/facturation.
3. Ne jamais ajouter de compte de service, clé privée, keystore ou fichier `.env` réel. Les secrets du backend doivent rester dans l'environnement de déploiement.
4. Remplacer toute clé Firebase client déjà exposée avec des restrictions insuffisantes et mettre à jour `app/google-services.json`.
5. Relire les obligations d'information et de consentement applicables avant de distribuer l'application.
