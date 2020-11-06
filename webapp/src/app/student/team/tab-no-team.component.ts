import {
  AfterViewInit,
  Component,
  EventEmitter,
  Input,
  OnDestroy,
  OnInit,
  Output,
  ViewChild,
} from '@angular/core';
import { MatSort } from '@angular/material/sort';
import { MatPaginator } from '@angular/material/paginator';
import { MatTableDataSource } from '@angular/material/table';
import { FormControl } from '@angular/forms';
import { Observable, Subject } from 'rxjs';
import { debounceTime, distinctUntilChanged, takeUntil } from 'rxjs/operators';
import { TeamProposal } from '../../models/team-proposal.model';
import { Student } from '../../models/student.model';
import { Proposal } from '../../models/proposal.model';
import * as moment from 'moment';
import {Course} from '../../models/course.model';
import {ToastrService} from 'ngx-toastr';

/**
 * TabNoTeamComponent
 *
 * It represents the view for the no team tab
 */
@Component({
  selector: 'app-tab-no-team',
  templateUrl: './tab-no-team.component.html',
  styleUrls: ['./tab-no-team.component.css'],
})
export class TabNoTeamComponent implements AfterViewInit, OnInit, OnDestroy {
  minDate = moment(new Date()).format('YYYY-MM-DD');
  maxDate = moment(this.minDate).add(9, 'M').format('YYYY-MM-DD');

  currentUser: Student;
  course: Course;
  date: string = null;
  chosenMembers: Student[] = [];
  dataSource = new MatTableDataSource<Student>(); // Table datasource dynamically modified
  dataSourceProposals = new MatTableDataSource<Proposal>();
  colsToDisplay = ['select', 'id', 'name', 'surname']; // Columns to be displayed in the table
  colsToDisplayProposals = [
    'cancel',
    'proposalCreator',
    'teamName',
    'membersAndStatus',
    'deadline',
    'select',
  ];
  addStudentControl = new FormControl(); // Form control to input the user to be enrolled
  teamNameControl = new FormControl();
  private destroy$: Subject<boolean> = new Subject<boolean>(); // Private subject to perform the unsubscriptions when component is destroyed
  @Output() searchStudentsEvent = new EventEmitter<string>(); // Event emitter for the search students (autocompletions)
  @Output() submitTeamEvent = new EventEmitter<TeamProposal>(); // Event emitter for the search students (autocompletions)
  @Output() proposalAcceptedEvent = new EventEmitter<string>();
  @Output() proposalRejectedEvent = new EventEmitter<string>();
  @Output() proposalDeletedEvent = new EventEmitter<string>();
  @ViewChild(MatSort, { static: true }) sort: MatSort; // Mat sort for the table
  @ViewChild(MatPaginator) paginator: MatPaginator; // Mat paginator for the table
  @Input() filteredStudents: Observable<Student[]>; // List of students matching search criteria
  @Input() set availableStudents(students: Student[]) {
    // Enrolled students to be displayed in the table
    const userInfo = JSON.parse(localStorage.getItem('currentUser'));
    this.currentUser = students.find((s) => s.id === userInfo.id);
    this.dataSource.data = students.filter((s) => s.id !== userInfo.id);
  }
  @Input() set proposals(proposals: Proposal[]) {
    this.dataSourceProposals.data = proposals;
  }
  @Input() set currentCourse(course: Course) {
    this.course = course;
  }

  constructor(private toastrService: ToastrService) {
  }

  ngOnInit() {
    /** Setting filter to autocomplete */
    this.addStudentControl.valueChanges
        .pipe(
            takeUntil(this.destroy$),
            // wait 300ms after each keystroke before considering the term
            debounceTime(300),
            // ignore new term if same as previous term
            distinctUntilChanged()
        )
        .subscribe((name: string) => this.searchStudentsEvent.emit(name));
  }

  ngOnDestroy() {
    /** Destroying subscription */
    this.destroy$.next(true);
    this.destroy$.unsubscribe();
  }

  ngAfterViewInit() {
    /** Setting paginator and sort after ng containers are initialized */
    this.dataSource.paginator = this.paginator;
    this.dataSource.sort = this.sort;
  }

  selectDate(date: string) {
    this.date = date;
  }

  removeWishMember(student: Student) {
    this.chosenMembers = this.chosenMembers.filter((x) => x.id !== student.id);
  }

  addWishMember(student: Student) {
    if (!this.chosenMembers.find((x) => x.id === student.id)) {
      if (this.chosenMembers.length + 2 > this.course.teamMaxSize) {
        this.toastrService.info(
            `You already reached the maximum limit for members (MAX ${this.course.teamMaxSize})`,
            'Ops! Invalid operation 😅');
        return;
      }
      this.chosenMembers.push(student);
      this.addStudentControl.setValue('');
    }
  }

  submitTeam() {
    if (this.chosenMembers.length + 1 < this.course.teamMinSize) {
      this.toastrService.info(
          `Too few members (MIN ${this.course.teamMinSize})`,
          'Ops! Invalid operation 😅');
      return;
    }
    this.chosenMembers.push(this.currentUser);
    let deadlineDate: Date;
    if (this.date) {
      deadlineDate = new Date(this.date);
    } else {
      deadlineDate = new Date();
    }
    deadlineDate.setDate(deadlineDate.getDate() + 1);
    if (
        this.teamNameControl.valid &&
        this.chosenMembers.length &&
        deadlineDate >= new Date()
    ) {
      this.submitTeamEvent.emit(
          new TeamProposal(
              this.teamNameControl.value,
              this.chosenMembers.map(x => x.id),
              deadlineDate.getTime().toString(10)
          )
      );
      this.chosenMembers = [];
      this.teamNameControl.setValue('');
    }
  }

  /** Function to set the value displayed in input and mat-options */
  displayFn(student: Student): string {
    return student ? Student.displayFn(student) : '';
  }

  dateString(statusTs: string): string {
    const date = new Date(statusTs);
    return (
        date.toLocaleDateString('en-GB') +
        ' at ' +
        date.toLocaleTimeString('en-GB', { hour: '2-digit', minute: '2-digit' })
    );
  }

  displayFnMembers(members: string[]): string {
    let returnedString = '';
    members.forEach((s) => (returnedString += s + '\n'));
    return returnedString;
  }

  acceptProposal(token: string) {
    this.proposalAcceptedEvent.emit(token);
  }

  rejectProposal(token: string) {
    this.proposalRejectedEvent.emit(token);
  }

  accepted(members: string[]): boolean {
    if (
        members.includes(
            `${this.currentUser.name} ${this.currentUser.surname} (${this.currentUser.id}) : ACCEPTED`
        )
    ) {
      return true;
    }
  }

  deleteProposal(token: string) {
    this.proposalDeletedEvent.emit(token);
  }

  isPickable(id: string) {
    return !this.chosenMembers.find(x => x.id === id);
  }
}
