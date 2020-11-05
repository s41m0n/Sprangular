import {Component, OnDestroy, OnInit} from '@angular/core';
import {first, takeUntil} from 'rxjs/operators';
import { TeamService } from 'src/app/services/team.service';
import { MatDialog } from '@angular/material/dialog';
import { NewVmDialogComponent } from 'src/app/modals/new-vm/new-vm.component';
import { VmService } from 'src/app/services/vm.service';
import { ImageViewerDialogComponent } from '../../modals/image-viewer/image-viewer-dialog.component';
import {VmStudentDetails} from '../../models/vm-student-details.model';
import {Subject} from 'rxjs';
import {ActivatedRoute, Router} from '@angular/router';
import {VmOwnersDialogComponent} from '../../modals/vm-owners/vm-owners-dialog.component';
import {VmOptionsDialogComponent} from '../../modals/vm-options/vm-options-dialog.component';

/**
 * VmsContainer
 *
 * It displays the Vms view (WIP)
 */
@Component({
  selector: 'app-tab-student-vms-cont',
  templateUrl: './tab-vms.container.html'
})
export class TabStudentVmsContComponent implements OnInit, OnDestroy {
  vsd: VmStudentDetails[] = []; // The current vms
  destroy$: Subject<boolean> = new Subject<boolean>();
  inTeam: boolean;

  constructor(
      public dialog: MatDialog,
      private router: Router,
      private route: ActivatedRoute,
      private teamService: TeamService,
      private vmService: VmService) {
  }

  ngOnInit() {
    this.teamService.currentTeamSubject
        .asObservable()
        .pipe(takeUntil(this.destroy$))
        .subscribe(t => {
          this.inTeam = t !== null;
          if (this.inTeam) {
            this.vmService
                .getTeamVms()
                .pipe(first())
                .subscribe((vms) => {
                  this.vsd = vms;
                  this.route.queryParams.subscribe((queryParam) =>
                      queryParam && queryParam.newVm ? this.newVm() : null);
                  this.route.queryParams.subscribe((queryParam) =>
                      queryParam && queryParam.studentConnect ? this.connect(queryParam.studentConnect) : null);
                  this.route.queryParams.subscribe((queryParam) =>
                      queryParam && queryParam.editOwners ? this.editOwners(queryParam.editOwners) : null);
                  this.route.queryParams.subscribe((queryParam) =>
                      queryParam && queryParam.editVm ? this.openDialogVmOption(queryParam.editVm) : null);
                });
          }
    });
  }

  ngOnDestroy() {
    this.destroy$.next(true);
    this.destroy$.unsubscribe();
  }

  /** Private function to refresh the list of vms */
  refreshVMs() {
    // Check if already received the current course
    this.vmService
        .getTeamVms()
        .pipe(first())
        .subscribe((vms) => {
          this.vsd = vms;
        });
  }

  turnVm(vmId: number) {
    this.vmService
        .triggerVm(vmId, this.vsd.find((vm) => vm.vm.id === vmId).vm.active)
        .pipe(first())
        .subscribe((_) => this.refreshVMs());
  }

  editOwners(vmId: string) {
    const vsd = this.vsd.find(elem => elem.vm.id.toString() === vmId);
    const dialogRef = this.dialog.open(VmOwnersDialogComponent, {
      data: {vmDetails: vsd}
    });
    dialogRef
        .afterClosed()
        .pipe(first())
        .subscribe((result) => {
          if (result) {
            this.vmService
                .editOwner(vsd.vm.id, result)
                .pipe(first())
                .subscribe(() => this.refreshVMs());
          }
          this.router.navigate([this.router.url.split('?')[0]]);
        });
  }

  connect(vmId: string) {
    const vm = this.vsd.find(elem => elem.vm.id.toString() === vmId).vm;
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
              title: `VM: ${vm.id} -  ${vm.name}`,
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

  newVm() {
    const dialogRef = this.dialog.open(NewVmDialogComponent, {
      data: {
        currentActiveInstances: this.vsd.filter((vm) => vm.vm.active).length,
        maxActiveInstances: this.teamService.currentTeamSubject.value.maxActiveInstances,
        currentVCpu: this.vsd.map(vm => vm.vm.vcpu).reduce((acc, val) => acc + val, 0),
        maxVCpu: this.teamService.currentTeamSubject.value.maxVCpu,
        currentRam: this.vsd.map(vm => vm.vm.ram).reduce((acc, val) => acc + val, 0),
        maxRam: this.teamService.currentTeamSubject.value.maxRam,
        currentDisk: this.vsd.map(vm => vm.vm.diskStorage).reduce((acc, val) => acc + val, 0),
        maxDisk: this.teamService.currentTeamSubject.value.maxDiskStorage
      }
    });

    dialogRef
        .afterClosed()
        .pipe(first())
        .subscribe((result) => {
          if (result) {
            this.refreshVMs();
          }
          this.router.navigate([this.router.url.split('?')[0]]);
        });
  }

  openDialogVmOption(id: string): void {
    const selectedVm = this.vsd.find(vm => vm.vm.id.toString() === id);
    const dialogRef = this.dialog.open(VmOptionsDialogComponent, {
      data: {
        vmId: selectedVm.vm.id,
        vCpu: selectedVm.vm.vcpu,
        currentVCpu: this.vsd.map(vm => vm.vm.vcpu).reduce((acc, val) => acc + val, 0),
        maxVCpu: this.teamService.currentTeamSubject.value.maxVCpu,
        ram: selectedVm.vm.ram,
        currentRam: this.vsd.map(vm => vm.vm.ram).reduce((acc, val) => acc + val, 0),
        maxRam: this.teamService.currentTeamSubject.value.maxRam,
        disk: selectedVm.vm.diskStorage,
        currentDisk: this.vsd.map(vm => vm.vm.diskStorage).reduce((acc, val) => acc + val, 0),
        maxDisk: this.teamService.currentTeamSubject.value.maxDiskStorage
      },
    });

    dialogRef
        .afterClosed()
        .pipe(first())
        .subscribe((result) => {
          if (result) {
            this.refreshVMs();
          }
          this.router.navigate([this.router.url.split('?')[0]]);
        });
  }

  deleteVm(vmId: number) {
    this.vmService
        .removeVm(vmId)
        .pipe(first())
        .subscribe(() => this.refreshVMs());
  }
}
