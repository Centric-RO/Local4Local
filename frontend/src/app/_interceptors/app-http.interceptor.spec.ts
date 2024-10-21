import { fakeAsync, TestBed, tick } from '@angular/core/testing';
import { HTTP_INTERCEPTORS, HttpHandler, HttpRequest, HttpResponse } from '@angular/common/http';

import { AppHttpInterceptor } from './app-http.interceptor';
import { AppLoaderService } from '../services/app-loader.service';
import { of, throwError } from 'rxjs';

describe('AppHttpInterceptor', () => {
	let interceptor: AppHttpInterceptor;
	let appLoaderService: AppLoaderService;
	let httpHandler: HttpHandler;
	let httpRequest: HttpRequest<any>;

	beforeEach(() => {
		const appLoaderServiceMock = {
			loaderShow: jest.fn()
		};

		const httpHandlerMock = {
			handle: jest.fn().mockReturnValue(of(new HttpResponse({ status: 200 })))
		};

		TestBed.configureTestingModule({
			providers: [
				AppHttpInterceptor,
				{ provide: AppLoaderService, useValue: appLoaderServiceMock },
				{ provide: HttpHandler, useValue: httpHandlerMock },
				{ provide: HTTP_INTERCEPTORS, useClass: AppHttpInterceptor, multi: true }
			]
		});

		interceptor = TestBed.inject(AppHttpInterceptor);
		appLoaderService = TestBed.inject(AppLoaderService);
		httpHandler = TestBed.inject(HttpHandler);
		httpRequest = new HttpRequest('GET', '/test');
	});

	it('should be created', () => {
		expect(interceptor).toBeTruthy();
	});

	it('should handle successful response and decrement totalRequests', fakeAsync(() => {
		interceptor.intercept(httpRequest, httpHandler).subscribe();
		tick();
		expect(interceptor['totalRequests']).toBe(0);
		expect(appLoaderService.loaderShow).toHaveBeenCalledWith(false);
	}));

	it('should handle error response and decrement totalRequests', fakeAsync(() => {
		const errorHandler = {
			handle: jest.fn().mockReturnValue(throwError(() => new Error('Error')))
		};

		interceptor.intercept(httpRequest, errorHandler as any).subscribe({
			next: () => {
				/* eslint-disable @typescript-eslint/no-empty-function */
			},
			error: () => {
				tick();
				expect(interceptor['totalRequests']).toBe(0);
				expect(appLoaderService.loaderShow).toHaveBeenCalledWith(false);
			}
		});
	}));
});
