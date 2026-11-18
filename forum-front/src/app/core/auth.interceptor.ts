import { HttpErrorResponse, HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { catchError, throwError } from 'rxjs';

import { AuthService } from './auth.service';

// Login et inscription n'ont pas besoin de token (et Spring refuserait un vieux token même là)
const PUBLIC_URLS = ['/api/auth/login', '/api/auth/register'];

/**
 * Ajoute automatiquement "Authorization: Bearer <token>" à chaque requête vers /api,
 * et déconnecte l'utilisateur si le serveur répond 401 (token expiré).
 */
export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const auth = inject(AuthService);
  const token = auth.token();

  const isApi = req.url.startsWith('/api/');
  const isPublic = PUBLIC_URLS.some((url) => req.url.startsWith(url));

  // les requêtes HTTP sont immuables : on les clone pour ajouter le header
  const request =
    token && isApi && !isPublic
      ? req.clone({ setHeaders: { Authorization: `Bearer ${token}` } })
      : req;

  return next(request).pipe(
    catchError((err: HttpErrorResponse) => {
      if (err.status === 401 && isApi && !isPublic) {
        auth.expireSession();
      }
      return throwError(() => err);
    }),
  );
};
