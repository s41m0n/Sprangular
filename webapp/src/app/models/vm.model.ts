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
  vCpu: number;
  diskStorage: number;
  ram: number;

  constructor(
    id: number = 0,
    vCpu: number = 1,
    ram: number = 0.5,
    diskStorage: number = 2
  ) {
    this.id = id;
    this.vCpu = vCpu;
    this.ram = ram;
    this.diskStorage = diskStorage;
  }
}
