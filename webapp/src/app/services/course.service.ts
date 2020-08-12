import { Injectable } from '@angular/core';

import { Observable, of } from 'rxjs';
import { HttpClient } from '@angular/common/http';
import { tap, catchError, map } from 'rxjs/operators';
import { ToastrService } from 'ngx-toastr';
import { Course } from '../models/course.model';
import { Assignment } from '../models/assignment.model';
import { Student } from '../models/student.model';
import { VM } from '../models/vm.model';

/**
 * CourseService service
 * 
 * This service is responsible of handling all the interactions with courses through rest api.
 * 
 */
@Injectable({
  providedIn: 'root'
})
export class CourseService{
  baseURL = 'api/courses';

  constructor(private http: HttpClient,
    private toastrService: ToastrService) {}
  
  /**
   * Function to retrieve a Course resource given a path
   * 
   * @param(path) the requested path 
   */
  getCourseByPath(path: string) : Observable<Course> {
    return this.http.get<Course[]>(`${this.baseURL}?path_like=${path}&_limit=1`)
      .pipe(
        //If I don't know a priori which data the server sends me --> map(res => Object.assign(new Course(), res)),
        //Take the first one (json-server does not support direct search, but we have to use _like query)
        map(x => x.shift()),
        tap(() => console.log(`fetched course by path ${path} - getCourses()`)),
        catchError(this.handleError<Course>(`getCourseByPath(${path})`))
      )
  }

  /**
   * Function to retrieve all students enroll to a Course
   * 
   * @param(course) the objective course  
   */
  getEnrolledStudents(course : Course) : Observable<Student[]>{
    return this.http.get<Student[]>(`${this.baseURL}/${course.id}/students?_expand=team`)
      .pipe(
        //If I don't know a priori which data the server sends me --> map(res => res.map(r => Object.assign(new Student(), r))),
        tap(() => console.log(`fetched enrolled ${course.name} students - getEnrolledStudents()`)),
        catchError(this.handleError<Student[]>(`getEnrolledStudents(${course.name})`))
      );
  }
  
  /**
   * Function to retrieve the list of Courses available
   */
  getCourses() : Observable<Course[]>{
    return this.http.get<Course[]>(this.baseURL)
      .pipe(
        //If I don't know a priori which data the server sends me --> map(res => res.map(r => Object.assign(new Course(), r))),
        tap(() => console.log(`fetched courses - getCourses()`)),
        catchError(this.handleError<Course[]>(`getCourses()`))
      )
  }

  getCourseAssignments(course : Course) : Observable<Assignment[]>{
    return this.http.get<Assignment[]>(`${this.baseURL}/${course.id}/assignments?_expand=professor`)
      .pipe(
        tap(() => console.log(`fetched course ${course.name} assignments - getCourseAssignments()`)),
        catchError(this.handleError<Assignment[]>(`getCourseAssignments(${course.name})`))
      );
  }

  getCourseVMs(course : Course) : Observable<VM[]>{
    return this.http.get<Assignment[]>(`${this.baseURL}/${course.id}/vms?_expand=team`)
      .pipe(
        tap(() => console.log(`fetched course ${course.name} vms - getCourseVMs()`)),
        catchError(this.handleError<VM[]>(`getCourseVMs(${course.name})`))
      );
  }

  /**
   * Handle Http operation that failed.
   * Let the app continue.
   * @param operation - name of the operation that failed
   * @param result - optional value to return as the observable result
   */
  private handleError<T>(operation = 'operation', result?: T, show: boolean = true, message: string = 'An error occurred while performing') {
    return (error: any): Observable<T> => {
      const why = `${message} ${operation}: ${error}`;
      
      if(show) this.toastrService.error(why, 'Error 😅');
      console.log(why);

      // Let the app keep running by returning an empty result.
      return of(result as T);
    };
  }
}
