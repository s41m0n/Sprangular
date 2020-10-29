import { Component, EventEmitter, OnDestroy, OnInit } from '@angular/core';
import { MatDialog, MatDialogRef } from '@angular/material/dialog';
import {
  FormBuilder,
  FormGroup,
  Validators,
  FormControl,
} from '@angular/forms';
import { CourseService } from 'src/app/services/course.service';
import { ProfessorService } from 'src/app/services/professor.service';
import { Course } from 'src/app/models/course.model';
import { Observable, of, Subject } from 'rxjs';
import { ConfirmationDialogComponent } from '../confirmation-dialog/confirmation-dialog.component';
import { Professor } from 'src/app/models/professor.model';
import { AuthService } from 'src/app/services/auth.service';
import {
  first,
  debounceTime,
  distinctUntilChanged,
  takeUntil,
} from 'rxjs/operators';
import { FileInput } from 'ngx-material-file-input';

@Component({
  selector: 'app-edit-course-dialog',
  templateUrl: './edit-course-dialog.component.html',
  styleUrls: ['./edit-course-dialog.component.css'],
})
export class EditCourseDialogComponent implements OnInit, OnDestroy {
  form: FormGroup;
  course: Course;
  checked: boolean;
  private destroy$: Subject<boolean> = new Subject<boolean>(); // Private subject to perform the unsubscriptions when component is destroyed
  professors: Observable<Professor[]>;
  currentProfessorId: string;
  addProfessorControl = new FormControl();
  searchProfessorEvent = new EventEmitter<string>();
  filteredProfessors: Observable<Professor[]>;

  constructor(
    private fb: FormBuilder,
    private courseService: CourseService,
    private authService: AuthService,
    public dialogRef: MatDialogRef<EditCourseDialogComponent>,
    private professorService: ProfessorService,
    public dialog: MatDialog
  ) {}

  ngOnInit(): void {
    this.form = this.fb.group({
      teamMinSize: ['', [Validators.min(1), Validators.max(10)]],
      teamMaxSize: ['', [Validators.min(1), Validators.max(10)]],
    });
    this.courseService.course
      .asObservable()
      .pipe(takeUntil(this.destroy$))
      .subscribe((c) => {
        if (c) {
          this.course = c;
          this.form.get('teamMinSize').setValue(c.teamMinSize);
          this.form.get('teamMaxSize').setValue(c.teamMaxSize);
          this.checked = c.enabled;
          this.refreshProfessors();
        }
      });
    this.currentProfessorId = this.authService.currentUserValue.id;
    this.addProfessorControl.valueChanges
      .pipe(
        takeUntil(this.destroy$),
        // wait 300ms after each keystroke before considering the term
        debounceTime(300),
        // ignore new term if same as previous term
        distinctUntilChanged()
      )
      .subscribe((name: string) =>
        this.professorService
          .searchProfessors(name)
          .subscribe(
            (arr) =>
              (this.filteredProfessors = of(
                arr.filter((p) => p.id !== this.authService.currentUserValue.id)
              ))
          )
      );
  }

  ngOnDestroy() {
    this.destroy$.next(true);
    this.destroy$.unsubscribe();
  }

  editCourse() {
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
          this.dialogRef.close();
        }
      });
  }

  deleteCourse() {
    const confirmRef = this.dialog.open(ConfirmationDialogComponent, {
      disableClose: false,
    });
    confirmRef.componentInstance.confirmMessage = `Are you sure you want to delete ${this.course.name} course?\nThis operation cannot be undone!`;
    confirmRef
      .afterClosed()
      .pipe(first())
      .subscribe((result) => {
        if (result) {
          this.courseService
            .deleteCourse()
            .pipe(first())
            .subscribe((res) => {
              if (res) {
                this.dialogRef.close(null);
              }
            });
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

  displayFn(professor: Professor): string {
    return professor ? Professor.displayFn(professor) : '';
  }

  removeProfessor(professor: Professor) {
    this.courseService
      .removeProfessorFromCourse(professor, this.course)
      .pipe(first())
      .subscribe((res) => {
        if (res) {
          this.refreshProfessors();
        }
      });
  }

  addProfessor() {
    this.professors.pipe(first()).subscribe((arr) => {
      if (arr.find((p) => p.id === this.addProfessorControl.value.id)) {
        this.addProfessorControl.setValue('');
        return;
      }
      this.courseService
        .addProfessorToCourse(this.addProfessorControl.value, this.course)
        .pipe(first())
        .subscribe((res) => {
          if (res) {
            this.refreshProfessors();
          }
        });
      this.addProfessorControl.setValue('');
    });
  }

  private refreshProfessors() {
    this.professors = this.courseService
      .getCourseProfessors(this.course)
      .pipe(first());
  }
}
