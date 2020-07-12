import { Injectable } from '@angular/core';
import { HttpRequest, HttpHandler, HttpEvent, HttpInterceptor } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError } from 'rxjs/operators';

import { AuthService } from '../services/auth.service';
import * as moment from 'moment';
import { Router } from '@angular/router';
import { ToastrService } from 'ngx-toastr';
import { User } from '../models/user.model';

/** ErrorInterceptor service
 * 
 *  This service is responsible of checking if a request has been performed without authorization
 *  (like when a token expires or a much privileged resource is requested)
 */
@Injectable()
export class ErrorInterceptor implements HttpInterceptor {
    constructor(private authService: AuthService,
        private router: Router,
        private toastrService: ToastrService) {}

    /** Method to intercept the error response, check if 401 and throw an error */
    intercept(request: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
        return next.handle(request).pipe(catchError(err => {
            const currentUser = this.authService.currentUserValue;
            if (err.status === 401 && currentUser) {
                // auto logout if 401 response returned from api
                if(moment().isBefore(User.getTokenExpireTime(currentUser.accessToken))) this.toastrService.info(`Your authentication token expired. Login again please`, 'Sorry 😰');
                else this.toastrService.info(`You requested a resource you don't have access to`, 'Sorry 😰');
                this.authService.logout(false);
                this.router.navigate(['/'], { queryParams: { returnUrl: location.pathname, 'doLogin': true } });
            }    
            const error = err.error.message || err.statusText;    
            return throwError(error);
        }))
    }
}