import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { catchError, map, tap } from 'rxjs/operators';
import { BehaviorSubject, Observable, throwError } from 'rxjs';
import { User } from '../models/user.model';
import { ToastrService } from 'ngx-toastr';
import * as moment from 'moment';
import { environment } from 'src/environments/environment';

/**
 * AuthService service
 *
 * This service is responsible of:
 *    - all the interaction with the IdentityProvider, to authenticate a user
 *    - keeping track of the currentLogged user and its token
 */
@Injectable({
  providedIn: 'root',
})
export class AuthService {
  // Current User Subject: keeps hold of the current value and emits it to any new subscribers as soon as they subscribe
  private currentUserSubject: BehaviorSubject<User>;

  constructor(private http: HttpClient, private toastrService: ToastrService) {
    // Check if logged user via localStorage and set it;
    let user = JSON.parse(localStorage.getItem('currentUser'));
    // Check if accessToken expired
    if (user && moment().isBefore(User.getTokenExpireTime(user.accessToken))) {
      localStorage.removeItem('currentUser');
      this.toastrService.info(
        `Your authentication token expired. Login again please`,
        'Sorry 😰'
      );
      user = null;
    }
    this.currentUserSubject = new BehaviorSubject<User>(user);
  }

  /**
   * Function to return the current user behavioural value (called when a component cannot subscribe but needs immediately the value)
   * @returns the current user
   */
  public get currentUserValue(): User {
    return this.currentUserSubject.value;
  }

  /**
   * Function to get current user subject as observable
   */
  public getUserObservable(): Observable<User> {
    return this.currentUserSubject.asObservable();
  }

  /**
   * Function to authenticate a user via the IdentityProvider
   *
   * @param(email)    the login email
   * @param(password) the login password
   */
  login(id: string, password: string) {
    return this.http
      .post<any>(
        environment.login_url,
        { id, password },
        environment.base_http_headers
      )
      .pipe(
        map((authResult) => {
          // store user details and basic auth credentials in local storage to keep user logged in between page refreshes
          // Assigning the Admin Role since json-server-auth is not able to assign it directly in the jwt token
          const user = new User(
            id,
            authResult.id_token,
            this.parseJwt(authResult.id_token).roles
          );
          localStorage.setItem('currentUser', JSON.stringify(user));
          this.currentUserSubject.next(user);
          return authResult;
        }),
        tap(() => {
          this.toastrService.success(`Hi ${id}`, 'Awesome 😃');
          console.log(`logged ${id}`);
        }),
        catchError((err) => {
          this.toastrService.error(`Error: ${err}`, 'Authentication failed');
          return throwError(err);
        })
      );
  }

  register(
    email: string,
    name: string,
    surname: string,
    id: string,
    password: string,
    pic: object
  ) {
    return this.http
      .post(
        environment.register_url_student,
        {
          email,
          password,
          name,
          surname,
        },
        environment.base_http_headers
      )
      .pipe(
        tap((_) =>
          this.toastrService.success(
            `Successfully registered! Check your email`,
            'Awesome 😃'
          )
        ),
        catchError((err) => {
          this.toastrService.error('Registration failed', 'Error 😅');
          return throwError(err);
        })
      );
  }

  /**
   * Function to perform logout
   *
   * Remove user from local storage and emits a new event with a null user.
   */
  logout(showMsg: boolean = true) {
    localStorage.removeItem('currentUser');
    this.currentUserSubject.next(null);
    if (showMsg) {
      this.toastrService.success(`Logout with success`, 'Awesome 😃');
    }
    console.log(`logged out`);
  }

  parseJwt(token: string) {
    const base64Url = token.split('.')[1];
    const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');
    const jsonPayload = decodeURIComponent(
      atob(base64)
        .split('')
        .map((c) => {
          return '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2);
        })
        .join('')
    );
    return JSON.parse(jsonPayload);
  }
}
