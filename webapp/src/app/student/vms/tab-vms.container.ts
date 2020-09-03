import { Component, OnInit, OnDestroy } from '@angular/core';
import { VM } from '../../models/vm.model';
import { Course } from '../../models/course.model';
import { CourseService } from '../../services/course.service';
import { first, takeUntil } from 'rxjs/operators';
import { Subject } from 'rxjs';
import { TeamService } from 'src/app/services/team.service';
import { Team } from 'src/app/models/team.model';

/**
 * VmsContainer
 *
 * It displays the Vms view (WIP)
 */
@Component({
  selector: 'app-tab-student-vms-cont',
  templateUrl: './tab-vms.container.html'
})
export class TabStudentVmsContainer implements OnInit, OnDestroy {

  private course: Course;                                      // The current selected course
  private team: Team;
  vms: VM[] = [];                             // The current vms
  private destroy$: Subject<boolean> = new Subject<boolean>();  // Private subject to perform the unsubscriptions when the component is destroyed

  constructor(private teamService: TeamService,
              private courseService: CourseService) { }

  ngOnInit(): void {
    // Subscribe to the Broadcaster course selected, to update the current rendered course
    this.courseService.currentCourseSubject.asObservable().pipe(takeUntil(this.destroy$)).subscribe(course => {
      this.course = course;
    });
    this.teamService.currentTeamSubject.asObservable().pipe(takeUntil(this.destroy$)).subscribe(team => {
      this.team = team;
      this.refreshVMs();
    });
  }

  ngOnDestroy() {
    /** Destroying subscription */
    this.destroy$.next(true);
    this.destroy$.unsubscribe();
  }


  /** Private function to refresh the list of enrolled students*/
  private refreshVMs() {
    // Check if already received the current course
    if (!this.course || !this.team) {
      this.vms = [];
      return;
    }
    this.teamService.getTeamVms(this.team).pipe(first()).subscribe(vms => this.vms = vms);
  }

    newVm() {
        console.log('method to implement');
    }
}

