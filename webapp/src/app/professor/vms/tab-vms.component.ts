import {Component, AfterViewInit, Input, ChangeDetectorRef, OnInit} from '@angular/core';
import { MatTableDataSource } from '@angular/material/table';

import { VM } from '../../models/vm.model';
import {VmOptionsModalComponent} from '../../modals/vm-options-modal/vm-options-modal.component';
import {MatDialog} from '@angular/material/dialog';

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
  @Input() set vms( vms: VM[] ) {              // VMs to be displayed in the table
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
      data: { vCpu: selectedVm.vCpu, ram: selectedVm.ram, disk: selectedVm.disk }
    });

    dialogRef.afterClosed().subscribe(result => {
      if (result) {
        console.log('The dialog was closed', result);
        selectedVm.vCpu = result.vCpu;
        selectedVm.ram = result.ram;
        selectedVm.disk = result.disk;
        this.dataSource.data[index] = selectedVm;
      }
    });
  }

  killVm(id: number) {
    console.log('Method to implent');
  }

  connectToVm(id: number) {
    console.log('Method to implement');
  }
}
