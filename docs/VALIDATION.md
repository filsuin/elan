# Validation de la livraison 0.3.0

**9 octobre 2026.** Ce rapport porte sur les sources de cette livraison. Les anciennes affirmations de compilation et de tests sur appareil de la 0.2.0 ne sont pas reprises comme preuve pour la 0.3.0.

## Exécuté dans cet environnement

| Contrôle | Résultat |
| --- | --- |
| Compilation Kotlin du domaine et des catalogues | Réussie, Kotlin CLI 1.9.0 / OpenJDK 21. Sans API Android. |
| `sh tools/test-domain.sh` | **200 contrôles réussis** : 51 contrôles de base, 149 complémentaires. |
| Parseur Kotlin sur sources `.kt` / `.kts` | 41 fichiers, zéro erreur de syntaxe. Ne vérifie pas la résolution des API Android/Compose. |
| XML / TOML / JSON | Lecture structurée réussie ; aucun caractère de contrôle inattendu dans les sources. |
| Alias Gradle | 27 références vers le catalogue vérifiées. Pas une résolution réseau des dépendances. |
| Scripts shell | Syntaxe vérifiée par `sh -n`. Scripts Windows non exécutés ici. |
| Manifest | Ressources référencées présentes ; permissions notifications/redémarrage uniquement ; pas de permission Internet. |
| Tests unitaires Gradle `:app:testDebugUnitTest` | Réussis le 9 octobre 2026, y compris JSON v1/v2 et règles de pratique. |
| Android Lint `:app:lintDebug` | Réussi, 0 erreur. |
| APK debug `:app:assembleDebug` | Réussi avec SDK Android 36 et JDK 21. APK 0.3.0 généré. |
| Tests Compose | Compilation réussie ; exécution reportée car l'appareil connecté était `UNAUTHORIZED` dans ADB. |

Les contrôles métier couvrent les 48 identifiants, les schémas, les champs des fiches, les recherches/filtres/favoris, les 10 parcours, la symétrie gauche/droit des parcours fournis, les limites de l'éditeur, les cycles respiratoires, les transitions, ticks retardés, pauses, sauts, enregistrements idempotents, statistiques, limites de dates et changements d'heure.

Journaux joints : `domain-test-results.txt`, `syntax-results.txt`, `static-results.txt`.

## Compilation Android locale

Commande exécutée avec succès :

```powershell
.\gradlew.bat :app:testDebugUnitTest :app:lintDebug :app:assembleDebug
```

L'APK est disponible dans `app/build/outputs/apk/debug/app-debug.apk`. Le fichier `android-build-attempt.txt` documente uniquement une ancienne tentative dans l'environnement de préparation de la livraison.

## Non exécuté / à ne pas confondre avec un succès

- Exécution des tests Compose sur appareil après réautorisation ADB, et validation de la variante release/R8.
- Installation/mise à jour avec signature réelle, sélecteur de fichiers, erreurs disque, moteur vocal et notifications.
- Mesures d'accessibilité, rendu sur téléphone, consommation de batterie et comportements propres aux constructeurs.
- Build/Docker du backend, distinct et non connecté à l'app.
- Validation pédagogique, anatomique ou clinique des fiches yoga, rythmes et schémas.

## Recette avant diffusion

Exécuter les commandes et la checklist du `GUIDE_DEPLOIEMENT.md`. Tester particulièrement les refus de notification, un canal bloqué, l'arrêt forcé suivi d'une réouverture, le changement de fuseau, l'absence de voix française hors ligne, la rotation/police 200 %, les sauvegardes v1/v2 et l'annulation d'un brouillon. Lancer les tests d'interface sur un appareil de test sans données importantes.

Ne pas publier comme produit validé médicalement. Les durées sont des réglages d'interface facultatifs ; une relecture qualifiée du contenu et des schémas reste nécessaire.
