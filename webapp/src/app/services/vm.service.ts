import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { HttpClient } from '@angular/common/http';
import { ToastrService } from 'ngx-toastr';
import { catchError, map, tap } from 'rxjs/operators';
import { environment } from 'src/environments/environment';
import { VmModel } from '../models/vm-model.model';
import { Course } from '../models/course.model';
import { VM } from '../models/vm.model';
import { TeamService } from './team.service';
import {handleError} from '../helpers/handle.error';

/** Vm service
 *
 *  This service is responsible of all the interaction with vms resources through Rest api.
 */
@Injectable({
  providedIn: 'root',
})
export class VmService {
  constructor(private http: HttpClient, private toastrService: ToastrService, private teamService: TeamService) { }
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
            handleError<VmModel[]>(`searchModels(${name})`, [], true)
          )
        );
    }
  */
  public getModelForCourse(course: Course): Observable<VmModel> {
    return this.http.get<VmModel[]>(`${environment.production}/vmModel`).pipe(
      map((x) => x.shift()),
      tap((x) =>
        console.log(
          `found models for course - getModelsForCourse(${course.acronym})`
        )
      ),
      catchError(
        handleError<VmModel>(this.toastrService,
          `getModelsForCourse(${course.acronym})`,
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
    model.courseId = course.acronym;
    return this.http
      .put<VmModel>(
        `${environment.base_courses_url}`,
        model,
        environment.base_http_headers
      )
      .pipe(
        tap((x) =>
          console.log(
            `updated course model - assignVmModelToCourse(${course.acronym})`
          )
        ),
        catchError(
          handleError<VmModel>(this.toastrService,
            `assignVmModelToCourse(${course.acronym})`,
            null,
            false
          )
        )
      );
  }

  public createVmForTeam(vmDTO: VM, teamId: number = this.teamService.currentTeamSubject.value.id): Observable<VM> {
    return this.http
      .post<VM>(`${environment.base_teams_url}/${teamId}/vms`, vmDTO, environment.base_http_headers)
      .pipe(
        tap((x) =>
          this.toastrService.success(
            `Created VM ${x.id} for team ${teamId}`,
            'Congratulations 😃'
          )
        ),
        catchError(
          handleError<VM>(this.toastrService, `createVmForTeam(${teamId})`, null, false)
        )
      );
  }

  public getTeamVms(teamId: number = this.teamService.currentTeamSubject.value.id): Observable<VM[]> {
    return this.http
      .get<VM[]>(`${environment.base_teams_url}/${teamId}/vms`)
      .pipe(
        tap(() => console.log(`fetched team ${teamId} vms - getTeamVms()`)),
        catchError(handleError<VM[]>(this.toastrService, `getTeamVms(${teamId}`))
      );
  }

  public triggerVm(vmId: number, vmActive: boolean): Observable<VM> {
    // If the vm is active, request the turn off, request the turn on otherwise
    const active = vmActive ? 'false' : 'true';
    return this.http.put<VM>(`${environment.base_vms_url}/${vmId}/trigger`, { active }, environment.base_http_headers).pipe(
      tap(() => console.log(`triggered vm ${vmId} - triggerVm()`)),
      catchError(handleError<VM>(this.toastrService, `triggerVm(${vmId})`))
    );
  }

  public addOwner(vmId: number, studentId: string): Observable<VM> {
    return this.http.post<VM>(`${environment.base_vms_url}/${vmId}/addOwner`, { studentId }, environment.base_http_headers)
      .pipe(
        tap(() => console.log(`added owner ${studentId} to vm ${vmId} - addOwner()`)),
        catchError(handleError<VM>(this.toastrService, `addOwner(${vmId}, ${studentId})`))
      );
  }

  public getInstance(vmId: number): Observable<any> {
    return this.http.get(`${environment.base_vms_url}/${vmId}/instance`, {
                        responseType: 'blob'
                    })
      .pipe(
        tap(() => console.log(`returned instance for vm ${vmId} - getInstance()`)),
        catchError(handleError<any>(this.toastrService, `getInstance(${vmId})`))
      );
  }

  public removeVm(vmId: number) {
    return this.http.delete(`${environment.base_vms_url}/${vmId}`)
      .pipe(
        tap(() => console.log(`removed vm ${vmId} - removeVm()`)),
        catchError(handleError(this.toastrService, `removeVm(${vmId})`))
      );
  }
}
