import { Component, OnDestroy } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { takeUntil, first } from 'rxjs/operators';
import { CourseService } from '../services/course.service';
import { Subject, Observable, of } from 'rxjs';

/** ProfessorComponent
 * 
 *  This component handles the entire professor course homepage (for now set to /professor/courses/:coursename, later will be moved and all the other view will be developed)
 */
@Component({
  selector: 'app-professor',
  templateUrl: './professor.component.html'
})
export class ProfessorComponent implements OnDestroy {

  private destroy$: Subject<boolean> = new Subject<boolean>();  //Private subject to perform the unsubscriptions when the component is destroyed
  error: Observable<String>;
  navLinks = [                                                  //All available navigation links (tabs)
    {label: 'Students',path: 'students'},
    {label: 'Vms',path: 'vms'},
    {label: 'Assignments',path: 'assignments'}
  ]

  constructor(private route: ActivatedRoute,
    private courseService: CourseService) {
    
    //Register to route params to check and try to load the course requested as parameter (:coursename)
    this.route.params.pipe(takeUntil(this.destroy$)).subscribe(params => {
      const coursename = params['coursename'];
      //Retrieve the course associated to that parameter
      this.courseService.getCourseByPath(coursename).pipe(first()).subscribe(course => {
        //Announce the current course and, if empty, signal NotFound
        this.courseService.currentCourseSubject.next(course);
        this.error = of(course? null : `Course ${coursename} does not exist`);
      });
    });
  }

  ngOnDestroy(): void {
    //Announce a null course and unsubscribe
    this.courseService.currentCourseSubject.next(null);
    this.destroy$.next(true);
    this.destroy$.unsubscribe();
  }
}