import { Injectable } from '@angular/core';
import { Router, CanActivate, ActivatedRouteSnapshot, RouterStateSnapshot } from '@angular/router';
import { AuthService } from '../services/auth.service';
import * as moment from 'moment';
import { ToastrService } from 'ngx-toastr';
import { User } from '../models/user.model';

/** Authentication Guard service
 * 
 *  This service is responsible of checking if the user is authenticated and is allowed
 *  to perform the required operation depending on its privileges.
 */
@Injectable({ providedIn: 'root' })
export class AuthGuard implements CanActivate {
    constructor(
        private router: Router,
        private authService: AuthService,
        private toastrService: ToastrService
    ) {}

    /** Method called to perform the checks */
    canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot) {
        const currentUser = this.authService.currentUserValue;
        // Check if the user is logged
        if(!currentUser) {
            // not logged in so redirect to login page with the return url
            console.log('This route requires login!');
            this.router.navigate(['/'], { queryParams: { returnUrl: state.url, 'doLogin': true } });
            return false;
        }
        //Check if token expired
        if(moment().isBefore(User.getTokenExpireTime(currentUser.accessToken))) {
            console.log('Token expired, cannot access route!');
            this.toastrService.info(`Your authentication token expired. Login again please`, 'Sorry 😰');
            this.authService.logout(false);
            this.router.navigate(['/'], { queryParams: { returnUrl: state.url, 'doLogin': true } });
            return false;
        }
        // check if route is restricted by role
        if (route.data.roles && !route.data.roles.includes(currentUser.role)) {
            // role not authorised so redirect to home page
            console.log(`Unprivileged user (${currentUser.email} is a ${currentUser.role}) for the route ${state.url}!`);
            this.router.navigate(['/']);
            return false;
        }
 
        // authorized so return true
        return true;
    }
}