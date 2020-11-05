import { Component, OnDestroy, OnInit } from '@angular/core';
import { Student } from '../../models/student.model';
import { StudentService } from '../../services/student.service';
import {filter, finalize, first, map, switchMap, takeUntil} from 'rxjs/operators';
import { Observable, Subject } from 'rxjs';
import { CourseService } from '../../services/course.service';
import { NavigationEnd, Router } from '@angular/router';

/**
 * StudentsContainer class
 *
 * It contains the StudentComponent and all the application logic
 */
@Component({
  selector: 'app-tab-students-cont',
  templateUrl: './tab-students.container.html',
})
export class TabStudentsContComponent implements OnInit, OnDestroy {
  enrolledStudents: Student[] = []; // The current enrolled list
  filteredStudents: Observable<Student[]>; // The list of students matching a criteria
  private searchTerms = new Subject<string>(); // The search criteria emitter
  private destroy$: Subject<boolean> = new Subject<boolean>(); // Private subject to perform the unsubscriptions when component is destroyed
  private previousUrl = '';
  navSub;

  constructor(
    private studentService: StudentService,
    private courseService: CourseService,
    private router: Router
  ) {
    this.navSub = this.router.events
      .pipe(filter((event) => event instanceof NavigationEnd))
      .subscribe((event: NavigationEnd) => {
        if (
          !this.previousUrl.includes(
            this.courseService.currentCourseSubject.value
          )
        ) {
          this.refreshEnrolled();
        }
        this.previousUrl = event.url;
      });
  }

  ngOnInit(): void {
    // Subscribe to the search terms emitter
    this.filteredStudents = this.searchTerms.pipe(
      takeUntil(this.destroy$),
      // switch to new search observable each time the term changes
      switchMap((name: string) => this.studentService.searchStudents(name)),
      map(elems => elems.filter(s => !this.enrolledStudents.map(e => e.id).includes(s.id)))
    );
  }

  ngOnDestroy() {
    /** Destroying subscription */
    this.destroy$.next(true);
    this.destroy$.unsubscribe();
    if (this.navSub) {
      this.navSub.unsubscribe();
    }
  }

  /**
   * Function to push new search terms
   *
   * @param(name) the new search terms
   */
  searchStudents(name: string): void {
    this.searchTerms.next(name);
  }

  /**
   * Function to unenroll a student, which calls the apposite service function and refresh the list
   *
   * @param(students) the list of students to be unenrolled
   */
  unenrollStudents(students: Student[]) {
    this.courseService
      .unenrollStudents2(students)
      .pipe(
        first(),
        finalize(() => this.refreshEnrolled())
      )
      .subscribe();
  }

  /**
   * Function to enroll a student, which calls the apposite service function and refresh the list
   *
   * @param(students) the list of students to be enrolled
   */
  enrollStudents(students: Student[]) {
    this.courseService
      .enrollStudents(students)
      .pipe(
        first(),
        finalize(() => this.refreshEnrolled())
      )
      .subscribe();
  }

  enrollWithCsv(formData: FormData) {
    this.courseService
      .enrollWithCsv(formData)
      .pipe(
        first(),
        finalize(() => this.refreshEnrolled())
      )
      .subscribe();
  }

  /** Private function to refresh the list of enrolled students */
  private refreshEnrolled() {
    // Check if already received the current course
    if (!this.courseService.currentCourseSubject.value) {
      this.enrolledStudents = [];
      return;
    }
    this.courseService
      .getEnrolledStudents(this.courseService.currentCourseSubject.value)
      .pipe(first())
      .subscribe((students) => (this.enrolledStudents = students));
  }
}
