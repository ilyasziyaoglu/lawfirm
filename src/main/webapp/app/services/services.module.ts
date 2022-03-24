import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';

import {ServicesRoutingModule} from './services-routing.module';
import {ServicesComponent} from './services.component';
import {ServiceDetailComponent} from './service-detail/service-detail.component';


@NgModule({
  declarations: [
    ServicesComponent,
    ServiceDetailComponent
  ],
  imports: [
    CommonModule,
    ServicesRoutingModule
  ]
})
export class ServicesModule { }
