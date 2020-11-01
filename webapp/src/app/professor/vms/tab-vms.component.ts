import {
  AfterViewInit,
  Component,
  EventEmitter,
  Input,
  Output,
} from '@angular/core';
import { MatTableDataSource } from '@angular/material/table';

import { VM } from '../../models/vm.model';
import { VmOptionsDialogComponent } from '../../modals/vm-options/vm-options-dialog.component';
import { MatDialog } from '@angular/material/dialog';
import { NewVmDialogComponent } from '../../modals/new-vm/new-vm.component';
import {EditTeamVmOptionsDialogComponent} from '../../modals/edit-team-vm-options/edit-team-vm-options-dialog.component';
import {first} from 'rxjs/operators';
import {of} from 'rxjs';

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
  dataSources = new Array<MatTableDataSource<VM>>();
  teams = new Set();
  @Input() set vms(vms: VM[]) {
    this.dataSources = new Array<MatTableDataSource<VM>>();
    this.teams = new Set();
    // VMs to be displayed in the table
    vms.forEach(vm => this.teams.add(vm.team.id));
    this.teams.forEach(team => {
      const data = new MatTableDataSource<VM>();
      data.data = vms.filter(vm => vm.team.id === team);
      this.dataSources.push(data);
    });
  }
  @Output() wipeVmEvent = new EventEmitter<number>();
  @Output() connectVmEvent = new EventEmitter<VM>();
  @Output() triggerVmEvent = new EventEmitter<number>();
  @Output() refreshVmList = new EventEmitter();

  constructor(public dialog: MatDialog) {}

  ngAfterViewInit() {}

  openDialogTeamOption(teamId: number, index: number): void {
    const vms          = this.dataSources[index].data;
    const teamToChange = vms[0].team;
    const dialogRef    = this.dialog.open(EditTeamVmOptionsDialogComponent, {
      data: {
        teamId: teamToChange.id,
        maxTotalInstances: teamToChange.maxTotalInstances,
        currentMaxTotalInstances: vms.length,
        maxActiveInstances: teamToChange.maxActiveInstances,
        currentMaxActiveInstances: vms.filter(vm => vm.active).length,
        maxVCpu: teamToChange.maxVCpu,
        currentMaxVCpu: vms.map(vm => vm.vcpu).reduce((acc, val) => acc + val, 0),
        maxRam: teamToChange.maxRam,
        currentMaxRam: vms.map(vm => vm.ram).reduce((acc, val) => acc + val, 0),
        maxDiskStorage: teamToChange.maxDiskStorage,
        currentMaxDiskStorage: vms.map(vm => vm.diskStorage).reduce((acc, val) => acc + val, 0)
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

  // openDialogVmOption(id: number, i: number): void {
  //   const selectedVm = this.dataSources[i].data.find((vm) => vm.id === id);
  //   const index = this.dataSources[i].data.findIndex((vm) => vm === selectedVm);
  //   const dialogRef = this.dialog.open(VmOptionsDialogComponent, {
  //     width: '300px',
  //     data: {
  //       vCpu: selectedVm.vcpu,
  //       maxVCpu: selectedVm.team.maxVCpu,
  //       ram: selectedVm.ram,
  //       maxRam: selectedVm.team.maxRam,
  //       disk: selectedVm.diskStorage,
  //       maxDisk: selectedVm.team.maxDiskStorage
  //     },
  //   });
  //
  //   dialogRef
  //       .afterClosed()
  //       .pipe(first())
  //       .subscribe((result) => {
  //         if (result) {
  //           this.refreshVmList.emit();
  //         }
  //       });
  // }

  killVm(id: number) {
    this.wipeVmEvent.emit(id);
  }

  connectToVm(id: VM) {
    this.connectVmEvent.emit(id);
  }

  triggerVm(id: number) {
    this.triggerVmEvent.emit(id);
  }

  newVm() {
    const newVmDialog = this.dialog.open(NewVmDialogComponent, {
      width: '300px',
      data: { teams: ['aggiungere', 'dati'], courses: ['aggiungere', 'dati'] },
    });
  }
}
