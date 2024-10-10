import { Component, inject, OnInit } from '@angular/core';
import { FormUtil } from '../../util/form.util';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { L4LErrorStateMatcher } from '../../helpers/error-state-matcher';
import { commonRoutingConstants } from '../../_constants/common-routing.constants';
import { AuthService } from '../../services/auth.service';
import { ActivatedRoute, Router } from '@angular/router';
import { RegexUtil } from '../../util/regex.util';

@Component({
    selector: 'app-mfa',
    templateUrl: './mfa.component.html',
    styleUrl: './mfa.component.scss'
})
export class MfaComponent implements OnInit {
    public hasRequiredError = FormUtil.hasRequiredError;
    public hasPatternError = FormUtil.hasPatternError;

    public form: FormGroup;
    public matcher = new L4LErrorStateMatcher();
    public userIsBlocked = false;

    public get invalidCode(): boolean | undefined {
        return this.form.get('code')?.hasError('invalidCode');
    }
    
    private returnUrl: string = commonRoutingConstants.dashboard;
    private fb = inject(FormBuilder);
    private authService = inject(AuthService);
    private router = inject(Router);
    private route = inject(ActivatedRoute);

    public ngOnInit(): void {
        this.createForm();
        this.setReturnUrl();
    }

    public verifyOtpCode(): void {
        if (!this.form.valid) {
            return;
        }

        this.performMfa();
    }

    private setReturnUrl(): void {
        const encodedReturnUrl = this.route.snapshot.queryParams['returnUrl'] || commonRoutingConstants.dashboard;
        this.returnUrl = decodeURIComponent(encodedReturnUrl);
    }

    private createForm(): void {
        this.form = this.fb.group({
            code: ['', [Validators.required, Validators.pattern(RegexUtil.mfaRegexPattern)]],
        });
    }

    private performMfa(): void {
        const mfaCode = this.form.get('code')?.value;

        this.authService.verifyOtpCode(mfaCode).subscribe(() => {
            this.router.navigateByUrl(this.returnUrl);
        },
        () => {
                this.form.get('code')?.setErrors({ invalidCode: true });
        });
    }
}
