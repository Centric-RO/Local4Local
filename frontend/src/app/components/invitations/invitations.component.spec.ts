import { ComponentFixture, TestBed } from '@angular/core/testing';

import { InvitationsComponent } from './invitations.component';
import { InviteMerchantDialogComponent } from '../invite-merchant-dialog/invite-merchant-dialog.component';
import { CUSTOM_ELEMENTS_SCHEMA, NO_ERRORS_SCHEMA } from '@angular/core';
import { TranslateModule, TranslateService } from '@ngx-translate/core';
import { MerchantService } from '../../services/merchant.service';
import { MatDialog } from '@angular/material/dialog';
import { of } from 'rxjs';
import { PageEvent } from '@angular/material/paginator';

describe('InvitationsComponent', () => {
	let component: InvitationsComponent;
	let fixture: ComponentFixture<InvitationsComponent>;
	let merchantService: any;

	const matDialogMock = {
		open: jest.fn().mockReturnValue({
			afterClosed: jest.fn().mockReturnValue(of(true))
		})
	};

	beforeEach(async () => {
		global.structuredClone = jest.fn((val) => {
			return JSON.parse(JSON.stringify(val));
		});

		merchantService = {
			getPaginatedInvitations: jest.fn().mockReturnValue(of()),
			countAllInvitations: jest.fn().mockReturnValue(of(12))
		};

		await TestBed.configureTestingModule({
			declarations: [InvitationsComponent, InviteMerchantDialogComponent],
			schemas: [CUSTOM_ELEMENTS_SCHEMA, NO_ERRORS_SCHEMA],
			imports: [TranslateModule.forRoot()],
			providers: [
				TranslateService,
				{ provide: MerchantService, useValue: merchantService },
				{ provide: MatDialog, useValue: matDialogMock }
			]
		}).compileComponents();

		fixture = TestBed.createComponent(InvitationsComponent);
		component = fixture.componentInstance;
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});

	it('should call initData with correct parameters on page change', () => {
		const initDataSpy = jest.spyOn(component as any, 'initData');
		const event: PageEvent = { pageIndex: 1, pageSize: 25, length: 100 };

		component.onPageChange(event);

		expect(initDataSpy).toHaveBeenCalledWith(event.pageIndex, event.pageSize);
	});

	it('should open invite merchant dialog', () => {
		component.openInviteMerchantsDialog();
		expect(matDialogMock.open).toHaveBeenCalledWith(InviteMerchantDialogComponent, {
			width: '560px',
			autoFocus: false,
			disableClose: true,
			hasBackdrop: true,
			restoreFocus: false
		});
	});
});
