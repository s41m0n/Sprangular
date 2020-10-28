import {Component, OnInit} from '@angular/core';
import {FileInput} from 'ngx-material-file-input';
import {FormBuilder, FormGroup} from '@angular/forms';
import {CourseService} from '../../services/course.service';
import {MatDialogRef} from '@angular/material/dialog';

@Component({
  selector: 'app-new-assignment',
  templateUrl: './new-assignment-dialog.component.html',
  styleUrls: ['./new-assignment-dialog.component.css']
})
export class NewAssignmentDialogComponent implements OnInit {
  date: string = null;
  form: FormGroup;

  constructor(private fb: FormBuilder,
              private courseService: CourseService,
              public dialogRef: MatDialogRef<NewAssignmentDialogComponent>) {}

  ngOnInit() {
    this.form = this.fb.group({
      title: [''],
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
    formData.append('dueDate', new Date(this.date).getTime().toString(10));
    const fileInput: FileInput = this.form.get('doc').value;
    formData.append('document', fileInput.files[0]);

    this.courseService.createAssignment(formData).subscribe((assignment) => {
      if (assignment) {
        this.dialogRef.close(true);
      }
    });
  }
}