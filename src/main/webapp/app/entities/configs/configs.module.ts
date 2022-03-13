import {NgModule} from '@angular/core';
import {SharedModule} from 'app/shared/shared.module';
import {ConfigsComponent} from './list/configs.component';
import {ConfigsDetailComponent} from './detail/configs-detail.component';
import {ConfigsUpdateComponent} from './update/configs-update.component';
import {ConfigsDeleteDialogComponent} from './delete/configs-delete-dialog.component';
import {ConfigsRoutingModule} from './route/configs-routing.module';

@NgModule({
  imports: [SharedModule, ConfigsRoutingModule],
  declarations: [ConfigsComponent, ConfigsDetailComponent, ConfigsUpdateComponent, ConfigsDeleteDialogComponent],
  entryComponents: [ConfigsDeleteDialogComponent],
})
export class ConfigsModule {}
