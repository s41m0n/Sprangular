import { Component, OnInit, OnDestroy } from '@angular/core';

import { Student } from '../../models/student.model';
import { StudentService } from '../../services/student.service';
import { first, switchMap, takeUntil, finalize } from 'rxjs/operators';
import { Subject, Observable } from 'rxjs';
import { Course } from 'src/app/models/course.model';
import { CourseService } from 'src/app/services/course.service';
import { BroadcasterService } from 'src/app/services/broadcaster.service';

/**
 * StudentsContainer class
 * 
 * It contains the StudentComponent and all the application logic
 */
@Component({
  selector: 'app-students-cont',
  templateUrl: './students.container.html',
})
export class StudentsContainer implements OnInit, OnDestroy{

  private course : Course;                                      //The current selected course
  enrolledStudents: Student[] = [];                             //The current enrolled list
  filteredStudents : Observable<Student[]>;                     //The list of students matching a criteria
  private searchTerms = new Subject<string>();                  //The search criteria emitter
  private destroy$: Subject<boolean> = new Subject<boolean>();  //Private subject to perform the unsubscriptions when the component is destroyed

  constructor(private studentService : StudentService,
    private broadcaster: BroadcasterService,
    private courseService: CourseService) {}

  ngOnInit(): void {
    //Subscribe to the Broadcaster course selected, to update the current rendered course
    this.broadcaster.subscribeCourse().pipe(takeUntil(this.destroy$)).subscribe(course => {
      this.course = course;
      this.refreshEnrolled();
    })
    //Subscribe to the search terms emitter
    this.filteredStudents = this.searchTerms.pipe(
      takeUntil(this.destroy$),
      // switch to new search observable each time the term changes
      switchMap((name: string) => this.studentService.searchStudents(name)),
    );
  }

  ngOnDestroy() {
    /** Destroying subscription */
    this.destroy$.next(true);
    this.destroy$.unsubscribe();
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
    this.studentService.unenrollStudents(students, this.course)
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
    this.studentService.enrollStudents(students, this.course)
      .pipe(
        first(),
        finalize(() => this.refreshEnrolled())
      )
      .subscribe();
  }

  /** Private function to refresh the list of enrolled students*/
  private refreshEnrolled() {
    //Check if already received the current course
    if(!this.course) {
      this.enrolledStudents = [];
      return;
    }
    this.courseService.getEnrolledStudents(this.course).pipe(first()).subscribe(students => this.enrolledStudents = students);
  }
}
