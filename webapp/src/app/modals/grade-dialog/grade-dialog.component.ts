import {Component, Inject, OnInit} from '@angular/core';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {MAT_DIALOG_DATA, MatDialogRef} from '@angular/material/dialog';
import {AssignmentAndUploadService} from '../../services/assignment-and-upload.service';
import {first} from 'rxjs/operators';

@Component({
  selector: 'app-grade-dialog',
  templateUrl: 'grade-dialog.component.html',
})
export class GradeDialogComponent implements OnInit {
  form: FormGroup;
  assignmentInvalid = false;

  constructor(
      private fb: FormBuilder,
      public dialogRef: MatDialogRef<GradeDialogComponent>,
      private assignmentService: AssignmentAndUploadService,
      @Inject(MAT_DIALOG_DATA) public data: any) {
  }

  ngOnInit() {
    this.form = this.fb.group({
      grade: ['', Validators.min(1)]
    });
  }

  onSubmit() {
    if (this.form.invalid) {
      return;
    }
    this.assignmentService.evaluateAssignment(Number.parseInt(this.data.assignmentSolutionId, 10), this.form.get('grade').value)
        .pipe(first())
        .subscribe(
            (res) => this.dialogRef.close(res),
            () => this.assignmentInvalid = true
        );
  }
}
