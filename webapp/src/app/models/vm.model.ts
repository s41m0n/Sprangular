import { Team } from './team.model';

/**
 * Model for VM resource
 *
 * @param(id)     the id of the VM
 * @param(name)   the name of the VM
 * @param(path)   the path of the VM (remote path)
 * @param(vCpu)   numbers of vCpu of the VM
 * @param(ram)    ram size of the VM
 * @param(disk)   disk size of the VM
 * @param(team)?  the resolved team object (if any)
 */
export class VM {
  id: number;
  name: string;
  vCpu: number;
  diskStorage: number;
  ram: number;
  teamId: number;
  active: boolean;

  constructor(
    id: number = 0,
    name: string = '',
    path: string = '',
    vCpu: number = 1,
    ram: number = 0.5,
    disk: number = 2,
    teamId: number = 0
  ) {
    this.id = id;
    this.name = name;
    this.vCpu = vCpu;
    this.ram = ram;
    this.diskStorage = disk;
    this.teamId = teamId;
  }
}
