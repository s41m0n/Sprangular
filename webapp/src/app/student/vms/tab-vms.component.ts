import { EventEmitter, Component, Input, Output } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { first } from 'rxjs/operators';
import { VmOwnersDialogComponent } from 'src/app/modals/vm-owners/vm-owners-dialog.component';
import { VM } from '../../models/vm.model';
import {VmOptionsDialogComponent} from '../../modals/vm-options/vm-options-dialog.component';
import {ToastrService} from 'ngx-toastr';
import {AuthService} from '../../services/auth.service';
import {TeamService} from '../../services/team.service';
import {VmStudentDetails} from '../../models/vm-student-details.model';
import {Resource} from '../../models/resource.model';
import {ConfirmationDialogComponent} from '../../modals/confirmation-dialog/confirmation-dialog.component';

/**
 * StudentsComponent
 *
 * It represents the view for the Students tab
 */
@Component({
  selector: 'app-tab-student-vms',
  templateUrl: './tab-vms.component.html',
  styleUrls: ['./tab-vms.component.css'],
})
export class TabStudentVmsComponent {
  dataSource: VmStudentDetails[]; // Table datasource dynamically modified
  resources = this.availableTeamResources([]);
  hasTeam: boolean;

  @Input() set vms(vms: VmStudentDetails[]) {
    this.dataSource = vms;
    this.resources  = this.availableTeamResources(vms.map(value => value.vm));
  }
  @Input() set inTeam(inTeam: boolean) {
    this.hasTeam = inTeam;
  }
  @Output() turnVmEvent = new EventEmitter<number>();
  @Output() editOwnerEvent = new EventEmitter<any>();
  @Output() connectEvent = new EventEmitter<VM>();
  @Output() refreshVmList = new EventEmitter();
  @Output() deleteVmEvent = new EventEmitter<number>();
  @Output() createVmEvent = new EventEmitter();

  constructor(private toastrService: ToastrService,
              private authService: AuthService,
              private teamService: TeamService,
              public dialog: MatDialog) {}

  triggerTurn(vmId: number, enable: boolean) {
    if (enable && this.dataSource.filter(vm => vm.vm.active).length + 1 > this.teamService.currentTeamSubject.value.maxActiveInstances) {
      this.toastrService.info(
          `Reached max numbers of active VMs`,
          'Ops! 😅'
      );
      return;
    }
    this.turnVmEvent.emit(vmId);
  }

  editOwners(vsd: VmStudentDetails) {
    const dialogRef = this.dialog.open(VmOwnersDialogComponent, {
      data: {vmDetails: vsd}
    });
    dialogRef
        .afterClosed()
        .pipe(first())
        .subscribe((result) => {
          if (result) {
            this.editOwnerEvent.emit({ vmId: vsd.vm.id, studentId: result });
          }
        });
  }

  connectToVm(vm: VM) {
    this.connectEvent.emit(vm);
  }

  openDialogVmOption(id: number): void {
    const selectedVm = this.dataSource.find((vm) => vm.vm.id === id);
    const dialogRef = this.dialog.open(VmOptionsDialogComponent, {
      data: {
        vmId: selectedVm.vm.id,
        vCpu: selectedVm.vm.vcpu,
        currentVCpu: this.dataSource.map(vm => vm.vm.vcpu).reduce((acc, val) => acc + val, 0),
        maxVCpu: this.teamService.currentTeamSubject.value.maxVCpu,
        ram: selectedVm.vm.ram,
        currentRam: this.dataSource.map(vm => vm.vm.ram).reduce((acc, val) => acc + val, 0),
        maxRam: this.teamService.currentTeamSubject.value.maxRam,
        disk: selectedVm.vm.diskStorage,
        currentDisk: this.dataSource.map(vm => vm.vm.diskStorage).reduce((acc, val) => acc + val, 0),
        maxDisk: this.teamService.currentTeamSubject.value.maxDiskStorage
      },
    });

    dialogRef
        .afterClosed()
        .pipe(first())
        .subscribe((result) => {
          if (result) {
            this.refreshVmList.emit();
          }
        });
  }

  isOwner(vmId: number) {
    return this.dataSource.find((vm) => vm.vm.id === vmId).owners
        .find(stud => stud.id.toString() === this.authService.currentUserValue.id.toString());
  }

  availableTeamResources(vms: VM[]) {
    if (vms.length === 0) {
      vms = undefined;
    }
    const team = this.teamService.currentTeamSubject.value;
    return [
      new Resource(
          '#VMs',
          team.maxTotalInstances,
          vms ? vms.length : 0
      ),
      new Resource(
          '#Actives',
          team.maxActiveInstances,
          vms ? vms.filter((vm) => vm.active).length : 0
      ),
      new Resource(
          'VCpus',
          team.maxVCpu,
          vms ? vms.map(vm => vm.vcpu).reduce((acc, val) => acc + val, 0) : 0
      ),
      new Resource(
          'Ram',
          team.maxRam,
          vms ? vms.map(vm => vm.ram).reduce((acc, val) => acc + val, 0) : 0
      ),
      new Resource(
          'DiskGB',
          team.maxDiskStorage,
          vms ? vms.map(vm => vm.diskStorage).reduce((acc, val) => acc + val, 0) : 0
      )
    ];
  }

  deleteVm(vm: VM) {
    const confirmRef = this.dialog.open(ConfirmationDialogComponent, {
      disableClose: false,
    });
    confirmRef.componentInstance.confirmMessage = `Are you sure you want to delete "${vm.name}" vm?\nThis operation cannot be undone!`;
    confirmRef
        .afterClosed()
        .pipe(first())
        .subscribe((result) => {
          if (result) {
            this.deleteVmEvent.emit(vm.id);
          }
        });
  }

  checkResourceAvailability() {
    if (!this.dataSource) {
      return true;
    }
    if (this.dataSource.length >= this.teamService.currentTeamSubject.value.maxTotalInstances) {
      return true;
    }

    const currentMaxVCpu = this.dataSource.map(vm => vm.vm.vcpu).reduce((acc, val) => acc + val, 0);
    const currentMaxRam = this.dataSource.map(vm => vm.vm.ram).reduce((acc, val) => acc + val, 0);
    const currentMaxDiskStorage = this.dataSource.map(vm => vm.vm.diskStorage).reduce((acc, val) => acc + val, 0);

    return !(currentMaxVCpu < this.teamService.currentTeamSubject.value.maxVCpu &&
        currentMaxRam < this.teamService.currentTeamSubject.value.maxRam &&
        currentMaxDiskStorage < this.teamService.currentTeamSubject.value.maxDiskStorage);
  }
}
