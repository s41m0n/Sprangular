import { Component, ViewChild, AfterViewInit, OnInit, Input, Output, EventEmitter, OnDestroy } from '@angular/core';
import {SelectionModel} from '@angular/cdk/collections';
import { MatSort } from '@angular/material/sort';
import { MatPaginator } from '@angular/material/paginator';
import { MatTableDataSource } from '@angular/material/table';
import { FormControl } from '@angular/forms';
import { Observable, Subject } from 'rxjs';
import { debounceTime, distinctUntilChanged, takeUntil} from 'rxjs/operators';

import { Student } from '../../models/student.model';

/**
 * StudentsComponent
 * 
 * It represents the view for the Students tab
 */
@Component({
  selector: 'app-students',
  templateUrl: './students.component.html',
  styleUrls: ['./students.component.css']
})
export class StudentsComponent implements AfterViewInit, OnInit, OnDestroy{

  dataSource = new MatTableDataSource<Student>();                     //Table datasource dynamically modified
  selection = new SelectionModel<Student>(true, []);                  //Keeps track of the selected rows
  colsToDisplay = ["select", "serial", "name", "firstName", "team"];  //Columns to be displayed in the table
  addStudentControl = new FormControl();                              //Form control to input the user to be enrolled
  private destroy$: Subject<boolean> = new Subject<boolean>();        //Private subject to perform the unsubscriptions when the component is destroyed
  @Output() addStudentsEvent = new EventEmitter<Student[]>();         //Event emitter for the enroll student
  @Output() removeStudentsEvent = new EventEmitter<Student[]>();      //Event emitter for the unenroll student
  @Output() searchStudentsEvent = new EventEmitter<string>();         //Event emitter for the search students (autocompletions)
  @ViewChild(MatSort, {static: true}) sort: MatSort;                  //Mat sort for the table
  @ViewChild(MatPaginator) paginator: MatPaginator;                   //Mat paginator for the table
  @Input() filteredStudents : Observable<Student[]>;                  //List of students matching search criteria
  @Input() set enrolledStudents( students: Student[] ) {              //Enrolled students to be displayed in the table
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

  /** Function to check whether the number of selected elements matches the total number of rows.*/
  isAllSelected() : boolean{
    return this.selection.selected.length === this.dataSource.data.length;
  }

  /** Function to select all rows if they are not all selected; otherwise clear selection. */
  masterToggle() {
    this.isAllSelected() ?
        this.selection.clear() :
        this.dataSource.data.forEach(row => this.selection.select(row));
  }

  /** Function to retrieve a checkbox label */
  checkboxLabel(row?: Student): string {
    if (!row) return `${this.isAllSelected() ? 'select' : 'deselect'} all`;
    return `${this.selection.isSelected(row) ? 'deselect' : 'select'} row ${row.serial}`;
  }

  /** Function to emit the unenroll event for the selected rows */
  deleteSelected() {
    if(this.selection.selected.length) {
      this.removeStudentsEvent.emit(this.selection.selected);
      this.selection.clear();
    }
  }

  /** Function to emit enroll student event */
  addStudent() {
    this.addStudentsEvent.emit([this.addStudentControl.value]);
    this.addStudentControl.setValue('');
  }

  /** Function to set the value displayed in input and mat-options */
  displayFn(student: Student): string{
    return student? Student.displayFn(student) : '';
  }
}
