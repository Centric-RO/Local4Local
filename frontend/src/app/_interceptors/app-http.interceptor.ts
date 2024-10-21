import {
	HttpEvent,
	HttpHandler,
	HttpInterceptor,
	HttpRequest,
	HttpResponse
} from '@angular/common/http';
import { Injectable } from '@angular/core';
import { AppLoaderService } from '../services/app-loader.service';
import { Observable, tap } from 'rxjs';

@Injectable()
export class AppHttpInterceptor implements HttpInterceptor {
	private totalRequests = 0;

	constructor(private readonly appLoaderService: AppLoaderService) {}

	intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
		this.totalRequests++;
		this.appLoaderService.loaderShow(true);

		return next.handle(req).pipe(
			tap({
				next: (event: HttpEvent<any>) => this.handleResponse(event),
				error: () => this.decreaseRequests()
			})
		);
	}

	private decreaseRequests(): void {
		this.totalRequests--;
		if (this.totalRequests <= 0) {
			this.appLoaderService.loaderShow(false);
			this.totalRequests = 0;
		}
	}

	private handleResponse(event: HttpEvent<any>): void {
		if (event instanceof HttpResponse) {
			this.decreaseRequests();
		}
	}
}
