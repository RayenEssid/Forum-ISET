// Ces interfaces décrivent la FORME du JSON renvoyé par Spring Boot (les DTO de ForumDtos / AuthDtos).
// Ce ne sont pas des tables : elles servent seulement à typer les données côté Angular.

export type Role = 'ETUDIANT' | 'ENSEIGNANT' | 'ADMIN';

export function roleLabel(role: Role): string {
  switch (role) {
    case 'ETUDIANT':
      return 'Étudiant';
    case 'ENSEIGNANT':
      return 'Enseignant';
    default:
      return 'Admin';
  }
}

export interface UserView {
  id: number;
  fullName: string;
  email: string;
  role: Role;
}

export interface TokenResponse {
  accessToken: string;
  tokenType: string;
  expiresInSeconds: number;
}

export interface Community {
  id: number;
  name: string;
  privateCommunity: boolean;
  ownerId: number;
  ownerName: string;
  key: string | null; // renvoyée seulement au propriétaire (et à l'admin)
}

export interface Publication {
  id: number;
  title: string;
  content: string;
  communityId: number;
  authorId: number;
  authorName: string;
  authorRole: Role;
  createdAt: string;
  nbCommentaires: number;
}

export interface Commentaire {
  id: number;
  publicationId: number;
  contenu: string;
  authorName: string;
  authorRole: Role;
  reponseEnseignant: boolean;
  createdAt: string;
}

export interface StudyResource {
  id: number;
  communityId: number;
  title: string;
  description: string | null;
  module: string | null;
  attachment: string;
  authorName: string;
  createdAt: string;
}
