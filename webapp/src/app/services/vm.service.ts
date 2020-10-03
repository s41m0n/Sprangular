import { Injectable } from '@angular/core';

import { Observable, of } from 'rxjs';
import { HttpClient } from '@angular/common/http';
import { ToastrService } from 'ngx-toastr';
import { catchError, map, tap } from 'rxjs/operators';
import { environment } from 'src/environments/environment';
import { VmModel } from '../models/vm-model.model';
import { Course } from '../models/course.model';
import { VM } from '../models/vm.model';
import { Team } from '../models/team.model';

/** Vm service
 *
 *  This service is responsible of all the interaction with vms resources through Rest api.
 */
@Injectable({
  providedIn: 'root',
})
export class VmService {
  constructor(private http: HttpClient, private toastrService: ToastrService) {}
/*
  public searchModels(name: string): Observable<VmModel[]> {
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
      .get<VmModel[]>(`${environment.base_vm_models_url}?name_like=${name}`)
      .pipe(
        // If I don't know a priori which data the server sends me --> map(res => res.map(r => Object.assign(new Student(), r))),
        tap((x) =>
          console.log(
            `found ${x.length} results matching ${name} - searchModels()`
          )
        ),
        catchError(
          this.handleError<VmModel[]>(`searchModels(${name})`, [], true)
        )
      );
  }
*/
  public getModelForCourse(course: Course): Observable<VmModel> {
    return this.http.get<VmModel[]>(`${environment.production}/vmModel`).pipe(
      map((x) => x.shift()),
      tap((x) =>
        console.log(
          `found models for course - getModelsForCourse(${course.id})`
        )
      ),
      catchError(
        this.handleError<VmModel>(
          `getModelsForCourse(${course.id})`,
          null,
          false
        )
      )
    );
  }

  public assignVmModelToCourse(
    model: VmModel,
    course: Course
  ): Observable<VmModel> {
    model.courseId = course.id;
    return this.http
      .put<VmModel>(
        `${environment.base_courses_url}`,
        model,
        environment.base_http_headers
      )
      .pipe(
        tap((x) =>
          console.log(
            `updated course model - assignVmModelToCourse(${course.id})`
          )
        ),
        catchError(
          this.handleError<VmModel>(
            `assignVmModelToCourse(${course.id})`,
            null,
            false
          )
        )
      );
  }

  public createVmForTeam(vm: VM, team: Team): Observable<VM> {
    vm.teamId = team.id;
    return this.http
      .post<VM>(`${environment.base_teams_url}/${team.id}/vms`, vm, environment.base_http_headers)
      .pipe(
        tap((x) =>
          this.toastrService.success(
            `Created VM ${x.name} for team ${team.name}`,
            'Congratulations 😃'
          )
        ),
        catchError(
          this.handleError<VM>(`createVmForTeam(${team.id})`, null, false)
        )
      );
  }

  public getTeamVms(team: Team): Observable<VM[]> {
    return this.http
      .get<VM[]>(`${environment.base_teams_url}/${team.id}/vms`)
      .pipe(
        tap(() => console.log(`fetched team ${team.id} vms - getTeamVms()`)),
        catchError(this.handleError<VM[]>(`getTeamVms(${team.id}`))
      );
  }

  /**
   * Handle Http operation that failed.
   * Let the app continue.
   * @param operation - name of the operation that failed
   * @param result - optional value to return as the observable result
   * @param show - is it visible
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
