import {Component} from '@angular/core';
import {CourseService} from '../../services/course.service';
import {first} from 'rxjs/operators';
import {AssignmentStatus} from '../../models/assignment-solution.model';
import {StudentAssignmentDetails} from '../../models/student-assignment-details.model';
import {ImageViewerDialogComponent} from '../../modals/image-viewer/image-viewer-dialog.component';
import {MatDialog} from '@angular/material/dialog';
import {AssignmentAndUploadService} from '../../services/assignment-and-upload.service';
import {ActivatedRoute, Router} from '@angular/router';
import {Upload} from '../../models/upload.model';

/**
 * AssignmentsContainer
 *
 * It displays the Assignments view
 */
@Component({
  selector: 'app-tab-student-assignments-cont',
  templateUrl: './tab-assignments.container.html'
})
export class TabStudentAssignmentsContComponent {
  assignments: StudentAssignmentDetails[] = [];
  assignmentUploads: Upload[] = [];

  constructor(private courseService: CourseService,
              private assignmentService: AssignmentAndUploadService,
              private router: Router,
              private route: ActivatedRoute,
              public dialog: MatDialog) {
    this.courseService.getStudentCourseAssignments(this.courseService.currentCourseSubject.value).pipe(first()).subscribe(assignments => {
      this.assignments = assignments;
      this.route.queryParams.subscribe((queryParam) =>
          queryParam && queryParam.studentAssignment ? this.viewAssignment(queryParam.studentAssignment) : null
      );
    });
  }

  viewAssignment(assId: string) {
    const element = this.assignments.find(a => a.assignmentId.toString() === assId);
    this.assignmentService.readStudentAssignment(element.assignmentId).pipe(first()).subscribe(instance => {
      if (!instance) { return; }
      const url = URL.createObjectURL(instance);
      const dialogRef = this.dialog.open(ImageViewerDialogComponent, {
        data: {title: `Assignment: ${element.assignmentId} - ${element.name}`,
          imageSrc: url,
          downloadable: true,
          dl_name: `assignment_${element.assignmentId}`
        }
      });
      dialogRef.afterClosed().subscribe(() => {
        URL.revokeObjectURL(url);
        if (element.status === AssignmentStatus.NULL) {
          this.refreshAssignmentsDetails();
        }
        this.router.navigate([this.router.url.split('?')[0]]);
      });
    });
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

  private refreshAssignmentsDetails() {
    this.courseService.getStudentCourseAssignments(this.courseService.course.value.acronym)
        .pipe(first())
        .subscribe(as => this.assignments = as);
  }

  refreshUploads(assSolId: number) {
    this.assignmentService.getAssignmentSolutionUploads(assSolId)
        .pipe(first())
        .subscribe((uploads) => {
          this.assignmentUploads = uploads;
        });
  }

  getUploads(assSolId: number) {
    this.assignmentService.getAssignmentSolutionUploads(assSolId)
        .pipe(
            first()
        ).subscribe(uploads => this.assignmentUploads = uploads);
  }
}

