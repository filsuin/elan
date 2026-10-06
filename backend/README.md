# Élan API

Backend Ktor + PostgreSQL pour servir l'idée du jour. Les idées sont actuellement éditoriales et stockées en base ; un adaptateur d'API externe pourra être ajouté sans modifier l'API Android.

## Déploiement Docker

Sur le serveur :

```bash
cd /opt/elan/backend
cp .env.example .env
sed -i "s/CHANGE_ME_WITH_A_LONG_RANDOM_VALUE/$(openssl rand -base64 32 | tr -dc 'A-Za-z0-9' | head -c 32)/" .env
docker network create proxy 2>/dev/null || true
docker compose up -d --build
docker compose logs -f elan-api
```

Vérification locale :

```bash
curl http://127.0.0.1:8080/health
curl "http://127.0.0.1:8080/api/v1/daily-ideas?date=2026-10-02"
```

Le conteneur API n'expose pas le port sur l'hôte : Nginx Proxy Manager doit rejoindre le réseau Docker `proxy` et utiliser `elan-api:8080` comme destination.
