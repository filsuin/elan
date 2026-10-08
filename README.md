# Élan — routines, confiance en soi et humeur

Une base native Android en français, construite avec **Kotlin, Jetpack Compose et Material 3 / Material You**. Sans compte, sans serveur, sans publicité et sans clé API. L'application fonctionne hors ligne après installation.

Le projet produit un APK de développement dans `app/build/outputs/apk/debug/app-debug.apk`. La version 0.2.0 a été compilée, analysée par Android Lint et testée sur un Pixel 8 sous Android 17 le 8 octobre 2026. Voir `docs/VALIDATION.md` pour le détail et les vérifications qui restent à effectuer avant une diffusion publique.

## Démarrage sur Windows

1. Décompresser le ZIP dans un dossier local, par exemple `C:\Projets\ElanAndroid`.
2. **Double-cliquer sur `PREPARER_WINDOWS.cmd`.** Ce script télécharge le petit binaire officiel Gradle Wrapper et vérifie son SHA-256. Il n'est pas fourni dans le ZIP. Une connexion Internet est nécessaire.
3. Dans Android Studio : **Open**, puis sélectionner le dossier **ElanAndroid**, celui qui contient `settings.gradle.kts`.
4. Dans le SDK Manager, installer **Android SDK Platform 36** et **Build-Tools 35.0.0** si Android Studio les demande. Accepter leurs licences dans Android Studio.
5. Dans les paramètres Gradle, choisir un **JDK 17 ou 21**. Android Studio avec prise en charge d'AGP 8.13 est nécessaire. Synchroniser le projet ; les dépendances sont téléchargées au premier lancement.
6. Sélectionner un émulateur ou un téléphone Android **8.0 / API 26 minimum**, puis cliquer sur **Run**.

Ne pas ouvrir seulement le sous-dossier `app`. Ne pas recopier `local.properties` depuis un autre ordinateur : Android Studio doit utiliser ton propre chemin de SDK.

Pour créer ensuite un APK de test, après la synchronisation initiale :

```powershell
.\gradlew.bat :app:testDebugUnitTest :app:lintDebug :app:assembleDebug
```

Ou double-cliquer sur `CONSTRUIRE_APK_WINDOWS.cmd`.

Fichier attendu **après une compilation réussie** :

```
app\build\outputs\apk\debug\app-debug.apk
```

Il s'agit d'un APK de développement, pas d'une version signée pour publication. Pour installer depuis Android Studio, activer les options développeur et le débogage USB sur le téléphone, puis autoriser cet ordinateur.

## macOS / Linux

```bash
sh tools/prepare-wrapper.sh
chmod +x gradlew
./gradlew :app:testDebugUnitTest :app:lintDebug :app:assembleDebug
```

Le SDK doit déjà être configuré via Android Studio, `ANDROID_HOME`, ou un fichier local `local.properties` contenant `sdk.dir=...`. Le premier build requiert Internet. Les scripts n'installent pas le SDK ni le JDK.

## Ce que contient l'application

| Espace | Fonctionnalités |
| --- | --- |
| Aujourd'hui | Conseil du jour, 14 conseils en rotation, 4 routines initiales, ajout/modification/suppression de routines, cases quotidiennes, progression, 14 invitations pour la confiance en soi, saisie d'une petite victoire. |
| Yoga | 6 fiches de yoga doux/repos avec schémas vectoriels, matériel, étapes, précautions et minuteur facultatif avec pause/remise à zéro. |
| Humeur | 5 ressentis, note facultative, thèmes, une saisie modifiable par date, aperçu des 7 derniers jours, historique 7 jours/30 jours/tout, modification et suppression d'une humeur. Les petites victoires sont aussi affichées. |
| Mon espace | Prénom facultatif, thème système/clair/sombre, couleurs dynamiques, export JSON, restauration avec validation et confirmation, effacement des données. |

**Material You :** les couleurs du système sont utilisées sur Android 12 et plus lorsque l'option est activée. Sur Android 8 à 11, ou lorsque l'option est désactivée, une palette sauge claire/sombre prend le relais. Les cartes, boutons, champs, indicateurs et navigation utilisent Material 3.

Les champs de texte ont un bouton d'enregistrement : **le simple fait de saisir ne persiste pas le texte dans le journal**. Les brouillons peuvent être conservés temporairement par l'interface, mais seul un enregistrement confirmé doit être considéré comme conservé.

## Comportements importants

- La date de référence est celle du téléphone. Elle est rafraîchie au retour dans l'app et toutes les 30 secondes. Les cases sont propres à chaque date ; l'historique n'est pas effacé au changement de jour.
- Les conseils tournent de façon déterministe tous les 14 jours. Ce ne sont pas des conseils générés par IA, ni des notifications.
- Une nouvelle sauvegarde d'humeur pour la même date remplace cette humeur, sans créer de doublon et sans effacer les cases ou la petite victoire.
- Supprimer une humeur ne supprime pas la petite victoire du même jour. La suppression d'une routine retire aussi ses références dans les jours passés.
- Le minuteur utilise une horloge monotone et se met en pause lorsque l'app passe en arrière-plan. La rotation conserve son état via le ViewModel mais le met en pause. Un redémarrage du processus remet la durée à zéro d'avancement. Il n'y a ni alarme, ni notification, ni service de fond.
- Le graphique ne remplace jamais une journée sans saisie par une mauvaise humeur. Il n'y a ni diagnostic ni score global de santé mentale.

## Données et sauvegarde

Stockage : fichier JSON versionné via **Jetpack DataStore**, dans le dossier privé Android de l'application. Les écritures sont transactionnelles. Aucun mot de passe propre à Élan et aucun chiffrement applicatif ne sont ajoutés. L'app n'a pas de permission Internet.

La sauvegarde cloud Android et le transfert système sont configurés pour exclure les données. **Désinstaller l'app ou effacer ses données supprime le journal local.** Exporter d'abord pour le conserver.

Les sauvegardes exportées contiennent les informations en clair : les conserver dans un emplacement de confiance. Choisir un fournisseur cloud dans le sélecteur de documents peut entraîner une synchronisation par ce fournisseur. L'effacement dans l'app ne supprime pas les copies exportées.

La restauration accepte uniquement le format Élan v1, au maximum 10 Mo. Le fichier est validé **avant** confirmation et remplacement. Un fichier invalide n'est pas appliqué. Une corruption du stockage local produit une erreur explicite, sans réinitialisation silencieuse.

`examples/journal-demo.json` contient des **données fictives**, du 26 septembre au 2 octobre 2026, pour essayer l'historique. Importer ce fichier remplace le journal actuel : utiliser une installation de test ou exporter d'abord.

## Modifier le projet

| Modification | Fichier principal |
| --- | --- |
| Conseils et invitations pour la confiance | `app/src/main/java/fr/elan/app/content/DailyContent.kt` |
| Positions, étapes, durées et précautions | `content/YogaCatalog.kt` |
| Routines de départ et modèles | `domain/Models.kt` |
| Règles de mise à jour du journal | `domain/JournalActions.kt` |
| Couleurs de repli, typographie, formes | `ui/theme/Theme.kt` |
| Écrans | `ui/screens/` |
| Schémas de positions | `ui/components/PoseIllustration.kt` |
| Stockage et format exporté | `data/ElanRepository.kt` et `data/SnapshotJson.kt` |
| Versions des dépendances | `gradle/libs.versions.toml` |
| Nom affiché | `app/src/main/res/values/strings.xml` |

Les chemins abrégés du tableau se trouvent sous `app/src/main/java/fr/elan/app/`. Le texte de l'interface est volontairement en français dans les composants pour cette base ; externaliser dans `strings.xml` avant d'ajouter d'autres langues.

Identifiant de départ : `fr.elan.app`. Le remplacer par un identifiant que tu contrôles avant publication (namespace, applicationId, packages et manifeste si nécessaire). Le nom Élan est un nom de travail, sans vérification de disponibilité commerciale.

## Architecture et versions verrouillées

Interface Compose → ViewModel / StateFlow → Repository DataStore → JSON v1.
Les transformations du journal et le minuteur sont en Kotlin pur, indépendants d'Android.

| Outil | Version |
| --- | --- |
| Android Gradle Plugin | 8.13.2 |
| Gradle | 8.13 |
| Kotlin / plugin Compose | 2.2.21 |
| Compose BOM | 2025.04.01 |
| Compile SDK / Target SDK / Min SDK | 36 / 36 / 26 |
| Activity / Lifecycle / DataStore | 1.11.0 / 2.9.4 / 1.1.7 |

Ces versions sont fixées pour la base ; ce n'est pas une promesse d'utiliser les toutes dernières versions. Aucun composant en version alpha n'est demandé. Mettre à jour et retester l'ensemble avant publication.

## Tests et prochaines étapes

- Tests locaux Android/JVM : `gradlew.bat :app:testDebugUnitTest`.
- Lint : `gradlew.bat :app:lintDebug`.
- Tests d'interface sur appareil/émulateur : `gradlew.bat :app:connectedDebugAndroidTest`.
- Contrôles sans SDK, avec Kotlin CLI : `sh tools/test-domain.sh`.
- Aperçus Android Studio : `ui/Previews.kt`.

Lire `docs/VALIDATION.md` et effectuer sa checklist avant de distribuer l'application. Les tests JVM incluent des tests JSON ; les tests d'interface utilisent des données en mémoire et ne suppriment pas le vrai journal.

Ne sont pas implémentés : rappels et notifications, synchronisation multi-appareils, compte utilisateur, vidéos/audio, verrouillage biométrique, chiffrement de sauvegarde, mise en ligne Play Store. Pour un historique massif ou une application multi-utilisateur, remplacer le fichier JSON global par Room et concevoir les migrations.

Le contenu yoga est un point de départ éditorial : **le faire relire par un enseignant qualifié avant diffusion publique**. Les schémas sont simplifiés et non validés cliniquement. L'application n'est pas un dispositif médical.

Documentation de référence : `docs/SOURCES.md`. Informations sur le wrapper non inclus : `tools/README_WRAPPER.md`.
