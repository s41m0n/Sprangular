/**
 * Model for Course resource
 * 
 * @param(id)   the id of the course
 * @param(name) the name of the course
 * @param(path) the path of the course (remote path)
 */
export class Course {
    id: number;
    name: string;
    path: string;

    constructor(id: number = 0, name: string = '', path: string = ''){
        this.id = id;
        this.name = name;
        this.path = path;
    }
  }