import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {ToastrService} from 'ngx-toastr';
import {Observable} from 'rxjs';
import {environment} from '../../environments/environment';
import {catchError, tap} from 'rxjs/operators';
import {handleError} from '../helpers/handle.error';
import {AssignmentSolution} from '../models/assignment-solution.model';
import {AssignmentSolutionDetails} from '../models/assignment-solution-details.model';
import {Upload} from '../models/upload.model';

@Injectable({
  providedIn: 'root',
})
export class AssignmentAndUploadService {

  constructor(private http: HttpClient,
              private toastrService: ToastrService) {
  }

  getDocument(assignmentId: number): Observable<any> {
    return this.http.get(`${environment.base_assignments_url}/${assignmentId}/document`, {
      responseType: 'blob',
    }).pipe(
      tap(() => console.log(`returned document for assignment ${assignmentId} - getDocument()`)),
      catchError(handleError<any>(this.toastrService, `getDocument(${assignmentId})`))
    );
  }

  getSolutionsForAssignment(assignmentId: number): Observable<AssignmentSolutionDetails[]> {
    return this.http.get<AssignmentSolutionDetails[]>(`${environment.base_assignments_url}/${assignmentId}/solutions`)
        .pipe(
          tap(() => console.log(`fetched assignment solutions for assignment ${assignmentId} - getSolutionsForAssignment()`)),
          catchError(handleError<any>(this.toastrService, `getSolutionsForAssingment(${assignmentId})`)));
  }

  public getAssignmentSolutionUploads(assignmentSolutionId: number): Observable<Upload[]> {
    return this.http.get<Upload[]>(`${environment.base_assignmentSolutions_url}/${assignmentSolutionId}/uploads`)
        .pipe(
            tap(() => console.log(`fetched assignment solution ${assignmentSolutionId} uploads - getAssignmentUploads()`)),
            catchError(handleError<Upload[]>(this.toastrService, `getAssignmentUploads(${assignmentSolutionId})`)));
  }

  readStudentAssignment(assignmentId: number): Observable<any> {
    return this.http.get(`${environment.base_assignments_url}/${assignmentId}/studentDocument`, {
      responseType: 'blob',
    }).pipe(
        tap(() => console.log(`returned document for assignment ${assignmentId} - getDocument()`)),
        catchError(handleError<any>(this.toastrService, `getDocument(${assignmentId})`))
    );
  }

  evaluateAssignment(assignmentSolutionId: number, grade: string): Observable<AssignmentSolution> {
    return this.http.post<AssignmentSolution>(`${environment.base_assignmentSolutions_url}/${assignmentSolutionId}/grade`,
        {grade},
        environment.base_http_headers
    ).pipe(
        tap(() => console.log(`evaluated assignment ${assignmentSolutionId} - evaluateAssignment()`)),
        catchError(handleError<any>(this.toastrService, `evaluateAssignment(${assignmentSolutionId})`))
    );
  }

  getUploadDocument(uploadId: number): Observable<any> {
    return this.http.get(`${environment.base_uploads_url}/${uploadId}/document`, {
      responseType: 'blob',
    }).pipe(
        tap(() => console.log(`returned document for upload ${uploadId} - getUploadDocument()`)),
        catchError(handleError<any>(this.toastrService, `getUploadDocument(${uploadId})`))
    );
  }
}
