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
import {AssignmentStatus} from '../../models/assignment-solution.model';
import {GradeDialogComponent} from '../../modals/grade-dialog/grade-dialog.component';
import {ActivatedRoute, Router} from '@angular/router';
import {UploadsDialogComponent} from '../../modals/uploads/uploads-dialog.component';
import {NewAssignmentDialogComponent} from '../../modals/new-assignment/new-assignment-dialog.component';

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
  innerColsToDisplay = ['studentName', 'studentSurname', 'studentId', 'status', 'statusTs', 'grade', 'uploads'];
  @ViewChild(MatSort, {static: true}) sort: MatSort;                  // Mat sort for the table
  @ViewChild(MatPaginator) paginator: MatPaginator;                   // Mat paginator for the table
  @Input() set assignments(assignments: Assignment[]) {              // Assignments to be displayed in the table
    this.dataSource.data = assignments.sort(Assignment.compare);
  }
  expandedElement: Assignment | null;
  assignmentStatuses = Object.values(AssignmentStatus);
  filteredStatuses: string[] = [];

  constructor(public dialog: MatDialog,
              private assignmentService: AssignmentService,
              private sanitizer: DomSanitizer,
              private router: Router,
              private route: ActivatedRoute) {
    this.route.queryParams.subscribe((queryParam) =>
        queryParam && queryParam.solution
            ? this.uploadsDialog(queryParam.solution)
            : null
    );

    this.route.queryParams.subscribe((queryParam) =>
        queryParam && queryParam.addAssignment ? this.newAssignment() : null
    );
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
    this.assignmentService.getSolutionsForAssignment(row.id).pipe(first()).subscribe(
        solutions => this.innerDataSource.data = solutions.sort(AssignmentSolutionDetails.compare));
  }

  dateString(statusTs: string): string {
    const date = new Date(statusTs);
    return (
      date.toLocaleDateString('en-GB') +
      ' at ' +
      date.toLocaleTimeString('en-GB', { hour: '2-digit', minute: '2-digit' })
    );
  }

  changeFiltering(status: string) {
    this.filteredStatuses.includes(status) ?
        this.filteredStatuses = this.filteredStatuses.filter(s => s !== status) : this.filteredStatuses.push(status);
  }

  gradeAssignable(asd: AssignmentSolutionDetails): boolean {
    return !asd.grade && (asd.status === AssignmentStatus.REVIEWED || asd.status === AssignmentStatus.REVIEWED_UPLOADABLE);
  }

  assignGrade(element: AssignmentSolutionDetails) {
    const dialogRef = this.dialog.open(GradeDialogComponent, { data: {assignmentSolutionId: element.id.toString()}});
    dialogRef.afterClosed().pipe(first()).subscribe(res => {
      if (res) {
        element.status = res.status;
        element.statusTs = res.statusTs;
        element.grade = res.grade;
      }
    });
  }

  private uploadsDialog(id: string) {
    const dialogRef = this.dialog.open(UploadsDialogComponent, {
      width: '75%',
      data: { id },
    });
    dialogRef
        .afterClosed()
        .pipe(first())
        .subscribe((res) => {
          if (res) {
            const element = this.innerDataSource.data.find(e => e.id === Number.parseInt(id, 10));
            element.status = res.status;
            element.statusTs = res.timestamp;
          }
          this.router.navigate([this.router.url.split('?')[0]]);
        });
  }

  newAssignment() {
    const dialogRef = this.dialog.open(NewAssignmentDialogComponent, {
      width: '25%',
    });
    dialogRef
        .afterClosed()
        .pipe(first())
        .subscribe((result) => {
          if (result) {
            this.dataSource.data.push(result);
            this.assignments = this.dataSource.data;
          }
          this.router.navigate([this.router.url.split('?')[0]]);
        });
  }
}
