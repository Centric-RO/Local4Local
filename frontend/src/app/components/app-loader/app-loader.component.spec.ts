import { ComponentFixture, TestBed } from '@angular/core/testing';

import { AppLoaderComponent } from './app-loader.component';
import { BehaviorSubject } from 'rxjs';
import { AppLoaderService } from '../../services/app-loader.service';
import { ChangeDetectorRef } from '@angular/core';

describe('AppLoaderComponent', () => {
	let component: AppLoaderComponent;
	let fixture: ComponentFixture<AppLoaderComponent>;
	let appLoaderServiceMock: { loaderStateObservable$: BehaviorSubject<boolean> };
	let cdrMock: { detectChanges: jest.Mock };

	beforeEach(async () => {
		appLoaderServiceMock = {
			loaderStateObservable$: new BehaviorSubject<boolean>(false)
		};

		cdrMock = {
			detectChanges: jest.fn()
		};

		await TestBed.configureTestingModule({
			declarations: [AppLoaderComponent],
			providers: [
				{ provide: AppLoaderService, useValue: appLoaderServiceMock },
				{ provide: ChangeDetectorRef, useValue: cdrMock }
			]
		}).compileComponents();

		fixture = TestBed.createComponent(AppLoaderComponent);
		component = fixture.componentInstance;
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});

	it('should subscribe to loaderStateObservable$ on init', () => {
		const stateSpy = jest.spyOn(appLoaderServiceMock.loaderStateObservable$, 'subscribe');
		component.ngOnInit();
		expect(stateSpy).toHaveBeenCalled();
	});

	it('should update show property and call detectChanges on state change', () => {
		component.ngOnInit();

		appLoaderServiceMock.loaderStateObservable$.next(true);
		expect(component.show).toBe(true);
	});

	it('should unsubscribe from loaderStateObservable$ on destroy', () => {
		component.ngOnInit();
		const unsubscribeSpy = jest.spyOn(component['subscription'], 'unsubscribe');
		component.ngOnDestroy();
		expect(unsubscribeSpy).toHaveBeenCalled();
	});
});
