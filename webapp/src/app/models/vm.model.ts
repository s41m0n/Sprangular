import { Team } from './team.model';

/**
 * Model for VM resource
 *
 * @param(id)   the id of the VM
 * @param(name) the name of the VM
 * @param(path) the path of the VM (remote path)
 * @param(vCpu) numbers of vCpu of the VM
 * @param(ram)  ram size of the VM
 * @param(disk) disk size of the VM
 * @param(team)?     the resolved team object (if any) which MUST NOT be pushed (otherwise json-server modifies the entity setting this new field)
 */
export class VM {
    id: number;
    name: string;
    path: string;
    creationDate: string;
    vCpu: string;
    ram: string;
    disk: string;
    team?: Team;

    constructor(id: number = 0,
                name: string = '',
                path: string = '',
                creationDate: string = '',
                vCpu: string = '',
                ram: string = '',
                disk: string = ''){
        this.id = id;
        this.name = name;
        this.path = path;
        this.creationDate = creationDate;
        this.vCpu = vCpu;
        this.ram = ram;
        this.disk = disk;
    }

    /**vm
     * Static method to export a student like its server representation.
     *
     * In that case, the TEAM property is unset, to avoid that the resource in the server changes its representation
     * (it already has the teamId, should not also set the entire object inside it)
     *
     * @param vm
     */
    static export(vm: VM): VM {
        delete vm.team;
        return vm;
    }
  }
