import { Injectable } from '@angular/core';

import { Observable, of, BehaviorSubject } from 'rxjs';
import { HttpClient } from '@angular/common/http';
import { ToastrService } from 'ngx-toastr';
import { Team } from '../models/team.model';
import { VM } from '../models/vm.model';
import { tap, catchError } from 'rxjs/operators';
import { environment } from 'src/environments/environment';

/** Team service
 * 
 *  This service is responsible of all the interaction with teams resources through Rest api.
 */
@Injectable({
  providedIn: 'root'
})
export class TeamService {
  //Current Course Subject: keeps hold of the current value and emits it to any new subscribers as soon as they subscribe
  public currentTeamSubject: BehaviorSubject<Team>;

  constructor(private http: HttpClient,
    private toastrService: ToastrService) {
      this.currentTeamSubject = new BehaviorSubject<Team>(null);
    }

  public getTeamVms(team: Team) : Observable<VM[]>{
    return this.http.get<VM[]>(`${environment.base_teams_url}/?teamId_like=${team.id}`)
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