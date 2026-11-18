import { Routes } from '@angular/router';

import { authGuard } from './core/auth.guard';
import { Home } from './pages/home/home';
import { Login } from './pages/login/login';
import { PostDetail } from './pages/post-detail/post-detail';
import { Register } from './pages/register/register';

export const routes: Routes = [
  { path: '', redirectTo: 'home', pathMatch: 'full' },
  { path: 'login', component: Login },
  { path: 'register', component: Register },
  { path: 'home', component: Home, canActivate: [authGuard] },
  { path: 'posts/:id', component: PostDetail, canActivate: [authGuard] },
  { path: '**', redirectTo: 'home' },
];
