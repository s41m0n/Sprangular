import {Component} from '@angular/core';
import {CourseService} from '../../services/course.service';
import {first} from 'rxjs/operators';
import {Assignment} from '../../models/assignment.model';
import {StudentService} from '../../services/student.service';
import {AssignmentSolution} from '../../models/assignment-solution.model';
import {AuthService} from '../../services/auth.service';
import {User} from "../../models/user.model";

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
  student: User;

  constructor(private courseService: CourseService,
              private authService: AuthService) {
    this.courseService.getCourseAssignments(
        this.courseService.currentCourseSubject.value
    ).pipe(
        first()
    ).subscribe(assignments => this.assignments = assignments);

    console.log(authService.currentUserValue);
    this.student = authService.currentUserValue;
  }
}

