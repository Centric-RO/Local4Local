import { ComponentFixture, TestBed } from '@angular/core/testing';
import { MfaComponent } from './mfa.component';
import { FormBuilder, ReactiveFormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { of, throwError } from 'rxjs';
import { AuthService } from '../../services/auth.service';
import { ActivatedRoute } from '@angular/router';
import { commonRoutingConstants } from '../../_constants/common-routing.constants';
import { TranslateModule } from '@ngx-translate/core';

describe('MfaComponent', () => {
    let component: MfaComponent;
    let fixture: ComponentFixture<MfaComponent>;
    let authServiceMock: any;
    let routerMock: any;
    let activatedRouteMock: any;

    beforeEach(async () => {
        authServiceMock = {
            verifyOtpCode: jest.fn()
        };

        routerMock = {
            navigateByUrl: jest.fn()
        };

        activatedRouteMock = {
            snapshot: { queryParams: {} }
        };

        await TestBed.configureTestingModule({
            declarations: [MfaComponent],
            imports: [
                ReactiveFormsModule,
                TranslateModule.forRoot(),
            ],
            providers: [
                FormBuilder,
                { provide: AuthService, useValue: authServiceMock },
                { provide: Router, useValue: routerMock },
                { provide: ActivatedRoute, useValue: activatedRouteMock },
            ]
        }).compileComponents();

        fixture = TestBed.createComponent(MfaComponent);
        component = fixture.componentInstance;
        fixture.detectChanges();
    });

    it('should create the component', () => {
        expect(component).toBeTruthy();
    });

    it('should create the form on init', () => {
        component.ngOnInit();
        expect(component.form).toBeTruthy();
        expect(component.form.get('code')).toBeTruthy();
    });

    it('should set returnUrl from queryParams or default to dashboard', () => {
        activatedRouteMock.snapshot.queryParams['returnUrl'] = encodeURIComponent('/custom-return-url');
        component.ngOnInit();
        expect(component["returnUrl"]).toBe('/custom-return-url');

        activatedRouteMock.snapshot.queryParams['returnUrl'] = undefined;
        component.ngOnInit();
        expect(component["returnUrl"]).toBe(commonRoutingConstants.dashboard);
    });

    it('should not submit form if it is invalid', () => {
        component.form.get('code')?.setValue('invalid'); // Does not match the pattern
        component.verifyOtpCode();
        expect(authServiceMock.verifyOtpCode).not.toHaveBeenCalled();
    });

    it('should submit form and navigate to returnUrl if OTP verification is successful', () => {
        component.form.get('code')?.setValue('123456'); // Assuming it passes RegexUtil.mfaRegexPattern
        authServiceMock.verifyOtpCode.mockReturnValue(of({})); // Mock successful response

        component.verifyOtpCode();
        expect(authServiceMock.verifyOtpCode).toHaveBeenCalledWith('123456');
        expect(routerMock.navigateByUrl).toHaveBeenCalledWith(component["returnUrl"]);
    });

    it('should set invalidCode error if OTP verification fails', () => {
        component.form.get('code')?.setValue('123456');
        authServiceMock.verifyOtpCode.mockReturnValue(throwError(() => new Error('Invalid OTP'))); // Mock error response

        component.verifyOtpCode();
        expect(authServiceMock.verifyOtpCode).toHaveBeenCalledWith('123456');
        expect(component.form.get('code')?.hasError('invalidCode')).toBeTruthy();
    });
});
