import { Injectable } from '@angular/core';

import { BehaviorSubject, from, Observable, of } from 'rxjs';
import { HttpClient } from '@angular/common/http';
import { catchError, map, mergeMap, tap, toArray } from 'rxjs/operators';
import { ToastrService } from 'ngx-toastr';
import { Course } from '../models/course.model';
import { Assignment } from '../models/assignment.model';
import { Student } from '../models/student.model';
import { VM } from '../models/vm.model';
import { Professor } from '../models/professor.model';
import { ProfessorService } from './professor.service';
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
  public currentCourseSubject: BehaviorSubject<Course>;

  constructor(
    private http: HttpClient,
    private toastrService: ToastrService,
    private professorService: ProfessorService
  ) {
    this.currentCourseSubject = new BehaviorSubject<Course>(null);
  }

  /**
   * Function to retrieve a Course resource given a path
   *
   * @param(path) the requested path
   */
  getCourseByPath(path: string): Observable<Course> {
    return this.http
      .get<Course[]>(
        `${environment.base_courses_url}?path_like=${path}&_limit=1`
      )
      .pipe(
        // If I don't know a priori which data the server sends me --> map(res => Object.assign(new Course(), res)),
        // Take the first one (json-server does not support direct search, but we have to use _like query)
        map((x) => x.shift()),
        tap(() => console.log(`fetched course by path ${path} - getCourses()`)),
        catchError(this.handleError<Course>(`getCourseByPath(${path})`))
      );
  }

  /**
   * Function to retrieve all students enroll to a Course
   *
   * @param(course) the objective course
   */
  getEnrolledStudents(course: Course): Observable<Student[]> {
    return this.http
      .get<Student[]>(
        `${environment.base_courses_url}/${course.acronym}/students?_expand=team`
      )
      .pipe(
        // If I don't know a priori which data the server sends me --> map(res => res.map(r => Object.assign(new Student(), r))),
        tap(() =>
          console.log(
            `fetched enrolled ${course.name} students - getEnrolledStudents()`
          )
        ),
        catchError(
          this.handleError<Student[]>(`getEnrolledStudents(${course.name})`)
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

  getCourseVMs(course: Course): Observable<VM[]> {
    return this.http
      .get<VM[]>(
        `${environment.base_courses_url}/${course.acronym}/vms?_expand=team`
      )
      .pipe(
        tap(() =>
          console.log(`fetched course ${course.name} vms - getCourseVMs()`)
        ),
        catchError(this.handleError<VM[]>(`getCourseVMs(${course.name})`))
      );
  }

  getAvailableStudents(
    course: Course,
    currentUser: string
  ): Observable<Student[]> {
    return this.http
      .get<Student[]>(
        `${environment.base_courses_url}/${course.acronym}/students?teamId_like=0&email_ne=${currentUser}`
      )
      .pipe(
        tap(() =>
          console.log(
            `fetched available students in course ${course.name} - getAvailableStudents()`
          )
        ),
        catchError(
          this.handleError<Student[]>(`getAvailableStudents(${course.name})`)
        )
      );
  }

  getCourseAssignments(course: Course): Observable<Assignment[]> {
    return this.http
      .get<Assignment[]>(
        `${environment.base_assignments_url}?courseId=${course.acronym}&_expand=professor`
      )
      .pipe(
        tap(() =>
          console.log(
            `fetched assignments in course ${course.name} - getCourseAssignments()`
          )
        ),
        catchError(
          this.handleError<Assignment[]>(`getCourseAssignments(${course.name})`)
        )
      );
  }

  getCourseProfessors(course: Course): Observable<Professor[]> {
    return this.http
      .get<Professor[]>(
        `${environment.base_courses_url}/${course.acronym}?_expand=professor`
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
