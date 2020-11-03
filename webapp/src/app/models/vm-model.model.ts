/**
 * Model for VM resource
 *
 * @param(id)   the id of the VM
 * @param(name) the name of the VM
 * @param(path) the path of the VM (remote path)
 */
export class VmModel {
  id: number;
  name: string;
  courseId: string;

  constructor(id: number = 0, name: string = '', courseId: string) {
    this.id = id;
    this.name = name;
    this.courseId = courseId;
  }

  static displayFn(model: VmModel): string {
    return model.name + ' (' + model.id + ')';
  }
}
