import {Team} from './team.model';
import {VM} from './vm.model';
import {Resource} from "./resource.model";

export class VmProfessorDetails {
  team: Team;
  vms: VM[];
  resources: Resource[];

  constructor(team: Team,
              vms: VM[]) {
    this.team = team;
    this.vms = vms;
  }
}
