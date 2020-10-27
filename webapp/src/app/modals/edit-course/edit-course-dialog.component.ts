import { Component, OnInit } from '@angular/core';
import { MatDialogRef } from '@angular/material/dialog';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { first } from 'rxjs/operators';
import { CourseService } from 'src/app/services/course.service';
import { Course } from 'src/app/models/course.model';

@Component({
  selector: 'app-edit-course-dialog',
  templateUrl: './edit-course-dialog.component.html',
  styleUrls: ['./edit-course-dialog.component.css'],
})
export class EditCourseDialogComponent implements OnInit {
  form: FormGroup;
  course: Course;
  checked: boolean;

  constructor(
    private fb: FormBuilder,
    private courseService: CourseService,
    public dialogRef: MatDialogRef<EditCourseDialogComponent>
  ) {}

  ngOnInit(): void {
    this.course = this.courseService.course.getValue();
    this.form = this.fb.group({
      teamMinSize: [
        this.course.teamMinSize,
        [Validators.min(1), Validators.max(10)],
      ],
      teamMaxSize: [
        this.course.teamMaxSize,
        [Validators.min(1), Validators.max(10)],
      ],
    });
  }

  onSubmit() {
    if (this.form.invalid) {
      return;
    }
    const course = new Course(
      this.course.acronym,
      this.course.name,
      this.form.get('teamMinSize').value,
      this.form.get('teamMaxSize').value,
      this.checked
    );

    this.courseService
      .updateCourse(course)
      .pipe(first())
      .subscribe((res) => {
        if (res) {
          this.dialogRef.close();
        }
      });
  }

  deleteCourse() {
    this.courseService
      .deleteCourse()
      .pipe(first())
      .subscribe((res) => {
        if (res) {
          this.dialogRef.close();
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

  courseStatusChanged(value: boolean) {
    this.checked = value;
  }
}
