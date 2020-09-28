/**
 * Model for Assignment resource
 *
 * @param(id)   the id of the assignment
 * @param(name) the name of the assignment
 * @param(path) the path of the assignment (remote path)
 */
export class Assignment {
  id: number;
  name: string;
  path: string;

  constructor(id: number = 0, name: string = '', path: string = '') {
    this.id = id;
    this.name = name;
    this.path = path;
  }
}
