import {EventEmitter, Component, Input, Output} from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import {MatTableDataSource} from '@angular/material/table';
import { first } from 'rxjs/operators';
import { VmOwnerDialogComponent } from 'src/app/modals/vm-owner/vm-owner-dialog.component';
import {VM} from '../../models/vm.model';

/**
 * StudentsComponent
 *
 * It represents the view for the Students tab
 */
@Component({
  selector: 'app-tab-student-vms',
  templateUrl: './tab-vms.component.html',
  styleUrls: ['./tab-vms.component.css']
})
export class TabStudentVmsComponent {

  dataSource = new MatTableDataSource<VM>();                     // Table datasource dynamically modified

  @Input() set vms(vms: VM[]) {
    this.dataSource.data = vms;
  }
  @Output() turnVmEvent = new EventEmitter<number>();
  @Output() addOwnerEvent = new EventEmitter<any>();
  @Output() connectEvent = new EventEmitter<number>();

  constructor(public dialog: MatDialog) {
  }

  triggerTurn(vmId: number) {
    this.turnVmEvent.emit(vmId);
  }

  addOwner(vm: VM) {
    const dialogRef = this.dialog.open(VmOwnerDialogComponent);
    dialogRef.afterClosed()
      .pipe(first())
      .subscribe(result => {
        if (result) {
          this.addOwnerEvent.emit({vmId: vm.id, studentId: result});
        }
      });
  }

  connectToVm(vmId: number) {
    this.connectEvent.emit(vmId);
  }
}
