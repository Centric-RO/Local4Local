import { SidenavService } from "./sidenav.service";
import { Router } from "@angular/router";
import { TestBed } from "@angular/core/testing";
import { commonRoutingConstants } from "../_constants/common-routing.constants";

describe('SidenavService', () => {
	let sidenavService: SidenavService;
	let router: any;

	beforeEach(() => {
		const mockRouter = {
			url: `/${commonRoutingConstants.login}`,
			parseUrl: (url: string) => ({
				root: {
					children: {
						primary: {
							segments: [{ path: url.split('/')[1] }] 
						}
					}
				}
			})
		};

		TestBed.configureTestingModule({
			providers: [
				SidenavService,
				{ provide: Router, useValue: mockRouter }
			]
		});

		sidenavService = TestBed.inject(SidenavService);
		router = TestBed.inject(Router);
	});

	it('should be created', () => {
		expect(sidenavService).toBeTruthy();
	});

	describe('isRouteWithNavigation method', () => {
		it('should return false for path in "pathsToHideFor"', () => {
			expect(sidenavService.isRouteWithNavigation).toBeFalsy();
		});

		it('should return false for root', () => {
			router.url = '/';
			expect(sidenavService.isRouteWithNavigation).toBeFalsy();
		});
	});
});
