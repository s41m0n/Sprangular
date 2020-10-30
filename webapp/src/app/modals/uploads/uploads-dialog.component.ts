import {Component, Inject} from '@angular/core';
import {MatTableDataSource} from '@angular/material/table';
import {Upload} from '../../models/upload.model';
import {MAT_DIALOG_DATA, MatDialogRef} from '@angular/material/dialog';
import {AssignmentService} from '../../services/assignment.service';
import {first} from 'rxjs/operators';

@Component({
  selector: 'app-uploads-dialog',
  templateUrl: './uploads-dialog.component.html'
})
export class UploadsDialogComponent {

  dataSource = new MatTableDataSource<Upload>();
  colsToDisplay = ['id', 'comment', 'timestamp'];

  constructor(public dialogRef: MatDialogRef<UploadsDialogComponent>,
              private assignmentService: AssignmentService,
              @Inject(MAT_DIALOG_DATA) public data: any) {
    this.assignmentService.getAssignmentSolutionUploads(data.id).pipe(first()).subscribe(
        uploads => this.dataSource.data = uploads);
  }

  dateString(statusTs: string): string {
    const date = new Date(statusTs);
    return date.toLocaleDateString() + ' at ' + date.toLocaleTimeString();
  }
}
