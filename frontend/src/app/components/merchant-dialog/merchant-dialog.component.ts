import { Component, inject, OnInit } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { FormField } from '../../models/form-field.model';
import { FormBuilder, FormControl, FormGroup, ValidatorFn, Validators } from '@angular/forms';
import { L4LErrorStateMatcher } from '../../helpers/error-state-matcher';
import { FormUtil } from '../../util/form.util';
import { EsriLocatorService } from '../../services/esri-locator/esri-locator.service';
import { EsriSuggestionResult } from '../../models/esri-suggestion-response.model';
import AddressCandidate from '@arcgis/core/rest/support/AddressCandidate';
import { CategoryDto } from '../../_models/category-dto.model';
import { CategoryService } from '../../services/category.service';
import { MerchantService } from '../../services/merchant.service';
import { MerchantDto } from '../../models/merchant-dto.model';
import { ALREADY_REGISTERED_CODE, SUCCESS_CODE } from '../../_constants/error-constants';
import { RegexUtil } from '../../util/regex.util';
import { ColumnType } from '../../enums/column.enum';
import { TranslateService } from '@ngx-translate/core';
import { MatSnackBar } from '@angular/material/snack-bar';
import { CustomSnackbarComponent } from '../custom-snackbar/custom-snackbar.component';
import { SnackbarData } from '../../models/snackbar-data.model';
import { SnackbarType } from '../../_enums/snackbar-type.enum';

type ApprovalDialogValue = string | number | null | undefined;

@Component({
	selector: 'app-merchant-dialog',
	templateUrl: './merchant-dialog.component.html',
	styleUrls: ['./merchant-dialog.component.scss']
})
export class MerchantDialogComponent implements OnInit {
	public hasRequiredError = FormUtil.hasRequiredError;
	public hasPatternError = FormUtil.hasPatternError;

	public isApprovalDialog = false;
	public formFields: FormField[] = [];
	public form: FormGroup;
	public matcher = new L4LErrorStateMatcher();

	public suggestions: EsriSuggestionResult[] = [];
	public selectedLocation: AddressCandidate | null = null;
	public categories: CategoryDto[] = [];

	private fb = inject(FormBuilder);
	private esriLocatorService = inject(EsriLocatorService);
	private translateService = inject(TranslateService);
	private readonly data = inject(MAT_DIALOG_DATA);

	private readonly dialogRef = inject(MatDialogRef<MerchantDialogComponent>);
	private readonly categoryService = inject(CategoryService);
	private readonly merchantService = inject(MerchantService);
	private readonly snackBar = inject(MatSnackBar);

	private currentMerchantId: string;


	public get isDisabled(): boolean {
		return (this.form.invalid || !this.selectedLocation) && !this.isApprovalDialog;
	}

	public get hasNoSuggestions(): boolean {
		return this.suggestions.length === 0 && !this.selectedLocation
	}

	public get title(): string {
		return this.isApprovalDialog ? 'approveMerchant.title' : 'register.title';
	}

	public get subtitle(): string {
		return this.isApprovalDialog ? 'approveMerchant.subtitle' : 'register.subtitle';
	}

	public get actionButton(): string {
		return this.isApprovalDialog ? 'approveMerchant.title' : 'register.registerButton';
	}

	public ngOnInit(): void {
		this.checkFormMode();
		this.initCategories();
		this.initializeFormFields();
		this.createForm();
	}

	public onSearchAddress(event: Event): void {
		this.selectedLocation = null;

		const input = (event.target as HTMLInputElement).value.trim();

		if (input.length <= 2) {
			this.suggestions = [];
			return;
		}

		this.esriLocatorService.getSuggestions(input).subscribe({
			next: (suggestions) => (this.suggestions = suggestions),
			error: () => {
				this.suggestions = [];
			}
		});
	}

	public selectAddress(suggestion: EsriSuggestionResult): void {
		this.form.get('address')?.setValue(suggestion.text);

		this.suggestions = [];

		this.esriLocatorService.findLocationBasedOnSuggestion(suggestion).subscribe({
			next: (locationData) => (this.selectedLocation = locationData[0] ?? null),
			error: () => (this.selectedLocation = null)
		});
	}

	public closeDialog(success?: string): void {
		this.dialogRef.close(success);
	}

	public performAction(): void {
		if (this.isApprovalDialog) {
			this.approveMerchant(this.currentMerchantId);
			return;
		}

		this.registerMerchant();
	}

	public onKvkInput(event: Event): void {
		const input = event.target as HTMLInputElement;
		input.value = input.value.replace(/\D/g, '');
	}

	private approveMerchant(merchantId: string): void {
		this.merchantService.approveMerchant(merchantId).subscribe(() => {
			this.closeDialog(SUCCESS_CODE);
			this.showSuccessToast();
		})
	}

	private showSuccessToast(): void {
		const toasterMessage = this.translateService.instant('approveMerchant.success');

		this.snackBar.openFromComponent(CustomSnackbarComponent, {
			duration: 8000,
			data: new SnackbarData(toasterMessage, SnackbarType.SUCCESS),
			horizontalPosition: 'right',
			verticalPosition: 'bottom',
		});
	}

	private registerMerchant(): void {
		if (this.form.invalid) return;

		const merchantDto = this.createMerchantDto();

		this.merchantService.registerMerchant(merchantDto).subscribe({
			next: () => this.closeDialog(SUCCESS_CODE),
			error: ({ error: { message } }) => {
				const dialogResult = message === ALREADY_REGISTERED_CODE ? ALREADY_REGISTERED_CODE : null;
				this.closeDialog(dialogResult as string);
			}
		});
	}

	private createMerchantDto(): MerchantDto {
		const { companyName, kvk, category, address, contactEmail, website } = this.form.value;
		const { location } = this.selectedLocation ?? {};

		return {
			companyName,
			kvk: kvk,
			category,
			latitude: location?.y ?? 0.0,
			longitude: location?.x ?? 0.0,
			address,
			contactEmail,
			website
		};
	}

	private initializeFormFields(): void {
		this.formFields = [
			{
				formControl: 'companyName',
				labelKey: 'table.column.companyName',
				fieldType: 'input',
				required: true,
				maxLength: 256,
				isReadOnly: this.isApprovalDialog,
				requiredMessage: 'register.error.companyNameRequired'
			},
			{
				formControl: 'kvk',
				labelKey: 'table.column.kvkNumber',
				fieldType: 'input',
				required: true,
				maxLength: 8,
				isReadOnly: this.isApprovalDialog,
				requiredMessage: 'register.error.kvkNumberRequired',
				pattern: RegexUtil.kvkRegexPattern,
				patternMessage: 'register.error.kvkFormControlLength'
			},
			{
				formControl: 'category',
				labelKey: 'table.column.category',
				fieldType: this.isApprovalDialog ? 'input' : 'select',
				required: true,
				isReadOnly: this.isApprovalDialog,
				options: this.categories,
				requiredMessage: 'register.error.categoryRequired'
			},
			{
				formControl: 'address',
				labelKey: 'table.column.address',
				fieldType: 'input',
				required: true,
				isReadOnly: this.isApprovalDialog,
				maxLength: 256,
				requiredMessage: 'register.error.addressRequired'
			},
			{
				formControl: 'contactEmail',
				labelKey: 'register.contactEmail',
				fieldType: 'input',
				required: true,
				isReadOnly: this.isApprovalDialog,
				maxLength: 256,
				pattern: RegexUtil.emailRegexPattern,
				patternMessage: 'register.error.emailInvalid',
				requiredMessage: 'register.error.emailRequired'
			},
			{
				formControl: 'website',
				labelKey: 'register.website',
				fieldType: 'input',
				required: false,
				isReadOnly: this.isApprovalDialog,
				maxLength: 256,
				pattern: RegexUtil.urlRegexPattern,
				patternMessage: 'register.error.invalidUrl'
			}
		];

	}

	private createForm(): void {
		const merchantData = this.isApprovalDialog ? (this.data.merchant as MerchantDto) : null;

		const controls = this.formFields.reduce((acc, field) => {
			const formControlName = field.formControl as keyof MerchantDto;

			let formControlValue = merchantData ? merchantData[formControlName] : null;

			if (this.isApprovalDialog) {
				formControlValue = this.handleApprovalDialogValues(field, formControlValue);
			}

			acc[formControlName] = new FormControl(formControlValue, this.getValidators(field));
			return acc;
		}, {} as Record<string, FormControl>);

		this.form = this.fb.group(controls);
	}

	private handleApprovalDialogValues(field: FormField, value: ApprovalDialogValue): ApprovalDialogValue {
		if (field.formControl === ColumnType.WEBSITE && value === null) {
			return '-';
		}

		if (field.formControl === ColumnType.CATEGORY && value !== null) {
			return this.translateService.instant(value as string);
		}

		return value;
	}

	private getValidators(field: FormField): ValidatorFn[] {
		if (this.isApprovalDialog) {
			return [];
		}

		const validators = [];
		if (field.required) {
			validators.push(Validators.required);
		}

		if (field.pattern) {
			validators.push(Validators.pattern(field.pattern));
		}

		return validators;
	}


	private initCategories(): void {
		this.categoryService.categories.subscribe((data) => {
			this.categories = data;
		});
	}

	private checkFormMode(): void {
		this.isApprovalDialog = this.data?.isApprovalDialog ?? false;
		this.currentMerchantId = this.data?.merchant.id;
	}

}
