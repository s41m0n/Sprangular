import { Injectable } from '@angular/core';

import { Observable, of } from 'rxjs';
import { HttpClient } from '@angular/common/http';
import { ToastrService } from 'ngx-toastr';
import { tap, catchError, map } from 'rxjs/operators';
import { Course } from '../models/course.model';
import { Professor } from '../models/professor.model';

/** Team service
 * 
 *  This service is responsible of all the interaction with teams resources through Rest api.
 */
@Injectable({
  providedIn: 'root'
})
export class ProfessorService {
  baseURL : string = 'api/professors';

  constructor(private http: HttpClient,
    private toastrService: ToastrService) {}

  public getProfessorCourses(email: string) : Observable<Course[]>{
    return this.http.get<Professor[]>(`${this.baseURL}?email_like=${email}&_expand=course`)
      .pipe(
        map(professors => [professors.shift().course]),
        tap(() => console.log(`fetched professor ${email} courses - getProfessorCourses()`)),
        catchError(this.handleError<Course[]>(`getProfessorCourses(${email})`))
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