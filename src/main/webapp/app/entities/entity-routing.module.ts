import {NgModule} from '@angular/core';
import {RouterModule} from '@angular/router';

@NgModule({
  imports: [
    RouterModule.forChild([
      {
        path: 'service-points',
        data: { pageTitle: 'lawfirmApp.servicePoints.home.title' },
        loadChildren: () => import('./service-points/service-points.module').then(m => m.ServicePointsModule),
      },
      {
        path: 'services',
        data: { pageTitle: 'lawfirmApp.services.home.title' },
        loadChildren: () => import('./services/services.module').then(m => m.ServicesModule),
      },
      {
        path: 'employees',
        data: { pageTitle: 'lawfirmApp.employees.home.title' },
        loadChildren: () => import('./employees/employees.module').then(m => m.EmployeesModule),
      },
      {
        path: 'references',
        data: { pageTitle: 'lawfirmApp.references.home.title' },
        loadChildren: () => import('./references/references.module').then(m => m.ReferencesModule),
      },
      {
        path: 'job-applications',
        data: { pageTitle: 'lawfirmApp.jobApplications.home.title' },
        loadChildren: () => import('./job-applications/job-applications.module').then(m => m.JobApplicationsModule),
      },
      {
        path: 'configs',
        data: { pageTitle: 'lawfirmApp.configs.home.title' },
        loadChildren: () => import('./configs/config.module').then(m => m.ConfigModule),
      },
      {
        path: 'properties',
        data: { pageTitle: 'lawfirmApp.properties.home.title' },
        loadChildren: () => import('./properties/properties.module').then(m => m.PropertiesModule),
      },
      {
        path: 'config',
        data: { pageTitle: 'lawfirmApp.config.home.title' },
        loadChildren: () => import('./config/config.module').then(m => m.ConfigModule),
      },
      /* jhipster-needle-add-entity-route - JHipster will add entity modules routes here */
    ]),
  ],
})
export class EntityRoutingModule {}
