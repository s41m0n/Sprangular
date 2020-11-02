import {Component, Inject} from '@angular/core';
import {MatTableDataSource} from '@angular/material/table';
import {Upload} from '../../models/upload.model';
import {MAT_DIALOG_DATA, MatDialog, MatDialogRef} from '@angular/material/dialog';
import {AssignmentService} from '../../services/assignment.service';
import {first} from 'rxjs/operators';
import {AssignmentStatus} from '../../models/assignment-solution.model';
import {NewAssignmentUploadDialogComponent} from '../new-assignment-upload/new-assignment-upload-dialog.component';

@Component({
  selector: 'app-uploads-dialog',
  templateUrl: './uploads-dialog.component.html'
})
export class UploadsDialogComponent {

  dataSource = new MatTableDataSource<Upload>();
  colsToDisplay = ['timestamp', 'comment', 'status', 'download'];
  edits = false;

  constructor(
      public dialog: MatDialog,
      public dialogRef: MatDialogRef<UploadsDialogComponent>,
      private assignmentService: AssignmentService,
      @Inject(MAT_DIALOG_DATA) public data: any) {
    this.assignmentService.getAssignmentSolutionUploads(data.id).pipe(first()).subscribe(
        uploads => this.dataSource.data = uploads.sort(Upload.compare));
  }

  dateString(statusTs: string): string {
    const date = new Date(statusTs);
    return (
      date.toLocaleDateString('en-GB') +
      ' at ' +
      date.toLocaleTimeString('en-GB', { hour: '2-digit', minute: '2-digit' })
    );
  }

  uploadable(): boolean {
    return this.dataSource.data.length > 0 && this.dataSource.data[this.dataSource.data.length - 1].status === AssignmentStatus.DELIVERED;
  }

  uploadReview() {
    const dialogRef = this.dialog.open(NewAssignmentUploadDialogComponent,
        {
          data: { assignmentSolutionId: this.data.id }
        });
    dialogRef
        .afterClosed()
        .pipe(first())
        .subscribe((result) => {
          if (result) {
            this.dialogRef.close(true);
          }
        });
  }

  private refreshUploads() {
    this.assignmentService.getAssignmentSolutionUploads(this.data.id)
        .pipe(first())
        .subscribe((uploads) => {
          this.dataSource.data = uploads.sort(Upload.compare);
        });
  }
}
