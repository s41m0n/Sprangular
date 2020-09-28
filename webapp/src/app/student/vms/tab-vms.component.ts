import {AfterViewInit, Component, Input} from '@angular/core';
import {MatTableDataSource} from '@angular/material/table';
import {VM} from '../../models/vm.model';

/**
 * StudentsComponent
 *
 * It represents the view for the Students tab
 */
@Component({
  selector: 'app-tab-student-vms',
  templateUrl: './tab-vms.component.html'
})
export class TabStudentVmsComponent implements AfterViewInit {

  dataSource = new MatTableDataSource<VM>();                     // Table datasource dynamically modified

  ngAfterViewInit(): void {
  }

  @Input() set vms(vms: VM[]) {
    this.dataSource.data = vms;
  }

  connectToVm(id: number) {
    console.log('Method to implement');
  }
}
