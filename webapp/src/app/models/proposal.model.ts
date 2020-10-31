import { Student } from './student.model';

export class Proposal {
  token: string;
  proposalCreator: string;
  teamName: string;
  membersAndStatus: string[];
  deadline: string;

  constructor(
    token: string = '',
    proposalCreator: string,
    teamName: string,
    membersAndStatus: string[],
    deadline: string
  ) {
    this.token = token;
    this.proposalCreator = proposalCreator;
    this.teamName = teamName;
    this.membersAndStatus = membersAndStatus;
    this.deadline = deadline;
  }
}
