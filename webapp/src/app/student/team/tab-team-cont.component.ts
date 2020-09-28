import {Component, OnDestroy, OnInit} from '@angular/core';
import {first, mergeMap, switchMap, takeUntil} from 'rxjs/operators';
import {from, Observable, Subject} from 'rxjs';

import {Student} from '../../models/student.model';
import {Course} from '../../models/course.model';

import {StudentService} from '../../services/student.service';
import {CourseService} from '../../services/course.service';
import {Team} from 'src/app/models/team.model';
import {TeamService} from 'src/app/services/team.service';
import {AuthService} from 'src/app/services/auth.service';
import {TeamProposal} from 'src/app/models/team-proposal.model';

/**
 * TabTeamContainer class
 *
 * It contains the TabTeamComponent
 */
@Component({
  selector: 'app-tab-team-cont',
  templateUrl: './tab-team-cont.component.html',
})
export class TabTeamContComponent implements OnInit, OnDestroy {

  private course: Course;                                      // The current selected course
  public team: Team;
  availableStudents: Student[] = [];                             // The current enrolled list
  filteredStudents: Observable<Student[]>;                     // The list of students matching a criteria
  private searchTerms = new Subject<string>();                  // The search criteria emitter
  private destroy$: Subject<boolean> = new Subject<boolean>(); // Private subject to perform the unsubscriptions when component is destroyed

  constructor(private studentService: StudentService,
              private courseService: CourseService,
              private teamService: TeamService,
              private authService: AuthService) {
  }

  ngOnInit(): void {
    // Subscribe to current course
    this.courseService.currentCourseSubject.asObservable().pipe(takeUntil(this.destroy$)).subscribe(course => this.course = course);
    // Subscribe to current team
    this.teamService.currentTeamSubject.asObservable().pipe(takeUntil(this.destroy$)).subscribe(team => {
      this.team = team;
      this.refreshAvailableStudents();
    });
    // Subscribe to the search terms emitter
    this.filteredStudents = this.searchTerms.pipe(
        takeUntil(this.destroy$),
        // switch to new search observable each time the term changes
        switchMap((name: string) => this.studentService.searchStudentsInCourseAvailable(name, this.course)),
    );
  }

  ngOnDestroy() {
    /** Destroying subscription */
    this.destroy$.next(true);
    this.destroy$.unsubscribe();
  }

  /**
   * Function to push new search terms
   *
   * @param(name) the new search terms
   */
  searchStudents(name: string): void {
    this.searchTerms.next(name);
  }

  submitTeam(proposal: TeamProposal): void {
    console.log(proposal);
    this.teamService.createTeam(proposal.name, this.course).subscribe(team => {
      if (team.id) {
        from(proposal.students).pipe(
            mergeMap(student => {
              return this.studentService.setStudentTeam(team.id, student);
            })
        ).subscribe(x => alert('daje')); // TODO: also modify team for the current user and modify data in order to render the new page
      }
    });
  }

  /** Private function to refresh the list of enrolled students */
  private refreshAvailableStudents() {
    // Check if already received the current course or has a team
    if (!this.course || this.team) {
      this.availableStudents = [];
      return;
    }
    this.studentService.searchStudentsInCourseAvailable('', this.course, true)
        .pipe(first()).subscribe(students => this.availableStudents = students);
  }
}
