export class Proposal {
  token: string;
  proposalCreator: string;
  teamName: string;
  membersAndStatus: string[];
  deadline: string;
  valid: boolean;

  constructor(
    token: string = '',
    proposalCreator: string,
    teamName: string,
    membersAndStatus: string[],
    deadline: string,
    valid: boolean
  ) {
    this.token = token;
    this.proposalCreator = proposalCreator;
    this.teamName = teamName;
    this.membersAndStatus = membersAndStatus;
    this.deadline = deadline;
    this.valid = valid;
  }
}
