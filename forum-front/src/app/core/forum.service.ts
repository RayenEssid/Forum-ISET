import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';

import { Commentaire, Community, Publication, StudyResource } from './models';

/** Tous les appels vers ForumController (Spring Boot), regroupés au même endroit. */
@Injectable({ providedIn: 'root' })
export class ForumService {
  private http = inject(HttpClient);
  private base = '/api/forum';

  // ----- Communautés -----
  communities() {
    return this.http.get<Community[]>(`${this.base}/communities`);
  }

  createCommunity(name: string, privateCommunity: boolean) {
    return this.http.post<Community>(`${this.base}/communities`, { name, privateCommunity });
  }

  join(communityId: number, key: string) {
    return this.http.post<Community>(`${this.base}/communities/${communityId}/join`, { key });
  }

  // ----- Publications -----
  publications(communityId: number) {
    return this.http.get<Publication[]>(`${this.base}/publications`, {
      params: { communityId },
    });
  }

  publication(id: number) {
    return this.http.get<Publication>(`${this.base}/publications/${id}`);
  }

  createPublication(communityId: number, title: string, content: string) {
    return this.http.post<Publication>(`${this.base}/publications`, { communityId, title, content });
  }

  deletePublication(id: number) {
    return this.http.delete<void>(`${this.base}/publications/${id}`);
  }

  // ----- Commentaires -----
  commentaires(publicationId: number) {
    return this.http.get<Commentaire[]>(`${this.base}/publications/${publicationId}/commentaires`);
  }

  addCommentaire(publicationId: number, contenu: string) {
    return this.http.post<Commentaire>(`${this.base}/publications/${publicationId}/commentaires`, {
      contenu,
    });
  }

  // ----- Ressources pédagogiques -----
  resources(communityId: number) {
    return this.http.get<StudyResource[]>(`${this.base}/communities/${communityId}/resources`);
  }

  createResource(data: {
    communityId: number;
    title: string;
    description: string;
    module: string;
    attachment: string;
  }) {
    return this.http.post<StudyResource>(`${this.base}/resources`, data);
  }
}
