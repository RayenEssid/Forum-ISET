import { DatePipe } from '@angular/common';
import { Component, OnInit, computed, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';

import { apiError } from '../../core/errors';
import { ForumService } from '../../core/forum.service';
import { Community, Publication, StudyResource, roleLabel } from '../../core/models';

@Component({
  selector: 'app-home',
  imports: [FormsModule, DatePipe, RouterLink],
  templateUrl: './home.html',
})
export class Home implements OnInit {
  private forum = inject(ForumService);
  protected readonly roleLabel = roleLabel;

  // ----- données affichées -----
  communities = signal<Community[]>([]);
  selectedId = signal<number | null>(null);
  selected = computed(() => this.communities().find((c) => c.id === this.selectedId()) ?? null);
  publications = signal<Publication[]>([]);
  resources = signal<StudyResource[]>([]);

  message = signal('');
  error = signal('');

  // ----- formulaires (un signal par champ) -----
  newCommunityName = signal('');
  newCommunityPrivate = signal(false);

  joinId = signal('');
  joinKey = signal('');

  postTitle = signal('');
  postContent = signal('');

  resTitle = signal('');
  resModule = signal('');
  resDescription = signal('');
  resAttachment = signal('');

  ngOnInit(): void {
    this.loadCommunities();
  }

  private clearMessages(): void {
    this.message.set('');
    this.error.set('');
  }

  // GET /api/forum/communities : publiques + privées dont je suis membre
  loadCommunities(selectId?: number): void {
    this.forum.communities().subscribe({
      next: (list) => {
        this.communities.set(list);
        const wanted = selectId ?? this.selectedId();
        const target = list.find((c) => c.id === wanted) ?? list[0];
        if (target) {
          this.select(target.id);
        } else {
          this.selectedId.set(null);
        }
      },
      error: (err) => this.error.set(apiError(err, 'Impossible de charger les communautés.')),
    });
  }

  // Quand on clique sur une communauté : on charge ses publications et ses ressources
  select(id: number): void {
    this.selectedId.set(id);
    this.publications.set([]);
    this.resources.set([]);

    this.forum.publications(id).subscribe({
      next: (list) => {
        if (this.selectedId() === id) this.publications.set(list); // ignore une réponse "en retard"
      },
      error: (err) => this.error.set(apiError(err, 'Impossible de charger les publications.')),
    });

    this.forum.resources(id).subscribe({
      next: (list) => {
        if (this.selectedId() === id) this.resources.set(list);
      },
      error: (err) => this.error.set(apiError(err, 'Impossible de charger les ressources.')),
    });
  }

  createCommunity(): void {
    this.clearMessages();
    this.forum.createCommunity(this.newCommunityName(), this.newCommunityPrivate()).subscribe({
      next: (created) => {
        this.newCommunityName.set('');
        this.newCommunityPrivate.set(false);
        this.message.set('Communauté créée.');
        this.loadCommunities(created.id);
      },
      error: (err) => this.error.set(apiError(err, 'Création refusée.')),
    });
  }

  // POST /api/forum/communities/{id}/join avec la clé
  joinCommunity(): void {
    this.clearMessages();
    const id = Number(this.joinId());
    if (!Number.isInteger(id) || id <= 0) {
      this.error.set("L'identifiant de la communauté doit être un nombre.");
      return;
    }
    this.forum.join(id, this.joinKey()).subscribe({
      next: (joined) => {
        this.joinId.set('');
        this.joinKey.set('');
        this.message.set(`Vous avez rejoint « ${joined.name} ».`);
        this.loadCommunities(joined.id);
      },
      error: (err) => this.error.set(apiError(err, 'Impossible de rejoindre cette communauté.')),
    });
  }

  publish(): void {
    const id = this.selectedId();
    if (id === null) return;
    this.clearMessages();

    this.forum.createPublication(id, this.postTitle(), this.postContent()).subscribe({
      next: () => {
        this.postTitle.set('');
        this.postContent.set('');
        this.message.set('Publication ajoutée.');
        this.select(id);
      },
      error: (err) => this.error.set(apiError(err, 'Publication refusée.')),
    });
  }

  shareResource(): void {
    const id = this.selectedId();
    if (id === null) return;
    this.clearMessages();

    this.forum
      .createResource({
        communityId: id,
        title: this.resTitle(),
        description: this.resDescription(),
        module: this.resModule(),
        attachment: this.resAttachment(),
      })
      .subscribe({
        next: () => {
          this.resTitle.set('');
          this.resModule.set('');
          this.resDescription.set('');
          this.resAttachment.set('');
          this.message.set('Ressource partagée.');
          this.select(id);
        },
        error: (err) => this.error.set(apiError(err, 'Partage refusé.')),
      });
  }
}
