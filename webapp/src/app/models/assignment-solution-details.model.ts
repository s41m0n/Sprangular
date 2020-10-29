import {AssignmentStatus} from './assignment-solution.model';


export class AssignmentSolutionDetails {
  id: number;
  studentName: string;
  studentSurname: string;
  studentId: string;
  status: AssignmentStatus;
  statusTs: string;
  grade: string;
}
