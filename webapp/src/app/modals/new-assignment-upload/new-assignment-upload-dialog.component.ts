import {Component, Inject, OnInit} from '@angular/core';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {StudentService} from '../../services/student.service';
import {MAT_DIALOG_DATA, MatDialogRef} from '@angular/material/dialog';
import {Upload} from '../../models/upload.model';
import {first} from 'rxjs/operators';
import {FileInput} from 'ngx-material-file-input';

@Component({
    selector: 'app-new-assignment-upload',
    templateUrl: './new-assignment-upload-dialog.component.html',
    styleUrls: ['./new-assignment-upload-dialog.component.css'],
})
export class NewAssignmentUploadDialogComponent implements OnInit {
    form: FormGroup;
    assignmentInvalid = false;
    assignmentId: number;

    constructor(
        private fb: FormBuilder,
        private studentService: StudentService,
        public dialogRef: MatDialogRef<NewAssignmentUploadDialogComponent>,
        @Inject(MAT_DIALOG_DATA) public data: any) {
        this.assignmentId = data.assignmentId;
    }

    ngOnInit(): void {
        this.form = this.fb.group({
            comment: ['', Validators.pattern('^[A-Za-z .,;:!?]{1,128}$')],
            document: ['']
        });
    }

    onSubmit() {
        if (this.form.invalid) {
            return;
        }

        const formData = new FormData();
        formData.append('comment', this.form.get('comment').value);
        const fileInput: FileInput = this.form.get('document').value;
        formData.append('document', fileInput.files[0]);
        // TODO: not needing assId but assSolutionId!
        this.studentService
            .uploadAssignmentSolution(formData, this.assignmentId)
            .pipe(first())
            .subscribe(
                () => this.dialogRef.close(true),
                () => this.assignmentInvalid = true
            );
    }
}
