import { DatePipe } from '@angular/common';
import { Component, OnInit, computed, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';

import { AuthService } from '../../core/auth.service';
import { apiError } from '../../core/errors';
import { ForumService } from '../../core/forum.service';
import { Commentaire, Publication, roleLabel } from '../../core/models';

@Component({
  selector: 'app-post-detail',
  imports: [FormsModule, DatePipe, RouterLink],
  templateUrl: './post-detail.html',
})
export class PostDetail implements OnInit {
  private route = inject(ActivatedRoute);
  private router = inject(Router);
  private forum = inject(ForumService);
  protected auth = inject(AuthService);
  protected readonly roleLabel = roleLabel;

  // l'id vient de l'URL : /posts/5 -> 5
  id = Number(this.route.snapshot.paramMap.get('id'));

  post = signal<Publication | null>(null);
  commentaires = signal<Commentaire[]>([]);
  contenu = signal('');
  error = signal('');
  message = signal('');

  // Seul l'auteur ou l'admin peut supprimer (Spring Boot le vérifie aussi côté serveur)
  canDelete = computed(() => {
    const p = this.post();
    const u = this.auth.user();
    return !!p && !!u && (u.role === 'ADMIN' || u.id === p.authorId);
  });

  ngOnInit(): void {
    if (!Number.isInteger(this.id) || this.id <= 0) {
      this.error.set('Publication invalide.');
      return;
    }
    this.forum.publication(this.id).subscribe({
      next: (p) => this.post.set(p),
      error: (err) => this.error.set(apiError(err, 'Publication introuvable ou accès interdit.')),
    });
    this.loadCommentaires();
  }

  private loadCommentaires(): void {
    this.forum.commentaires(this.id).subscribe({
      next: (list) => this.commentaires.set(list),
      error: (err) => this.error.set(apiError(err, 'Impossible de charger les réponses.')),
    });
  }

  addCommentaire(): void {
    this.error.set('');
    this.message.set('');
    this.forum.addCommentaire(this.id, this.contenu()).subscribe({
      next: () => {
        this.contenu.set('');
        this.message.set('Réponse ajoutée.');
        this.loadCommentaires();
      },
      error: (err) => this.error.set(apiError(err, 'Réponse refusée.')),
    });
  }

  deletePost(): void {
    if (!confirm('Supprimer cette publication et ses réponses ?')) return;
    this.forum.deletePublication(this.id).subscribe({
      next: () => this.router.navigateByUrl('/home'),
      error: (err) => this.error.set(apiError(err, 'Suppression refusée.')),
    });
  }
}
