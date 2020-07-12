import { Component, OnDestroy } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { takeUntil } from 'rxjs/operators';
import { CourseService } from '../services/course.service';
import { BroadcasterService } from '../services/broadcaster.service';
import { Subject } from 'rxjs';

/** ProfessorComponent
 * 
 *  This component handles the entire professor course homepage (for now set to /professor/courses/:coursename, later will be moved and all the other view will be developed)
 */
@Component({
  selector: 'app-professor',
  templateUrl: './professor.component.html'
})
export class ProfessorComponent implements OnDestroy {

  isHomepage : boolean = true;                                  //Variable to decide whether to display course homepage or subview                                       
  private destroy$: Subject<boolean> = new Subject<boolean>();  //Private subject to perform the unsubscriptions when the component is destroyed
  navLinks = [                                                  //All available navigation links (tabs)
    {label: 'Students',path: 'students'},
    {label: 'Vms',path: 'vms'},
    {label: 'Assignments',path: 'assignments'}
  ]

  constructor(private route: ActivatedRoute,
    private courseService: CourseService,
    private broadcaster: BroadcasterService) {
    
    //Register to route params to check and try to load the course requested as parameter (:coursename)
    this.route.params.pipe(takeUntil(this.destroy$)).subscribe(params => {
      const coursename = params['coursename'];
      //Retrieve the course associated to that parameter
      this.courseService.getCourseByPath(coursename).pipe(takeUntil(this.destroy$)).subscribe(course => {
        //Announce the current course and, if empty, signal NotFound
        this.broadcaster.broadcastCourse(course)
        if(!course) this.broadcaster.broadcastNotFound(`Course ${coursename} does not exist`);
      });
    });
  }

  /** Function to retrieve current course value and display it in view */
  getCurrentCourse() {
    return this.broadcaster.getCurrentCourseValue();
  }

  /** Function to set that the user has required another page != Homepage */
  onActivate() {
    this.isHomepage = false;
  }

  /** Function to set that the user has come back to the homepage */
  onDeactivate() {
    this.isHomepage = true;
  }

  ngOnDestroy(): void {
    //Announce a null course and unsubscribe
    this.broadcaster.broadcastCourse(null);
    this.destroy$.next(true);
    this.destroy$.unsubscribe();
  }
}