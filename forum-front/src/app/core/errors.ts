import { HttpErrorResponse } from '@angular/common/http';

// Transforme une erreur HTTP de Spring Boot en message lisible pour l'utilisateur.
export function apiError(err: unknown, fallback: string): string {
  const e = err as HttpErrorResponse;

  if (e?.status === 0) {
    return 'Serveur injoignable : vérifie que Spring Boot est démarré (port 8081).';
  }

  const body = e?.error;
  if (body?.errors && typeof body.errors === 'object') {
    // erreurs de validation : { "email": "email invalide", ... }
    return Object.values(body.errors).join(' · ');
  }
  if (typeof body?.message === 'string') {
    return body.message;
  }
  if (e?.status === 403) return 'Action interdite.';
  if (e?.status === 401) return 'Session expirée : reconnecte-toi.';
  return fallback;
}
