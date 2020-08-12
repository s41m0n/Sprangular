import { Component, OnInit, OnDestroy } from '@angular/core';
import { Assignment } from 'src/app/models/assignment.model';
import { Course } from 'src/app/models/course.model';
import { CourseService } from 'src/app/services/course.service';
import { first, takeUntil } from 'rxjs/operators';
import { BroadcasterService } from 'src/app/services/broadcaster.service';
import { Subject } from 'rxjs';

/**
 * AssignmentsContainer
 * 
 * It displays the assignments view (WIP)
 */
@Component({
  selector: 'app-vms-cont',
  templateUrl: './assignments.container.html'
})
export class AssignmentsContainer implements OnInit, OnDestroy {

  private course : Course;                                      //The current selected course
  assignments: Assignment[] = [];                             //The current assignments
  private destroy$: Subject<boolean> = new Subject<boolean>();  //Private subject to perform the unsubscriptions when the component is destroyed

  constructor(private courseService: CourseService,
    private broadcaster: BroadcasterService) { }

  ngOnInit(): void {
    //Subscribe to the Broadcaster course selected, to update the current rendered course
    this.broadcaster.subscribeCourse().pipe(takeUntil(this.destroy$)).subscribe(course => {
      this.course = course;
      this.refreshAssignments();
    })    
  }

  ngOnDestroy() {
    /** Destroying subscription */
    this.destroy$.next(true);
    this.destroy$.unsubscribe();
  }

  /** Private function to refresh the list of enrolled students*/
  private refreshAssignments() {
    //Check if already received the current course
    if(!this.course) {
      this.assignments = [];
      return;
    }
    this.courseService.getCourseAssignments(this.course).pipe(first()).subscribe(assignments => this.assignments = assignments);
  }

}
