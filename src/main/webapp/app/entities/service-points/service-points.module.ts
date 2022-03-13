import {NgModule} from '@angular/core';
import {SharedModule} from 'app/shared/shared.module';
import {ServicePointsComponent} from './list/service-points.component';
import {ServicePointsDetailComponent} from './detail/service-points-detail.component';
import {ServicePointsUpdateComponent} from './update/service-points-update.component';
import {ServicePointsDeleteDialogComponent} from './delete/service-points-delete-dialog.component';
import {ServicePointsRoutingModule} from './route/service-points-routing.module';

@NgModule({
  imports: [SharedModule, ServicePointsRoutingModule],
  declarations: [ServicePointsComponent, ServicePointsDetailComponent, ServicePointsUpdateComponent, ServicePointsDeleteDialogComponent],
  entryComponents: [ServicePointsDeleteDialogComponent],
})
export class ServicePointsModule {}
