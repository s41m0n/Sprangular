import { Component, ViewChild, AfterViewInit, OnDestroy, Input } from '@angular/core';
import { MatSort } from '@angular/material/sort';
import { MatPaginator } from '@angular/material/paginator';
import { MatTableDataSource } from '@angular/material/table';
import { Subject } from 'rxjs';

import { Assignment } from '../../models/assignment.model';

/**
 * AssignmentsComponent
 * 
 * It represents the view for the Assignments tab
 */
@Component({
  selector: 'app-assignments',
  templateUrl: './assignments.component.html',
  styleUrls: ['./assignments.component.css']
})
export class AssignmentsComponent implements AfterViewInit, OnDestroy{

  dataSource = new MatTableDataSource<Assignment>();                     //Table datasource dynamically modified
  colsToDisplay = ["id", "name", "professor"];  //Columns to be displayed in the table
  private destroy$: Subject<boolean> = new Subject<boolean>();        //Private subject to perform the unsubscriptions when the component is destroyed
  @ViewChild(MatSort, {static: true}) sort: MatSort;                  //Mat sort for the table
  @ViewChild(MatPaginator) paginator: MatPaginator;                   //Mat paginator for the table
  @Input() set assignments( assignments: Assignment[] ) {              //Assignments to be displayed in the table
    this.dataSource.data = assignments;
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
}
