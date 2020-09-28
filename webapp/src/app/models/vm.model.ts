import {Team} from './team.model';

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
  path: string;
  creationDate: string;
  vCpu: number;
  ram: number;
  disk: number;
  teamId: number;
  team?: Team;

  constructor(id: number = 0,
              name: string = '',
              path: string = '',
              creationDate: string = '',
              vCpu: number = 1,
              ram: number = 0.5,
              disk: number = 2,
              teamId: number = 0) {
    this.id = id;
    this.name = name;
    this.path = path;
    this.creationDate = creationDate;
    this.vCpu = vCpu;
    this.ram = ram;
    this.disk = disk;
    this.teamId = teamId;
  }

  /**vm
   * Static method to export a student like its server representation.
   *
   * In that case, the TEAM property is unset, to avoid that the resource in the server changes its representation
   * (it already has the teamId, should not also set the entire object inside it)
   *
   * @param(vm) the vm object
   */
  static export(vm: VM): VM {
    delete vm.team;
    return vm;
  }
}
