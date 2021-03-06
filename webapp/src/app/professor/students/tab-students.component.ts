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
import { SelectionModel } from '@angular/cdk/collections';
import { MatSort } from '@angular/material/sort';
import { MatPaginator } from '@angular/material/paginator';
import { MatTableDataSource } from '@angular/material/table';
import { FormControl } from '@angular/forms';
import { Observable, Subject } from 'rxjs';
import { debounceTime, distinctUntilChanged, takeUntil } from 'rxjs/operators';
import { FileInput } from 'ngx-material-file-input';
import { Student } from '../../models/student.model';

/**
 * StudentsComponent
 *
 * It represents the view for the Students tab
 */
@Component({
  selector: 'app-tab-students',
  templateUrl: './tab-students.component.html',
})
export class TabStudentsComponent implements AfterViewInit, OnInit, OnDestroy {
  dataSource = new MatTableDataSource<Student>(); // Table datasource dynamically modified
  selection = new SelectionModel<Student>(true, []); // Keeps track of the selected rows
  colsToDisplay = ['select', 'id', 'surname', 'name', 'team']; // Columns to be displayed in the table
  addStudentControl = new FormControl(); // Form control to input the user to be enrolled
  csvControl = new FormControl();

  private destroy$: Subject<boolean> = new Subject<boolean>(); // Private subject to perform the unsubscriptions when component is destroyed

  @Output() addStudentsEvent = new EventEmitter<Student[]>(); // Event emitter for the enroll student
  @Output() removeStudentsEvent = new EventEmitter<Student[]>(); // Event emitter for the unenroll student
  @Output() searchStudentsEvent = new EventEmitter<string>(); // Event emitter for the search students (autocompletions)
  @Output() addStudentsWithCsv = new EventEmitter<FormData>();

  @ViewChild(MatSort, { static: true }) sort: MatSort; // Mat sort for the table
  @ViewChild(MatPaginator) paginator: MatPaginator; // Mat paginator for the table

  @Input() filteredStudents: Observable<Student[]>; // List of students matching search criteria
  @Input() set enrolledStudents(students: Student[]) {
    // Enrolled students to be displayed in the table
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

  /** Function to select all rows if they are not all selected; otherwise clear selection. */
  masterToggle() {
    this.isEntirePageSelected()
      ? this.selection.deselect(...this.getPageData())
      : this.selection.select(...this.getPageData());
  }

  /** Function to retrieve a checkbox label */
  checkboxLabel(row?: Student): string {
    if (!row) {
      return `${this.isEntirePageSelected() ? 'select' : 'deselect'} all`;
    }
    return `${this.selection.isSelected(row) ? 'deselect' : 'select'} row ${
      row.id + 1
    }`;
  }

  selectAll() {
    this.dataSource.data.forEach((row) => this.selection.select(row));
  }

  /** Function to emit the unenroll event for the selected rows */
  deleteSelected() {
    if (this.selection.selected.length) {
      this.removeStudentsEvent.emit(this.selection.selected);
      this.selection.clear();
    }
  }

  /** Function to emit enroll student event */
  addStudent() {
    this.addStudentsEvent.emit([this.addStudentControl.value]);
    this.addStudentControl.setValue('');
    this.selection.clear();
  }

  /** Function to set the value displayed in input and mat-options */
  displayFn(student: Student): string {
    return student ? Student.displayFn(student) : '';
  }

  enrollMany() {
    const formData = new FormData();
    const fileInput: FileInput = this.csvControl.value;
    formData.append('file', fileInput.files[0]);
    this.addStudentsWithCsv.emit(formData);
    this.csvControl.reset();
  }

  getPageData() {
    return this.dataSource._pageData(
      this.dataSource._orderData(this.dataSource.filteredData)
    );
  }

  isEntirePageSelected() {
    return this.getPageData().every((row) => this.selection.isSelected(row));
  }
}
