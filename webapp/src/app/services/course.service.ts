import { Injectable } from '@angular/core';

import { BehaviorSubject, from, Observable, of } from 'rxjs';
import { HttpClient } from '@angular/common/http';
import { catchError, first, mergeMap, tap, toArray } from 'rxjs/operators';
import { ToastrService } from 'ngx-toastr';
import { Course } from '../models/course.model';
import { Assignment } from '../models/assignment.model';
import { Student } from '../models/student.model';
import { VM } from '../models/vm.model';
import { Professor } from '../models/professor.model';
import { environment } from 'src/environments/environment';
import { handleError } from '../helpers/handle.error';

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
  public course: BehaviorSubject<Course>;

  constructor(private http: HttpClient, private toastrService: ToastrService) {
    this.currentCourseSubject = new BehaviorSubject<string>(null);
    this.course = new BehaviorSubject<Course>(null);
  }

  setNext(acronym: string) {
    this.currentCourseSubject.next(acronym);
    this.getCourse(acronym)
      .pipe(first())
      .subscribe((x) => this.course.next(x));
  }

  /**
   * Function to retrieve a Course resource given a path
   *
   * @param(path) the requested path
   */
  getCourse(acronym: string): Observable<Course> {
    return this.http
      .get<Course>(`${environment.base_courses_url}/${acronym}`)
      .pipe(
        tap(() =>
          console.log(`fetched course by path ${acronym} - getCourse()`)
        ),
        catchError(
          handleError<Course>(this.toastrService, `getCourse(${acronym})`)
        )
      );
  }

  /**
   * Function to retrieve all students enroll to a Course
   *
   * @param(course) the objective course
   */
  getEnrolledStudents(
    courseId: string = this.currentCourseSubject.value
  ): Observable<Student[]> {
    return this.http
      .get<Student[]>(`${environment.base_courses_url}/${courseId}/students`)
      .pipe(
        tap(() =>
          console.log(
            `fetched enrolled ${courseId} students - getEnrolledStudents()`
          )
        ),
        catchError(
          handleError<Student[]>(
            this.toastrService,
            `getEnrolledStudents(${courseId})`
          )
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
      catchError(handleError<Course[]>(this.toastrService, `getCourses()`))
    );
  }

  getCourseVMs(courseId: string): Observable<VM[]> {
    return this.http
      .get<VM[]>(`${environment.base_courses_url}/${courseId}/vms`)
      .pipe(
        tap(() =>
          console.log(`fetched course ${courseId} vms - getCourseVMs()`)
        ),
        catchError(
          handleError<VM[]>(this.toastrService, `getCourseVMs(${courseId})`)
        )
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
          handleError<Student[]>(
            this.toastrService,
            `getAvailableStudents(${courseId})`
          )
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
          handleError<Assignment[]>(
            this.toastrService,
            `getCourseAssignments(${courseId})`
          )
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
          handleError<Professor[]>(
            this.toastrService,
            `getCourseProfessors(${course.name})`
          )
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
   * Function to enroll students to a specific Course
   * Return value is ignored, since the we reload the entire list
   *
   * @param(students) the list of students to be enrolled
   * @param(course) the objective course
   */
  enrollStudents(
    students: Student[],
    courseId: string = this.currentCourseSubject.value
  ): Observable<Student[]> {
    return from(students).pipe(
      mergeMap((student: Student) => {
        // Checking if ADD has been pressed without selecting a student (or modifying the selected one)
        if (typeof student === 'string') {
          this.toastrService.error(
            `${student} is not a valid Student, please select one from the options`,
            'Error 😅'
          );
          return of(null);
        }
        return this.http
          .put<Student>(
            `${environment.base_courses_url}/${courseId}/enrollOne`,
            { studentId: student.id },
            environment.base_http_headers
          )
          .pipe(
            tap((s) => {
              this.toastrService.success(
                `Enrolled ${Student.displayFn(s)} to ${courseId}`,
                'Congratulations 😃'
              );
              console.log(
                `enrolled ${Student.displayFn(s)} - enrollStudents()`
              );
            }),
            catchError(
              handleError<Student>(
                this.toastrService,
                `enrollStudents(${Student.displayFn(student)}, ${courseId})`
              )
            )
          );
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
  unenrollStudents(
    students: Student[],
    courseId: string = this.currentCourseSubject.value
  ): Observable<Student[]> {
    return from(students).pipe(
      mergeMap((student) => {
        return this.http
          .put<Student>(
            `${environment.base_courses_url}/${courseId}/removeStudent`,
            { studentId: student.id },
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
              handleError<Student>(
                this.toastrService,
                `unenrollStudents(${Student.displayFn(student)}, ${courseId})`
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

  createCourse(course: Course): Observable<Course> {
    return this.http
      .post<Course>(
        environment.base_courses_url,
        course,
        environment.base_http_headers
      )
      .pipe(
        tap(() =>
          this.toastrService.success(
            `Course ${course.name} successfully created!`,
            'Awesome 😃'
          )
        ),
        catchError(
          handleError<Course>(
            this.toastrService,
            `createCourse(${course.name})`
          )
        )
      );
  }

  updateCourse(course: Course): Observable<Course> {
    return this.http
      .put<Course>(
        `${environment.base_courses_url}/${course.acronym}`,
        course,
        environment.base_http_headers
      )
      .pipe(
        tap(() =>
          this.toastrService.success(
            `Course ${course.name} successfully updated!`,
            'Awesome 😃'
          )
        ),
        catchError(
          handleError<Course>(
            this.toastrService,
            `updateCourse(${course.name})`
          )
        )
      );
  }

  changeCourseStatus(statusRequested: boolean): Observable<boolean> {
    const enabled = statusRequested ? 'true' : 'false';
    return this.http
      .put<any>(
        `${environment.base_courses_url}/${this.currentCourseSubject.value}/toggle`,
        { enabled },
        environment.base_http_headers
      )
      .pipe(
        tap(() =>
          console.log(`set course to ${statusRequested} - changeCourseStatus()`)
        ),
        catchError(
          handleError<Course>(
            this.toastrService,
            `changeCourseStatus(${statusRequested})`
          )
        )
      );
  }

  deleteCourse(courseAcronym: string = this.currentCourseSubject.value) {
    return this.http
      .delete<Course>(`${environment.base_courses_url}/${courseAcronym}`)
      .pipe(
        tap(() => console.log(`Course ${courseAcronym} deleted`)),
        catchError(handleError<Course>(this.toastrService))
      );
  }

  createAssignment(formData: FormData): Observable<Assignment> {
    return this.http
      .post<Assignment>(
        `${environment.base_courses_url}/${this.currentCourseSubject.value}/assignments`,
        formData
      )
      .pipe(
        tap(
          () =>
            this.toastrService.success(
              `Assignment successfully created`,
              `Awesome 😃`
            ),
          catchError(
            handleError<Assignment>(
              this.toastrService,
              `Assignment Creation Failed`
            )
          )
        )
      );
  }
}
