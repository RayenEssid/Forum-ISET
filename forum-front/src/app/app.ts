import { Component, OnInit, inject } from '@angular/core';
import { RouterLink, RouterOutlet } from '@angular/router';

import { AuthService } from './core/auth.service';
import { roleLabel } from './core/models';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, RouterLink],
  templateUrl: './app.html',
  styleUrl: './app.css',
})
export class App implements OnInit {
  protected readonly auth = inject(AuthService);
  protected readonly roleLabel = roleLabel;

  ngOnInit(): void {
    // si un token est déjà en mémoire (page rechargée), on récupère le profil
    this.auth.restore();
  }
}
