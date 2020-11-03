import {AssignmentStatus} from './assignment-solution.model';

export class Upload {
    id: number;
    imagePath: string;
    timestamp: string;
    comment: string;
    status: AssignmentStatus;
    author: string;

    constructor(id: number = 0,
                imagePath: string = '',
                timestamp: string = '',
                comment: string = '',
                status: AssignmentStatus,
                author: string) {
        this.id = id;
        this.imagePath = imagePath;
        this.timestamp = timestamp;
        this.comment = comment;
        this.status = status;
        this.author = author;
    }

    static compare(a: Upload, b: Upload) {
        return a.timestamp.localeCompare(b.timestamp);
    }
}
