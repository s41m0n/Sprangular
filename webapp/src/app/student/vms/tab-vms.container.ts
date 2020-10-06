import { Component, OnInit } from '@angular/core';
import { VM } from '../../models/vm.model';
import { first, takeUntil } from 'rxjs/operators';
import { TeamService } from 'src/app/services/team.service';
import { MatDialog } from '@angular/material/dialog';
import { NewVmComponent } from 'src/app/modals/new-vm/new-vm.component';
import { VmService } from 'src/app/services/vm.service';
import { Subject } from 'rxjs';

/**
 * VmsContainer
 *
 * It displays the Vms view (WIP)
 */
@Component({
  selector: 'app-tab-student-vms-cont',
  templateUrl: './tab-vms.container.html'
})
export class TabStudentVmsContComponent implements OnInit {

  vms: VM[] = null;                     // The current vms
  private destroy$: Subject<boolean> = new Subject<boolean>(); // Private subject to perform the unsubscriptions when component is destroyed
  
  constructor(public dialog: MatDialog,
    private teamService: TeamService,
    private vmService: VmService) {
  }

  ngOnInit(): void {
    this.teamService.currentTeamSubject.asObservable().pipe(takeUntil(this.destroy$)).subscribe(x => this.refreshVMs());
  }

  /** Private function to refresh the list of enrolled students */
  private refreshVMs() {
    // Check if already received the current course
    if (!this.teamService.currentTeamSubject.value) {
      this.vms = null;
      return;
    }
    this.vmService.getTeamVms().pipe(first()).subscribe(vms => this.vms = vms);
  }

  turnVm(vmId: number) {
    this.vmService.triggerVm(vmId).pipe(first()).subscribe(_ => this.refreshVMs());
  }

  addOwner(object : any) {
    this.vmService.addOwner(object.vmId, object.studentId).pipe(first()).subscribe(_ => this.refreshVMs());
  }

  connect(vmId : number) {
    this.vmService.getInstance(vmId).pipe(first()).subscribe(instance => console.log(instance));
  }

  newVm() {
    const dialogRef = this.dialog.open(NewVmComponent);
    dialogRef.afterClosed()
      .pipe(first())
      .subscribe(result => {
        if (result) {
          this.refreshVMs();
        }
      });
  }
}

