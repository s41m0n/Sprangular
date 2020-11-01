import {AssignmentStatus} from './assignment-solution.model';

export class StudentAssignmentDetails {
  assignmentId: number;
  name: string;
  releaseDate: string;
  dueDate: string;
  assignmentSolutionId: number;
  status: AssignmentStatus;
  grade: string;
  statusTs: string;

  constructor() {
  }
}
