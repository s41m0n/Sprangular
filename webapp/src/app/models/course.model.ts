import {Professor} from './professor.model';

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
  professorId: number;
  professor?: Professor;

  constructor(id: number = 0, name: string = '', path: string = '', professorId: number = 0) {
    this.id = id;
    this.name = name;
    this.path = path;
    this.professorId = professorId;
  }
}
