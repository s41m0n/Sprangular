import { Component } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { LoginDialogComponent } from './modals/login/login-dialog.component';
import { AuthService } from './services/auth.service';
import { ActivatedRoute, Router } from '@angular/router';
import { User } from './models/user.model';
import { first } from 'rxjs/operators';
import { Course } from './models/course.model';
import { CourseService } from './services/course.service';
import { Observable, of } from 'rxjs';
import { StudentService } from './services/student.service';
import { ProfessorService } from './services/professor.service';
import { RegisterDialogComponent } from './modals/register/register-dialog.component';
import { NewCourseDialogComponent } from './modals/new-course/new-course-dialog.component';
import {NewAssignmentDialogComponent} from './modals/new-assignment/new-assignment-dialog.component';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent {
  currentUser: User; // Variable to keep track of the current user
  courseList: Observable<Course[]>; // Variable to keep track (asynchronously) of the courses
  selectedCourseName: string; // Variable to store the current selected course name (notified by sub routes via Broadcaster service)
  inModal: boolean; // Variable to check if a modal is already open
  course: Course;

  // Unsubscribes are not performed here since alive till this root component is always alive and must be updated
  constructor(
    public dialog: MatDialog,
    private authService: AuthService,
    private route: ActivatedRoute,
    private courseService: CourseService,
    private router: Router,
    private studentService: StudentService,
    private professorService: ProfessorService
  ) {
    // Subscrive to current user and, if logged, refresh course list
    this.authService.getUserObservable().subscribe((user: User) => {
      this.currentUser = user;
      this.refreshCourses();
    });

    this.inModal = false;

    // Subscribe to Broadcaster selected course subject

    this.courseService.course
      .asObservable()
      .subscribe((x) => (this.course = x));

    this.courseService.currentCourseSubject
      .asObservable()
      .subscribe((course) => (this.selectedCourseName = course));

    // Subscribing to the route queryParam to check doLogin parameter
    this.route.queryParams.subscribe((queryParam) =>
      queryParam && queryParam.doLogin ? this.openLogin() : null
    );

    this.route.queryParams.subscribe((queryParam) =>
      queryParam && queryParam.doRegister ? this.openRegister() : null
    );

    this.route.queryParams.subscribe((queryParam) =>
      queryParam && queryParam.addCourse ? this.newCourse() : null
    );

    this.route.queryParams.subscribe((queryParam) =>
      queryParam && queryParam.addAssignment ? this.newAssignment() : null
    );
  }

  /** Login Dialog show function
   *
   *  This function opens a dialog window where the user
   *  can perform login. When the dialog closes, a value is returned
   *  in order to check whether the operation has been successfully
   *  executed (in that case the user is redirected to the previously desired page or root)
   *  or not.
   */
  openLogin() {
    this.inModal = true;
    const dialogRef = this.dialog.open(LoginDialogComponent, {
      width: '600px',
    });
    dialogRef
      .afterClosed()
      .pipe(first())
      .subscribe((result) => {
        if (result) {
          this.router.navigate([
            this.route.snapshot.queryParams.returnUrl || '/home',
          ]);
        } else {
          this.router.navigate(['/home']);
        }
        this.inModal = false;
      });
  }

  openRegister() {
    this.inModal = true;
    const dialogRef = this.dialog.open(RegisterDialogComponent, {
      width: '600px',
    });
    dialogRef
      .afterClosed()
      .pipe(first())
      .subscribe((result) => {
        if (result) {
          this.router.navigate([
            this.route.snapshot.queryParams.returnUrl || '/home',
          ]);
        } else {
          this.router.navigate(['/home']);
        }
        this.inModal = false;
      });
  }

  newCourse() {
    this.inModal = true;
    const dialogRef = this.dialog.open(NewCourseDialogComponent);
    dialogRef
      .afterClosed()
      .pipe(first())
      .subscribe((result) => {
        if (result) {
          this.refreshCourses();
          this.router.navigate([`/professor/courses/${result.acronym}`]);
        } else {
          this.router.navigate([this.router.url.split('?')[0]]);
        }
        this.inModal = false;
      });
  }

  newAssignment() {
    this.inModal = true;
    const dialogRef = this.dialog.open(NewAssignmentDialogComponent, {
      width: '400px',
    });
    dialogRef
        .afterClosed()
        .subscribe((result) => {
          if (result) {
            this.router.navigate([`/professor/courses/${this.selectedCourseName}/assignments`],
                {queryParams: {refreshAssignments: true}});
          } else {
            this.router.navigate([`/professor/courses/${this.selectedCourseName}/assignments`]);
          }
          this.inModal = false;
        });
  }

  /** Private function to refresh the list of courses */
  private refreshCourses() {
    if (!this.currentUser) {
      this.courseList = of([]);
    } else {
      if (this.currentUser.roles.includes('ROLE_STUDENT')) {
        this.courseList = this.studentService
          .getStudentCourses(this.currentUser.id)
          .pipe(first());
      } else if (this.currentUser.roles.includes('ROLE_PROFESSOR')) {
        this.courseList = this.professorService
          .getProfessorCourses(this.currentUser.id)
          .pipe(first());
      }
    }
  }

  /** Logout function
   *
   *  After calling all the proper functions to erase local data,
   *  the user is redirected to the webservice root
   */
  logout() {
    this.authService.logout();
    this.selectedCourseName = null;
    this.router.navigate(['/home']);
  }

  courseStatusChanged(status: boolean) {
    if (status === this.course.enabled) {
      return;
    }
    this.courseService.changeCourseStatus(status).subscribe(x => {
      this.course.enabled = x;
      this.refreshCourses();
    });
  }
}
