import { Component } from '@angular/core';
import { DomSanitizer } from '@angular/platform-browser';
import { VM } from '../../models/vm.model';
import { CourseService } from '../../services/course.service';
import { first } from 'rxjs/operators';
import { VmService } from 'src/app/services/vm.service';
import { MatDialog } from '@angular/material/dialog';
import { VmViewerModalComponent } from 'src/app/modals/vm-viewer/vm-viewer-modal.component';
import { createUrlResolverWithoutPackagePrefix } from '@angular/compiler';
import { UrlHandlingStrategy } from '@angular/router';

/**
 * VmsContainer
 *
 * It displays the Vms view (WIP)
 */
@Component({
  selector: 'app-tab-professor-vms-cont',
  templateUrl: './tab-vms.container.html'
})
export class TabProfessorVmsContComponent {
  mySrc = null;
  vms: VM[] = [];                             // The current vms

  constructor(public dialog: MatDialog,
              private courseService: CourseService,
              private vmService: VmService,
              private sanitizer: DomSanitizer) {
    this.refreshVmList();
  }

  refreshVmList() {
    this.courseService.getCourseVMs(this.courseService.currentCourseSubject.value).pipe(first()).subscribe(vms => this.vms = vms);
  }

  wipeVm(vmId: number) {
    this.vmService.removeVm(vmId).pipe(first()).subscribe(x => this.refreshVmList());
  }

  triggerVm(id: number) {
    this.vmService.triggerVm(id, this.vms.find(vm => vm.id === id).active).pipe(first()).subscribe(x => this.refreshVmList());
  }

  connect(vmId: number) {
    this.vmService.getInstance(vmId).pipe(first()).subscribe(instance => {
      if (!instance) { return; }
      const url = URL.createObjectURL(instance);
      const dialogRef = this.dialog.open(VmViewerModalComponent, {
        data: {id : vmId, imageSrc: this.sanitizer.bypassSecurityTrustUrl(url)}
      });
      dialogRef.afterClosed().subscribe(() => {
        URL.revokeObjectURL(url);
      });
    });
  }
}

