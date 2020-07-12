import { Component} from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { LoginDialogComponent } from './auth/login-dialog.component';
import { AuthService } from './services/auth.service';
import { ActivatedRoute, Router } from '@angular/router';
import { User } from './models/user.model';
import { first } from 'rxjs/operators';
import { Course } from './models/course.model';
import { CourseService } from './services/course.service';
import { Observable, of } from 'rxjs';
import { BroadcasterService } from './services/broadcaster.service';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html'
})
export class AppComponent{
  title = 'ai20-lab05';
  isNotFound : Observable<string>;        //Variable to keep track if a sub routes has signaled a NotFound error
  currentUser : User;                     //Variable to keep track of the current user
  courseList: Observable<Course[]>;       //Variable to keep track (asynchronously) of the courses
  selectedCourseName: string;             //Variable to store the current selected course name (notified by sub routes via Broadcaster service)

  //Unsubscribes are not performed here since alive till this root component is always alive and must be updated
  constructor(public dialog: MatDialog,
    private authService : AuthService,
    private broadcaster : BroadcasterService,
    private route: ActivatedRoute,
    private courseService: CourseService,
    private router: Router) {
      //Subscribe to Broadcaster NotFound event
      this.isNotFound = this.broadcaster.subscribeNotFound();
      //Subscrive to current user and, if logged, refresh course list      
      this.authService.currentUser.subscribe((user: User)  => {
        this.currentUser = user;
        this.courseList = user? this.courseService.getCourses().pipe(first()) : of([]);
      });

      //Subscribe to Broadcaster selected course subject
      this.broadcaster.subscribeCourse().subscribe(course => this.selectedCourseName = course? course.name : null);
      
      //Subscribing to the route queryParam to check doLogin parameter
      this.route.queryParams.subscribe(queryParam => queryParam && queryParam.doLogin? this.openLogin() : null);
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
    const dialogRef = this.dialog.open(LoginDialogComponent);
    dialogRef.afterClosed()
      .pipe(first())
      .subscribe(result => {
        if(result) this.router.navigate([this.route.snapshot.queryParams['returnUrl'] || '/']);
        else this.router.navigate(['/']);
    });
  }

  /** Logout function
   * 
   *  After calling all the proper functions to erase local data,
   *  the user is redirected to the webservice root
   */
  logout() {
    this.authService.logout();
    this.selectedCourseName = null;
    this.router.navigate(['/']);
  }
}

