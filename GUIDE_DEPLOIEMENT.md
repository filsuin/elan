# Élan 0.3.0 - Guide de déploiement

**Livraison du 9 octobre 2026 | Android | Projet Kotlin / Jetpack Compose**

> Cette livraison contient les sources et les tests, pas un APK compilé. Les 200 contrôles du domaine ont réussi. La compilation Android complète et les essais sur appareil restent à effectuer : le SDK Android et l'accès réseau nécessaire aux dépendances ne sont pas disponibles dans l'environnement utilisé pour préparer ce ZIP.

## 1. Quel ZIP utiliser ?

**`elan_v0.3.0_complet.zip` : choix recommandé.** Il contient le projet entier, modifié et documenté. Extraire dans un nouveau dossier, par exemple `C:\Projets`, pour obtenir `C:\Projets\elan`. Ne pas travailler directement dans la fenêtre de consultation du ZIP.

**`elan_v0.3.0_fichiers_modifies.zip` : mise à jour du projet source fourni.** Il contient uniquement les fichiers ajoutés ou modifiés, avec leurs chemins. Sauvegarder le projet original, fermer Android Studio, puis recopier le contenu du dossier `elan` du ZIP dans le dossier `elan` existant en acceptant les remplacements. Ne pas créer accidentellement `elan\elan`. Aucune suppression de source n'est requise.

Le ZIP différentiel s'applique **au `elan.zip` de cette conversation**. Si les mêmes fichiers ont été modifiés depuis, effectuer une fusion plutôt qu'un remplacement aveugle. Consulter `FICHIERS_MODIFIES.md` et `MANIFEST_MODIFICATIONS.json` pour les chemins et empreintes SHA-256.

## 2. Prérequis

| Élément | Configuration de cette livraison |
| --- | --- |
| Android Studio | Une version prenant en charge Android Gradle Plugin 8.13.2. Ne pas lancer une migration automatique vers AGP 9 pour ce premier essai. |
| JDK de Gradle | 17 minimum ; 17 ou 21 pour ce guide. |
| Android SDK | Platform 36 et Build-Tools 35.0.0 ; Platform-Tools pour ADB. |
| Gradle | 8.13, chargé par le wrapper du projet. |
| Kotlin | 2.2.21, déclaré dans le catalogue de versions. |
| Téléphone | Configuration cible : Android 8.0 / API 26 minimum. Validation multi-version à effectuer. |
| Internet | Nécessaire pour préparer le wrapper et télécharger SDK/dépendances. L'app utilise aussi HTTPS pour vérifier les mises à jour ; le journal reste local. |

La matrice Gradle / JDK / SDK correspond à la documentation Android de la branche AGP 8.13 [1]. Les versions sont figées dans `gradle/libs.versions.toml` ; ne pas les remplacer toutes par les dernières versions avant le premier build.

## 3. Préparer le projet sur Windows

### A. Préparer le wrapper

Dans `C:\Projets\elan`, double-cliquer sur **`PREPARER_WINDOWS.cmd`**. Il télécharge le `gradle-wrapper.jar` officiel et contrôle son SHA-256. Le JAR n'est pas embarqué dans ce ZIP ; la distribution Gradle et les bibliothèques seront également téléchargées au premier build.

Le script ne modifie pas les règles de sécurité PowerShell de manière permanente. Si les règles de l'entreprise bloquent un script, ne pas les désactiver globalement : faire valider l'exécution ou préparer le wrapper avec un Gradle 8.13 approuvé.

Empreintes attendues, issues de Gradle [2] :

```text
Gradle 8.13, distribution binaire :
20f1b1176237254a6fc204d8434196fa11a4cfb387567519c61556e8710aed78

Gradle 8.13, wrapper JAR :
81a82aaea5abcc8ff68b3dfcb58b3c3c429378efd98e7433460610fecd7ae45f
```

Ne pas ignorer un échec de vérification de somme de contrôle.

### B. Ouvrir Android Studio

Dans **Open**, sélectionner `C:\Projets\elan`, le dossier qui contient `settings.gradle.kts`, `build.gradle.kts`, `gradlew.bat` et `app/`. Ne pas ouvrir seulement `app/` ni `backend/`.

Dans **SDK Manager**, installer Android SDK Platform 36, Android SDK Build-Tools 35.0.0 et Android SDK Platform-Tools. Accepter les licences demandées. Dans **Settings > Build, Execution, Deployment > Build Tools > Gradle**, sélectionner un JDK 17 ou 21. Les libellés peuvent varier selon Android Studio.

Lancer **Sync Project with Gradle Files** et consulter son résultat dans l'IDE. Le projet ne fournit volontairement pas `local.properties`, qui dépend de ton ordinateur. Android Studio le crée avec le chemin local du SDK. Exemple à adapter seulement en cas de configuration manuelle :

```properties
sdk.dir=C:/Users/VOTRE_COMPTE/AppData/Local/Android/Sdk
```

### C. Compiler et contrôler

Ouvrir le terminal d'Android Studio à la racine du projet :

```powershell
.\gradlew.bat --version
.\gradlew.bat :app:testDebugUnitTest :app:lintDebug :app:assembleDebug
```

Le premier appel doit indiquer Gradle 8.13 et le JDK retenu. Le second doit se terminer par **BUILD SUCCESSFUL**. Le script `CONSTRUIRE_APK_WINDOWS.cmd` lance la même suite de tests / Lint / assemblage et conserve la fenêtre ouverte.

Fichiers obtenus après réussite :

```text
app\build\outputs\apk\debug\app-debug.apk
app\build\reports\tests\testDebugUnitTest\index.html
app\build\reports\lint-results-debug.html
```

En cas d'échec, conserver le message complet. Pour obtenir plus de contexte sans effacer le projet :

```powershell
.\gradlew.bat :app:testDebugUnitTest :app:lintDebug :app:assembleDebug --stacktrace
```

## 4. Installer sur un téléphone de test

**Avant une mise à jour :** ouvrir la version existante, aller dans les réglages puis **Exporter ma sauvegarde JSON**, et conserver le fichier hors de l'app. Ne pas désinstaller la version existante pour commencer.

Activer le débogage USB sur un appareil de test, le connecter, autoriser l'ordinateur et le choisir dans Android Studio, puis cliquer sur **Run**. Autre possibilité, avec ADB disponible dans le PATH :

```powershell
adb devices
adb install -r .\app\build\outputs\apk\debug\app-debug.apk
```

`-r` demande une mise à jour avec conservation des données. Android exige notamment une signature compatible avec l'app déjà installée [3]. Cette livraison conserve `applicationId = fr.elan.app` et porte `versionCode` à 5.

**Attention à la clé debug :** deux ordinateurs peuvent signer leurs APK de développement avec des certificats différents. `INSTALL_FAILED_UPDATE_INCOMPATIBLE` peut indiquer ce problème. Réutiliser l'environnement ou la clé de signature autorisée de la version installée. Ne pas partager une clé privée dans le ZIP.

Si la conservation de la signature est impossible, la désinstallation/réinstallation est une migration destructive : **exporter et vérifier la sauvegarde avant**, puis restaurer cette sauvegarde dans la nouvelle version. Sans sauvegarde, le journal local serait perdu.

## 5. Valider les nouveaux parcours

Les tests d'interface fournis se lancent sur un émulateur ou un téléphone de test connecté :

```powershell
.\gradlew.bat :app:connectedDebugAndroidTest
```

Ils ne remplacent pas la recette manuelle. Avant de diffuser l'APK, vérifier :

| Parcours | Résultat attendu |
| --- | --- |
| Catalogue | 48 fiches ; recherche sans distinction d'accents/casse ; filtres et favoris persistants après relance. |
| Séance | Côtés gauche/droit, durées cohérentes, transitions réglables ; pause possible avant tout mouvement. |
| Éditeur | Création, copie, modification, ordre, durées, suppression et avertissement avant abandon. |
| Lecteur | Pause/reprise, étape sautée, fin, enregistrement unique ; aucun temps crédité pour pause/installation/transition. |
| Cycle de vie | Rotation puis reprise ; arrière-plan et verrouillage mettent en pause ; pas d'audio continu après sortie. |
| Respiration | Quatre rythmes sans rétention ; durée effective affichée quand les cycles arrondissent la durée demandée. |
| Suivi | Temps réel, parcours partiels, filtre 7/30/90 jours/tout, objectif de semaine, suppression d'une entrée. |
| Rappel | Désactivé initialement ; autorisation sur Android 13+ ; refus respecté ; changement d'heure et redémarrage. |
| Sauvegarde | Import v1 ; aller-retour v2 avec favoris/séances/historique ; refus des fichiers invalides sans écrasement. |
| Accessibilité | Petit écran, paysage, police 200 %, clair/sombre, TalkBack, clavier. |

Faire ces essais sur une version ancienne prise en charge et un Android récent. La compatibilité configurée dans Gradle n'est pas une preuve d'essai sur toutes les versions.

## 6. Créer une version de diffusion signée

L'APK debug sert aux essais. Pour une diffusion durable, utiliser **Build > Generate Signed Bundle / APK** dans Android Studio : APK pour une installation directe, ou Android App Bundle pour le parcours de publication qui le demande [3].

Choisir le module `app`, la variante `release`, puis **réutiliser la clé de signature existante** si l'app a déjà été distribuée. Pour une première diffusion, créer une clé et la sauvegarder dans un emplacement sécurisé, avec son mot de passe et son alias. Ne pas changer l'identifiant d'application pour mettre à jour les installations existantes.

Le projet n'embarque aucune clé ni configuration de signature privée. Un simple `assembleRelease` ne garantit donc pas un APK de diffusion signé. Utiliser l'assistant et récupérer le fichier à l'emplacement qu'il indique. Tester **cette variante release**, qui active l'optimisation R8, puis vérifier la mise à jour d'une installation signée avec la même clé.

Avant publication publique : faire relire les postures et schémas par une personne qualifiée, vérifier les licences des dépendances, produire une politique de confidentialité adaptée au fonctionnement réel et consulter les exigences en vigueur du magasin. Cette livraison n'est ni une certification médicale ni une validation Play Store.

## 7. Sauvegardes et retour arrière

Le fichier privé conserve son nom `elan-journal-v1.json` pour trouver le journal préexistant. Le **contenu** passe au schéma v2 à la prochaine écriture. Les nouveaux champs ont des valeurs par défaut lors de la lecture d'une sauvegarde v1.

L'import valide le contenu puis demande confirmation avant de remplacer le journal. Il ne fusionne pas deux historiques. Après restauration, le rappel est désactivé : le réactiver volontairement sur ce téléphone.

**Le format v2 n'est pas promis compatible avec la version 0.2.0.** Garder l'export v1 fait avant migration et l'ancien APK/signature. Pour revenir à l'ancienne version, exporter aussi les données récentes à titre d'archive, puis préparer un retour contrôlé sur un appareil de test avec l'ancienne sauvegarde v1. Ne pas forcer une rétrogradation en place sur des données v2.

La sauvegarde automatique Android est désactivée. Le JSON exporté contient des données personnelles en clair, sans chiffrement ni mot de passe : choisir un emplacement de confiance. Un fournisseur cloud choisi dans le sélecteur Android peut le synchroniser. La permission Internet est utilisée uniquement pour les métadonnées publiques de version ; le journal n'est pas transmis.

## 8. macOS et Linux

Installer/configurer le SDK et un JDK compatible, ouvrir le projet avec Android Studio puis, depuis sa racine :

```bash
sh tools/prepare-wrapper.sh
chmod +x gradlew
./gradlew :app:testDebugUnitTest :app:lintDebug :app:assembleDebug
```

`ANDROID_HOME` ou `sdk.dir` dans `local.properties` doit pointer vers le SDK local. Les scripts n'installent pas eux-mêmes le SDK. Pour rejouer seulement les tests métier sans Android, avec Kotlin CLI 1.9+ et JDK 17+ :

```bash
sh tools/test-domain.sh
```

## 9. Dépannage

| Message ou comportement | Vérification |
| --- | --- |
| `gradle-wrapper.jar` absent | Lancer `PREPARER_WINDOWS.cmd` avant la première synchronisation. Vérifier l'accès HTTPS au dépôt officiel. |
| Échec SHA-256 | Ne pas contourner. Inspecter le fichier et le téléchargement ; reprendre depuis la source officielle [2]. |
| `SDK location not found` | Ouvrir le SDK Manager puis vérifier `local.properties` ou `ANDROID_HOME`. |
| Erreur JDK / version de classe | Vérifier le JDK dans Android Studio **et** dans `gradlew --version`. |
| Dépendance introuvable, proxy ou DNS | Vérifier les dépôts Google, Maven Central et Gradle. Ne pas ajouter de dépôt inconnu pour contourner. |
| Incompatibilité de signature | Réutiliser la clé précédente ; sauvegarder avant toute désinstallation. |
| Pas de voix | Installer/choisir une voix française hors ligne dans le moteur Android, puis relancer l'app. Le guidage visuel reste disponible [5]. |
| Pas de rappel | Vérifier l'interrupteur, les permissions et le canal de notification ; rouvrir après un arrêt forcé. Horaire approximatif [4, 6]. |
| Le suivi n'a pas augmenté | Appuyer sur **Enregistrer dans mon suivi** ou **Enregistrer et quitter**. Au moins une seconde de pratique effective est nécessaire. |
| Perte d'une pratique non enregistrée | Un arrêt du processus ne conserve pas le lecteur/brouillon. Le journal déjà enregistré reste distinct. |
| Journal illisible | Ne pas effacer les données. Vérifier l'espace libre et conserver les exports ; aucune réinitialisation silencieuse. |

## 10. Le backend du ZIP d'origine

Le journal reste local et le compte n'existe pas. Le client utilise seulement le backend pour chercher la version courante et ouvrir la page de téléchargement. Pour publier le backend et l'APK, suivre `backend/README.md` : l'adresse configurée est `https://elan.maxremonnay.fr`, l'API de version `/api/v1/app-update` et la page `/app/download`. Les notifications requièrent l'autorisation Android.

## Sources officielles

[1] Android Developers, compatibilité AGP 8.13 : https://developer.android.com/build/releases/agp-8-13-0-release-notes

[2] Gradle, sommes SHA-256 : https://gradle.org/release-checksums/

[3] Android Developers, signature et mises à jour : https://developer.android.com/studio/publish/app-signing

[4] Android Developers, alarmes inexactes : https://developer.android.com/develop/background-work/services/alarms

[5] Android Developers, voix et besoin de connexion : https://developer.android.com/reference/android/speech/tts/Voice

[6] Android Developers, autorisation de notification Android 13+ : https://developer.android.com/develop/ui/compose/notifications/notification-permission

Consultées pour cette livraison le 9 octobre 2026. Les comportements propres à Élan décrivent le code livré, pas des essais sur appareil qui n'ont pas eu lieu.
