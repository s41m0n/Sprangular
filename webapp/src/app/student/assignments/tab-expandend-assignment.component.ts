import {Component, Input} from '@angular/core';
import {Assignment} from '../../models/assignment.model';
import {MatTableDataSource} from '@angular/material/table';
import {Upload} from '../../models/upload.model';
import {AssignmentSolution, AssignmentStatus} from '../../models/assignment-solution.model';
import {NewVmComponent} from '../../modals/new-vm/new-vm.component';
import {first} from 'rxjs/operators';
import {MatDialog} from '@angular/material/dialog';
import {StudentService} from '../../services/student.service';
import {NewAssignmentUploadComponent} from '../../modals/new-assignment-upload/new-assignment-upload.component';

@Component({
  selector: 'app-tab-expandend-assignment',
  templateUrl: './tab-expandend-assignment.component.html',

})
export class TabExpandendAssignmentComponent {

  expandedElement: Assignment;
  assignmentSolution: AssignmentSolution;
  canUpload = false;
  dataSource = new MatTableDataSource<Upload>();
  studentId;
  colsToDisplay = ['id', 'localTimeStamp', 'comment'];

  constructor(
      public dialog: MatDialog,
      private studentService: StudentService
  ) {}

  @Input() set element(element: Assignment) {
    this.expandedElement = element;
  }

  @Input() set solution(assignmentSolution: AssignmentSolution) {
    this.assignmentSolution = assignmentSolution;
    if (assignmentSolution.status !== AssignmentStatus.READ &&
          assignmentSolution.status !== AssignmentStatus.REVIEWED_UPLOADABLE) {
      this.canUpload = true;
    }
  }

  @Input() set uploads(uploads: Upload[]) {
    this.dataSource.data = uploads;
  }

  @Input() set student(studentId: string) {
    this.studentId = studentId;
  }

  /** Private function to refresh the list of vms */
  private refreshUploads() {
    this.studentService
        .getAssignmentUploads(this.expandedElement.id)
        .pipe(first())
        .subscribe((uploads) => (this.dataSource.data = uploads));
  }

  newAssignmentSolution() {
    const dialogRef = this.dialog.open(NewAssignmentUploadComponent,
        {
          data: { assignmentId: this.expandedElement.id }
        });
    dialogRef
        .afterClosed()
        .pipe(first())
        .subscribe((result) => {
          if (result) {
            this.refreshUploads();
          }
        });
  }
}
