import { Component } from '@angular/core';
import { DomSanitizer } from '@angular/platform-browser';
import { VM } from '../../models/vm.model';
import { CourseService } from '../../services/course.service';
import { first } from 'rxjs/operators';
import { VmService } from 'src/app/services/vm.service';
import { MatDialog } from '@angular/material/dialog';
import { ImageViewerDialogComponent } from 'src/app/modals/image-viewer/image-viewer-dialog.component';
import {VmProfessorDetails} from '../../models/vm-professor-details.model';

/**
 * VmsContainer
 *
 * It displays the Vms view (WIP)
 */
@Component({
  selector: 'app-tab-professor-vms-cont',
  templateUrl: './tab-vms.container.html',
})
export class TabProfessorVmsContComponent {
  vmBundle: VmProfessorDetails[] = []; // The current vms

  constructor(
    public dialog: MatDialog,
    private courseService: CourseService,
    private vmService: VmService
  ) {
    this.refreshVmList();
  }

  refreshVmList() {
    this.courseService
      .getCourseVMs(this.courseService.currentCourseSubject.value)
      .pipe(first())
      .subscribe((vms) => (this.vmBundle = vms));
  }

  wipeVm(vmId: number) {
    this.vmService
      .removeVm(vmId)
      .pipe(first())
      .subscribe(() => this.refreshVmList());
  }

  triggerVm(event: any) {
    this.vmService
      .triggerVm(event.vmId, this.vmBundle.find((vdp) => vdp.team.id === event.teamId).vms
          .find(vm => vm.id === event.vmId).active)
      .pipe(first())
      .subscribe(() => this.refreshVmList());
  }

  connect(vm: VM) {
    this.vmService
      .getInstance(vm.id)
      .pipe(first())
      .subscribe((instance) => {
        if (!instance) {
          return;
        }
        const url = URL.createObjectURL(instance);
        const dialogRef = this.dialog.open(ImageViewerDialogComponent, {
          data: {
            title: `VM: ${vm.id} - ${vm.name}`,
            imageSrc: url,
            downloadable: false
          },
        });
        dialogRef.afterClosed().subscribe(() => {
          URL.revokeObjectURL(url);
        });
      });
  }
}
