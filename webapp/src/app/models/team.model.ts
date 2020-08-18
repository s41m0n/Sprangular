import { Student } from './student.model';

/**
 * Model for Team resource
 * 
 * @param(id)   the id of the team
 * @param(name) the name of the team
 * @param(courseId)   the id of the course
 */
export class Team {
    id: number;
    name: string;
    students: Array<Student>;
    courseId: number;

    constructor(id: number, name: string, courseId: number){
        this.id = id;
        this.name = name;
        this.courseId = courseId;
    }
  }