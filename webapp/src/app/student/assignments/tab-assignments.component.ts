import { Component, Input } from '@angular/core';
import { MatTableDataSource } from '@angular/material/table';
import { Assignment } from '../../models/assignment.model';

/**
 * StudentsComponent
 * 
 * It represents the view for the Students tab
 */
@Component({
  selector: 'app-tab-student-assignments',
  templateUrl: './tab-assignments.component.html'
})
export class TabStudentAssignmentsComponent{
  dataSource = new MatTableDataSource<Assignment>();                     //Table datasource dynamically modified
  colsToDisplay = ["id", "name", "path"];                //Columns to be displayed in the table
  @Input() set assignments(assignments: Assignment[]) {
    this.dataSource.data = assignments;
  }
}