import {Component, Input} from '@angular/core';
import {MatTableDataSource} from '@angular/material/table';
import {animate, state, style, transition, trigger} from '@angular/animations';
import {first} from 'rxjs/operators';
import {Upload} from '../../models/upload.model';
import {StudentAssignmentDetails} from '../../models/student-assignment.details';
import {AssignmentService} from '../../services/assignment.service';
import {ImageViewerDialogComponent} from '../../modals/image-viewer/image-viewer-dialog.component';
import {MatDialog} from '@angular/material/dialog';
import {DomSanitizer} from '@angular/platform-browser';
import {AssignmentStatus} from '../../models/assignment-solution.model';
import {CourseService} from '../../services/course.service';

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

  assignmentUploads: Upload[];

  @Input() set assignments(assignments: StudentAssignmentDetails[]) {
    this.regularDataSource.data = assignments.filter(assignment => Date.now() < Date.parse(assignment.dueDate));
    this.expiredDataSource.data = assignments.filter(assignment => Date.now() > Date.parse(assignment.dueDate));
  }

  expandedElement: StudentAssignmentDetails | null;

  constructor(private assignmentService: AssignmentService,
              private courseService: CourseService,
              public dialog: MatDialog,
              private sanitizer: DomSanitizer) {
  }

  showUploads(element) {
    this.expandedElement = this.expandedElement === element ? null : element;
    if (this.expandedElement === null) {
      return;
    }
    this.assignmentService.getAssignmentSolutionUploads(element.assignmentSolutionId)
        .pipe(
        first()
    ).subscribe(uploads => this.assignmentUploads = uploads.sort(Upload.compare));
  }

  dateString(statusTs: string): string {
    const date = new Date(statusTs);
    return (
      date.toLocaleDateString('en-GB') +
      ' at ' +
      date.toLocaleTimeString('en-GB', { hour: '2-digit', minute: '2-digit' })
    );
  }

  viewAssignment(element: StudentAssignmentDetails) {
    this.assignmentService.readStudentAssignment(element.assignmentId).pipe(first()).subscribe(instance => {
      if (!instance) { return; }
      const url = URL.createObjectURL(instance);
      const dialogRef = this.dialog.open(ImageViewerDialogComponent, {
        data: {title : `Assignment: ${element.assignmentId} - ${element.name}`, imageSrc: this.sanitizer.bypassSecurityTrustUrl(url)}
      });
      dialogRef.afterClosed().subscribe(() => {
        URL.revokeObjectURL(url);
        if (element.status === AssignmentStatus.NULL) {
          this.refreshAssignmentsDetails();
        }
      });
    });
  }

  private refreshAssignmentsDetails() {
    this.courseService.getStudentCourseAssignments(this.courseService.course.value.acronym)
        .pipe(first())
        .subscribe(as => this.assignments = as);
  }
}
