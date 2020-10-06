import {Component} from '@angular/core';
import {CourseService} from '../../services/course.service';
import {first} from 'rxjs/operators';
import {Assignment} from '../../models/assignment.model';

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
  assignments: Assignment[] = [];                             // The current assignments

  constructor(private courseService: CourseService) {
    this.courseService.getCourseAssignments(this.courseService.currentCourseSubject.value).pipe(first()).subscribe(assignments => this.assignments = assignments);
  }
}

