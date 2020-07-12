import { Team } from './team.model';

/**
 * Model for Student resource
 * 
 * @param(id)        the id of the student
 * @param(serial)    the serial value of the student (id in Polito)
 * @param(name)      the surname of the student
 * @param(firstName) the name of the student
 * @param(courseId)  the id of the course the student is enrolled to (0 if none)
 * @param(teamId)    the id of the team the student belongs to (0 if none)
 * @param(team)?     the resolved team object (if any) which MUST NOT be pushed (otherwise json-server modifies the entity setting this new field)
 */
export class Student {
    id: number;
    serial : string;
    name: string;
    firstName: string;
    courseId : number;
    teamId: number;
    team?: Team;
    
    constructor(id:number = 0, name:string = '', firstName:string = '',
        courseId:number = 0, teamId: number = 0){
        this.id = id;
        this.name = name;
        this.firstName = firstName;
        this.courseId = courseId;
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
        return student;
    }
    
    /**
     * Static method to return a user-friendly string representation of the student
     * 
     * @param student the student to be print
     */
    static displayFn(student : Student) : string {
        return student.name + ' ' + student.firstName + ' (' + student.serial + ')';
    }
}