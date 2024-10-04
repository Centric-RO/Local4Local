import { Component, inject, OnInit } from '@angular/core';
import { MerchantService } from "../../services/merchant.service";
import { MatDialog } from "@angular/material/dialog";
import { InviteMerchantDialogComponent } from "../invite-merchant-dialog/invite-merchant-dialog.component";


@Component({
	selector: 'app-dashboard',
	templateUrl: './dashboard.component.html',
	styleUrl: './dashboard.component.scss'
})
export class DashboardComponent implements OnInit {
	public merchantsCount = 0;
	private merchantService = inject(MerchantService);
	private dialog = inject(MatDialog);

	public ngOnInit(): void {
		this.initData();
	}

	public openInviteMerchantsDialog(): void {
		this.dialog.open(InviteMerchantDialogComponent, { width: '560px' });
	}

	private initData(): void {
		this.merchantService.countAllMerchants().subscribe(result => {
			this.merchantsCount = result;
		});
	}
}
