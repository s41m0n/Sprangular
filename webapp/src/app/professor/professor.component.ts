import { Component } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import { CourseService } from '../services/course.service';

/** ProfessorComponent
 *
 *  This component handles the entire professor course homepage (for now set to /professor/courses/:coursename,
 *    later will be moved and all the other view will be developed)
 */
@Component({
  selector: 'app-professor',
  templateUrl: './professor.component.html',
})
export class ProfessorComponent {
  navLinks = [
    // All available navigation links (tabs)
    { label: 'Students', path: 'students' },
    { label: 'Vms', path: 'vms' },
    { label: 'Assignments', path: 'assignments' },
  ];
  private destroy$: Subject<boolean> = new Subject<boolean>(); // Private subject to perform the unsubscriptions when component is destroyed

  constructor(private courseService : CourseService,
    private route : ActivatedRoute){
    this.route.params.pipe(takeUntil(this.destroy$)).subscribe(params => {
      this.courseService.setNext(params.coursename);
    });
  }

  ngOnDestroy(): void {
    this.courseService.currentCourseSubject.next(null);
    this.destroy$.next(true);
    this.destroy$.unsubscribe();
  }
}
