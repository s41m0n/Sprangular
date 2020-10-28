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
import { Observable, Subject } from 'rxjs';
import { ConfirmationDialogComponent } from '../confirmation-dialog/confirmation-dialog.component';
import { Professor } from 'src/app/models/professor.model';
import { AuthService } from 'src/app/services/auth.service';
import {
  first,
  debounceTime,
  distinctUntilChanged,
  takeUntil,
  switchMap,
} from 'rxjs/operators';

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
  professors: Professor[] = [];
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
        }
      });
    this.refreshProfessors();
    this.currentProfessorId = this.authService.currentUserValue.id;
    this.addProfessorControl.valueChanges
      .pipe(
        takeUntil(this.destroy$),
        // wait 300ms after each keystroke before considering the term
        debounceTime(300),
        // ignore new term if same as previous term
        distinctUntilChanged()
      )
      .subscribe(
        (name: string) =>
          (this.filteredProfessors = this.professorService.searchProfessors(
            name
          ))
      );
  }

  ngOnDestroy() {
    this.destroy$.next(true);
    this.destroy$.unsubscribe();
  }

  editCourse() {
    console.log('ciao');
    if (this.form.invalid) {
      return;
    }
    const course = new Course(
      this.courseService.course.value.acronym,
      this.courseService.course.value.name,
      this.form.get('teamMinSize').value,
      this.form.get('teamMaxSize').value,
      this.checked
    );

    this.courseService
      .updateCourse(course)
      .pipe(first())
      .subscribe((res) => {
        if (res) {
          this.courseService.course.next(res);
          this.dialogRef.close(res);
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
    if (this.addProfessorControl.value.id === this.currentProfessorId) {
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
  }

  private refreshProfessors() {
    this.courseService
      .getCourseProfessors(this.course)
      .subscribe((professors) => {
        this.professors = professors;
      });
  }
}
