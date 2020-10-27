import { Component, OnInit } from '@angular/core';
import { VM } from '../../models/vm.model';
import {first, map, takeUntil} from 'rxjs/operators';
import { TeamService } from 'src/app/services/team.service';
import { MatDialog } from '@angular/material/dialog';
import { NewVmDialogComponent } from 'src/app/modals/new-vm/new-vm.component';
import { VmService } from 'src/app/services/vm.service';
import {BehaviorSubject, Observable, of, Subject} from 'rxjs';
import { VmViewerDialogComponent } from '../../modals/vm-viewer/vm-viewer-dialog.component';
import { DomSanitizer } from '@angular/platform-browser';
import {Team} from '../../models/team.model';

/**
 * VmsContainer
 *
 * It displays the Vms view (WIP)
 */
@Component({
  selector: 'app-tab-student-vms-cont',
  templateUrl: './tab-vms.container.html',
  styleUrls: ['./tab-vms.container.css']
})
export class TabStudentVmsContComponent implements OnInit {
  vms: VM[] = null; // The current vms
  private destroy$: Subject<boolean> = new Subject<boolean>(); // Private subject to perform the unsubscriptions when component is destroyed
  inTeam$: boolean;

  constructor(
    public dialog: MatDialog,
    private teamService: TeamService,
    private vmService: VmService,
    private sanitizer: DomSanitizer
  ) {}

  ngOnInit(): void {
    this.teamService.currentTeamSubject
      .asObservable()
      .pipe(takeUntil(this.destroy$))
      .subscribe((t) => {
        this.inTeam$ = t !== null;
        if (this.inTeam$) {
          this.refreshVMs();
        }
      });
  }

  /** Private function to refresh the list of vms */
  private refreshVMs() {
    // Check if already received the current course
    if (!this.teamService.currentTeamSubject.value) {
      this.vms = null;
      return;
    }
    this.vmService
      .getTeamVms()
      .pipe(first())
      .subscribe((vms) => (this.vms = vms));
  }

  turnVm(vmId: number) {
    this.vmService
      .triggerVm(vmId, this.vms.find(vm => vm.id === vmId).active)
      .pipe(first())
      .subscribe((_) => this.refreshVMs());
  }

  addOwner(object: any) {
    this.vmService
      .addOwner(object.vmId, object.studentId)
      .pipe(first())
      .subscribe((_) => this.refreshVMs());
  }

  connect(vmId: number) {
    this.vmService
      .getInstance(vmId)
      .pipe(first())
      .subscribe((instance) => {
        if (!instance) { return; }
        const url = URL.createObjectURL(instance);
        const dialogRef = this.dialog.open(VmViewerDialogComponent, {
          data: {
            id: vmId,
            imageSrc: this.sanitizer.bypassSecurityTrustUrl(url),
          },
        });
        dialogRef.afterClosed().subscribe(() => {
          URL.revokeObjectURL(url);
        });
      });
  }

  newVm() {
    const dialogRef = this.dialog.open(NewVmDialogComponent);
    dialogRef
      .afterClosed()
      .pipe(first())
      .subscribe((result) => {
        if (result) {
          this.refreshVMs();
        }
      });
  }
}
