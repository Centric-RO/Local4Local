import { fakeAsync, TestBed, tick } from '@angular/core/testing';

import { AppLoaderService } from './app-loader.service';

describe('AppLoaderService', () => {
	let service: AppLoaderService;

	beforeEach(() => {
		TestBed.configureTestingModule({
			providers: [AppLoaderService]
		});

		service = TestBed.inject(AppLoaderService);
	});

	it('should be created', () => {
		expect(service).toBeTruthy();
	});

	describe('loader state emission', () => {
		const testCases = [
			{ expectedValue: true, description: 'true' },
			{ expectedValue: false, description: 'false' }
		];

		testCases.forEach(({ expectedValue, description }) => {
			it(`should emit loader state as ${description}`, fakeAsync(() => {
				let emittedValue: boolean | undefined;

				service.loaderStateObservable$.subscribe((value) => {
					emittedValue = value;
				});

				service.loaderShow(expectedValue);

				tick();

				expect(emittedValue).toBe(expectedValue);
			}));
		});
	});
});
