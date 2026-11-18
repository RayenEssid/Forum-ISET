import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';

import { AuthService } from './auth.service';

// Empêche d'ouvrir /home ou /posts/:id sans être connecté (la vraie sécurité reste dans Spring Security)
export const authGuard: CanActivateFn = () => {
  return inject(AuthService).isLoggedIn() ? true : inject(Router).createUrlTree(['/login']);
};
