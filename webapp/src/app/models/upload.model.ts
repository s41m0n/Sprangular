export class Upload {
    id: number;
    localDateTime: string;
    comment: string;
    imagePath: string;

    constructor(id: number = 0, comment: string = '', localDateTime: string = '') {
        this.id = id;
        this.comment = comment;
        this.localDateTime = localDateTime;
    }
}
