import { Component, OnDestroy, OnInit } from '@angular/core';
import { first, switchMap, takeUntil } from 'rxjs/operators';
import { Observable, Subject } from 'rxjs';
import { Student } from '../../models/student.model';
import { StudentService } from '../../services/student.service';
import { TeamService } from 'src/app/services/team.service';
import { TeamProposal } from 'src/app/models/team-proposal.model';
import { CourseService } from 'src/app/services/course.service';
import { Team } from 'src/app/models/team.model';
import { Proposal } from 'src/app/models/proposal.model';
import { Router } from '@angular/router';
import {Course} from '../../models/course.model';

/**
 * TabTeamContainer class
 *
 * It contains the TabTeamComponent
 */
@Component({
  selector: 'app-tab-team-cont',
  templateUrl: './tab-team.container.html',
})
export class TabTeamContComponent implements OnInit, OnDestroy {
  team: Team;
  course: Course;
  availableStudents: Student[] = []; // The current enrolled list
  filteredStudents: Observable<Student[]>; // The list of students matching a criteria
  proposals: Proposal[] = [];
  private searchTerms = new Subject<string>(); // The search criteria emitter
  private destroy$: Subject<boolean> = new Subject<boolean>(); // Private subject to perform the unsubscriptions when component is destroyed

  constructor(
    private studentService: StudentService,
    private teamService: TeamService,
    private courseService: CourseService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.courseService.course
        .asObservable()
        .pipe(takeUntil(this.destroy$))
        .subscribe(c => this.course = c);
    this.courseService
      .getAvailableStudents()
      .pipe(first())
      .subscribe((students) => (this.availableStudents = students));
    this.teamService.currentTeamSubject
      .asObservable()
      .pipe(takeUntil(this.destroy$))
      .subscribe((team) => (this.team = team));
    // Subscribe to the search terms emitter
    this.filteredStudents = this.searchTerms.pipe(
      takeUntil(this.destroy$),
      // switch to new search observable each time the term changes
      switchMap((name: string) =>
        this.studentService.searchStudentsInCourseAvailable(name)
      )
    );
    this.refreshProposals();
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
    this.teamService
      .createTeam(proposal)
      .pipe(first())
      .subscribe(() => this.refreshProposals());
  }

  proposalAccepted(token: string) {
    this.teamService
      .acceptProposal(token)
      .pipe(first())
      .subscribe((result) => {
        if (result) {
          this.teamService
            .getStudentTeam()
            .pipe(first())
            .subscribe((t) => this.teamService.currentTeamSubject.next(t));
          this.router.navigate([this.router.url]);
        }
        this.refreshProposals();
      });
  }

  proposalRejected(token: string) {
    this.teamService
      .rejectProposal(token)
      .pipe(first())
      .subscribe(() => this.refreshProposals());
  }

  proposalDeleted(token: string) {
    this.teamService
      .deleteProposal(token)
      .pipe(first())
      .subscribe(() => this.refreshProposals());
  }

  private refreshProposals() {
    this.studentService
      .getTeamProposalsForCourse()
      .pipe(first())
      .subscribe((proposals) => (this.proposals = proposals));
  }
}
