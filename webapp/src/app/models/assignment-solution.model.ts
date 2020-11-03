import {Course} from './course.model';

export enum AssignmentStatus {
    NULL = 'NULL',
    READ = 'READ',
    DELIVERED = 'DELIVERED',
    REVIEWED = 'REVIEWED',
    REVIEWED_UPLOADABLE = 'REVIEWED_UPLOADABLE',
    DEFINITIVE = 'DEFINITIVE'
}

export class AssignmentSolution {
    id: number;
    course: Course;
    dueDate: string;
    status: AssignmentStatus;
    statusTs: string;
    grade: string;

    constructor(id: number = 0, grade: string = '', status: number = 0) {
        this.id = id;
        this.grade = grade;
        this.status = AssignmentStatus[AssignmentStatus[status]];
    }

}

