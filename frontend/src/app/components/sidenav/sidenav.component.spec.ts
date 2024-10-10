import { SidenavComponent } from "./sidenav.component";
import { ComponentFixture, TestBed } from "@angular/core/testing";
import { AuthService } from "../../services/auth.service";
import { Router } from "@angular/router";
import { HttpClientTestingModule } from "@angular/common/http/testing";
import { commonRoutingConstants } from "../../_constants/common-routing.constants";
import { of } from "rxjs";
import { TranslateModule } from "@ngx-translate/core";
import { CUSTOM_ELEMENTS_SCHEMA, NO_ERRORS_SCHEMA } from "@angular/core";

describe('SidenavComponent', () => {
	let component: SidenavComponent;
	let fixture: ComponentFixture<SidenavComponent>;
	let authService: AuthService;
	let router: Router;

	beforeEach(async () => {
		const authServiceMock = {
			logout: jest.fn(() => of({}))
		};

		const routerMock = {
			navigate: jest.fn(),
			get url() {
				return `/${commonRoutingConstants.login}`; 
			},
		};

		await TestBed.configureTestingModule({
			declarations: [SidenavComponent],
			imports: [HttpClientTestingModule, TranslateModule.forRoot()],
			schemas: [
				CUSTOM_ELEMENTS_SCHEMA,
				NO_ERRORS_SCHEMA
			],
			providers: [
				{ provide: AuthService, useValue: authServiceMock },
				{ provide: Router, useValue: routerMock }
			],
		})
			.compileComponents();

		fixture = TestBed.createComponent(SidenavComponent);
		component = fixture.componentInstance;
		authService = TestBed.inject(AuthService);
		router = TestBed.inject(Router);
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});

	it('should call AuthService.logout and navigate to login on successful logout', () => {
		component.logout();

		expect(authService.logout).toHaveBeenCalled();
		expect(router.navigate).toHaveBeenCalledWith([commonRoutingConstants.login]);
	});

	it('should set expandedPanel to merchants when current route is merchants', () => {
		Object.defineProperty(router, 'url', { value: `/${commonRoutingConstants.merchants}` });

		component.updateExpandedPanel();

		expect(component.expandedPanel).toBe(commonRoutingConstants.merchants);
	});

	it('should set expandedPanel to null when current route is not merchants', () => {
		Object.defineProperty(router, 'url', { value: `/${commonRoutingConstants.dashboard}` });

		component.updateExpandedPanel();

		expect(component.expandedPanel).toBeNull();
	});

	it('should call router.navigate with the correct route and update expandedPanel after navigation', async () => {
		const routeToNavigate = commonRoutingConstants.merchants;
		Object.defineProperty(router, 'url', { value: `/${commonRoutingConstants.dashboard}` }); 

		jest.spyOn(router, 'navigate').mockImplementation(() => Promise.resolve() as any);
	
		await component.navigate(routeToNavigate);
	
		expect(router.navigate).toHaveBeenCalledWith([routeToNavigate]);
		expect(component.expandedPanel).toBe(null);
	});

});
