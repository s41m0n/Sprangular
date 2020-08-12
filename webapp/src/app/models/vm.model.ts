import { Team } from './team.model';

/**
 * Model for VM resource
 * 
 * @param(id)   the id of the VM
 * @param(name) the name of the VM
 * @param(path) the path of the VM (remote path)
 * @param(team)?     the resolved team object (if any) which MUST NOT be pushed (otherwise json-server modifies the entity setting this new field)
 */
export class VM {
    id: number;
    name: string;
    path: string;
    team?: Team;

    constructor(id: number = 0, name: string = '', path: string = ''){
        this.id = id;
        this.name = name;
        this.path = path;
    }

    /**
     * Static method to export a student like its server representation.
     * 
     * In that case, the TEAM property is unset, to avoid that the resource in the server changes its representation
     * (it already has the teamId, should not also set the entire object inside it)
     * 
     * @param student the student to be purified
     */
    static export(vm : VM) : VM {
        delete vm.team;
        return vm;
    }
  }