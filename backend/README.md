# Élan API - composant distinct, facultatif

Le backend Ktor/PostgreSQL ne reçoit ni compte, ni journal, ni sauvegarde privée. Le client Android 0.3.1 l'appelle uniquement pour vérifier une version (`GET /api/v1/app-update`) et afficher/télécharger l'APK (`/app/download`).

## Déploiement séparé

Sur un serveur déjà équipé de Docker Compose, depuis ce dossier :

```bash
cp .env.example .env
# Modifier POSTGRES_PASSWORD dans .env avec un secret long et unique.
chmod 600 .env
docker network inspect proxy >/dev/null 2>&1 || docker network create proxy
docker compose up -d --build
docker compose ps
docker compose logs --tail=100 elan-api
```

Ne pas exposer PostgreSQL sur Internet. L'API utilise `expose: 8080`, **pas `ports:`** : le port n'est pas publié sur l'hôte. Un simple `curl http://127.0.0.1:8080/health` sur l'hôte ne fonctionne donc pas avec le fichier Compose fourni.

Le reverse proxy Nginx Proxy Manager est sur le réseau Docker externe `proxy_shared` et peut joindre `elan-api:8080`. Configurer HTTPS et les protections adaptées avant toute exposition publique. Pour un test local volontaire, créer temporairement `compose.local-test.yml` :

```yaml
services:
  elan-api:
    ports:
      - "127.0.0.1:8080:8080"
```

Puis :

```bash
docker compose -f docker-compose.yml -f compose.local-test.yml up -d --build
curl --fail http://127.0.0.1:8080/health
```

Ce port est alors lié uniquement à l'interface locale. Pour le retirer après l'essai, recréer le service API avec la configuration de base :

```bash
docker compose -f docker-compose.yml up -d --force-recreate elan-api
```

Le volume `elan-postgres` contient les données de la base. Ne pas lancer `docker compose down -v` sauf effacement volontaire après sauvegarde. Le script SQL monté dans `/docker-entrypoint-initdb.d` initialise uniquement un volume neuf ; il ne constitue pas un moteur de migrations pour une base existante.

## Publication d'une mise à jour Android

Le reverse proxy doit publier `https://elan.maxremonnay.fr` en HTTPS et transférer ces chemins à `elan-api:8080`. La configuration compose monte `./updates` en lecture seule dans `/app/updates`. Le fichier `updates/elan.apk` est l'APK diffusé ; l'API ne répond qu'avec une annonce valide si ce fichier est lisible.

Ajouter à `.env` (sans remplacer les variables PostgreSQL déjà présentes) :

```dotenv
APP_UPDATE_VERSION_CODE=4
APP_UPDATE_VERSION_NAME=0.3.1
APP_UPDATE_PUBLIC_BASE_URL=https://elan.maxremonnay.fr
```

Pour chaque version, déposer l'APK correspondant sous `updates/elan.apk`, incrémenter `APP_UPDATE_VERSION_CODE` et modifier `APP_UPDATE_VERSION_NAME`, puis reconstruire/redémarrer `elan-api`. L'APK doit garder le même certificat de signature que la version installée. La page publique est `/app/download`; le manifeste utilisé par l'app est `/api/v1/app-update`.
