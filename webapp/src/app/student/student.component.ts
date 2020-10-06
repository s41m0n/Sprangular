import {Component, OnDestroy} from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { Subject } from 'rxjs';
import { first, takeUntil } from 'rxjs/operators';
import { AuthService } from '../services/auth.service';
import { CourseService } from '../services/course.service';
import { TeamService } from '../services/team.service';

/** StudentComponent
 *
 *  This component handles the entire student course homepage (for now set to /student/courses/:coursename,
 *    later will be moved and all the other view will be developed)
 */
@Component({
  selector: 'app-student',
  templateUrl: './student.component.html'
})
export class StudentComponent implements OnDestroy{

  navLinks = [                                                  // All available navigation links (tabs)
    {label: 'Team', path: 'teams'},
    {label: 'Vms', path: 'vms'},
    {label: 'Assignments', path: 'assignments'}
  ];
  private destroy$: Subject<boolean> = new Subject<boolean>(); // Private subject to perform the unsubscriptions when component is destroyed

  constructor(private courseService : CourseService,
    private teamService : TeamService,
    private route : ActivatedRoute){
    this.route.params.pipe(takeUntil(this.destroy$)).subscribe(params => {
      this.courseService.currentCourseSubject.next(params.coursename)
      this.teamService.getStudentTeam().pipe(first()).subscribe(team => this.teamService.currentTeamSubject.next(team));
    });
  }
  
  ngOnDestroy(): void {
    this.courseService.currentCourseSubject.next(null);
    this.teamService.currentTeamSubject.next(null);
    this.destroy$.next(true);
    this.destroy$.unsubscribe();
  }
}

