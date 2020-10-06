import {Component} from '@angular/core';
import {Assignment} from '../../models/assignment.model';
import {CourseService} from '../../services/course.service';
import {first} from 'rxjs/operators';

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

  constructor(private courseService: CourseService) {
    this.courseService.getCourseAssignments(this.courseService.currentCourseSubject.value).pipe(first()).subscribe(assignments => this.assignments = assignments);
  }

}
