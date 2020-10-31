import { Student } from './student.model';

export class Proposal {
  token: string;
  proposalCreator: Student;
  teamName: string;
  membersAndStatus: Map<string, string>;
  deadline: string;

  constructor(
    token: string = '',
    proposalCreator: Student,
    teamName: string,
    membersAndStatus: Map<string, string>,
    deadline: string
  ) {
    this.token = token;
    this.proposalCreator = proposalCreator;
    this.teamName = teamName;
    this.membersAndStatus = membersAndStatus;
    this.deadline = deadline;
  }
}
