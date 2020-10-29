import {AfterViewInit, Component, Input, ViewChild} from '@angular/core';
import {MatSort} from '@angular/material/sort';
import {MatPaginator} from '@angular/material/paginator';
import {MatTableDataSource} from '@angular/material/table';

import {Assignment} from '../../models/assignment.model';
import {first} from 'rxjs/operators';
import {ImageViewerDialogComponent} from '../../modals/image-viewer/image-viewer-dialog.component';
import {MatDialog} from '@angular/material/dialog';
import {CourseService} from '../../services/course.service';
import {DomSanitizer} from '@angular/platform-browser';
import {AssignmentService} from '../../services/assignment.service';

/**
 * AssignmentsComponent
 *
 * It represents the view for the Assignments tab
 */
@Component({
  selector: 'app-tab-professor-assignments',
  templateUrl: './tab-assignments.component.html',
  styleUrls: ['./tab-assignments.component.css']
})
export class TabProfessorAssignmentsComponent implements AfterViewInit {

  dataSource = new MatTableDataSource<Assignment>();                     // Table datasource dynamically modified
  colsToDisplay = ['id', 'name', 'releaseDate', 'dueDate', 'document']; // Columns to be displayed in the table
  @ViewChild(MatSort, {static: true}) sort: MatSort;                  // Mat sort for the table
  @ViewChild(MatPaginator) paginator: MatPaginator;                   // Mat paginator for the table
  @Input() set assignments(assignments: Assignment[]) {              // Assignments to be displayed in the table
    this.dataSource.data = assignments;
  }

  constructor(public dialog: MatDialog,
              private assignmentService: AssignmentService,
              private sanitizer: DomSanitizer) {
  }

  ngAfterViewInit() {
    /** Setting paginator and sort after ng containers are initialized */
    this.dataSource.paginator = this.paginator;
    this.dataSource.sort = this.sort;
  }

  viewAssignment(assignment: Assignment) {
    this.assignmentService.getDocument(assignment.id).pipe(first()).subscribe(instance => {
      if (!instance) { return; }
      const url = URL.createObjectURL(instance);
      const dialogRef = this.dialog.open(ImageViewerDialogComponent, {
        data: {title : `Assignment: ${assignment.id} - ${assignment.name}`, imageSrc: this.sanitizer.bypassSecurityTrustUrl(url)}
      });
      dialogRef.afterClosed().subscribe(() => {
        URL.revokeObjectURL(url);
      });
    });
  }
}
