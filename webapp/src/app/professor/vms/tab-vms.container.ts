import { Component } from '@angular/core';
import { CourseService } from '../../services/course.service';
import { first } from 'rxjs/operators';
import { VmService } from 'src/app/services/vm.service';
import { MatDialog } from '@angular/material/dialog';
import { ImageViewerDialogComponent } from 'src/app/modals/image-viewer/image-viewer-dialog.component';
import {VmProfessorDetails} from '../../models/vm-professor-details.model';
import {ActivatedRoute, Router} from '@angular/router';
import {EditTeamVmOptionsDialogComponent} from '../../modals/edit-team-vm-options/edit-team-vm-options-dialog.component';

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
    private vmService: VmService,
    private router: Router,
    private route: ActivatedRoute
  ) {
    this.courseService
        .getCourseVMs(this.courseService.currentCourseSubject.value)
        .pipe(first())
        .subscribe((vms) => {
          this.vmBundle = vms;
          this.route.queryParams.subscribe((queryParam) =>
              queryParam && queryParam.teamVmOptions ? this.openDialogTeamOption() : null);
          this.route.queryParams.subscribe((queryParam) => queryParam && queryParam.professorConnect ? this.connect() : null);
        });
  }

  refreshVmList() {
    this.courseService
      .getCourseVMs(this.courseService.currentCourseSubject.value)
      .pipe(first())
      .subscribe((vms) => (this.vmBundle = vms));
  }

  triggerVm(event: any) {
    this.vmService
      .triggerVm(event.vmId, this.vmBundle.find((vdp) => vdp.team.id === event.teamId).vms
          .find(vm => vm.id === event.vmId).active)
      .pipe(first())
      .subscribe(() => this.refreshVmList());
  }

  openDialogTeamOption(): void {
    const vpd = this.vmBundle.find(d => d.team.id.toString() === this.route.snapshot.queryParamMap.get('teamVmOptions'));
    const dialogRef = this.dialog.open(EditTeamVmOptionsDialogComponent, {
      data: {
        teamId: vpd.team.id,
        maxTotalInstances: vpd.team.maxTotalInstances,
        currentMaxTotalInstances: vpd.vms.length,
        maxActiveInstances: vpd.team.maxActiveInstances,
        currentMaxActiveInstances: vpd.vms.filter((vm) => vm.active).length,
        maxVCpu: vpd.team.maxVCpu,
        currentMaxVCpu: vpd.vms.map(vm => vm.vcpu).reduce((acc, val) => acc + val, 0),
        maxRam: vpd.team.maxRam,
        currentMaxRam: vpd.vms.map(vm => vm.ram).reduce((acc, val) => acc + val, 0),
        maxDiskStorage: vpd.team.maxDiskStorage,
        currentMaxDiskStorage: vpd.vms.map(vm => vm.diskStorage).reduce((acc, val) => acc + val, 0)
      },
    });
    dialogRef
        .afterClosed()
        .pipe(first())
        .subscribe((result) => {
          if (result) {
            this.refreshVmList();
          }
          this.router.navigate([this.router.url.split('?')[0]]);
        });
  }

  connect() {
    const vm = this.vmBundle.find(vpd => vpd.vms.some(
        v => v.id.toString() === this.route.snapshot.queryParamMap.get('professorConnect')))
        .vms.find(v => v.id.toString() === this.route.snapshot.queryParamMap.get('professorConnect'));
    this.vmService
      .getInstance(vm.id)
      .pipe(first())
      .subscribe((instance) => {
        if (!instance) {
          this.router.navigate([this.router.url.split('?')[0]]);
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
          this.router.navigate([this.router.url.split('?')[0]]);
        });
      });
  }
}
