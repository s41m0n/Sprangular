import {Component, OnDestroy} from '@angular/core';
import {ActivatedRoute} from '@angular/router';
import {first, takeUntil} from 'rxjs/operators';
import {CourseService} from '../services/course.service';
import {Observable, of, Subject} from 'rxjs';

/** AdminComponent
 *
 *  This component handles the entire admin homepage
 */
@Component({
  selector: 'app-admin',
  templateUrl: './admin.component.html'
})
export class AdminComponent implements OnDestroy {

  private destroy$: Subject<boolean> = new Subject<boolean>(); // Private subject to perform the unsubscriptions when component is destroyed
  error: Observable<string>;
  navLinks = [                                                  // All available navigation links (tabs)
    {label: 'Professors', path: 'professors'},
    {label: 'Models', path: 'models'}
  ];

  constructor(private route: ActivatedRoute,
              private courseService: CourseService) {
    // Register to route params to check and try to load the course requested as parameter (:coursename)
    this.route.params.pipe(takeUntil(this.destroy$)).subscribe(params => {
      const coursename = params.coursename;
      // Retrieve the course associated to that parameter
      this.courseService.getCourseByPath(coursename).pipe(first()).subscribe(course => {
        // Announce the current course and, if empty, signal NotFound
        this.courseService.currentCourseSubject.next(course);
        this.error = of(course ? null : `Course ${coursename} does not exist`);
      });
    });
  }

  ngOnDestroy(): void {
    // Announce a null course and unsubscribe
    this.courseService.currentCourseSubject.next(null);
    this.destroy$.next(true);
    this.destroy$.unsubscribe();
  }
}
