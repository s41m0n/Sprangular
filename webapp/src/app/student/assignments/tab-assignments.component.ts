import {Component, EventEmitter, Input, Output} from '@angular/core';
import {MatTableDataSource} from '@angular/material/table';
import {animate, state, style, transition, trigger} from '@angular/animations';
import {Upload} from '../../models/upload.model';
import {StudentAssignmentDetails} from '../../models/student-assignment-details.model';
import {MatDialog} from '@angular/material/dialog';
import {AssignmentStatus} from '../../models/assignment-solution.model';
import {NewAssignmentUploadDialogComponent} from '../../modals/new-assignment-upload/new-assignment-upload-dialog.component';
import {first} from 'rxjs/operators';
import {ActivatedRoute, Router} from '@angular/router';

/**
 * StudentsComponent
 *
 * It represents the view for the Students tab
 */
@Component({
  selector: 'app-tab-student-assignments',
  templateUrl: './tab-assignments.component.html',
  animations: [
    trigger('detailExpand', [
      state('collapsed', style({height: '0px', minHeight: '0'})),
      state('expanded', style({height: '*'})),
      transition('expanded <=> collapsed', animate('225ms cubic-bezier(0.4, 0.0, 0.2, 1)')),
    ]),
  ],
})
export class TabStudentAssignmentsComponent {
  regularDataSource = new MatTableDataSource<StudentAssignmentDetails>();                     // Table datasource dynamically modified
  expiredDataSource = new MatTableDataSource<StudentAssignmentDetails>();                     // Table datasource dynamically
  colsToDisplay = ['name', 'releaseDate', 'dueDate', 'status', 'statusTs', 'grade', 'document', 'uploads'];
  innerDataSource = new MatTableDataSource<Upload>();
  innerColsToDisplay = ['timestamp', 'author', 'comment', 'status', 'download'];
  canUpload: boolean;
  expandedElement: StudentAssignmentDetails | null;

  @Input() set assignments(assignments: StudentAssignmentDetails[]) {
    this.regularDataSource.data = assignments.filter(
        assignment => (Date.now() < Date.parse(assignment.dueDate) && assignment.status !== AssignmentStatus.DEFINITIVE)
        || assignment.status === AssignmentStatus.REVIEWED_UPLOADABLE);
    this.expiredDataSource.data = assignments.filter(
        assignment => (Date.now() >= Date.parse(assignment.dueDate) || assignment.status === AssignmentStatus.DEFINITIVE)
        && assignment.status !== AssignmentStatus.REVIEWED_UPLOADABLE);
  }
  @Input() set uploads(uploads: Upload[]) {
    this.innerDataSource.data = uploads.sort(Upload.compare);
  }
  @Output() getUploadsEvent = new EventEmitter<number>();
  @Output() refreshUploadsEvent = new EventEmitter<number>();

  constructor(public dialog: MatDialog,
              private router: Router,
              private route: ActivatedRoute) {
    this.route.queryParams.subscribe((queryParam) =>
        queryParam && queryParam.studentUpload ? this.newAssignmentSolution(queryParam.studentUpload) : null
    );
  }

  // Handles the opening of the uploads section
  showUploads(element: StudentAssignmentDetails) {
    this.expandedElement = this.expandedElement === element ? null : element;
    if (this.expandedElement === null) {
      return;
    }
    this.canUpload = element.status === AssignmentStatus.READ || element.status === AssignmentStatus.REVIEWED_UPLOADABLE;
    this.getUploadsEvent.emit(element.assignmentSolutionId);
  }

  newAssignmentSolution(assSolId: string) {
    const dialogRef = this.dialog.open(NewAssignmentUploadDialogComponent,
        {
          data: { assignmentSolutionId: assSolId }
        });
    dialogRef
        .afterClosed()
        .pipe(first())
        .subscribe((result) => {
          if (result) {
            this.refreshUploadsEvent.emit(this.expandedElement.assignmentSolutionId);
            this.expandedElement.status = AssignmentStatus.DELIVERED;
            this.expandedElement.statusTs = result.timestamp;
            this.canUpload = false;
          }
          this.router.navigate([this.router.url.split('?')[0]]);
        });
  }

  /** Function to express the date in a user friendly way */
  dateString(statusTs: string): string {
    const date = new Date(statusTs);
    return (
      date.toLocaleDateString('en-GB') +
      ' at ' +
      date.toLocaleTimeString('en-GB', { hour: '2-digit', minute: '2-digit' })
    );
  }
}
