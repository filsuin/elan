# Sources et choix techniques

Sources officielles consultées pour l'extension du 9 octobre 2026. Les textes et schémas ajoutés sont originaux ; ces pages ne valident pas chacune des fiches ni leurs durées.

## Android et compilation

- Compatibilité Android Gradle Plugin 8.13 (Gradle 8.13, JDK 17, SDK/API 36) : https://developer.android.com/build/releases/agp-8-13-0-release-notes
- Sommes de contrôle officielles Gradle : https://gradle.org/release-checksums/
- Signature d'une application et compatibilité des mises à jour : https://developer.android.com/studio/publish/app-signing
- Alarmes inexactes et reprogrammation : https://developer.android.com/develop/background-work/services/alarms
- Autorisation de notification Android 13+ : https://developer.android.com/develop/ui/compose/notifications/notification-permission
- Voix du moteur Android, `isNetworkConnectionRequired` : https://developer.android.com/reference/android/speech/tts/Voice
- Synthèse vocale : https://developer.android.com/reference/android/speech/tts/TextToSpeech
- Gestion d'effets Compose : https://developer.android.com/develop/ui/compose/side-effects
- Android 8.0 / API 26 : https://developer.android.com/about/versions/oreo/android-8.0

## Contenu et sécurité de la pratique

NCCIH / NIH, **Yoga: Effectiveness and Safety** : https://www.nccih.nih.gov/health/yoga-effectiveness-and-safety

Repères généraux : adapter les exercices, respecter ses limites, s'arrêter en cas d'inconfort et s'appuyer sur un accompagnement qualifié. Ces repères ne constituent pas une validation des schémas, ni une preuve de sécurité de chaque posture pour toute personne. Aucune promesse thérapeutique individuelle n'est faite.

La référence NHS du projet initial concernant l'estime de soi est conservée comme origine éditoriale des contenus de journal non réécrits : https://www.nhs.uk/mental-health/self-help/tips-and-support/raise-low-self-esteem/

## Périmètre de confidentialité

Le client ne possède pas de permission Internet. Les sauvegardes manuelles sont des JSON en clair, confiés au fournisseur de fichiers choisi par l'utilisateur. Les annonces utilisent seulement des voix déclarées hors ligne par le moteur Android ; ce code n'audite pas le fonctionnement interne d'un moteur tiers installé sur le téléphone. Ne pas présenter ces choix comme un chiffrement de bout en bout ou une synchronisation sécurisée entre appareils.
