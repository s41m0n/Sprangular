import {Course} from './course.model';

export enum AssignmentStatus {
    NULL,
    READ,
    DELIVERED,
    REVIEWED,
    REVIEWED_UPLOADABLE,
    DEFINITIVE
}

export class AssignmentSolution {
    id: number;
    course: Course;
    dueDate: string;
    status: AssignmentStatus;
    grade: string;

    constructor(id: number = 0, grade: string = '', status: number = 0) {
        this.id = id;
        this.grade = grade;
        this.status = AssignmentStatus[AssignmentStatus[status]];
    }

}

