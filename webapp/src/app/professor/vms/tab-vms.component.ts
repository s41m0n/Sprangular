import {
  AfterViewInit,
  Component,
  EventEmitter,
  Input,
  Output,
} from '@angular/core';
import { VM } from '../../models/vm.model';
import { MatDialog } from '@angular/material/dialog';
import { NewVmDialogComponent } from '../../modals/new-vm/new-vm.component';
import {EditTeamVmOptionsDialogComponent} from '../../modals/edit-team-vm-options/edit-team-vm-options-dialog.component';
import {first} from 'rxjs/operators';
import {VmProfessorDetails} from '../../models/vm-professor-details.model';

/**
 * VmsComponent
 *
 * It represents the view for the VMs tab
 */
@Component({
  selector: 'app-tab-professor-vms',
  templateUrl: './tab-vms.component.html',
  styleUrls: ['./tab-vms.component.css'],
})
export class TabProfessorVmsComponent implements AfterViewInit {
  dataSources: VmProfessorDetails[];

  @Input() set vms(vms: VmProfessorDetails[]) {
    this.dataSources = vms;
  }
  @Output() wipeVmEvent = new EventEmitter<number>();
  @Output() connectVmEvent = new EventEmitter<VM>();
  @Output() triggerVmEvent = new EventEmitter<{teamId: number, vmId: number}>();
  @Output() refreshVmList = new EventEmitter();

  constructor(public dialog: MatDialog) {}

  ngAfterViewInit() {}

  openDialogTeamOption(vpd: VmProfessorDetails): void {
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
            this.refreshVmList.emit();
          }
        });
  }

  killVm(id: number) {
    this.wipeVmEvent.emit(id);
  }

  connectToVm(vm: VM) {
    this.connectVmEvent.emit(vm);
  }

  triggerVm(teamId: number, vmId: number) {
    this.triggerVmEvent.emit({teamId, vmId});
  }

  newVm() {
    const newVmDialog = this.dialog.open(NewVmDialogComponent, {
      width: '300px',
      data: { teams: ['aggiungere', 'dati'], courses: ['aggiungere', 'dati'] },
    });
  }
}
