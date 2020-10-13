import { Component, OnInit } from '@angular/core';
import { MatDialogRef } from '@angular/material/dialog';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { first } from 'rxjs/operators';
import { CourseService } from 'src/app/services/course.service';

@Component({
  selector: 'app-new-course',
  templateUrl: './new-course-dialog.component.html',
  styleUrls: ['./new-course-dialog.component.css'],
})
export class NewCourseComponent implements OnInit {
  form: FormGroup;
  vmInvalid = false;

  constructor(
    private fb: FormBuilder,
    private courseService: CourseService,
    public dialogRef: MatDialogRef<NewCourseComponent>
  ) {}

  ngOnInit(): void {
    this.form = this.fb.group({
      vCpu: [2, [Validators.min(1), Validators.max(6)]],
      ram: [2, [Validators.min(1), Validators.max(4)]],
      diskStorage: [5, [Validators.min(1), Validators.max(5)]],
    });
  }

  pickle(name: string, value: number): void {
    this.form[name].value = value;
    alert(value);
  }

  onSubmit() {
    if (this.form.invalid) {
      return;
    }
  }
}
