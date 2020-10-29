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
  dataSource = new MatTableDataSource<VM>(); // Table datasource dynamically modified
  @Input() set vms(vms: VM[]) {
    // VMs to be displayed in the table
    this.dataSource.data = vms;
  }
  @Output() wipeVmEvent = new EventEmitter<number>();
  @Output() connectVmEvent = new EventEmitter<VM>();
  @Output() triggerVmEvent = new EventEmitter<number>();

  constructor(public dialog: MatDialog) {}

  ngAfterViewInit() {}

  openDialog(id: number): void {
    const selectedVm = this.dataSource.data.find((vm) => vm.id === id);
    const index = this.dataSource.data.findIndex((vm) => vm === selectedVm);
    const dialogRef = this.dialog.open(VmOptionsDialogComponent, {
      width: '300px',
      data: {
        vCpu: selectedVm.vcpu,
        ram: selectedVm.ram,
        disk: selectedVm.diskStorage,
      },
    });

    dialogRef.afterClosed().subscribe((result) => {
      if (result) {
        console.log('The dialog was closed', result);
        selectedVm.vcpu = Number(result.vCpu);
        selectedVm.ram = Number(result.ram);
        selectedVm.diskStorage = Number(result.disk);
        this.dataSource.data[index] = selectedVm;
        // TODO - da pushare la modifica
      }
    });
  }

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
