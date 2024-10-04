import { Injectable } from '@angular/core';
import { CanActivate, Router, ActivatedRouteSnapshot, RouterStateSnapshot } from '@angular/router';
import { Observable, of } from 'rxjs';
import { catchError, switchMap } from 'rxjs/operators';
import { AuthService } from '../services/auth.service';
import { commonRoutingConstants } from '../_constants/common-routing.constants';

@Injectable({
    providedIn: 'root'
})
export class AuthGuard implements CanActivate {

    constructor(private authService: AuthService, private router: Router) { }

    canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<boolean> {
        return this.authService.isAuthenticatedObservable().pipe(
            switchMap(isAuthenticated => {
                if (isAuthenticated) {
                    return this.checkAccess();
                }

                const returnUrl = state.url;
                return this.authService.getTokenInfo().pipe(
                    switchMap(() => this.checkAccess()),
                    catchError(() => this.redirectToLogin(returnUrl)) 
                );

            }),
            catchError(() => this.redirectToLogin(state.url))  
        );
    }

    private checkAccess(): Observable<boolean> {
        const tokenValid = this.authService.isTokenValid();
        const isManager = this.authService.isRoleManager();

        if (tokenValid && isManager) {
            return of(true);
        }

        return this.redirectToLogin();
    }

    private redirectToLogin(returnUrl?: string): Observable<boolean> {
        this.router.navigate([commonRoutingConstants.login], { queryParams: { returnUrl } });
        return of(false);
    }
}
