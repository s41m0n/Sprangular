import { Team } from './team.model';
import { Course } from './course.model';

/**
 * Model for Student resource
 * 
 * @param(id)        the id of the student
 * @param(email)     the email of the student
 * @param(name)      the name of the student
 * @param(surname)   the surname of the student
 * @param(courseId)  the id of the course the student is enrolled to (0 if none)
 * @param(teamId)    the id of the team the student belongs to (0 if none)
 * @param(team)?     the resolved team object (if any) which MUST NOT be pushed (otherwise json-server modifies the entity setting this new field)
 * @param(course)?   the resolved course object (if any) which MUST NOT be pushed (otherwise json-server modifies the entity setting this new field)
 */
export class Student {
    id: number;
    email: string;
    name: string;
    surname: string;
    courseId : number;
    teamId: number;
    team?: Team;
    course? : Course;
    
    constructor(id:number = 0, email: string = '', name:string = '', surname:string = '',
        courseId:number = 0, teamId: number = 0){
        this.id = id;
        this.email = email;
        this.name = name;
        this.surname = surname;
        this.courseId = courseId;
        this.teamId = teamId;
    }
    
    /**
     * Static method to export a student like its server representation.
     * 
     * In that case, the TEAM property is unset, to avoid that the resource in the server changes its representation
     * (it already has the teamId, should not also set the entire object inside it)
     * 
     * @param student the student to be purified
     */
    static export(student : Student) : Student {
        delete student.team;
        delete student.course;
        return student;
    }
    
    /**
     * Static method to return a user-friendly string representation of the student
     * 
     * @param student the student to be print
     */
    static displayFn(student : Student) : string {
        return student.surname + ' ' + student.name + ' (' + student.id + ')';
    }
}