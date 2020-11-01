import { EventEmitter, Component, Input, Output } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { MatTableDataSource } from '@angular/material/table';
import { first } from 'rxjs/operators';
import { VmOwnerDialogComponent } from 'src/app/modals/vm-owner/vm-owner-dialog.component';
import { VM } from '../../models/vm.model';
import {VmOptionsDialogComponent} from '../../modals/vm-options/vm-options-dialog.component';
import {ToastrService} from 'ngx-toastr';
import {AuthService} from '../../services/auth.service';

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
  dataSource = new MatTableDataSource<VM>(); // Table datasource dynamically modified
  team;

  @Input() set vms(vms: VM[]) {
    this.dataSource.data = vms;
    if (vms && vms.length > 0) {
      this.team = vms[0].team;
    }
  }
  @Output() turnVmEvent = new EventEmitter<number>();
  @Output() addOwnerEvent = new EventEmitter<any>();
  @Output() connectEvent = new EventEmitter<number>();
  @Output() refreshVmList = new EventEmitter();

  constructor(private toastrService: ToastrService,
              private authService: AuthService,
              public dialog: MatDialog) {}

  triggerTurn(vmId: number, enable: boolean) {
    if (enable && this.dataSource.data.filter((vm) => vm.active).length + 1 > this.team.maxActiveInstances) {
      this.toastrService.info(
          `Reached max numbers of active VMs`,
          'Ops! 😅'
      );
      return;
    }
    this.turnVmEvent.emit(vmId);
  }

  addOwner(vm: VM) {
    const dialogRef = this.dialog.open(VmOwnerDialogComponent);
    dialogRef
        .afterClosed()
        .pipe(first())
        .subscribe((result) => {
          if (result) {
            this.addOwnerEvent.emit({ vmId: vm.id, studentId: result });
          }
        });
  }

  connectToVm(vmId: number) {
    this.connectEvent.emit(vmId);
  }

  openDialogVmOption(id: number): void {
    const selectedVm = this.dataSource.data.find((vm) => vm.id === id);
    const dialogRef = this.dialog.open(VmOptionsDialogComponent, {
      data: {
        vmId: selectedVm.id,
        vCpu: selectedVm.vcpu,
        currentVCpu: this.dataSource.data.map(vm => vm.vcpu).reduce((acc, val) => acc + val, 0),
        maxVCpu: this.team.maxVCpu,
        ram: selectedVm.ram,
        currentRam: this.dataSource.data.map(vm => vm.ram).reduce((acc, val) => acc + val, 0),
        maxRam: this.team.maxRam,
        disk: selectedVm.diskStorage,
        currentDisk: this.dataSource.data.map(vm => vm.diskStorage).reduce((acc, val) => acc + val, 0),
        maxDisk: this.team.maxDiskStorage
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
    const selectedVm = this.dataSource.data.find((vm) => vm.id === vmId);
    const currentStudent = this.authService.currentUserValue.id;
    return !selectedVm.owners.find(stud => stud.id.toString() === currentStudent.toString());
  }
}
