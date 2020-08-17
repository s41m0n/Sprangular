import { Component, OnDestroy } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { takeUntil, first } from 'rxjs/operators';
import { CourseService } from '../services/course.service';
import { Subject, Observable, of } from 'rxjs';

/** AdminComponent
 * 
 *  This component handles the entire admin homepage 
 */
@Component({
  selector: 'app-admin',
  templateUrl: './admin.component.html'
})
export class AdminComponent implements OnDestroy {

  private destroy$: Subject<boolean> = new Subject<boolean>();  //Private subject to perform the unsubscriptions when the component is destroyed
  error: Observable<String>;
  navLinks = [                                                  //All available navigation links (tabs)
    {label: 'Professors',path: 'professors'},
    {label: 'Models',path: 'models'}
  ]

  constructor(private route: ActivatedRoute,
    private courseService: CourseService) {
  }

  ngOnDestroy(): void {
    //Announce a null course and unsubscribe
    this.courseService.currentCourseSubject.next(null);
    this.destroy$.next(true);
    this.destroy$.unsubscribe();
  }
}