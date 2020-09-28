import {AfterViewInit, Component, Input} from '@angular/core';
import {MatTableDataSource} from '@angular/material/table';

import {VM} from '../../models/vm.model';
import {VmOptionsModalComponent} from '../../modals/vm-options-modal/vm-options-modal.component';
import {MatDialog} from '@angular/material/dialog';
import {NewVmComponent} from '../../modals/new-vm/new-vm.component';

/**
 * VmsComponent
 *
 * It represents the view for the VMs tab
 */
@Component({
  selector: 'app-tab-professor-vms',
  templateUrl: './tab-vms.component.html'
})
export class TabProfessorVmsComponent implements AfterViewInit {

  dataSource = new MatTableDataSource<VM>();                     // Table datasource dynamically modified
  @Input() set vms(vms: VM[]) {              // VMs to be displayed in the table
    this.dataSource.data = vms;
  }

  constructor(public dialog: MatDialog) {
  }

  ngAfterViewInit() {
  }

  openDialog(id: number): void {
    const selectedVm = this.dataSource.data.find(vm => vm.id === id);
    const index = this.dataSource.data.findIndex(vm => vm === selectedVm);
    const dialogRef = this.dialog.open(VmOptionsModalComponent, {
      width: '300px',
      data: {vCpu: selectedVm.vCpu, ram: selectedVm.ram, disk: selectedVm.disk}
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        console.log('The dialog was closed', result);
        selectedVm.vCpu = Number(result.vCpu);
        selectedVm.ram = Number(result.ram);
        selectedVm.disk = Number(result.disk);
        this.dataSource.data[index] = selectedVm;
        // TODO - da pushare la modifica
      }
    });
  }

  killVm(id: number) {
    console.log('Method to implent');
  }

  connectToVm(id: number) {
    console.log('Method to implement');
  }

  newVm() {
    const newVmDialog = this.dialog.open(NewVmComponent, {
      width: '300px',
      data: {teams: ['aggiungere', 'dati'], courses: ['aggiungere', 'dati']}
    });

    newVmDialog.afterClosed().subscribe(result => {
      if (result) {
        console.log('The dialog was closed', result);
        const vm = new VM(
            // TODO - l'id dove lo prendiamo?
            100,
            result.vmName,
            result.vmPath,
            new Date().toDateString(),
            Number(result.vmVCpu),
            Number(result.vmRam),
            Number(result.vmDisk)
        );
        vm.team = result.vmTeam;
        this.dataSource.data.push(vm);
      }
      // TODO - pushare la nuova vm sul db
    });
  }
}
