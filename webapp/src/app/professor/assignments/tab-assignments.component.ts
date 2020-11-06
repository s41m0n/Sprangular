import {AfterViewInit, Component, EventEmitter, Input, Output, ViewChild} from '@angular/core';
import {MatSort} from '@angular/material/sort';
import {MatPaginator} from '@angular/material/paginator';
import {MatTableDataSource} from '@angular/material/table';

import {Assignment} from '../../models/assignment.model';
import {first} from 'rxjs/operators';
import {MatDialog} from '@angular/material/dialog';
import {DomSanitizer} from '@angular/platform-browser';
import {animate, state, style, transition, trigger} from '@angular/animations';
import {AssignmentSolutionDetails} from '../../models/assignment-solution-details.model';
import {AssignmentStatus} from '../../models/assignment-solution.model';
import {GradeDialogComponent} from '../../modals/grade-dialog/grade-dialog.component';
import {ActivatedRoute, Router} from '@angular/router';
import {UploadsDialogComponent} from '../../modals/uploads/uploads-dialog.component';
import {NewAssignmentUploadDialogComponent} from '../../modals/new-assignment-upload/new-assignment-upload-dialog.component';

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

  activeDataSource = new MatTableDataSource<Assignment>();                     // Table datasource dynamically modified
  expiredDataSource = new MatTableDataSource<Assignment>();
  innerDataSource = new MatTableDataSource<AssignmentSolutionDetails>();
  colsToDisplay = ['name', 'releaseDate', 'dueDate', 'document', 'solutions']; // Columns to be displayed in the table
  innerColsToDisplay = ['studentName', 'studentSurname', 'studentId', 'status', 'statusTs', 'grade', 'uploads'];
  expandedElement: Assignment | null;
  assignmentStatuses = Object.values(AssignmentStatus);
  filteredStatuses: string[] = [];
  @ViewChild(MatSort, {static: true}) sort: MatSort;                  // Mat sort for the table
  @ViewChild('pagOne') paginator: MatPaginator;                   // Mat paginator for the table
  @ViewChild('pagTwo') paginatorBis: MatPaginator;                   // Mat paginator for the table
  @Input() set assignments(assignments: Assignment[]) {              // Assignments to be displayed in the table
    this.activeDataSource.data = assignments.filter(a => Date.now() < Date.parse(a.dueDate)).sort(Assignment.compare);
    this.expiredDataSource.data = assignments.filter(a => Date.now() >= Date.parse(a.dueDate)).sort(Assignment.compare);
  }
  @Input() set setAssignmentSolutions(val: AssignmentSolutionDetails[]) {
    this.innerDataSource.data = val;
  }
  @Output() assignmentSolutionEvent = new EventEmitter<number>();
  @Output() viewDocEvent = new EventEmitter<{solId: number, upId: number}>();

  constructor(public dialog: MatDialog,
              private sanitizer: DomSanitizer,
              private router: Router,
              private route: ActivatedRoute) {
  }

  ngAfterViewInit() {
    /** Setting paginator and sort after ng containers are initialized */
    this.activeDataSource.paginator = this.paginator;
    this.activeDataSource.sort = this.sort;
    this.expiredDataSource.sort = this.sort;
    this.expiredDataSource.paginator = this.paginatorBis;

    this.route.queryParams.subscribe((queryParam) => {
      if (queryParam && queryParam.solution) {
        this.uploadsDialog(queryParam.solution);
        if (queryParam.professorUpload) {
          this.uploadReview(queryParam.solution);
        } else if (queryParam.professorImage) {
          const solId: number = queryParam.solution;
          const upId: number = queryParam.professorImage;
          this.viewDocEvent.emit({solId, upId});
        }
      }
    });

    this.route.queryParams.subscribe((queryParam) =>
        queryParam && queryParam.assignGrade ? this.assignGrade(queryParam.assignGrade) : null
    );
  }

  // Handles the opening of the assignment solutions section
  showSolutions(row: Assignment) {
    this.expandedElement = this.expandedElement === row ? null : row;
    if (this.expandedElement === null) {
      return;
    }
    this.assignmentSolutionEvent.emit(row.id);
  }

  /** Function to express the date in a user friendly way */
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

  assignGrade(assSolId: string) {
    const dialogRef = this.dialog.open(GradeDialogComponent, { data: {assignmentSolutionId: assSolId}});
    dialogRef.afterClosed().pipe(first()).subscribe(res => {
      if (res) {
        const element = this.innerDataSource.data.find(e => e.id.toString() === assSolId);
        element.status = res.status;
        element.statusTs = res.statusTs;
        element.grade = res.grade;
      }
      this.router.navigate([this.router.url.split('?')[0]]);
    });
  }

  private uploadsDialog(id: string) {
    if (this.dialog.openDialogs.length > 0) {
      return;
    }
    const dialogRef = this.dialog.open(UploadsDialogComponent, {
      width: '70%',
      data: { id },
    });
    dialogRef
        .afterClosed()
        .pipe(first())
        .subscribe(() => {
          this.router.navigate([this.router.url.split('?')[0]]);
        });
  }

  uploadReview(assSolId: string) {
    const dialogRef = this.dialog.open(NewAssignmentUploadDialogComponent,
        {
          data: { assignmentSolutionId: assSolId }
        });
    dialogRef
        .afterClosed()
        .pipe(first())
        .subscribe((result) => {
          if (result) {
            const element = this.innerDataSource.data.find(e => e.id === Number.parseInt(assSolId, 10));
            if (element) {
              element.status = result.status;
              element.statusTs = result.timestamp;
            }
            this.dialog.closeAll();
            this.router.navigate([this.router.url.split('?')[0]]);
          } else {
            this.router.navigate([this.router.url.split('?')[0]], {queryParams: {solution: assSolId}});
          }
        });
  }
}
