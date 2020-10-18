export class ProfessorUpload {
    id: number;
    localDateTime: string;
    comment: string;
    releaseDate: string;
    imagePath: string;
    studentUploadId: number;

    constructor(id: number = 0, comment: string = '', localDateTime: string = '') {
        this.id = id;
        this.comment = comment;
        this.localDateTime = localDateTime;
    }
}
