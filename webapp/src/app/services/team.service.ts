import { Injectable } from '@angular/core';

import { BehaviorSubject, Observable } from 'rxjs';
import { HttpClient } from '@angular/common/http';
import { ToastrService } from 'ngx-toastr';
import { Team } from '../models/team.model';
import { catchError, tap } from 'rxjs/operators';
import { environment } from 'src/environments/environment';
import { TeamProposal } from '../models/team-proposal.model';
import { CourseService } from './course.service';
import { AuthService } from './auth.service';
import { handleError } from '../helpers/handle.error';

/** Team service
 *
 *  This service is responsible of all the interaction with teams resources through Rest api.
 */
@Injectable({
  providedIn: 'root',
})
export class TeamService {
  public currentTeamSubject: BehaviorSubject<Team>;

  constructor(
      private http: HttpClient,
      private toastrService: ToastrService,
      private courseService: CourseService,
      private authService: AuthService
  ) {
    this.currentTeamSubject = new BehaviorSubject<Team>(null);
  }

  /**
   *
   * @param proposal The details of the proposal
   * @param courseId The course acronym
   */
  public createTeam(
      proposal: TeamProposal,
      courseId: string = this.courseService.currentCourseSubject.value
  ): Observable<Team> {
    return this.http
        .post<Team>(
            `${environment.base_courses_url}/${courseId}/teams`,
            proposal,
            environment.base_http_headers
        )
        .pipe(
            tap(() =>
                console.log(`created team ${proposal.teamName} - createTeam()`)
            ),
            catchError(
                handleError<Team>(
                    this.toastrService,
                    `createTeam(${proposal.teamName}`
                )
            )
        );
  }

  /**
   *
   * @param teamId The team id
   * @param formData The details of the resources update
   */
  public updateTeamVmResources(teamId: number, formData: FormData): Observable<Team> {
    return this.http
        .put<Team>(
            `${environment.base_teams_url}/${teamId}/updateVmsResourceLimits`,
            formData
        ).pipe(
            tap((team: Team) => {
              this.toastrService.success(
                  `Team ${team.name} successfully updated!`,
                  'Awesome 😃'
              );
              this.currentTeamSubject.next(team);
            }),
            catchError(
                handleError<Team>(
                    this.toastrService,
                    `updateTeam(${teamId})`
                )
            )
        );
  }

  /**
   *
   * @param courseId The course id
   * @param studentId The student id
   */
  public getStudentTeam(
      courseId: string = this.courseService.currentCourseSubject.value,
      studentId: string = this.authService.currentUserValue.id
  ): Observable<Team> {
    return this.http
        .get<Team>(
            `${environment.base_students_url}/${studentId}/teams/${courseId}`
        )
        .pipe(
            tap(() =>
                console.log(
                    `retrieved team of ${studentId} for course ${courseId} - getStudentTeam()`
                )
            ),
            catchError(
                handleError<Team>(
                    this.toastrService,
                    `getStudentTeam(${courseId}, ${studentId})`,
                    null,
                    false
                )
            )
        );
  }

  /**
   *
   * @param token The token id
   */
  public acceptProposal(token: string): Observable<boolean> {
    return this.http
      .get<boolean>(
        `${environment.base_teams_url}/confirmInvitation/${token}`,
        environment.base_http_headers
      )
      .pipe(
        tap(() => console.log(`accepted proposal ${token} - acceptProposal()`)),
        catchError(
          handleError<boolean>(this.toastrService, `acceptProposal(${token})`)
        )
      );
  }

  /**
   *
   * @param token The token id
   */
  public rejectProposal(token: string): Observable<boolean> {
    return this.http
      .get<boolean>(
        `${environment.base_teams_url}/rejectInvitation/${token}`,
        environment.base_http_headers
      )
      .pipe(
        tap(() => console.log(`rejected proposal ${token} - rejectProposal()`)),
        catchError(
          handleError<boolean>(this.toastrService, `rejectProposal(${token})`)
        )
      );
  }

  /**
   *
   * @param token The token id
   */
  public deleteProposal(token: string): Observable<boolean> {
    return this.http
      .get<boolean>(
        `${environment.base_teams_url}/deleteProposal/${token}`,
        environment.base_http_headers
      )
      .pipe(
        tap(() => console.log(`deleted proposal ${token} - deleteProposal()`)),
        catchError(
          handleError<boolean>(this.toastrService, `deleteProposal(${token})`)
        )
      );
  }
}
