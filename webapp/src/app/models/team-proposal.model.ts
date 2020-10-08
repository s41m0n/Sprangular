import { Student } from './student.model';

export class TeamProposal {
  teamName: string;
  studentIds: string[];
  deadline: number;

  constructor(
      teamName: string = '',
      studentIds: string[] = [],
      deadline: number = 0)
  {
    this.teamName = teamName;
    this.studentIds = studentIds;
    if (deadline === 0) {
      const date = new Date();
      date.setDate(date.getDate() + 5);
      deadline = date.getTime();
    }
    this.deadline = deadline;
  }
}
