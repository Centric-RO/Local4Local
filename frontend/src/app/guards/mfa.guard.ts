/* eslint-disable @typescript-eslint/no-unused-vars */
import { inject, Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivate, RouterStateSnapshot } from '@angular/router';
import { AuthService } from '../services/auth.service';
import { catchError, Observable, of, switchMap } from 'rxjs';
import { GuardUtil } from './guard';

@Injectable({
    providedIn: 'root'
})
export class MfaGuard extends GuardUtil implements CanActivate {

    private authService = inject(AuthService);
    
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
            return this.redirectToDashboard();
        }

        return of(true);
    }

}