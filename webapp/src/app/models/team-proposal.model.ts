import {Student} from './student.model';

/**
 * Model for Team resource
 *
 * @param(id)   the id of the team
 * @param(name) the name of the team
 * @param(courseId)   the id of the course
 */
export class TeamProposal {
  name: string;
  students: Student[];

  constructor(name: string, students: Student[]) {
    this.name = name;
    this.students = students;
  }
}
