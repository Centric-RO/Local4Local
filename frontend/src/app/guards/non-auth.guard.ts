import { Injectable } from "@angular/core";
import { CanActivate, Router } from "@angular/router";
import { AuthService } from "../services/auth.service";
import { catchError, Observable, of, switchMap, tap } from "rxjs";
import { commonRoutingConstants } from "../_constants/common-routing.constants";


@Injectable({
    providedIn: 'root'
})
export class NonAuthGuard implements CanActivate {

    constructor(private authService: AuthService, private router: Router) { }

    canActivate(): Observable<boolean> | Promise<boolean> | boolean {
        return this.authService.isAuthenticatedObservable().pipe(
            switchMap(isAuthenticated =>
                isAuthenticated ? this.handleAuthenticated() : this.handleUnauthenticated()
            ),
            catchError(() => of(true))
        );
    }

    private handleAuthenticated(): Observable<boolean> {
        if (this.isValidSession() || this.isRememberMeSessionValid()) {
            return this.redirectToDashboard();
        }

        this.authService.logout();
        return of(true);
    }

    private isValidSession() {
        return this.authService.isTokenValid() && this.authService.isRoleManager();
    }

    private isRememberMeSessionValid() {
        return !this.authService.isTokenValid() && this.authService.isRoleManager() && this.authService.isRememberMeActive();
    }

    private handleUnauthenticated(): Observable<boolean> {
        return this.authService.getTokenInfo().pipe(
            switchMap(() => this.authService.isTokenValid() ? this.redirectToDashboard() : of(true)),
            catchError(() => of(true))
        );
    }

    private redirectToDashboard(): Observable<boolean> {
        return of(false).pipe(
            tap(() => this.router.navigate([commonRoutingConstants.dashboard]))
        );
    }
}
