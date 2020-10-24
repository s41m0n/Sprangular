import { Injectable } from '@angular/core';

import { BehaviorSubject, Observable, of } from 'rxjs';
import { HttpClient } from '@angular/common/http';
import { ToastrService } from 'ngx-toastr';
import { Team } from '../models/team.model';
import { catchError, tap } from 'rxjs/operators';
import { environment } from 'src/environments/environment';
import { TeamProposal } from '../models/team-proposal.model';
import { CourseService } from './course.service';
import { AuthService } from './auth.service';
import { Student } from '../models/student.model';
import {handleError} from '../helpers/handle.error';

/** Team service
 *
 *  This service is responsible of all the interaction with teams resources through Rest api.
 */
@Injectable({
  providedIn: 'root',
})
export class TeamService {
  public currentTeamSubject: BehaviorSubject<Team>;

  constructor(private http: HttpClient,
              private toastrService: ToastrService,
              private courseService: CourseService,
              private authService: AuthService) {
    this.currentTeamSubject = new BehaviorSubject<Team>(null);
  }

  public createTeam(proposal: TeamProposal, courseId: string = this.courseService.currentCourseSubject.value): Observable<Team> {
    return this.http
      .post<Team>(
        `${environment.base_courses_url}/${courseId}/teams`,
        proposal,
        environment.base_http_headers
      )
      .pipe(
        tap(() => console.log(`created team ${proposal.teamName} - createTeam()`)),
        catchError(handleError<Team>(this.toastrService, `createTeam(${proposal.teamName}`))
      );
  }

  public getStudentTeam(courseId: string = this.courseService.currentCourseSubject.value, studentId: string = this.authService.currentUserValue.id): Observable<Team>{
    return this.http.get<Team>(`${environment.base_students_url}/${studentId}/teams/${courseId}`)
      .pipe(
        tap(() => console.log(`retrieved team of ${studentId} for course ${courseId} - getStudentTeam()`)),
        catchError(handleError<Team>(this.toastrService, `getStudentTeam(${courseId}, ${studentId})`, null, false))
      );
  }

  public getStudentsInTeam(teamId: number = this.currentTeamSubject.value.id) {
    return this.http.get<Student[]>(`${environment.base_teams_url}/${teamId}/members`)
      .pipe(
        tap(() => console.log(`retrieved members of team ${teamId} - getStudentsInTeam()`)),
        catchError(handleError<Student[]>(this.toastrService, `getStudentsInTeam(${teamId})`))
      );
  }
}
