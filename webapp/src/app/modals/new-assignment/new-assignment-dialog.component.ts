import {Component, OnInit} from '@angular/core';
import {FileInput} from 'ngx-material-file-input';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {CourseService} from '../../services/course.service';
import {MatDialogRef} from '@angular/material/dialog';
import {first} from 'rxjs/operators';
import * as moment from 'moment';

@Component({
  selector: 'app-new-assignment',
  templateUrl: './new-assignment-dialog.component.html',
  styleUrls: ['./new-assignment-dialog.component.css']
})
export class NewAssignmentDialogComponent implements OnInit {
  date: string = null;
  minDate = moment(new Date()).format('YYYY-MM-DD');
  maxDate = moment(this.minDate).add(9, 'M').format('YYYY-MM-DD');
  form: FormGroup;

  constructor(private fb: FormBuilder,
              private courseService: CourseService,
              public dialogRef: MatDialogRef<NewAssignmentDialogComponent>) {}

  ngOnInit() {
    this.form = this.fb.group({
      title: ['', [Validators.pattern('^[A-Za-z0-9 -]{2,32}$')]],
      doc: [''],
      date: ['']
    });
  }

  selectDate(date: string) {
    this.date = date;
  }

  onSubmit() {
    if (this.form.invalid) {
      return;
    }
    const formData = new FormData();
    formData.append('name', this.form.get('title').value);
    const deadlineDate = new Date(this.date);
    deadlineDate.setDate(deadlineDate.getDate() + 1);
    formData.append('dueDate', deadlineDate.getTime().toString(10));
    const fileInput: FileInput = this.form.get('doc').value;
    formData.append('document', fileInput.files[0]);

    this.courseService.createAssignment(formData).pipe(first()).subscribe((assignment) => {
      if (assignment) {
        this.dialogRef.close(assignment);
      }
    });
  }
}
