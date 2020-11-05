import {Component, Inject} from '@angular/core';
import {MatTableDataSource} from '@angular/material/table';
import {Upload} from '../../models/upload.model';
import {MAT_DIALOG_DATA, MatDialog, MatDialogRef} from '@angular/material/dialog';
import {AssignmentAndUploadService} from '../../services/assignment-and-upload.service';
import {first} from 'rxjs/operators';
import {AssignmentStatus} from '../../models/assignment-solution.model';
import {NewAssignmentUploadDialogComponent} from '../new-assignment-upload/new-assignment-upload-dialog.component';
import {ImageViewerDialogComponent} from '../image-viewer/image-viewer-dialog.component';
import {DomSanitizer} from '@angular/platform-browser';
import {ActivatedRoute, Router} from '@angular/router';

@Component({
  selector: 'app-uploads-dialog',
  templateUrl: './uploads-dialog.component.html'
})
export class UploadsDialogComponent {

  dataSource = new MatTableDataSource<Upload>();
  colsToDisplay = ['timestamp', 'author', 'comment', 'status', 'download'];
  edits = false;

  constructor(
      public dialog: MatDialog,
      public dialogRef: MatDialogRef<UploadsDialogComponent>,
      public sanitizer: DomSanitizer,
      private router: Router,
      private route: ActivatedRoute,
      private assignmentService: AssignmentAndUploadService,
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
}
