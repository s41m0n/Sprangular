import { Component, OnInit, OnDestroy } from '@angular/core';
import { VM } from 'src/app/models/vm.model';
import { Course } from 'src/app/models/course.model';
import { CourseService } from 'src/app/services/course.service';
import { first, takeUntil } from 'rxjs/operators';
import { BroadcasterService } from 'src/app/services/broadcaster.service';
import { Subject } from 'rxjs';

/**
 * VmsContainer
 * 
 * It displays the Vms view (WIP)
 */
@Component({
  selector: 'app-vms-cont',
  templateUrl: './vms.container.html'
})
export class VmsContainer implements OnInit, OnDestroy {

  private course : Course;                                      //The current selected course
  vms: VM[] = [];                             //The current vms
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
      this.vms = [];
      return;
    }
    this.courseService.getCourseVMs(this.course).pipe(first()).subscribe(vms => this.vms = vms);
  }

}

