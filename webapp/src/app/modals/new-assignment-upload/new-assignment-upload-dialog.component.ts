import {Component, Inject, OnInit} from '@angular/core';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {StudentService} from '../../services/student.service';
import {MAT_DIALOG_DATA, MatDialogRef} from '@angular/material/dialog';
import {first} from 'rxjs/operators';
import {FileInput} from 'ngx-material-file-input';
import {AuthService} from '../../services/auth.service';
import {ProfessorService} from '../../services/professor.service';

@Component({
    selector: 'app-new-assignment-upload',
    templateUrl: './new-assignment-upload-dialog.component.html',
    styleUrls: ['./new-assignment-upload-dialog.component.css'],
})
export class NewAssignmentUploadDialogComponent implements OnInit {
    form: FormGroup;
    assignmentInvalid = false;
    assignmentSolutionId: number;

    constructor(
        private fb: FormBuilder,
        private studentService: StudentService,
        private professorService: ProfessorService,
        private authService: AuthService,
        public dialogRef: MatDialogRef<NewAssignmentUploadDialogComponent>,
        @Inject(MAT_DIALOG_DATA) public data: any) {
        this.assignmentSolutionId = data.assignmentSolutionId;
    }

    ngOnInit(): void {
        this.form = this.fb.group({
            comment: ['', Validators.pattern('^[A-Za-z .,;:!?]{1,128}$')],
            document: [''],
            reuploadable: false
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
        formData.append('reUploadable', this.form.get('reuploadable').value);
        if (this.isProfessor()) {
            this.professorService
                .professorAssignmentUpload(formData, this.assignmentSolutionId)
                .pipe(first())
                .subscribe(
                    (res) => this.dialogRef.close(res),
                    () => this.assignmentInvalid = true
                );
        } else {
            this.studentService
                .studentAssignmentUpload(formData, this.assignmentSolutionId)
                .pipe(first())
                .subscribe(
                    (res) => this.dialogRef.close(res),
                    () => this.assignmentInvalid = true
                );
        }
    }

    isProfessor(): boolean {
        return this.authService.currentUserValue.roles[0] === 'ROLE_PROFESSOR';
    }
}
