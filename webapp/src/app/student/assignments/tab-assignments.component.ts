import {Component, Input} from '@angular/core';
import {MatTableDataSource} from '@angular/material/table';
import {Assignment} from '../../models/assignment.model';
import {animate, state, style, transition, trigger} from '@angular/animations';
import {AssignmentSolution} from '../../models/assignment-solution.model';
import {filter, first} from 'rxjs/operators';
import {User} from '../../models/user.model';
import {StudentService} from '../../services/student.service';
import {Upload} from '../../models/upload.model';

/**
 * StudentsComponent
 *
 * It represents the view for the Students tab
 */
@Component({
  selector: 'app-tab-student-assignments',
  templateUrl: './tab-assignments.component.html',
  animations: [
    trigger('detailExpand', [
      state('collapsed', style({height: '0px', minHeight: '0'})),
      state('expanded', style({height: '*'})),
      transition('expanded <=> collapsed', animate('225ms cubic-bezier(0.4, 0.0, 0.2, 1)')),
    ]),
  ],
})
export class TabStudentAssignmentsComponent {
  regularDataSource = new MatTableDataSource<Assignment>();                     // Table datasource dynamically modified
  expiredDataSource = new MatTableDataSource<Assignment>();                     // Table datasource dynamically
  currentStudent: User;
  colsToDisplay = ['name', 'dueDate'];                // Columns to be displayed in the table
  studentService: StudentService;

  regularAssignmentSolution: AssignmentSolution;
  regularAssignmentUploads: Upload[];
  expiredAssignmentSolution: AssignmentSolution;
  expiredAssignmentUploads: Upload[];

  @Input() set assignments(assignments: Assignment[]) {
    this.regularDataSource.data = assignments.filter(assignment => Date.now() < Date.parse(assignment.dueDate));
    this.expiredDataSource.data = assignments.filter(assignment => Date.now() > Date.parse(assignment.dueDate));
  }

  @Input() set student(student: User) {
    this.currentStudent = student;
  }

  regularExpandedElement: Assignment | null;
  expiredExpandedElement: Assignment | null;

  constructor(private studServ: StudentService) {
    this.studentService = studServ;
  }

  onClickElement(element, table) {
    this.expiredExpandedElement = this.expiredExpandedElement === element ? null : element;
    this.regularExpandedElement = this.regularExpandedElement === element ? null : element;
    this.studentService.getAssignmentSolution(element.id)
        .pipe(
        first()
    ).subscribe(solution => table === 'regular' ? this.regularAssignmentSolution = solution : this.expiredAssignmentSolution = solution);

    this.studentService.getAssignmentUploads(element.id)
        .pipe(
        first()
    ).subscribe(uploads => table === 'regular' ? this.regularAssignmentUploads = uploads : this.expiredAssignmentUploads = uploads);

  }
}
