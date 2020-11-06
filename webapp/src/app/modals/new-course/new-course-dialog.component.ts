import { Component, OnInit } from '@angular/core';
import { MatDialogRef } from '@angular/material/dialog';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { first } from 'rxjs/operators';
import { CourseService } from 'src/app/services/course.service';
import { FileInput } from 'ngx-material-file-input';

@Component({
  selector: 'app-new-course',
  templateUrl: './new-course-dialog.component.html',
  styleUrls: ['./new-course-dialog.component.css'],
})
export class NewCourseDialogComponent implements OnInit {
  form: FormGroup;

  constructor(
    private fb: FormBuilder,
    private courseService: CourseService,
    public dialogRef: MatDialogRef<NewCourseDialogComponent>
  ) {}

  ngOnInit(): void {
    this.form = this.fb.group({
      acronym: ['', [Validators.pattern('^[A-Z0-9]{2,10}$')]],
      name: ['', [Validators.pattern('^[A-Za-z0-9 -]{1,32}$')]],
      teamMinSize: [1, [Validators.min(1), Validators.max(10)]],
      teamMaxSize: [1, [Validators.min(1), Validators.max(10)]],
      vmModel: [''],
    });
  }

  onSubmit() {
    if (this.form.invalid) {
      return;
    }
    const formData = new FormData();
    formData.append('acronym', this.form.get('acronym').value);
    formData.append('name', this.form.get('name').value);
    formData.append('teamMinSize', this.form.get('teamMinSize').value);
    formData.append('teamMaxSize', this.form.get('teamMaxSize').value);
    formData.append('enabled', 'true');
    const fileInput: FileInput = this.form.get('vmModel').value;
    formData.append('vmModel', fileInput.files[0]);

    this.courseService
      .createCourse(formData)
      .pipe(first())
      .subscribe((res) => {
        if (res) {
          this.dialogRef.close(formData);
        }
      });
  }

  adaptMin(value: number) {
    if (
      this.form.get('teamMinSize').value > this.form.get('teamMaxSize').value
    ) {
      this.form.get('teamMaxSize').setValue(value);
    }
  }

  adaptMax(value: number) {
    if (
      this.form.get('teamMaxSize').value < this.form.get('teamMinSize').value
    ) {
      this.form.get('teamMinSize').setValue(value);
    }
  }
}
