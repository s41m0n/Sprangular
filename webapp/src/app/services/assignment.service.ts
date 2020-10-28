import {Injectable} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {ToastrService} from 'ngx-toastr';
import {Observable} from 'rxjs';
import {environment} from '../../environments/environment';
import {catchError, tap} from 'rxjs/operators';
import {handleError} from '../helpers/handle.error';

@Injectable({
  providedIn: 'root',
})
export class AssignmentService {

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
}
