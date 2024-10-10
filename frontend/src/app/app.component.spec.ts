import { TestBed } from '@angular/core/testing';
import { RouterModule } from '@angular/router';
import { AppComponent } from './app.component';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { TranslateModule, TranslateService } from '@ngx-translate/core';
import { HttpClientModule } from "@angular/common/http";
import { CUSTOM_ELEMENTS_SCHEMA, NO_ERRORS_SCHEMA } from "@angular/core";
import { HomeComponent } from './components/home/home.component';
import { SidenavService } from "./services/sidenav.service";

describe('AppComponent', () => {
	let sidenavService: any;

	beforeEach(async () => {
		const mockSidenavService = {
			isRouteWithNavigation: true
		}

		await TestBed.configureTestingModule({
			imports: [RouterModule.forRoot([])],
			declarations: [AppComponent],
		}).compileComponents();

		await TestBed.configureTestingModule({
			imports: [
				HttpClientModule,
				BrowserAnimationsModule,
				TranslateModule.forRoot()],
			schemas: [
				CUSTOM_ELEMENTS_SCHEMA,
				NO_ERRORS_SCHEMA
			],
			declarations: [AppComponent, HomeComponent],
			providers: [
				TranslateService,
				{ provide: SidenavService, useValue: mockSidenavService}
			],
		}).compileComponents();

		sidenavService = TestBed.inject(SidenavService);
	});

	it('should create the app', () => {
		const fixture = TestBed.createComponent(AppComponent);
		const app = fixture.componentInstance;
		expect(app).toBeTruthy();
	});

	it('should return the value of sidenavService.isRouteWithNavigation', () => {
		sidenavService.isRouteWithNavigation = true;

		const fixture = TestBed.createComponent(AppComponent);
		const app = fixture.componentInstance;
		expect(app.shouldShowSidenav).toBeTruthy();
	});
});
