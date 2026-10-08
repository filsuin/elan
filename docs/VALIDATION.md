# Validation de cette livraison

## Effectivement exécuté

- Build Android complet `:app:assembleDebug` avec Android SDK 36 et JDK 21 le 8 octobre 2026.
- Tests unitaires Gradle `:app:testDebugUnitTest` réussis.
- Android Lint `:app:lintDebug` réussi avec 0 erreur. Les avertissements restants signalent principalement des versions de dépendances plus récentes.
- 4 tests d'interface `:app:connectedDebugAndroidTest` réussis sur un Pixel 8 sous Android 17 : humeur, catalogue yoga, ajout de routine et modification d'une victoire passée.
- APK 0.2.0 installé avec succès par ADB sur l'appareil de test, avec conservation des données (`adb install -r`).
- Compilation du code **Kotlin pur** du domaine et des catalogues avec Kotlin CLI 1.9.0 / OpenJDK 21.
- **51 contrôles de logique réussis** avec `tools/DomainSmokeTest.kt` : mises à jour, absence de doublons, préservation des champs, nouveaux jours, limites de saisie, filtres historiques, rotation des conseils, catalogue yoga et minuteur monotone.
- Analyse syntaxique de tous les fichiers Kotlin/Kotlin DSL avec le parseur Kotlin : aucune erreur de syntaxe.
- Lecture structurée des fichiers XML, TOML et JSON ; contrôle des chemins de ressources ; contrôle de syntaxe des scripts shell ; vérification de l'intégrité du ZIP.

Le journal d'exécution des 51 contrôles est dans `domain-test-results.txt`.

## Vérifications restant à effectuer

- Inspection visuelle d'un rendu Android réel, validation TalkBack et essais d'import/export via le sélecteur système.
- Validation sur Android 8, Android 11 et Android 12+, ainsi qu'avec une taille de police à 200 %, en paysage et sur petit écran.
- Parcours manuel complet d'import/export, espace disque insuffisant, changement de date et reprise après arrêt du processus.
- Relecture des contenus yoga par une personne qualifiée.

## Checklist avant diffusion

- [x] Préparer le wrapper, synchroniser puis exécuter `:app:testDebugUnitTest :app:lintDebug :app:assembleDebug`.
- [x] Exécuter `:app:connectedDebugAndroidTest` sur un appareil de test.
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
