# Élan API - composant distinct, facultatif

Le backend Ktor/PostgreSQL ne reçoit ni compte, ni journal, ni sauvegarde privée. Le client Android l'appelle uniquement pour vérifier une version (`GET /api/v1/app-update`) et afficher ou télécharger l'APK (`/app/download`).

## Déploiement séparé

Sur un serveur déjà équipé de Docker Compose :

```bash
cp .env.example .env
# Modifier POSTGRES_PASSWORD dans .env avec un secret long et unique.
chmod 600 .env
docker network inspect proxy >/dev/null 2>&1 || docker network create proxy
docker compose up -d --build
docker compose ps
docker compose logs --tail=100 elan-api
```

Ne pas exposer PostgreSQL sur Internet. L'API utilise `expose: 8080`, pas `ports:` : le port n'est pas publié sur l'hôte. Le reverse proxy doit publier HTTPS et joindre `elan-api:8080` sur le réseau Docker `proxy_shared`.

La configuration Compose monte `./updates` en lecture seule dans `/app/updates`. Le fichier `updates/elan.apk` est l'APK diffusé ; l'API ne répond avec une annonce valide que si ce fichier est lisible.

## Publication d'une mise à jour Android

Ajouter à `.env` :

```dotenv
APP_UPDATE_VERSION_CODE=6
APP_UPDATE_VERSION_NAME=0.3.3
APP_UPDATE_PUBLIC_BASE_URL=https://elan.maxremonnay.fr
```

Aucun compte tiers ni secret de notification n'est requis. Pour chaque version, déposer l'APK sous `updates/elan.apk`, incrémenter `APP_UPDATE_VERSION_CODE` et modifier `APP_UPDATE_VERSION_NAME`, puis reconstruire/redémarrer `elan-api`. L'APK doit garder le même certificat de signature que la version installée. La page publique est `/app/download`; le manifeste est `/api/v1/app-update`.
