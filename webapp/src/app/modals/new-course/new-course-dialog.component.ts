import { Component, OnInit } from '@angular/core';
import { MatDialogRef } from '@angular/material/dialog';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { first } from 'rxjs/operators';
import { CourseService } from 'src/app/services/course.service';
import { Course } from 'src/app/models/course.model';

@Component({
  selector: 'app-new-course',
  templateUrl: './new-course-dialog.component.html',
  styleUrls: ['./new-course-dialog.component.css'],
})
export class NewCourseComponent implements OnInit {
  form: FormGroup;
  courseInvalid = false;

  constructor(
    private fb: FormBuilder,
    private courseService: CourseService,
    public dialogRef: MatDialogRef<NewCourseComponent>
  ) {}

  ngOnInit(): void {
    this.form = this.fb.group({
      acronym: [''],
      name: [''],
      teamMinSize: [1, [Validators.min(1), Validators.max(10)]],
      teamMaxSize: [1, [Validators.min(1), Validators.max(10)]],
    });
  }

  onSubmit() {
    if (this.form.invalid) {
      return;
    }
    const course = new Course(
      this.form.get('acronym').value.toLowerCase(),
      this.form.get('name').value,
      this.form.get('teamMinSize').value,
      this.form.get('teamMaxSize').value, false
    );
    this.courseService
      .createCourse(course)
      .pipe(first())
      .subscribe((res) => {
        if (res) {
          this.dialogRef.close(true);
        } else {
          this.courseInvalid = true;
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
