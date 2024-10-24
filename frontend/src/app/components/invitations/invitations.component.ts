import { Component, inject } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { InviteMerchantDialogComponent } from '../invite-merchant-dialog/invite-merchant-dialog.component';
import { CustomDialogConfigUtil } from '../../config/custom-dialog-config';
import { PageEvent } from '@angular/material/paginator';
import { forkJoin } from 'rxjs';
import { MerchantService } from '../../services/merchant.service';
import { MatTableDataSource } from '@angular/material/table';
import { InvitationDto } from '../../models/invitation-dto.model';

@Component({
	selector: 'app-invitations',
	templateUrl: './invitations.component.html',
	styleUrl: './invitations.component.scss'
})
export class InvitationsComponent {
    public readonly noDataTitle: string = 'inviteMerchants.noData.title';
	public readonly noDataDescription: string = 'inviteMerchants.noData.description';

    public dataSource: MatTableDataSource<InvitationDto>;
	public data: InvitationDto[] = [];
	public noOfInvitations = 0;

	private currentPageIndex = 0;
	private currentPageSize = 10;

    private readonly dialog = inject(MatDialog);
    private readonly merchantsService = inject(MerchantService);

	public openInviteMerchantsDialog(): void {
		this.dialog.open(InviteMerchantDialogComponent, CustomDialogConfigUtil.GENERIC_MODAL_CONFIG);
	}

    public onPageChange(event: PageEvent): void {
		this.currentPageIndex = event.pageIndex;
		this.currentPageSize = event.pageSize;
		this.initData(event.pageIndex, event.pageSize);
	}

    private initData(pageIndex: number, pageSize: number): void {
		forkJoin({
			invitations: this.merchantsService.getPaginatedInvitations(pageIndex, pageSize),
			count: this.merchantsService.countAllInvitations()
		}).subscribe(({ invitations, count }) => {
			this.data = invitations;
			this.dataSource = new MatTableDataSource<InvitationDto>(invitations);
			this.noOfInvitations = count;
		});
	}
}
