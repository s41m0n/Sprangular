import {Component} from '@angular/core';
import {Assignment} from '../../models/assignment.model';
import {CourseService} from '../../services/course.service';
import {ActivatedRoute, Router} from '@angular/router';
import {first} from 'rxjs/operators';
import {ImageViewerDialogComponent} from '../../modals/image-viewer/image-viewer-dialog.component';
import {MatDialog} from '@angular/material/dialog';
import {AssignmentAndUploadService} from '../../services/assignment-and-upload.service';
import {AssignmentSolutionDetails} from '../../models/assignment-solution-details.model';

/**
 * AssignmentsContainer
 *
 * It displays the assignments view (WIP)
 */
@Component({
  selector: 'app-tab-professor-vms-cont',
  templateUrl: './tab-assignments.container.html'
})
export class TabProfessorAssignmentsContComponent {
  assignments: Assignment[] = [];                             // The current assignments
  assignmentSolutions: AssignmentSolutionDetails[] = [];

  constructor(private courseService: CourseService,
              private assignmentService: AssignmentAndUploadService,
              public dialog: MatDialog,
              private router: Router,
              private route: ActivatedRoute) {
    this.courseService.getCourseAssignments(this.courseService.currentCourseSubject.value)
        .pipe(first()).subscribe(assignments => {
          this.assignments = assignments;
          this.route.queryParams.subscribe((queryParam) =>
              queryParam && queryParam.professorAssignment ? this.viewAssignment(queryParam.professorAssignment) : null);
    });
  }

  viewAssignment(assId: string) {
    const assignment = this.assignments.find(a => a.id.toString() === assId);
    this.assignmentService.getDocument(assignment.id).pipe(first()).subscribe(instance => {
      if (!instance) {
        this.router.navigate([this.router.url.split('?')[0]]);
        return;
      }
      const url = URL.createObjectURL(instance);
      const dialogRef = this.dialog.open(ImageViewerDialogComponent, {
        data: {title: `Assignment: ${assignment.id} - ${assignment.name}`,
          imageSrc: url,
          downloadable: true,
          dl_name: `assignment_${assignment.id}`
        }
      });
      dialogRef.afterClosed().subscribe(() => {
        URL.revokeObjectURL(url);
        this.router.navigate([this.router.url.split('?')[0]]);
      });
    });
  }

  viewDocument(object: any) {
    this.assignmentService.getUploadDocument(object.upId).pipe(first()).subscribe(instance => {
      if (!instance) {
        this.router.navigate([this.router.url.split('?')[0]], {queryParams: {solution: object.solId}});
        return;
      }
      const url = URL.createObjectURL(instance);
      const dialogRef = this.dialog.open(ImageViewerDialogComponent, {
        data: {title: `Upload: ${object.upId}`,
          imageSrc: url,
          downloadable: true,
          dl_name: `upload_${object.upId}`
        }
      });
      dialogRef.afterClosed().subscribe(() => {
        URL.revokeObjectURL(url);
        this.router.navigate([this.router.url.split('?')[0]], {queryParams: {solution: object.solId}});
      });
    });
  }

  getAssignmentSolutions(assignmentId: number) {
    this.assignmentService.getSolutionsForAssignment(assignmentId).pipe(first()).subscribe(
        solutions => this.assignmentSolutions = solutions.sort(AssignmentSolutionDetails.compare));
  }
}
