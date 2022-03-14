import {NgModule} from '@angular/core';
import {RouterModule} from '@angular/router';

import {errorRoute} from './layouts/error/error.route';
import {navbarRoute} from './layouts/navbar/navbar.route';
import {DEBUG_INFO_ENABLED} from 'app/app.constants';
import {Authority} from 'app/config/authority.constants';
import {AboutComponent} from 'app/about/about.component';

import {UserRouteAccessService} from 'app/core/auth/user-route-access.service';

@NgModule({
  imports: [
    RouterModule.forRoot(
      [
        {
          path: 'admin',
          data: {
            authorities: [Authority.ADMIN],
          },
          canActivate: [UserRouteAccessService],
          loadChildren: () => import('./admin/admin-routing.module').then(m => m.AdminRoutingModule),
        },
        {
          path: 'about',
          component: AboutComponent,
        },
        {
          path: 'our-services',
          loadChildren: () => import('./services/services.module').then(m => m.ServicesModule),
        },
        {
          path: 'team',
          loadChildren: () => import('./team/team.module').then(m => m.TeamModule),
        },
        {
          path: 'career',
          loadChildren: () => import('./career/career.module').then(m => m.CareerModule),
        },
        {
          path: 'contact',
          loadChildren: () => import('./contact/contact.module').then(m => m.ContactModule),
        },
        {
          path: 'account',
          loadChildren: () => import('./account/account.module').then(m => m.AccountModule),
        },
        {
          path: 'login',
          loadChildren: () => import('./login/login.module').then(m => m.LoginModule),
        },
        {
          path: '',
          loadChildren: () => import(`./entities/entity-routing.module`).then(m => m.EntityRoutingModule),
        },
        navbarRoute,
        ...errorRoute,
      ],
      { enableTracing: DEBUG_INFO_ENABLED }
    ),
  ],
  exports: [RouterModule],
})
export class AppRoutingModule {}
