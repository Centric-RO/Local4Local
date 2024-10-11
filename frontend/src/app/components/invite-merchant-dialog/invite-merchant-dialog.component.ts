import { Component, inject, OnInit } from '@angular/core';
import { FormField } from '../../models/form-field.model';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { L4LErrorStateMatcher } from '../../helpers/error-state-matcher';
import { FormUtil } from '../../util/form.util';
import { MatDialogRef } from '@angular/material/dialog';
import { RegexUtil } from '../../util/regex.util';
import { MatChipInputEvent } from '@angular/material/chips';
import { MerchantService } from '../../services/merchant.service';
import { InviteMerchantsDto } from '../../models/invite-merchants-dto.model';
import { SnackbarType } from '../../_enums/snackbar-type.enum';
import { CustomSnackbarComponent } from '../custom-snackbar/custom-snackbar.component';
import { MatSnackBar } from '@angular/material/snack-bar';
import { TranslateService } from '@ngx-translate/core';
import { SnackbarData } from '../../models/snackbar-data.model';

@Component({
	selector: 'app-invite-merchant-dialog',
	templateUrl: './invite-merchant-dialog.component.html',
	styleUrl: './invite-merchant-dialog.component.scss'
})
export class InviteMerchantDialogComponent implements OnInit {
	public hasRequiredError = FormUtil.hasRequiredError;

	public merchantEmails: Set<string> = new Set<string>();
	public emailError: string;

	public formFields: FormField[] = [];
	public form: FormGroup;
	public matcher = new L4LErrorStateMatcher();

	private readonly fb = inject(FormBuilder);
	private readonly dialogRef = inject(MatDialogRef<InviteMerchantDialogComponent>);
	private readonly merchantService = inject(MerchantService);
	private readonly snackBar = inject(MatSnackBar);
	private readonly translateService = inject(TranslateService);


	public get isFormValid(): boolean {
		return this.form.valid && this.merchantEmails.size > 0;
	}

	public get merchantEmailsArray(): string[] {
		return [...this.merchantEmails];
	}

	public get invitationMessageLength(): number {
		return this.form.controls['invitationMessage'].value.length;
	}

	public ngOnInit(): void {
		this.createForm();
	}

	public closeDialog(success?: string): void {
		this.dialogRef.close(success);
	}

	public handleEnterKeyup(event: MatChipInputEvent): void {
		const emailRegex = new RegExp(RegexUtil.emailRegexPattern);
		const email = (event.value || '').trim();

		if (!email || !emailRegex.test(email)) {
			this.emailError = 'inviteMerchants.error.emailPattern';
			return;
		}

		if (this.merchantEmails.has(email)) {
			this.emailError = 'inviteMerchants.error.emailAlreadyInList';
			return;
		}

		if (this.merchantEmails.size >= 5) {
			this.emailError = 'inviteMerchants.error.emailsLimitReached';
			return;
		}

		this.merchantEmails.add(email);
		this.emailError = '';
		event.chipInput!.clear();
	}

	public removeEmailFromList(email: string): void {
		this.merchantEmails.delete(email);
	}

	public inviteMerchants(): void {
		if (this.form.invalid) return;
		this.sendInvitations();
	}

	private sendInvitations(): void {
		const inviteSuppliersDto = this.getFormValuesToInviteMerchantsDto();
		this.merchantService.inviteMerchants(inviteSuppliersDto).subscribe(() => {
			this.dialogRef.close(true);
			this.showSuccessToaster(inviteSuppliersDto.emails.length === 1);
		});
	}

	private getFormValuesToInviteMerchantsDto(): InviteMerchantsDto {
		return new InviteMerchantsDto(
			this.merchantEmailsArray,
			this.form.controls['invitationMessage'].value
		);
	}

	private createForm(): void {
		this.form = this.fb.group({
			invitationMessage: ['', [Validators.required, Validators.maxLength(1024)]],
			email: ['']
		});
	}

	private showSuccessToaster(isSingleMail: boolean): void {
		const message = isSingleMail ? 'inviteMerchants.successOne' : 'inviteMerchants.successMultiple';
		const toasterMessage = this.translateService.instant(message);

		this.snackBar.openFromComponent(CustomSnackbarComponent, {
			duration: 8000,
			data: new SnackbarData(toasterMessage, SnackbarType.SUCCESS),
			horizontalPosition: 'right',
			verticalPosition: 'bottom',
		});
	}

}
