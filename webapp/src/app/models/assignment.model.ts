import { Course } from './course.model';

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
  course: Course;
  releaseDate: string;
  dueDate: string;
  path: string;

  constructor(id: number = 0, name: string = '') {
    this.id = id;
    this.name = name;
    this.path = '';
  }

}
