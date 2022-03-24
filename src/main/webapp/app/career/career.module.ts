import {NgModule} from '@angular/core';
import {CommonModule} from '@angular/common';

import {CareerRoutingModule} from './career-routing.module';
import {CareerComponent} from './career.component';
import {MatTabsModule} from "@angular/material/tabs";
import {FormsModule, ReactiveFormsModule} from "@angular/forms";


@NgModule({
  declarations: [
    CareerComponent,
  ],
  imports: [
    CommonModule,
    CareerRoutingModule,
    MatTabsModule,
    FormsModule,
    ReactiveFormsModule,
  ]
})
export class CareerModule {
}
