import { Component, computed, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';

import { AuthService } from '../../core/auth.service';
import { apiError } from '../../core/errors';
import { Role } from '../../core/models';

@Component({
  selector: 'app-register',
  imports: [FormsModule, RouterLink],
  templateUrl: './register.html',
})
export class Register {
  private auth = inject(AuthService);
  private router = inject(Router);

  type = signal<Exclude<Role, 'ADMIN'>>('ETUDIANT');
  registryId = signal('');
  fullName = signal('');
  email = signal('');
  password = signal('');
  loading = signal(false);
  error = signal('');

  // le libellé du champ change selon le type de compte
  idLabel = computed(() =>
    this.type() === 'ETUDIANT' ? "N° d'inscription (registre étudiants)" : 'Code enseignant (registre enseignants)',
  );

  submit(): void {
    this.error.set('');
    this.loading.set(true);

    const email = this.email();
    const password = this.password();

    this.auth
      .register({
        type: this.type(),
        registryId: this.registryId(),
        fullName: this.fullName(),
        email,
        password,
      })
      .subscribe({
        // compte créé -> on connecte directement l'utilisateur
        next: () =>
          this.auth.login(email, password).subscribe({
            next: () => {
              this.loading.set(false);
              this.router.navigateByUrl('/home');
            },
            error: () => {
              this.loading.set(false);
              this.router.navigateByUrl('/login');
            },
          }),
        error: (err) => {
          this.loading.set(false);
          this.error.set(apiError(err, 'Inscription refusée.'));
        },
      });
  }
}
