import { Component, ViewChild, AfterViewInit, OnDestroy, Input } from '@angular/core';
import { MatSort } from '@angular/material/sort';
import { MatPaginator } from '@angular/material/paginator';
import { MatTableDataSource } from '@angular/material/table';
import { Subject } from 'rxjs';

import { VM } from '../../models/vm.model';

/**
 * VmsComponent
 * 
 * It represents the view for the VMs tab
 */
@Component({
  selector: 'app-vms',
  templateUrl: './vms.component.html',
  styleUrls: ['./vms.component.css']
})
export class VmsComponent implements AfterViewInit, OnDestroy{

  dataSource = new MatTableDataSource<VM>();                     //Table datasource dynamically modified
  colsToDisplay = ["id", "name", "team"];  //Columns to be displayed in the table
  private destroy$: Subject<boolean> = new Subject<boolean>();        //Private subject to perform the unsubscriptions when the component is destroyed
  @ViewChild(MatSort, {static: true}) sort: MatSort;                  //Mat sort for the table
  @ViewChild(MatPaginator) paginator: MatPaginator;                   //Mat paginator for the table
  @Input() set vms( vms: VM[] ) {              //VMs to be displayed in the table
    this.dataSource.data = vms;
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
