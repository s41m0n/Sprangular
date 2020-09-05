import { Injectable } from '@angular/core';

import { Observable, of } from 'rxjs';
import { HttpClient } from '@angular/common/http';
import { ToastrService } from 'ngx-toastr';
import { tap, catchError, map } from 'rxjs/operators';
import { Course } from '../models/course.model';
import { Professor } from '../models/professor.model';
import { environment } from 'src/environments/environment';

/** Team service
 * 
 *  This service is responsible of all the interaction with teams resources through Rest api.
 */
@Injectable({
  providedIn: 'root'
})
export class ProfessorService {
  constructor(private http: HttpClient,
    private toastrService: ToastrService) {}

  public getProfessorCourses(email: string) : Observable<Course[]>{
    return this.http.get<Professor[]>(`${environment.base_professors_url}?email_like=${email}&_expand=course`)
      .pipe(
        map(professors => [professors.shift().course]),
        tap(() => console.log(`fetched professor ${email} courses - getProfessorCourses()`)),
        catchError(this.handleError<Course[]>(`getProfessorCourses(${email})`))
      );
  }

  public updateProfessor(professor: Professor): Observable<Professor> {
    return this.http.put<Professor>(`${environment.base_professors_url}/${professor.id}`, professor, environment.base_http_headers)
      .pipe(
        tap(() => console.log(`updated professor ${professor.email} - updateProfessor()`)),
        catchError(this.handleError<Professor>(`updateProfessor(${professor.email})`))
      );
  }


  /**
   * Function to retrieve all professors whose name matches a specific string
   * 
   * @param(name) the string which should be contained in the student name
   */
  searchProfessors(name: string): Observable<Professor[]> {
    //Checking if it is actually a string and does not have whitespaces in the middle (if it has them at beginning or end, trim)
    if (typeof name !== 'string' || !(name = name.trim()) || name.indexOf(' ') >= 0) {
      return of([]);
    }
    return this.http.get<Professor[]>(`${environment.base_professors_url}?surname_like=${name}`).pipe(
      //If I don't know a priori which data the server sends me --> map(res => res.map(r => Object.assign(new Student(), r))),
      tap(x => console.log(`found ${x.length} results matching ${name} - searchProfessors()`)),
      catchError(this.handleError<Professor[]>(`searchProfessors(${name})`, [], false))
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