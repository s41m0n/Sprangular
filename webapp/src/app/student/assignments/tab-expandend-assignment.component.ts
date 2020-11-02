import {Component, Input} from '@angular/core';
import {MatTableDataSource} from '@angular/material/table';
import {Upload} from '../../models/upload.model';
import {AssignmentStatus} from '../../models/assignment-solution.model';
import {first} from 'rxjs/operators';
import {MatDialog} from '@angular/material/dialog';
import {NewAssignmentUploadDialogComponent} from '../../modals/new-assignment-upload/new-assignment-upload-dialog.component';
import {StudentAssignmentDetails} from '../../models/student-assignment.details';
import {AssignmentAndUploadService} from '../../services/assignment-and-upload.service';
import {ImageViewerDialogComponent} from '../../modals/image-viewer/image-viewer-dialog.component';

@Component({
  selector: 'app-tab-expandend-assignment',
  templateUrl: './tab-expandend-assignment.component.html',

})
export class TabExpandendAssignmentComponent {

  expandedElement: StudentAssignmentDetails;
  canUpload = false;
  dataSource = new MatTableDataSource<Upload>();
  studentId;
  colsToDisplay = ['timestamp', 'author', 'comment', 'status', 'download'];

  constructor(
      public dialog: MatDialog,
      private assignmentService: AssignmentAndUploadService
  ) {}

  @Input() set element(element: StudentAssignmentDetails) {
    this.expandedElement = element;
    this.canUpload = element && (element.status === AssignmentStatus.READ || element.status === AssignmentStatus.REVIEWED_UPLOADABLE);
  }

  @Input() set uploads(uploads: Upload[]) {
    this.dataSource.data = uploads;
  }

  /** Private function to refresh the list of vms */
  private refreshUploads() {
    this.assignmentService.getAssignmentSolutionUploads(this.expandedElement.assignmentSolutionId)
        .pipe(first())
        .subscribe((uploads) => {
          this.dataSource.data = uploads.sort(Upload.compare);
          this.expandedElement.status = AssignmentStatus.DELIVERED;
          this.expandedElement.statusTs = this.dataSource.data[uploads.length - 1].timestamp;
          this.canUpload = false;
        });
  }

  newAssignmentSolution() {
    const dialogRef = this.dialog.open(NewAssignmentUploadDialogComponent,
        {
          data: { assignmentSolutionId: this.expandedElement.assignmentSolutionId }
        });
    dialogRef
        .afterClosed()
        .pipe(first())
        .subscribe((result) => {
          if (result) {
            this.refreshUploads();
          }
        });
  }

  dateString(statusTs: string): string {
    const date = new Date(statusTs);
    return (
      date.toLocaleDateString('en-GB') +
      ' at ' +
      date.toLocaleTimeString('en-GB', { hour: '2-digit', minute: '2-digit' })
    );
  }

  viewDocument(upload: Upload) {
    this.assignmentService.getUploadDocument(upload.id).pipe(first()).subscribe(instance => {
      if (!instance) { return; }
      const url = URL.createObjectURL(instance);
      const dialogRef = this.dialog.open(ImageViewerDialogComponent, {
        data: {title: `Upload: ${upload.id}`,
          imageSrc: url,
          downloadable: true,
          dl_name: `upload_${upload.id}`
        }
      });
      dialogRef.afterClosed().subscribe(() => {
        URL.revokeObjectURL(url);
      });
    });
  }
}
