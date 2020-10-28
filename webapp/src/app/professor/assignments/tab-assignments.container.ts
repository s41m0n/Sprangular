import {Component, OnDestroy} from '@angular/core';
import {Assignment} from '../../models/assignment.model';
import {CourseService} from '../../services/course.service';
import {ActivatedRoute, Router} from '@angular/router';
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
export class TabProfessorAssignmentsContComponent implements OnDestroy {

  assignments: Assignment[] = [];                             // The current assignments
  navSub;

  constructor(private courseService: CourseService,
              private router: Router,
              private route: ActivatedRoute) {
    this.courseService.getCourseAssignments(this.courseService.currentCourseSubject.value)
        .pipe(first()).subscribe(assignments => this.assignments = assignments);
    this.navSub = this.route.queryParams.subscribe((queryParam) =>
          queryParam && queryParam.refreshAssignments ? this.refreshAssignments() : null);
  }

  refreshAssignments() {
    this.courseService.getCourseAssignments(this.courseService.currentCourseSubject.value)
        .pipe(first()).subscribe(assignments => this.assignments = assignments);
    this.router.navigate([this.router.url.split('?')[0]]);
  }

  ngOnDestroy() {
    if (this.navSub) {
      this.navSub.unsubscribe();
    }
  }
}
