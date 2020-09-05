import { Component, ViewChild, AfterViewInit, OnInit, Input, Output, EventEmitter, OnDestroy } from '@angular/core';
import { MatSort } from '@angular/material/sort';
import { MatPaginator } from '@angular/material/paginator';
import { MatTableDataSource } from '@angular/material/table';
import { FormControl } from '@angular/forms';
import { Observable, Subject } from 'rxjs';
import { debounceTime, distinctUntilChanged, takeUntil} from 'rxjs/operators';

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
  styleUrls: ['./tab-no-team.component.css']
})
export class TabNoTeamComponent implements AfterViewInit, OnInit, OnDestroy{

  chosenMembers: Student[] = [];
  dataSource = new MatTableDataSource<Student>();                     //Table datasource dynamically modified
  colsToDisplay = ["select", "id", "name", "surname"];                //Columns to be displayed in the table
  addStudentControl = new FormControl();                              //Form control to input the user to be enrolled
  teamNameControl = new FormControl();
  private destroy$: Subject<boolean> = new Subject<boolean>();        //Private subject to perform the unsubscriptions when the component is destroyed
  @Output() searchStudentsEvent = new EventEmitter<string>();         //Event emitter for the search students (autocompletions)
  @Output() submitTeamEvent = new EventEmitter<TeamProposal>();         //Event emitter for the search students (autocompletions)
  @ViewChild(MatSort, {static: true}) sort: MatSort;                  //Mat sort for the table
  @ViewChild(MatPaginator) paginator: MatPaginator;                   //Mat paginator for the table
  @Input() filteredStudents : Observable<Student[]>;                  //List of students matching search criteria
  @Input() set availableStudents( students: Student[] ) {              //Enrolled students to be displayed in the table
    this.dataSource.data = students;
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
    ).subscribe((name: string) => this.searchStudentsEvent.emit(name));
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

  removeWishMember(student: Student) {
    this.chosenMembers = this.chosenMembers.filter(x => x.id != student.id);
  }

  addWishMember(student: Student) {
    if(!this.chosenMembers.find(x => x.id == student.id)) this.chosenMembers.push(student);
  }

  submitTeam() {
    if(this.teamNameControl.valid && this.chosenMembers.length) this.submitTeamEvent.emit(new TeamProposal(this.teamNameControl.value, this.chosenMembers));
  }

  /** Function to set the value displayed in input and mat-options */
  displayFn(student: Student): string{
    return student? Student.displayFn(student) : '';
  }
}
