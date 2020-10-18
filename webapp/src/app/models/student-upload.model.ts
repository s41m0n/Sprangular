export class StudentUpload {
    id: number;
    localDateTime: string;
    comment: string;
    releaseDate: string;
    imagePath: string;
    assignmentSolutionId: number;
    professorUpdateId: number;

    constructor(id: number = 0, comment: string = '', localDateTime: string = '') {
        this.id = id;
        this.comment = comment;
        this.localDateTime = localDateTime;
    }
}
