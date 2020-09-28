import {AfterViewInit, Component, Input, ViewChild} from '@angular/core';
import {MatSort} from '@angular/material/sort';
import {MatPaginator} from '@angular/material/paginator';
import {MatTableDataSource} from '@angular/material/table';

import {Assignment} from '../../models/assignment.model';

/**
 * AssignmentsComponent
 *
 * It represents the view for the Assignments tab
 */
@Component({
  selector: 'app-tab-professor-assignments',
  templateUrl: './tab-assignments.component.html'
})
export class TabProfessorAssignmentsComponent implements AfterViewInit {

  dataSource = new MatTableDataSource<Assignment>();                     // Table datasource dynamically modified
  colsToDisplay = ['id', 'name', 'professor'];                          // Columns to be displayed in the table
  @ViewChild(MatSort, {static: true}) sort: MatSort;                  // Mat sort for the table
  @ViewChild(MatPaginator) paginator: MatPaginator;                   // Mat paginator for the table
  @Input() set assignments(assignments: Assignment[]) {              // Assignments to be displayed in the table
    this.dataSource.data = assignments;
  }

  ngAfterViewInit() {
    /** Setting paginator and sort after ng containers are initialized */
    this.dataSource.paginator = this.paginator;
    this.dataSource.sort = this.sort;
  }
}
