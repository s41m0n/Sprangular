import {Component, OnDestroy} from '@angular/core';
import {ActivatedRoute} from '@angular/router';
import {first, takeUntil} from 'rxjs/operators';
import {CourseService} from '../services/course.service';
import {TeamService} from '../services/team.service';
import {Observable, of, Subject} from 'rxjs';
import {StudentService} from '../services/student.service';
import {AuthService} from '../services/auth.service';

/** StudentComponent
 *
 *  This component handles the entire student course homepage (for now set to /student/courses/:coursename,
 *    later will be moved and all the other view will be developed)
 */
@Component({
  selector: 'app-student',
  templateUrl: './student.component.html'
})
export class StudentComponent implements OnDestroy {

  private destroy$: Subject<boolean> = new Subject<boolean>(); // Private subject to perform the unsubscriptions when component is destroyed
  error: Observable<string>;
  navLinks = [                                                  // All available navigation links (tabs)
    {label: 'Team', path: 'teams'},
    {label: 'Vms', path: 'vms'},
    {label: 'Assignments', path: 'assignments'}
  ];

  constructor(private route: ActivatedRoute,
              private courseService: CourseService,
              private teamService: TeamService,
              private studentService: StudentService,
              private authService: AuthService) {

    // Register to route params to check and try to load the course requested as parameter (:coursename)
    this.route.params.pipe(takeUntil(this.destroy$)).subscribe(params => {
      const coursename = params.coursename;
      // Retrieve the course associated to that parameter
      this.courseService.getCourseByPath(coursename).pipe(takeUntil(this.destroy$)).subscribe(course => {
        // Announce the current course and, if empty, signal NotFound
        this.courseService.currentCourseSubject.next(course);
        if (!course || !course.acronym) {
          this.error = of(`Course ${coursename} does not exist`);
        } else {
          this.error = of(null);
          this.studentService.getStudentByEmail(this.authService.currentUserValue.id).pipe(first()).subscribe(student => {
            /*if (student.team && student.team.courseId === course.acronym) {
              this.studentService.getStudentsInTeam(student.teamId).pipe(first()).subscribe(students => {
                student.team.students = students;
                teamService.currentTeamSubject.next(student.team);
              });
            } else {
              teamService.currentTeamSubject.next(null);
            }*/
          });
        }
      });
    });
  }

  ngOnDestroy(): void {
    // Announce a null course and unsubscribe
    this.courseService.currentCourseSubject.next(null);
    this.teamService.currentTeamSubject.next(null);
    this.destroy$.next(true);
    this.destroy$.unsubscribe();
  }
}
