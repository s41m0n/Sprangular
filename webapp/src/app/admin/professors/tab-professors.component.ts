import { Component, ViewChild, AfterViewInit, OnInit, Input, Output, EventEmitter, OnDestroy } from '@angular/core';
import {SelectionModel} from '@angular/cdk/collections';
import { MatSort } from '@angular/material/sort';
import { MatPaginator } from '@angular/material/paginator';
import { MatTableDataSource } from '@angular/material/table';
import { FormControl } from '@angular/forms';
import { Observable, Subject } from 'rxjs';
import { debounceTime, distinctUntilChanged, takeUntil, tap} from 'rxjs/operators';

import { Professor } from '../../models/professor.model';
import { MatDialog } from '@angular/material/dialog';

/**
 * TabProfessorsComponent
 * 
 * It represents the view for the admin course
 */
@Component({
  selector: 'app-tab-admin-professors',
  templateUrl: './tab-professors.component.html'
})
export class TabAdminProfessorsComponent implements AfterViewInit, OnInit, OnDestroy{

  dataSource = new MatTableDataSource<Professor>();                     //Table datasource dynamically modified
  selection = new SelectionModel<Professor>(true, []);                  //Keeps track of the selected rows
  colsToDisplay = ["select", "id", "name", "surname"];                //Columns to be displayed in the table
  assignProfessorControl = new FormControl();                              //Form control to input the user to be enrolled
  private destroy$: Subject<boolean> = new Subject<boolean>();        //Private subject to perform the unsubscriptions when the component is destroyed
  @Output() searchProfessorEvent = new EventEmitter<string>();         //Event emitter for the search students (autocompletions)
  @Output() assignProfessorEvent = new EventEmitter<Professor[]>();         //Event emitter for the search students (autocompletions)
  @Output() removeProfessorEvent = new EventEmitter<Professor[]>();         //Event emitter for the search students (autocompletions)
  @ViewChild(MatSort, {static: true}) sort: MatSort;                  //Mat sort for the table
  @ViewChild(MatPaginator) paginator: MatPaginator;                   //Mat paginator for the table
  @Input() filteredProfessors : Observable<Professor[]>;                  //List of students matching search criteria
  @Input() set courseProfessors( professors: Professor[] ) {              //Enrolled students to be displayed in the table
    this.dataSource.data = professors;
  }

  constructor(public dialog: MatDialog) {}

  ngOnInit() {
    /** Setting filter to autocomplete */
    this.assignProfessorControl.valueChanges
    .pipe(
      takeUntil(this.destroy$),
      // wait 300ms after each keystroke before considering the term
      debounceTime(300),
      // ignore new term if same as previous term
      distinctUntilChanged()
    ).subscribe((name: string) => this.searchProfessorEvent.emit(name));
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
  checkboxLabel(row?: Professor): string {
    if (!row) return `${this.isAllSelected() ? 'select' : 'deselect'} all`;
    return `${this.selection.isSelected(row) ? 'deselect' : 'select'} row ${row.id}`;
  }

  createProfessorModal() {
    alert("To be continued...");
  }

  assignProfessorToCourse() {
    this.assignProfessorEvent.emit([this.assignProfessorControl.value]);
    this.assignProfessorControl.setValue('');
  }

  removeProfessorFromCourse() {
    if(this.selection.selected.length) {
      this.removeProfessorEvent.emit(this.selection.selected);
      this.selection.clear();
    }
  }

  /** Function to set the value displayed in input and mat-options */
  displayFn(prof: Professor): string{
    return prof? Professor.displayFn(prof) : '';
  }
}
