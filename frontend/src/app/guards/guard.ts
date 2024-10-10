import { inject, Injectable } from "@angular/core";
import { Router } from "@angular/router";
import { Observable, of } from "rxjs";
import { commonRoutingConstants } from "../_constants/common-routing.constants";

@Injectable({
	providedIn: 'root'
})
export class GuardUtil {

    private router = inject(Router);
    
    public redirectToLogin(returnUrl?: string): Observable<boolean> {
        this.router.navigate([commonRoutingConstants.login], { queryParams: { returnUrl } });
        return of(false);
    }

    public redirectToDashboard(returnUrl?: string): Observable<boolean> {
        this.router.navigate([commonRoutingConstants.dashboard], { queryParams: { returnUrl } });
        return of(false);
    }
}