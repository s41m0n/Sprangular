import {Team} from './team.model';
import {VM} from './vm.model';

export class VmProfessorDetails {
  team: Team;
  vms: VM[];

  constructor(team: Team,
              vms: VM[]) {
    this.team = team;
    this.vms = vms;
  }
}
