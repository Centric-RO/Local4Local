import { Component, inject, OnInit } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { FormField } from '../../models/form-field.model';
import { FormBuilder, FormControl, FormGroup, Validators } from '@angular/forms';
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

@Component({
	selector: 'app-merchant-dialog',
	templateUrl: './merchant-dialog.component.html',
	styleUrls: ['./merchant-dialog.component.scss']
})
export class MerchantDialogComponent implements OnInit {
	public hasRequiredError = FormUtil.hasRequiredError;
	public hasPatternError = FormUtil.hasPatternError;

	public isRegistrationDialog = false;
	public formFields: FormField[] = [];
	public form: FormGroup;
	public matcher = new L4LErrorStateMatcher();

	public suggestions: EsriSuggestionResult[] = [];
	public selectedLocation: AddressCandidate | null = null;
	public categories: CategoryDto[] = [];

	private fb = inject(FormBuilder);
	private esriLocatorService = inject(EsriLocatorService);
	private readonly data = inject(MAT_DIALOG_DATA);

	private readonly dialogRef = inject(MatDialogRef<MerchantDialogComponent>);
	private readonly categoryService = inject(CategoryService);
	private readonly merchantService = inject(MerchantService);

	public get isDisabled(): boolean {
		return this.form.invalid || !this.selectedLocation;
	}

	public get hasNoSuggestions(): boolean {
		return this.suggestions.length === 0 && !this.selectedLocation
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

	public registerMerchant(): void {
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

	public onKvkInput(event: Event): void {
		const input = event.target as HTMLInputElement;
		input.value = input.value.replace(/\D/g, '');
	}

	private createMerchantDto(): MerchantDto {
		const { companyName, kvkNumber, category, address, contactEmail, website } = this.form.value;
		const { location } = this.selectedLocation ?? {};

		return {
			companyName,
			kvk: kvkNumber,
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
				isReadOnly: this.isRegistrationDialog,
				requiredMessage: 'register.error.companyNameRequired'
			},
			{
				formControl: 'kvkNumber',
				labelKey: 'table.column.kvkNumber',
				fieldType: 'input',
				required: true,
				maxLength: 8,
				isReadOnly: this.isRegistrationDialog,
				requiredMessage: 'register.error.kvkNumberRequired',
				pattern: RegexUtil.kvkRegexPattern,
				patternMessage: 'register.error.kvkFormControlLength'
			},
			{
				formControl: 'category',
				labelKey: 'table.column.category',
				fieldType: 'select',
				required: true,
				isReadOnly: this.isRegistrationDialog,
				options: this.categories,
				requiredMessage: 'register.error.categoryRequired'
			},
			{
				formControl: 'address',
				labelKey: 'table.column.address',
				fieldType: 'input',
				required: true,
				isReadOnly: this.isRegistrationDialog,
				maxLength: 256,
				requiredMessage: 'register.error.addressRequired'
			},
			{
				formControl: 'contactEmail',
				labelKey: 'register.contactEmail',
				fieldType: 'input',
				required: true,
				isReadOnly: this.isRegistrationDialog,
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
				isReadOnly: this.isRegistrationDialog,
				maxLength: 256,
				pattern: RegexUtil.urlRegexPattern,
				patternMessage: 'register.error.invalidUrl'
			}
		];

	}

	private createForm(): void {
		const controls: Record<string, FormControl> = {};

		const merchantData = this.isRegistrationDialog ? this.data.merchant as MerchantDto : null;

		this.formFields.forEach((field) => {
			const validators = [];
			if (field.required) {
				validators.push(Validators.required);
			}

			if (field.pattern) {
				validators.push(Validators.pattern(field.pattern));
			}

			let formControlValue = null;

			if (merchantData) {
				switch (field.formControl) {
					case 'companyName':
						formControlValue = merchantData.companyName || null;
						break;
					case 'kvkNumber':
						formControlValue = merchantData.kvk || null;
						break;
					case 'category':
						formControlValue = merchantData.category || null;
						break;
					case 'address':
						formControlValue = merchantData.address || null;
						break;
					case 'contactEmail':
						formControlValue = merchantData.contactEmail || null;
						break;
					case 'website':
						formControlValue = merchantData.website || null;
						break;
				}
			}

			controls[field.formControl] = new FormControl(formControlValue, validators);
		});

		this.form = this.fb.group(controls);
	}

	private initCategories(): void {
		this.categoryService.categories.subscribe((data) => {
			this.categories = data;
		});
	}

	private checkFormMode(): void {
		this.isRegistrationDialog = this.data.isRegistrationDialog ?? false;
	}

}
