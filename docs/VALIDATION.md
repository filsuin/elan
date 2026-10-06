# Validation de cette livraison

## Effectivement exécuté

- Compilation du code **Kotlin pur** du domaine et des catalogues avec Kotlin CLI 1.9.0 / OpenJDK 21.
- **51 contrôles de logique réussis** avec `tools/DomainSmokeTest.kt` : mises à jour, absence de doublons, préservation des champs, nouveaux jours, limites de saisie, filtres historiques, rotation des conseils, catalogue yoga et minuteur monotone.
- Analyse syntaxique de tous les fichiers Kotlin/Kotlin DSL avec le parseur Kotlin : aucune erreur de syntaxe.
- Lecture structurée des fichiers XML, TOML et JSON ; contrôle des chemins de ressources ; contrôle de syntaxe des scripts shell ; vérification de l'intégrité du ZIP.

Le journal d'exécution des 51 contrôles est dans `domain-test-results.txt`.

## Non exécuté : ne pas confondre avec les contrôles ci-dessus

- Synchronisation Gradle, résolution effective des dépendances et compilation de l'APK Android.
- Android Lint.
- Tests JVM Gradle utilisant la vraie bibliothèque org.json.
- Tests d'interface sur émulateur ou téléphone.
- Inspection visuelle d'un rendu Android réel, validation TalkBack et essais d'import/export via le sélecteur système.
- Exécution des scripts PowerShell/Windows et téléchargement du wrapper officiel.

Le conteneur ne possédait pas le SDK Android et ses accès aux téléchargements Gradle/Google/GitHub n'ont pas abouti. **Aucun APK n'est donc fourni et la compilation de l'ensemble n'est pas certifiée.** Un contrôle de syntaxe n'est pas une résolution des types/API Compose.

## Checklist avant diffusion

- [ ] Préparer le wrapper, synchroniser puis exécuter `:app:testDebugUnitTest :app:lintDebug :app:assembleDebug`.
- [ ] Exécuter `:app:connectedDebugAndroidTest` sur un émulateur.
- [ ] Tester Android 8/11 (couleurs de repli), Android 12+ (couleurs dynamiques), ainsi qu'un appareil Android récent.
- [ ] Vérifier clair/sombre, changement de fond d'écran, paysage, petit écran, taille de police 200 %, clavier et TalkBack.
- [ ] Ajouter, renommer, cocher et supprimer une routine ; quitter complètement l'app puis la rouvrir.
- [ ] Enregistrer une humeur puis la modifier : une seule entrée pour cette date. Supprimer l'humeur et vérifier que la victoire est conservée.
- [ ] Enregistrer une victoire et vérifier sa présence dans l'historique.
- [ ] Sur émulateur, tester le passage de minuit et le retour au premier plan ; vérifier qu'un brouillon de la veille ne devient pas celui du nouveau jour.
- [ ] Lancer/pause/reprendre/réinitialiser le minuteur ; vérifier le passage en arrière-plan, le verrouillage d'écran et la rotation.
- [ ] Exporter puis restaurer sur une installation de test ; comparer toutes les informations.
- [ ] Tenter un JSON invalide, une version inconnue, des dates dupliquées et un fichier > 10 Mo : refuser sans écrasement.
- [ ] Tester une erreur d'écriture / espace insuffisant : afficher un échec, jamais un faux message de sauvegarde.
- [ ] Tester l'effacement global : journal et prénom supprimés, routines de départ restaurées, anciens brouillons non réaffichés.
- [ ] Faire relire les fiches yoga et leurs schémas par un enseignant qualifié.
- [ ] Avant publication : renommer le package, préparer la signature, tester le build release, vérifier les exigences du store et rédiger la politique de confidentialité adaptée.
