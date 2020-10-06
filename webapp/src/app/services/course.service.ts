import { Injectable } from '@angular/core';

import { BehaviorSubject, from, Observable, of } from 'rxjs';
import { HttpClient } from '@angular/common/http';
import { catchError, mergeMap, tap, toArray } from 'rxjs/operators';
import { ToastrService } from 'ngx-toastr';
import { Course } from '../models/course.model';
import { Assignment } from '../models/assignment.model';
import { Student } from '../models/student.model';
import { VM } from '../models/vm.model';
import { Professor } from '../models/professor.model';
import { environment } from 'src/environments/environment';

/**
 * CourseService service
 *
 * This service is responsible of handling all the interactions with courses through rest api.
 *
 */
@Injectable({
  providedIn: 'root',
})
export class CourseService {
  // Current Course Subject: keeps hold of the current value and emits it to any new subscribers as soon as they subscribe
  public currentCourseSubject: BehaviorSubject<string>;

  constructor(
    private http: HttpClient,
    private toastrService: ToastrService,
  ) {
    this.currentCourseSubject = new BehaviorSubject<string>(null);    
  }

  /**
   * Function to retrieve a Course resource given a path
   *
   * @param(path) the requested path
   */
  getCourse(acronym: string): Observable<Course> {
    return this.http
      .get<Course>(
        `${environment.base_courses_url}/${acronym}`
      )
      .pipe(
        tap(() => console.log(`fetched course by path ${acronym} - getCourse()`)),
        catchError(this.handleError<Course>(`getCourse(${acronym})`))
      );
  }

  /**
   * Function to retrieve all students enroll to a Course
   *
   * @param(course) the objective course
   */
  getEnrolledStudents(courseId: string = this.currentCourseSubject.value): Observable<Student[]> {
    return this.http
      .get<Student[]>(
        `${environment.base_courses_url}/${courseId}/students`
      )
      .pipe(
        tap(() =>
          console.log(
            `fetched enrolled ${courseId} students - getEnrolledStudents()`
          )
        ),
        catchError(
          this.handleError<Student[]>(`getEnrolledStudents(${courseId})`)
        )
      );
  }

  /**
   * Function to retrieve the list of Courses available
   */
  getCourses(): Observable<Course[]> {
    return this.http.get<Course[]>(environment.base_courses_url).pipe(
      // If I don't know a priori which data the server sends me --> map(res => res.map(r => Object.assign(new Course(), r))),
      tap(() => console.log(`fetched courses - getCourses()`)),
      catchError(this.handleError<Course[]>(`getCourses()`))
    );
  }

  getCourseVMs(courseId: string): Observable<VM[]> {
    return this.http
      .get<VM[]>(
        `${environment.base_courses_url}/${courseId}/vms`
      )
      .pipe(
        tap(() =>
          console.log(`fetched course ${courseId} vms - getCourseVMs()`)
        ),
        catchError(this.handleError<VM[]>(`getCourseVMs(${courseId})`))
      );
  }

  getAvailableStudents(
    courseId: string = this.currentCourseSubject.value
  ): Observable<Student[]> {
    return this.http
      .get<Student[]>(
        `${environment.base_courses_url}/${courseId}/availableStudents`
      )
      .pipe(
        tap(() =>
          console.log(
            `fetched available students in course ${courseId} - getAvailableStudents()`
          )
        ),
        catchError(
          this.handleError<Student[]>(`getAvailableStudents(${courseId})`)
        )
      );
  }

  getCourseAssignments(courseId: string): Observable<Assignment[]> {
    return this.http
      .get<Assignment[]>(
        `${environment.base_courses_url}/${courseId}/assignments`
      )
      .pipe(
        tap(() =>
          console.log(
            `fetched assignments in course ${courseId} - getCourseAssignments()`
          )
        ),
        catchError(
          this.handleError<Assignment[]>(`getCourseAssignments(${courseId})`)
        )
      );
  }

  getCourseProfessors(course: Course): Observable<Professor[]> {
    return this.http
      .get<Professor[]>(
        `${environment.base_courses_url}/${course.acronym}/professors`
      )
      .pipe(
        tap(() =>
          console.log(
            `fetched professors in course ${course.name} - getCourseProfessors()`
          )
        ),
        catchError(
          this.handleError<Professor[]>(`getCourseProfessors(${course.name})`)
        )
      );
  }

  assignProfessorsToCourse(
    professors: Professor[],
    course: Course
  ): Observable<Professor[]> {
    return from(professors).pipe(
      mergeMap((professor: Professor) => {
        // Checking if ADD has been pressed without selecting a professor (or modifying the selected one)
        if (typeof professor === 'string') {
          this.toastrService.error(
            `${professor} is not a valid Professor, please select one from the options`,
            'Error 😅'
          );
          return of(null);
        }
        return this.updateCourse(course);
      }),
      toArray()
    );
  }

  /**
   * Function to unenroll students from a specific course.
   * Return value is ignored, since the we reload the entire list
   *
   * @param(students) the list of students to be unenrolled
   * @param(course) the objective course
   */
  unenrollStudents(students: Student[], courseId: string = this.currentCourseSubject.value): Observable<Student[]> {
    return from(students).pipe(
      mergeMap((student) => {
        return this.http
          .put<Student>(
            `${environment.base_courses_url}/${courseId}/removeStudent`,
            {studentId: student.id},
            environment.base_http_headers
          )
          .pipe(
            tap((s) => {
              this.toastrService.success(
                `Unenrolled ${Student.displayFn(s)} from ${courseId}`,
                'Congratulations 😃'
              );
              console.log(
                `unenrolled ${Student.displayFn(s)} - unenrollStudents()`
              );
            }),
            catchError(
              this.handleError<Student>(
                `unenrollStudents(${Student.displayFn(student)}, ${
                  courseId
                })`
              )
            )
          );
      }),
      toArray()
    );
  }

  removeProfessorsFromCourse(
    professors: Professor[],
    course: Course
  ): Observable<Professor[]> {
    return from(professors).pipe(
      mergeMap((professor: Professor) => {
        // Checking if ADD has been pressed without selecting a professor (or modifying the selected one)
        if (typeof professor === 'string') {
          this.toastrService.error(
            `${professor} is not a valid Professor, please select one from the options`,
            'Error 😅'
          );
          return of(null);
        }
        return this.updateCourse(course);
      }),
      toArray()
    );
  }

  private updateCourse(course: Course): Observable<Course> {
    return this.http
      .put<Course>(
        `${environment.base_courses_url}/${course.acronym}`,
        course,
        environment.base_http_headers
      )
      .pipe(
        tap(() =>
          console.log(`updated course ${course.name} - updateCourse()`)
        ),
        catchError(this.handleError<Course>(`updateCourse(${course.name})`))
      );
  }

  /**
   * Handle Http operation that failed.
   * Let the app continue.
   * @param operation - name of the operation that failed
   * @param result - optional value to return as the observable result
   * @param show - is it visible or not
   * @param message - error message
   */
  private handleError<T>(
    operation = 'operation',
    result?: T,
    show: boolean = true,
    message: string = 'An error occurred while performing'
  ) {
    return (error: any): Observable<T> => {
      const why = `${message} ${operation}: ${error}`;

      if (show) {
        this.toastrService.error(why, 'Error 😅');
      }
      console.log(why);

      // Let the app keep running by returning an empty result.
      return of(result as T);
    };
  }
}
