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
  currentUser: Student;
  date: string = null;
  chosenMembers: Student[] = [];
  dataSource = new MatTableDataSource<Student>(); // Table datasource dynamically modified
  colsToDisplay = ['select', 'id', 'name', 'surname']; // Columns to be displayed in the table
  addStudentControl = new FormControl(); // Form control to input the user to be enrolled
  teamNameControl = new FormControl();
  private destroy$: Subject<boolean> = new Subject<boolean>(); // Private subject to perform the unsubscriptions when component is destroyed
  @Output() searchStudentsEvent = new EventEmitter<string>(); // Event emitter for the search students (autocompletions)
  @Output() submitTeamEvent = new EventEmitter<TeamProposal>(); // Event emitter for the search students (autocompletions)
  @ViewChild(MatSort, { static: true }) sort: MatSort; // Mat sort for the table
  @ViewChild(MatPaginator) paginator: MatPaginator; // Mat paginator for the table
  @Input() filteredStudents: Observable<Student[]>; // List of students matching search criteria
  @Input() set availableStudents(students: Student[]) {
    // Enrolled students to be displayed in the table
    const userInfo = JSON.parse(localStorage.getItem('currentUser'));
    this.currentUser = students.find((s) => s.id === userInfo.id);
    this.dataSource.data = students.filter((s) => s.id !== userInfo.id);
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
      this.chosenMembers.push(student);
    }
  }

  submitTeam() {
    this.chosenMembers.push(this.currentUser);
    const dateToPush = new Date(this.date);
    if (
      this.teamNameControl.valid &&
      this.chosenMembers.length &&
      dateToPush >= new Date()
    ) {
      this.submitTeamEvent.emit(
        new TeamProposal(
          this.teamNameControl.value,
          this.chosenMembers.map((x) => x.id),
          dateToPush.getTime()
        )
      );
    }
  }

  /** Function to set the value displayed in input and mat-options */
  displayFn(student: Student): string {
    return student ? Student.displayFn(student) : '';
  }
}
