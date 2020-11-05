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
    this.route.queryParams.subscribe((queryParam) =>
        queryParam && queryParam.professorUpload ? this.uploadReview() : null
    );
    this.route.queryParams.subscribe((queryParam) =>
        queryParam && queryParam.professorImage ? this.viewDocument(queryParam.professorImage) : null
    );
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
            this.dialogRef.close(result);
          }
          this.router.navigate([this.router.url.split('?')[0]], {queryParams: {solution: this.data.id}});
        });
  }

  viewDocument(uploadId: number) {
    this.assignmentService.getUploadDocument(uploadId).pipe(first()).subscribe(instance => {
      if (!instance) {
        this.router.navigate([this.router.url.split('?')[0]], {queryParams: {solution: this.data.id}});
        return;
      }
      const url = URL.createObjectURL(instance);
      const dialogRef = this.dialog.open(ImageViewerDialogComponent, {
        data: {title: `Upload: ${uploadId}`,
          imageSrc: url,
          downloadable: true,
          dl_name: `upload_${uploadId}`
        }
      });
      dialogRef.afterClosed().subscribe(() => {
        URL.revokeObjectURL(url);
        this.router.navigate([this.router.url.split('?')[0]], {queryParams: {solution: this.data.id}});
      });
    });
  }
}
