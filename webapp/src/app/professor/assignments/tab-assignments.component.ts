import {AfterViewInit, Component, Input, ViewChild} from '@angular/core';
import {MatSort} from '@angular/material/sort';
import {MatPaginator} from '@angular/material/paginator';
import {MatTableDataSource} from '@angular/material/table';

import {Assignment} from '../../models/assignment.model';
import {first} from 'rxjs/operators';
import {MatDialog} from '@angular/material/dialog';
import {DomSanitizer} from '@angular/platform-browser';
import {AssignmentAndUploadService} from '../../services/assignment-and-upload.service';
import {animate, state, style, transition, trigger} from '@angular/animations';
import {AssignmentSolutionDetails} from '../../models/assignment-solution-details.model';
import {AssignmentStatus} from '../../models/assignment-solution.model';
import {GradeDialogComponent} from '../../modals/grade-dialog/grade-dialog.component';
import {ActivatedRoute, Router} from '@angular/router';
import {UploadsDialogComponent} from '../../modals/uploads/uploads-dialog.component';
import {NewAssignmentDialogComponent} from '../../modals/new-assignment/new-assignment-dialog.component';
import {NewAssignmentUploadDialogComponent} from '../../modals/new-assignment-upload/new-assignment-upload-dialog.component';
import {ImageViewerDialogComponent} from '../../modals/image-viewer/image-viewer-dialog.component';

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

  constructor(public dialog: MatDialog,
              private assignmentService: AssignmentAndUploadService,
              private sanitizer: DomSanitizer,
              private router: Router,
              private route: ActivatedRoute) {
    this.route.queryParams.subscribe((queryParam) => {
        if (queryParam && queryParam.solution) {
          this.uploadsDialog(queryParam.solution);
          if (queryParam.professorUpload) {
            this.uploadReview(queryParam.solution);
          } else if (queryParam.professorImage) {
            this.viewDocument(queryParam.solution, queryParam.professorImage);
          }
        }
    });

    this.route.queryParams.subscribe((queryParam) =>
        queryParam && queryParam.addAssignment ? this.newAssignment() : null
    );
  }

  ngAfterViewInit() {
    /** Setting paginator and sort after ng containers are initialized */
    this.activeDataSource.paginator = this.paginator;
    this.activeDataSource.sort = this.sort;
    this.expiredDataSource.sort = this.sort;
    this.expiredDataSource.paginator = this.paginatorBis;
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
    if (this.dialog.openDialogs.length > 0) {
      return;
    }
    const dialogRef = this.dialog.open(UploadsDialogComponent, {
      width: '75%',
      data: { id },
    });
    dialogRef
        .afterClosed()
        .pipe(first())
        .subscribe(() => {
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
            this.activeDataSource.data.push(result);
            this.assignments = this.activeDataSource.data;
          }
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
            element.status = result.status;
            element.statusTs = result.timestamp;
            this.dialog.closeAll();
            this.router.navigate([this.router.url.split('?')[0]]);
          } else {
            this.router.navigate([this.router.url.split('?')[0]], {queryParams: {solution: assSolId}});
          }
        });
  }

  viewDocument(assSolId: string, uploadId: number) {
    this.assignmentService.getUploadDocument(uploadId).pipe(first()).subscribe(instance => {
      if (!instance) {
        this.router.navigate([this.router.url.split('?')[0]], {queryParams: {solution: assSolId}});
        return;
      }
      const url = URL.createObjectURL(instance);
      const dialogRef = this.dialog.open(ImageViewerDialogComponent, {
        data: {title: `Upload: ${uploadId}`,
          imageSrc: url,
          downloadable: true,
          dl_name: `upload_${uploadId}`
        }
      });
      dialogRef.afterClosed().subscribe(() => {
        URL.revokeObjectURL(url);
        this.router.navigate([this.router.url.split('?')[0]], {queryParams: {solution: assSolId}});
      });
    });
  }
}
