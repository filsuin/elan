# Pourquoi une préparation ?

Le petit fichier binaire officiel `gradle-wrapper.jar` n'a pas pu être téléchargé dans l'environnement de génération. Il n'est donc pas dans cette archive. Les sources, lanceurs et propriétés du projet sont présents.

Avant d'ouvrir le projet, lance `PREPARER_WINDOWS.cmd` sous Windows, ou `sh tools/prepare-wrapper.sh` sous macOS/Linux. Les lanceurs `gradlew` et `gradlew.bat` appellent aussi cette préparation automatiquement.

Le script télécharge **uniquement** le wrapper officiel depuis le tag `v8.13.0` du dépôt Gradle, puis vérifie son SHA-256 avant de l'installer :

```
81a82aaea5abcc8ff68b3dfcb58b3c3c429378efd98e7433460610fecd7ae45f
```

La distribution Gradle 8.13, téléchargée ensuite au premier build, est également verrouillée par checksum dans `gradle-wrapper.properties` :

```
20f1b1176237254a6fc204d8434196fa11a4cfb387567519c61556e8710aed78
```

Référence officielle : https://gradle.org/release-checksums/

Les lanceurs shell/batch sont des lanceurs minimaux propres à ce projet, pas des copies présentées comme officielles. La classe exécutée après téléchargement est celle du wrapper officiel. Aucun changement permanent de configuration PowerShell, aucun droit administrateur, aucune modification de l'antivirus ne sont nécessaires. Le paramètre `ExecutionPolicy Bypass` du lanceur Windows ne concerne que le processus de préparation courant.

Après un premier build, `gradlew wrapper --gradle-version 8.13` peut remplacer les lanceurs minimaux par ceux générés par Gradle. Conserver `distributionSha256Sum` et vérifier le checksum du JAR avant de versionner les fichiers.
