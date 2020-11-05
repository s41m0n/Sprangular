import {Component} from '@angular/core';
import {Assignment} from '../../models/assignment.model';
import {CourseService} from '../../services/course.service';
import {ActivatedRoute, Router} from '@angular/router';
import {first} from 'rxjs/operators';
import {ImageViewerDialogComponent} from '../../modals/image-viewer/image-viewer-dialog.component';
import {MatDialog} from '@angular/material/dialog';
import {AssignmentAndUploadService} from '../../services/assignment-and-upload.service';

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

  constructor(private courseService: CourseService,
              private assignmentService: AssignmentAndUploadService,
              public dialog: MatDialog,
              private router: Router,
              private route: ActivatedRoute) {
    this.courseService.getCourseAssignments(this.courseService.currentCourseSubject.value)
        .pipe(first()).subscribe(assignments => {
          this.assignments = assignments;
          this.route.queryParams.subscribe((queryParam) => queryParam && queryParam.professorAssignment ? this.viewAssignment() : null);
    });
  }

  viewAssignment() {
    const assignment = this.assignments.find(a => a.id.toString() === this.route.snapshot.queryParamMap.get('professorAssignment'));
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
}
