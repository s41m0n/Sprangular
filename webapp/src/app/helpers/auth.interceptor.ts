import { Injectable } from '@angular/core';
import { HttpRequest, HttpHandler, HttpEvent, HttpInterceptor } from '@angular/common/http';
import { Observable } from 'rxjs';
import { AuthService } from '../services/auth.service';

/** BasicAuthInterceptor service
 * 
 *  This service is responsible of injecting the Authorization header to all the requested
 *  if the user is authenticated.
 */
@Injectable()
export class AuthInterceptor implements HttpInterceptor {
    constructor(private _authService: AuthService) { }

    /** Method to intercept and injecting the new header to the request */
    intercept(request: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
        // add authorization header with basic auth credentials if available
        const currentUser = this._authService.currentUserValue;
        if (currentUser) {
            request = request.clone({
                setHeaders: { 
                    Authorization: `Bearer ${currentUser.accessToken}`
                }
            });
        }

        return next.handle(request);
    }
}