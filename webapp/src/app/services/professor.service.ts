import { Injectable } from '@angular/core';

import { Observable, of } from 'rxjs';
import { HttpClient } from '@angular/common/http';
import { ToastrService } from 'ngx-toastr';
import { catchError, tap } from 'rxjs/operators';
import { Course } from '../models/course.model';
import { Professor } from '../models/professor.model';
import { environment } from 'src/environments/environment';
import { handleError } from '../helpers/handle.error';
import { Upload } from '../models/upload.model';

/** Team service
 *
 *  This service is responsible of all the interaction with teams resources through Rest api.
 */
@Injectable({
  providedIn: 'root',
})
export class ProfessorService {
  constructor(private http: HttpClient, private toastrService: ToastrService) {}

  public getProfessorCourses(professorId: string): Observable<Course[]> {
    return this.http
      .get<Course[]>(
        `${environment.base_professors_url}/${professorId}/courses`
      )
      .pipe(
        tap(() =>
          console.log(
            `fetched professor ${professorId} courses - getProfessorCourses()`
          )
        ),
        catchError(
          handleError<Course[]>(
            this.toastrService,
            `getProfessorCourses(${professorId})`
          )
        )
      );
  }

  public updateProfessor(professor: Professor): Observable<Professor> {
    return this.http
      .put<Professor>(
        `${environment.base_professors_url}/${professor.id}`,
        professor,
        environment.base_http_headers
      )
      .pipe(
        tap(() =>
          console.log(
            `updated professor ${professor.email} - updateProfessor()`
          )
        ),
        catchError(
          handleError<Professor>(
            this.toastrService,
            `updateProfessor(${professor.email})`
          )
        )
      );
  }

  /**
   * Function to retrieve all professors whose name matches a specific string
   *
   * @param(name) the string which should be contained in the student name
   */
  searchProfessors(name: string): Observable<Professor[]> {
    // Checking if it is actually a string and does not have whitespaces in the middle (if it has them at beginning or end, trim)
    if (typeof name !== 'string') {
      return of([]);
    } else {
      name = name.trim();
      if (!name || name.indexOf(' ') >= 0) {
        return of([]);
      }
    }
    return this.http
      .get<Professor[]>(
        `${environment.base_professors_url}?surname_like=${name}`
      )
      .pipe(
        // If I don't know a priori which data the server sends me --> map(res => res.map(r => Object.assign(new Student(), r))),
        tap((x) =>
          console.log(
            `found ${x.length} results matching ${name} - searchProfessors()`
          )
        ),
        catchError(
          handleError<Professor[]>(
            this.toastrService,
            `searchProfessors(${name})`,
            [],
            false
          )
        )
      );
  }

  professorAssignmentUpload(
      uploadDetails: FormData,
      assignmentSolutionId: number
  ): Observable<Upload> {
    return this.http
        .post<Upload>(
            `${environment.base_assignmentSolutions_url}/${assignmentSolutionId}/professorUpload`,
            uploadDetails
        )
        .pipe(
            tap((x) =>
                this.toastrService.success(
                    `Uploaded a new assignment solution`,
                    'Congratulations 😃'
                )
            ),
            catchError(
                handleError<Upload>(
                    this.toastrService,
                    `uploadAssignmentSolution(${assignmentSolutionId})`
                )
            )
        );
  }
}
