import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ReactiveFormsModule, Validators } from '@angular/forms';
import { LoginComponent } from './login.component';
import { TranslateModule } from '@ngx-translate/core';
import { MatIconModule } from '@angular/material/icon';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { NO_ERRORS_SCHEMA } from '@angular/core';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { of, take, throwError } from 'rxjs';
import { commonRoutingConstants } from '../../_constants/common-routing.constants';
import { Role } from '../../enums/roles.enum';
import { CaptchaService } from '../../services/captcha.service';
import { ActivatedRoute, Router } from '@angular/router';
import { CaptchaStatus } from '../../_enums/captcha.enum';
import { AuthService } from '../../services/auth.service';

describe('LoginComponent', () => {
    let component: LoginComponent;
    let fixture: ComponentFixture<LoginComponent>;
    let captchaService: CaptchaService;

    const authServiceMock = {
        login: jest.fn().mockReturnValue(of({}))
    };

    const routerMock = {
        navigate: jest.fn(),
        navigateByUrl: jest.fn()
    };

    beforeEach(async () => {
        await TestBed.configureTestingModule({
            declarations: [LoginComponent],
            schemas: [NO_ERRORS_SCHEMA],
            imports: [
                ReactiveFormsModule,
                TranslateModule.forRoot(),
                MatIconModule,
                MatCheckboxModule,
                HttpClientTestingModule
            ],
            providers: [
                { provide: AuthService, useValue: authServiceMock },
                { provide: Router, useValue: routerMock },
                { provide: ActivatedRoute, useValue: { snapshot: { queryParams: {} } } },
                CaptchaService
            ]

        }).compileComponents();
    });

    beforeEach(() => {
        fixture = TestBed.createComponent(LoginComponent);
        component = fixture.componentInstance;
        captchaService = TestBed.inject(CaptchaService);
        fixture.detectChanges();
        jest.clearAllMocks();
        component.userIsBlocked = false;
        component.form.reset();
    });

    it('should create the component', () => {
        expect(component).toBeTruthy();
    });

    it('should not call authServiceMock.login() if form is invalid', () => {
        const loginSpy = jest.spyOn(authServiceMock, 'login').mockReturnValue(of({}) as any);
        component.form.get('email')?.setValue('');
        component.form.get('password')?.setValue('');

        component.login();

        expect(loginSpy).not.toHaveBeenCalled();
    });

    it('should call authServiceMock.login() with correct data when form is valid', () => {
        const loginSpy = jest.spyOn(authServiceMock, 'login').mockReturnValue(of({}) as any);
        component.form.get('email')?.setValue('test@example.com');
        component.form.get('password')?.setValue('Password123');
        component.form.get('rememberMe')?.setValue(true);

        component.login();

        expect(loginSpy).toHaveBeenCalledWith({
            email: 'test@example.com',
            password: 'Password123',
            rememberMe: true,
            role: Role.MANAGER,
            reCaptchaResponse: null
        });
    });

    it('should navigate to MFA after successful login', () => {
        jest.spyOn(authServiceMock, 'login').mockReturnValue(of({}) as any);
        const navigateSpy = jest.spyOn(routerMock as any, 'navigate');
    
        component.form.get('email')?.setValue('test@example.com');
        component.form.get('password')?.setValue('Password123');
    
        component.login();
    
        expect(navigateSpy).toHaveBeenCalledWith([commonRoutingConstants.mfa]);
    });

    it('should navigate to MFA with returnUrl in queryParams if returnUrl is not MFA', () => {
        jest.spyOn(authServiceMock, 'login').mockReturnValue(of({}) as any);
        const navigateSpy = jest.spyOn(routerMock as any, 'navigate');
        
        const returnUrl = '/dashboard';
        component["route"].snapshot.queryParams = { returnUrl };
    
        component.form.get('email')?.setValue('test@example.com');
        component.form.get('password')?.setValue('Password123');
    
        component.login();
    
        expect(navigateSpy).toHaveBeenCalledWith([commonRoutingConstants.mfa], { queryParams: { returnUrl } });
    });
    
    it('should create a form group with form controls', () => {
        expect(component.form.contains('email')).toBeTruthy();
        expect(component.form.contains('password')).toBeTruthy();
        expect(component.form.contains('rememberMe')).toBeTruthy();
    });

    it('email control should be invalid if empty', () => {
        const emailControl = component.form.get('email');
        emailControl?.setValue('');
        expect(emailControl?.invalid).toBeTruthy();
        expect(emailControl?.errors?.['required']).toBeTruthy();
    });

    it('password control should be invalid if empty', () => {
        const passwordControl = component.form.get('password');
        passwordControl?.setValue('');
        expect(passwordControl?.invalid).toBeTruthy();
        expect(passwordControl?.errors?.['required']).toBeTruthy();
    });

    it('email control should be invalid if pattern does not match', () => {
        const emailControl = component.form.get('email');
        emailControl?.setValue('invalid-email');
        expect(emailControl?.invalid).toBeTruthy();
        expect(emailControl?.errors?.['pattern']).toBeTruthy();
    });

    it('it should throw error and captcha validator to be null', () => {
        component.ngOnInit();
        component.form.setValue({
            email: 'test@example.com',
            password: 'password',
            rememberMe: false,
            reCaptchaResponse: ''
        });
        authServiceMock.login.mockReturnValue(throwError('Some other error message'));

        component.login();

        expect(authServiceMock.login).toHaveBeenCalled();
        expect(component.form.get('reCaptchaResponse')?.validator).toBe(null);
    });


    it('should handle error if login fails', () => {
        jest.spyOn(authServiceMock, 'login').mockReturnValue(throwError(() => new Error('Login failed')));
        component.form.get('email')?.setValue('test@example.com');
        component.form.get('password')?.setValue('Password123');

        component.login();

        expect(routerMock.navigate).not.toHaveBeenCalled();
    });
    describe('Recaptcha', () => {
        beforeEach(() => {
            component.ngOnInit();
        });
        it('should not add validators if user is not blocked', () => {
            component.userIsBlocked = true;
            const expectedStatus: CaptchaStatus = CaptchaStatus.CREATED;

            captchaService.displayCaptchaObservable.pipe(take(1)).subscribe((status) => {
                expect(status).toEqual(expectedStatus);
                expect(component['addRecaptchaValidatorsAndDetechChanges']).not.toBeCalled();
                expect(component.userIsBlocked).toBeFalsy();
                expect(component.form.get('reCaptchaResponse')?.hasValidator(Validators.required)).toBe(false);
            });
            captchaService.displayCaptchaSubject.next(expectedStatus);
        });
        it('should subscribe to captcha service and reset recaptcha when status is not CREATED', () => {
            const expectedStatus: CaptchaStatus = CaptchaStatus.CREATED;

            captchaService.displayCaptchaObservable.pipe(take(1)).subscribe((status) => {
                expect(status).toEqual(expectedStatus);
                expect(component['addRecaptchaValidatorsAndDetechChanges']).toBeCalled();
                expect(component.userIsBlocked).toBeTruthy();
                expect(component.form.get('reCaptchaResponse')?.hasValidator(Validators.required)).toBe(true);
            });
            captchaService.displayCaptchaSubject.next(expectedStatus);
        });

        it('should show recaptcha and have validators when form is valid and user is not blocked', () => {
            const expectedStatus: CaptchaStatus = CaptchaStatus.INVALID_CREDENTIALS;
            captchaService.displayCaptchaObservable.pipe(take(1)).subscribe((status) => {
                expect(status).toEqual(expectedStatus);
                expect(component['addRecaptchaValidatorsAndDetechChanges']).toBeCalled();
                expect(component['ngRecaptcha']['reset']).toBeCalled();
                const recaptcha = component.form.get('reCaptchaResponse');
                if (recaptcha) {
                    expect(recaptcha.hasValidator(Validators.required)).toBe(true);
                }
            });
            captchaService.displayCaptchaSubject.next(expectedStatus);
        });

        it('should not add validators if recaptcha is null', () => {
            component.form.setValue({
                email: 'test@example.com',
                password: 'password',
                rememberMe: false,
                reCaptchaResponse: null
            });
            const expectedStatus: CaptchaStatus = CaptchaStatus.INVALID_CREDENTIALS;
            captchaService.displayCaptchaObservable.pipe(take(1)).subscribe(() => {
                expect(component.form.get('reCaptchaResponse')?.hasValidator(Validators.required)).toBe(true);
            });
            captchaService.displayCaptchaSubject.next(expectedStatus);
        });
    });

    it('it should not perform login when user is blocked and no captcha', () => {
        component.ngOnInit();
        component.form.setValue({
            email: 'test@example.com',
            password: 'password',
            rememberMe: false,
            reCaptchaResponse: ''
        });
        component.userIsBlocked = true;
        component.login();
        expect(authServiceMock.login).toBeCalledTimes(0);
    });

    it('should navigate to the forgot password page', () => {
        component.navigateToForgotPass();
        expect(routerMock.navigate).toHaveBeenCalledWith([commonRoutingConstants.recover]);
    });

});
