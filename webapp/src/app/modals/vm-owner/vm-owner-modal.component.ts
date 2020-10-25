import { Component, Inject, OnDestroy, OnInit } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import { Student } from 'src/app/models/student.model';
import { TeamService } from 'src/app/services/team.service';

@Component({
  selector: 'app-vm-options-modal',
  templateUrl: './vm-owner-modal.component.html',
})
export class VmOwnerModalComponent implements OnDestroy {
  members: Student[];
  private destroy$: Subject<boolean> = new Subject<boolean>(); // Private subject to perform the unsubscriptions when component is destroyed

  constructor(
    public dialogRef: MatDialogRef<VmOwnerModalComponent>,
    @Inject(MAT_DIALOG_DATA) public data: any,
    private teamService: TeamService
  ) {
    console.log(this.teamService.currentTeamSubject.value);
    this.teamService.currentTeamSubject
      .asObservable()
      .pipe(takeUntil(this.destroy$))
      .subscribe((x) => (this.members = x.members));
  }

  onNoClick(): void {
    this.dialogRef.close(this.data);
  }

  ngOnDestroy() {
    this.destroy$.next(null);
    this.destroy$.unsubscribe();
  }
}
