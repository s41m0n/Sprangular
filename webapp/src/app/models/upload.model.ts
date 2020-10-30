export class Upload {
    id: number;
    localDateTime: string;
    comment: string;
    imagePath: string;
    imageFile: File;

    constructor(id: number = 0,
                comment: string = '',
                localDateTime: string = '',
                imageFile: File = new File([], '')) {
        this.id = id;
        this.comment = comment;
        this.localDateTime = localDateTime;
        this.imageFile = imageFile;
    }
}
