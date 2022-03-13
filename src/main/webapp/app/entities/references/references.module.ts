import {NgModule} from '@angular/core';
import {SharedModule} from 'app/shared/shared.module';
import {ReferencesComponent} from './list/references.component';
import {ReferencesDetailComponent} from './detail/references-detail.component';
import {ReferencesUpdateComponent} from './update/references-update.component';
import {ReferencesDeleteDialogComponent} from './delete/references-delete-dialog.component';
import {ReferencesRoutingModule} from './route/references-routing.module';

@NgModule({
  imports: [SharedModule, ReferencesRoutingModule],
  declarations: [ReferencesComponent, ReferencesDetailComponent, ReferencesUpdateComponent, ReferencesDeleteDialogComponent],
  entryComponents: [ReferencesDeleteDialogComponent],
})
export class ReferencesModule {}
