import {AfterViewInit, Component, Input, ViewChild} from '@angular/core';
import {MatSort} from '@angular/material/sort';
import {MatPaginator} from '@angular/material/paginator';
import {MatTableDataSource} from '@angular/material/table';

import {Assignment} from '../../models/assignment.model';
import {first} from 'rxjs/operators';
import {ImageViewerDialogComponent} from '../../modals/image-viewer/image-viewer-dialog.component';
import {MatDialog} from '@angular/material/dialog';
import {DomSanitizer} from '@angular/platform-browser';
import {AssignmentService} from '../../services/assignment.service';
import {animate, state, style, transition, trigger} from '@angular/animations';
import {AssignmentSolutionDetails} from '../../models/assignment-solution-details.model';

/**
 * AssignmentsComponent
 *
 * It represents the view for the Assignments tab
 */
@Component({
  selector: 'app-tab-professor-assignments',
  templateUrl: './tab-assignments.component.html',
  styleUrls: ['./tab-assignments.component.css'],
  animations: [
    trigger('detailExpand', [
      state('collapsed', style({height: '0px', minHeight: '0'})),
      state('expanded', style({height: '*'})),
      transition('expanded <=> collapsed', animate('225ms cubic-bezier(0.4, 0.0, 0.2, 1)')),
    ]),
  ],
})
export class TabProfessorAssignmentsComponent implements AfterViewInit {

  dataSource = new MatTableDataSource<Assignment>();                     // Table datasource dynamically modified
  innerDataSource = new MatTableDataSource<AssignmentSolutionDetails>();
  colsToDisplay = ['name', 'releaseDate', 'dueDate', 'document', 'solutions']; // Columns to be displayed in the table
  innerColsToDisplay = ['studentName', 'studentSurname', 'studentId', 'status', 'statusTs', 'grade'];
  @ViewChild(MatSort, {static: true}) sort: MatSort;                  // Mat sort for the table
  @ViewChild(MatPaginator) paginator: MatPaginator;                   // Mat paginator for the table
  @Input() set assignments(assignments: Assignment[]) {              // Assignments to be displayed in the table
    this.dataSource.data = assignments;
  }
  expandedElement: Assignment | null;

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

  showSolutions(row: Assignment) {
    this.expandedElement = this.expandedElement === row ? null : row;
    if (this.expandedElement === null) {
      return;
    }
    this.assignmentService.getSolutionsForAssignment(row.id).pipe(first()).subscribe(solutions => this.innerDataSource.data = solutions);
  }

  dateString(statusTs: string): string {
    const date = new Date(statusTs);
    return date.toLocaleTimeString() + ' - ' + date.toLocaleDateString();
  }
}
