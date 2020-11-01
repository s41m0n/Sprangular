import {Component, Input} from '@angular/core';
import {MatTableDataSource} from '@angular/material/table';
import {Upload} from '../../models/upload.model';
import {AssignmentStatus} from '../../models/assignment-solution.model';
import {first} from 'rxjs/operators';
import {MatDialog} from '@angular/material/dialog';
import {NewAssignmentUploadDialogComponent} from '../../modals/new-assignment-upload/new-assignment-upload-dialog.component';
import {StudentAssignmentDetails} from '../../models/student-assignment.details';
import {AssignmentService} from '../../services/assignment.service';

@Component({
  selector: 'app-tab-expandend-assignment',
  templateUrl: './tab-expandend-assignment.component.html',

})
export class TabExpandendAssignmentComponent {

  expandedElement: StudentAssignmentDetails;
  canUpload = false;
  dataSource = new MatTableDataSource<Upload>();
  studentId;
  colsToDisplay = ['timestamp', 'comment', 'status', 'download'];

  constructor(
      public dialog: MatDialog,
      private assignmentService: AssignmentService
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
    return date.toLocaleDateString() + ' at ' + date.toLocaleTimeString();
  }
}
