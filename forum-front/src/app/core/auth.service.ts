import { HttpClient } from '@angular/common/http';
import { Injectable, computed, inject, signal } from '@angular/core';
import { Router } from '@angular/router';
import { catchError, switchMap, tap, throwError } from 'rxjs';

import { Role, TokenResponse, UserView } from './models';

const TOKEN_KEY = 'forum_token';

export interface RegisterData {
  type: Exclude<Role, 'ADMIN'>;
  registryId: string;
  fullName: string;
  email: string;
  password: string;
}

/**
 * Gère la connexion : appelle /api/auth/*, garde le JWT et le profil.
 * Le token est gardé dans sessionStorage (il disparaît à la fermeture de l'onglet).
 */
@Injectable({ providedIn: 'root' })
export class AuthService {
  private http = inject(HttpClient);
  private router = inject(Router);

  readonly token = signal<string | null>(sessionStorage.getItem(TOKEN_KEY));
  readonly user = signal<UserView | null>(null);
  readonly isLoggedIn = computed(() => this.token() !== null);

  // Appelé au démarrage de l'application : recharge le profil si un token existe déjà
  restore(): void {
    if (!this.token()) return;
    this.http.get<UserView>('/api/auth/me').subscribe({
      next: (profile) => this.user.set(profile),
      error: () => this.clear(),
    });
  }

  login(email: string, password: string) {
    return this.http.post<TokenResponse>('/api/auth/login', { email, password }).pipe(
      tap((res) => {
        sessionStorage.setItem(TOKEN_KEY, res.accessToken);
        this.token.set(res.accessToken);
      }),
      // une fois le token reçu, on récupère le profil (nom, rôle)
      switchMap(() => this.http.get<UserView>('/api/auth/me')),
      tap((profile) => this.user.set(profile)),
      catchError((err) => {
        this.clear();
        return throwError(() => err);
      }),
    );
  }

  register(data: RegisterData) {
    return this.http.post<UserView>('/api/auth/register', data);
  }

  logout(): void {
    this.clear();
    this.router.navigateByUrl('/login');
  }

  private clear(): void {
    sessionStorage.removeItem(TOKEN_KEY);
    this.token.set(null);
    this.user.set(null);
  }

  // utilisé par l'interceptor quand le serveur répond 401 (token expiré)
  expireSession(): void {
    if (this.token()) this.logout();
  }
}
