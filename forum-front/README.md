# ISET Connect : frontend Angular 22

Interface web du forum. Elle parle au backend Spring Boot (dossier `forum`) par l'API REST + JWT.

## Prérequis
- Node.js **22.22.3 ou plus** (ou 24.15+) : vérifie avec `node -v`
- Le backend Spring Boot doit tourner sur le port **8081** (`server.port=8081`)

## Lancer
```
npm install
npm start
```
Puis ouvre http://localhost:4200

## Changer le port du backend
Modifie `target` dans `src/proxy.conf.json` (ex : http://localhost:8080), puis relance `npm start`.

## Structure
```
src/app/
├── app.ts / app.html          barre de navigation + <router-outlet>
├── app.config.ts              HttpClient + interceptor JWT
├── app.routes.ts              /login /register /home /posts/:id
├── core/
│   ├── models.ts              formes du JSON (DTO Spring Boot)
│   ├── auth.service.ts        login / register / token / profil
│   ├── auth.interceptor.ts    ajoute "Authorization: Bearer <token>"
│   ├── auth.guard.ts          bloque /home sans connexion
│   ├── forum.service.ts       tous les appels /api/forum/*
│   └── errors.ts              messages d'erreur lisibles
└── pages/
    ├── login/  register/  home/  post-detail/
```
