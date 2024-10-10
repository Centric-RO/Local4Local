import { Component, inject } from "@angular/core";
import { commonRoutingConstants } from "../../_constants/common-routing.constants";
import { Router } from "@angular/router";
import { AuthService } from "../../services/auth.service";
import { SidenavItem } from "../../_models/sidenav-item.model";

@Component({
	selector: 'app-sidenav',
	templateUrl: './sidenav.component.html',
	styleUrls: ['./sidenav.component.scss']
})
export class SidenavComponent {
	private router = inject(Router);
	private authService = inject(AuthService);

	public expandedPanel: string | null = null;

	public links: SidenavItem[] = [
		{
			id: 'dashboard',
			icon: 'dashboard',
			title: 'general.dashboard',
			route: commonRoutingConstants.dashboard,
		},
		{
			id: 'merchants',
			icon: 'storefront',
			title: 'general.merchants',
			route: '',
			children: [
				{
					id: 'merchants-list',
					icon: '',
					title: 'general.merchants',
					route: commonRoutingConstants.merchants,
				},
				{
					id: 'invitations',
					icon: '',
					title: 'general.invitations',
					route: commonRoutingConstants.invitations,
				}
			]
		},
		{
			id: 'profile',
			icon: 'settings',
			title: 'general.profile',
			route: commonRoutingConstants.profile,
		}
	];


	public isCurrentRoute(route: string): boolean {
		return this.router.url.slice(1) === route;
	}

	public isMerchantsTab(): boolean {
		return this.expandedPanel === commonRoutingConstants.merchants;
	}

	public isMerchantsRoute(): boolean {
		const currentRoute = this.router.url.slice(1);
		return currentRoute === commonRoutingConstants.merchants || currentRoute === commonRoutingConstants.invitations;
	}

	public logout(): void {
		this.authService.logout().subscribe(() => {
			this.navigate(commonRoutingConstants.login);
		});
	}

	public navigate(route: string): void {
		this.router.navigate([route]).then(() => {
			this.updateExpandedPanel();
		});
	}

	public updateExpandedPanel(): void {
		this.expandedPanel = this.isMerchantsRoute() ? commonRoutingConstants.merchants : null;
	}

}