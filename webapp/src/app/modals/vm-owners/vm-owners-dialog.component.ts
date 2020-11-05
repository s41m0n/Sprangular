import { Component, Inject, OnDestroy } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import { Student } from 'src/app/models/student.model';
import { TeamService } from 'src/app/services/team.service';

@Component({
  selector: 'app-vm-options-modal',
  templateUrl: './vm-owners-dialog.component.html',
})
export class VmOwnersDialogComponent implements OnDestroy {
  members: Student[];
  private destroy$: Subject<boolean> = new Subject<boolean>(); // Private subject to perform the unsubscriptions when component is destroyed

  constructor(
    public dialogRef: MatDialogRef<VmOwnersDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: any,
    private teamService: TeamService
  ) {
    this.teamService.currentTeamSubject
      .asObservable()
      .pipe(takeUntil(this.destroy$))
      .subscribe((x) => (this.members = x.members.filter(s => s.id !== JSON.parse(localStorage.getItem('currentUser')).id)));
  }

  ngOnDestroy() {
    this.destroy$.next(null);
    this.destroy$.unsubscribe();
  }

  selectColor(studentId: string) {
    return this.data.vmDetails.owners.map(s => s.id.toString()).includes(studentId) ? 'warn' : 'primary';
  }
}
