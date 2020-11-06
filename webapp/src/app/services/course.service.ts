import { Injectable } from '@angular/core';

import { BehaviorSubject, from, Observable, of } from 'rxjs';
import { HttpClient } from '@angular/common/http';
import { catchError, first, mergeMap, tap, toArray } from 'rxjs/operators';
import { ToastrService } from 'ngx-toastr';
import { Course } from '../models/course.model';
import { Assignment } from '../models/assignment.model';
import { Student } from '../models/student.model';
import { Professor } from '../models/professor.model';
import { environment } from 'src/environments/environment';
import { handleError } from '../helpers/handle.error';
import {StudentAssignmentDetails} from '../models/student-assignment-details.model';
import {VmProfessorDetails} from '../models/vm-professor-details.model';

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

  /**
   * Set the current course
   * @param acronym The course id
   */
  setNext(acronym: string) {
    this.currentCourseSubject.next(acronym);
    if (!acronym) {
      this.course.next(null);
      return;
    }
    this.getCourse(acronym)
      .pipe(first())
      .subscribe((x) => this.course.next(x));
  }

  /**
   * Function to retrieve a Course resource given a path
   *
   * @param(acronym) the course acronym
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
   * @param(courseId) the course acronym
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

  /**
   * Get all the vms of the course
   * @param courseId The course acronym
   */
  getCourseVMs(courseId: string): Observable<VmProfessorDetails[]> {
    return this.http
      .get<VmProfessorDetails[]>(`${environment.base_courses_url}/${courseId}/vms`)
      .pipe(
        tap(() =>
          console.log(`fetched course ${courseId} vms - getCourseVMs()`)
        ),
        catchError(
          handleError<VmProfessorDetails[]>(this.toastrService, `getCourseVMs(${courseId})`)
        )
      );
  }

  /**
   * Get all the students available to join a team
   * @param courseId The course acronym
   */
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

  /**
   * Get all the assignment of the course
   * @param courseId The course acronym
   */
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

  /**
   * Get the professors of the course
   * @param course The course
   */
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

  /**
   * Add the professor as manager of the course
   * @param professor The professor
   * @param course The course
   */
  addProfessorToCourse(
    professor: Professor,
    course: Course
  ): Observable<Professor> {
    return this.http
      .put<any>(
        `${environment.base_courses_url}/${course.acronym}/addProfessor`,
        { professorId: professor.id },
        environment.base_http_headers
      )
      .pipe(
        tap((p) => {
          if (p) {
            this.toastrService.success(
              `Added ${professor.id} to ${course.acronym}`,
              'Congratulations 😃'
            );
          } else {
            this.toastrService.info(
              `Professor ${professor.id} already teaches ${course.acronym}`
            );
          }
        }),
        catchError(
          handleError<Professor>(
            this.toastrService,
            `addProfessor(${professor.id}, ${course.acronym})`
          )
        )
      );
  }

  /**
   * Function to enroll students to a specific Course
   * Return value is ignored, since the we reload the entire list
   *
   * @param(students) the list of students to be enrolled
   * @param(courseId) the objective course
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
            tap(() => {
              this.toastrService.success(
                `Enrolled ${Student.displayFn(student)} to ${courseId}`,
                'Congratulations 😃'
              );
              console.log(
                `enrolled ${Student.displayFn(student)} - enrollStudents()`
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
   * Enroll students inside a CSV file
   * @param formData The data with all the students
   * @param courseId The course acronym
   */
  enrollWithCsv(
    formData: FormData,
    courseId: string = this.currentCourseSubject.value
  ) {
    return this.http
      .put<boolean[]>(
        `${environment.base_courses_url}/${courseId}/enrollMany`,
        formData
      )
      .pipe(
        tap((p) => {
          if (p.includes(false)) {
            this.toastrService.info(
              `One or more students were already enrolled in ${courseId}`
            );
          } else {
            this.toastrService.success(
              `Successfully enrolled one or more students in ${courseId}`,
              'Congratulations 😃'
            );
          }
        }),
        catchError(
          handleError<Professor>(
            this.toastrService,
            `enrollWithCsv(csv, ${courseId})`
          )
        )
      );
  }

  /**
   * Remove the student from the curse
   * @param student The student
   * @param courseId The course acronym
   */
  unenrollStudent(student: Student, courseId: string) {
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
  }

  /**
   * Remove more than one student from the course
   * @param students The students
   * @param courseId The course acronym
   */
  unenrollMultipleStudents(
    students: Student[],
    courseId: string = this.currentCourseSubject.value
  ) {
    const studentsString: string[] = [];
    students.forEach((el) => studentsString.push(el.id));
    return this.http
      .put<Student>(
        `${environment.base_courses_url}/${courseId}/removeStudents`,
        studentsString,
        environment.base_http_headers
      )
      .pipe(
        tap(() => {
          this.toastrService.success(
            `Successfully unenrolled one ore more students from ${courseId}`,
            'Congratulations 😃'
          );
        }),
        catchError(
          handleError<Student>(this.toastrService, `unenrollStudents failed`)
        )
      );
  }

  /**
   * Remove the professor as manager of the course
   * @param professor The professor
   * @param course The course
   */
  removeProfessorFromCourse(
    professor: Professor,
    course: Course
  ): Observable<Professor> {
    return this.http
      .put<Professor>(
        `${environment.base_courses_url}/${course.acronym}/removeProfessor`,
        { professorId: professor.id },
        environment.base_http_headers
      )
      .pipe(
        tap((p) => {
          this.toastrService.success(
            `Removed ${Professor.displayFn(p)} from ${course.acronym}`,
            'Congratulations 😃'
          );
        }),
        catchError(
          handleError<Professor>(
            this.toastrService,
            `removeProfessor(${Professor.displayFn(professor)}, ${
              course.acronym
            })`
          )
        )
      );
  }

  /**
   *
   * @param formData The course information
   */
  createCourse(formData: FormData): Observable<Course> {
    return this.http.post<Course>(environment.base_courses_url, formData).pipe(
      tap(() =>
        this.toastrService.success(
          `Course ${formData.get('name')} successfully created!`,
          'Awesome 😃'
        )
      ),
      catchError(
        handleError<Course>(
          this.toastrService,
          `createCourse(${formData.get('name')})`
        )
      )
    );
  }

  /**
   *
   * @param formData The course update information
   */
  updateCourse(formData: FormData): Observable<Course> {
    return this.http
      .put<Course>(
        `${environment.base_courses_url}/${this.course.value.acronym}`,
        formData
      )
      .pipe(
        tap((course) => {
          this.toastrService.success(
            `Course ${this.course.value.name} successfully updated!`,
            'Awesome 😃'
          );
          this.course.next(course);
        }),
        catchError(
          handleError<Course>(
            this.toastrService,
            `updateCourse(${this.course.value.name})`
          )
        )
      );
  }

  /**
   *
   * @param courseAcronym The course acronym
   */
  deleteCourse(courseAcronym: string = this.course.value.acronym) {
    return this.http
      .delete<Course>(`${environment.base_courses_url}/${courseAcronym}`)
      .pipe(
        tap(() => console.log(`Course ${courseAcronym} deleted`)),
        catchError(handleError<Course>(this.toastrService))
      );
  }

  /**
   *
   * @param formData The assignment details
   */
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

  /**
   *
   * @param courseAcronym The course acronym
   */
  getStudentCourseAssignments(courseAcronym: string): Observable<StudentAssignmentDetails[]> {
    return this.http
        .get<StudentAssignmentDetails[]>(
            `${environment.base_courses_url}/${courseAcronym}/studentAssignments`
        )
        .pipe(
            tap(() =>
                console.log(
                    `fetched assignments in course ${courseAcronym} - getCourseAssignments()`
                )
            ),
            catchError(
                handleError<StudentAssignmentDetails[]>(
                    this.toastrService,
                    `getCourseAssignments(${courseAcronym})`
                )
            )
        );
  }
}
