import {AssignmentStatus} from './assignment-solution.model';

export class Upload {
    id: number;
    imagePath: string;
    timestamp: string;
    comment: string;
    status: AssignmentStatus;

    constructor(id: number = 0,
                imagePath: string = '',
                timestamp: string = '',
                comment: string = '',
                status: AssignmentStatus) {
        this.id = id;
        this.imagePath = imagePath;
        this.timestamp = timestamp;
        this.comment = comment;
        this.status = status;
    }

    static compare(a: Upload, b: Upload) {
        return a.timestamp.localeCompare(b.timestamp);
    }
}
