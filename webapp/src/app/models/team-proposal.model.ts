import { Student } from './student.model';

export class TeamProposal {
  teamName: string;
  studentIds: string[];
  deadline: string;

  constructor(
    teamName: string = '',
    studentIds: string[] = [],
    deadline: string
  ) {
    this.teamName = teamName;
    this.studentIds = studentIds;
    this.deadline = deadline;
  }
}
