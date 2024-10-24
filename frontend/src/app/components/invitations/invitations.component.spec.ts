import { ComponentFixture, TestBed } from '@angular/core/testing';

import { InvitationsComponent } from './invitations.component';
import { InviteMerchantDialogComponent } from '../invite-merchant-dialog/invite-merchant-dialog.component';
import { CUSTOM_ELEMENTS_SCHEMA, NO_ERRORS_SCHEMA } from '@angular/core';
import { TranslateModule, TranslateService } from '@ngx-translate/core';
import { MerchantService } from '../../services/merchant.service';
import { MatDialog } from '@angular/material/dialog';

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
        await TestBed.configureTestingModule({
            declarations: [InvitationsComponent,InviteMerchantDialogComponent],
            schemas: [CUSTOM_ELEMENTS_SCHEMA, NO_ERRORS_SCHEMA],
			imports: [TranslateModule.forRoot()],
			providers: [
				TranslateService,
				{ provide: MerchantService, useValue: merchantService },
				{ provide: MatDialog, useValue: matDialogMock }
			]
        })
            .compileComponents();

        fixture = TestBed.createComponent(InvitationsComponent);
        component = fixture.componentInstance;
        fixture.detectChanges();
    });

    it('should create', () => {
        expect(component).toBeTruthy();
    });
});
