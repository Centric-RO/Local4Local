import { ComponentFixture, TestBed } from '@angular/core/testing';
import { FormBuilder, Validators } from '@angular/forms';
import { MAT_DIALOG_DATA, MatDialog, MatDialogRef } from '@angular/material/dialog';
import { TranslateModule, TranslateService } from '@ngx-translate/core';
import { NO_ERRORS_SCHEMA } from '@angular/core';
import { EsriSuggestionResult } from '../../models/esri-suggestion-response.model';
import { of, throwError } from 'rxjs';
import { MatAutocomplete } from '@angular/material/autocomplete';
import { EsriLocatorService } from '../../services/esri-locator/esri-locator.service';
import AddressCandidate from '@arcgis/core/rest/support/AddressCandidate';
import { CategoryService } from '../../services/category.service';
import { HttpClientTestingModule } from '@angular/common/http/testing';
import { MerchantService } from '../../services/merchant.service';
import { ALREADY_REGISTERED_CODE } from '../../_constants/error-constants';
import { MerchantDialogComponent } from './merchant-dialog.component';
import { FormField } from '../../models/form-field.model';
import { ColumnType } from '../../enums/column.enum';
import { MerchantDialogType } from '../../enums/merchant-dialog-type.enum';

const matDialogRefStub = {
	close: jest.fn()
};

const merchantServiceStub = {
	registerMerchant: jest.fn().mockReturnValue(of({})),
	approveMerchant: jest.fn().mockReturnValue(of({}))
};

const matDialogStub = {
	open: jest.fn()
};
const matDialogDataStub = {};

describe('MerchantDialogComponent', () => {
	let component: MerchantDialogComponent;
	let fixture: ComponentFixture<MerchantDialogComponent>;
	let esriLocatorService: EsriLocatorService;
	let merchantService: MerchantService;
	let translateService: TranslateService;

	beforeEach(async () => {
		await TestBed.configureTestingModule({
			declarations: [MerchantDialogComponent],
			providers: [
				FormBuilder,
				{ provide: MatDialogRef, useValue: matDialogRefStub },
				{ provide: MatDialog, useValue: matDialogStub },
				{
					provide: EsriLocatorService,
					useValue: {
						findLocationBasedOnSuggestion: jest.fn(),
						getSuggestions: jest.fn()
					}
				},
				{ provide: MerchantService, useValue: merchantServiceStub },
				{ provide: MAT_DIALOG_DATA, useValue: matDialogDataStub },
				CategoryService
			],
			schemas: [NO_ERRORS_SCHEMA],
			imports: [TranslateModule.forRoot(), MatAutocomplete, HttpClientTestingModule]
		}).compileComponents();

		fixture = TestBed.createComponent(MerchantDialogComponent);
		translateService = { instant: jest.fn() } as unknown as TranslateService;
		component = fixture.componentInstance;
		esriLocatorService = TestBed.inject(EsriLocatorService);
		merchantService = TestBed.inject(MerchantService);
		fixture.detectChanges();
	});

	it('should create', () => {
		expect(component).toBeTruthy();
	});

	describe('Form Initialization', () => {
		it('should initialize form fields correctly', () => {
			component.ngOnInit();
			expect(component.formFields).toBeDefined();
			expect(component.formFields.length).toBe(6);
			expect(component.formFields[0].formControl).toBe('companyName');
		});

		test.each([
			['companyName', 'required'],
			['kvk', 'required'],
			['category', 'required'],
			['address', 'required'],
			['contactEmail', 'required'],
			['website', null]
		])('should initialize %s field with %s validator', (controlName, expectedError) => {
			component.ngOnInit();
			const control = component.form.get(controlName);
			expect(control).toBeTruthy();

			if (expectedError) {
				expect(control?.hasError(expectedError)).toBe(true);
			} else {
				expect(control?.errors).toBeNull();
			}
		});
	});

	it('should close the dialog on onNoClick', () => {
		component.closeDialog(ALREADY_REGISTERED_CODE);
		expect(matDialogRefStub.close).toHaveBeenCalled();
	});

	describe('Form Submission', () => {
		test.each([
			[{ companyName: '', kvk: '', category: '', address: '', contactEmail: '', website: '' }, true],
			[
				{
					companyName: 'Valid Company',
					kvk: '12345678',
					category: 'Category 1',
					address: 'Valid Address',
					contactEmail: 'domain@example.com',
					website: 'https://valid.url'
				},
				false
			]
		])('should mark form as %s when form data is %s', (formValue, expectedValidity) => {
			component.form.setValue(formValue);
			component['registerMerchant']();
			expect(component.form.valid).toBe(!expectedValidity);
		});
	});

	describe('onSearchAddress', () => {
		it('should clear suggestions if input length is <= 2', () => {
			const event = { target: { value: 'ab' } } as unknown as Event;

			component.onSearchAddress(event);

			expect(component.suggestions).toEqual([]);
		});

		it('should fetch and set address suggestions on valid input', () => {
			const mockSuggestions: EsriSuggestionResult[] = [
				{ text: 'Suggestion 1', magicKey: 'key' },
				{ text: 'Suggestion 2', magicKey: 'key1' }
			];
			jest.spyOn(esriLocatorService, 'getSuggestions').mockReturnValue(of(mockSuggestions));
			const event = { target: { value: '123' } } as unknown as Event;

			component.onSearchAddress(event);

			expect(esriLocatorService.getSuggestions).toHaveBeenCalledWith('123');
			expect(component.suggestions).toEqual(mockSuggestions);
		});

		it('should handle error during address suggestions fetch', () => {
			jest.spyOn(esriLocatorService, 'getSuggestions').mockReturnValue(throwError(() => new Error('Error')));
			const event = { target: { value: '123' } } as unknown as Event;

			component.onSearchAddress(event);

			expect(esriLocatorService.getSuggestions).toHaveBeenCalledWith('123');
			expect(component.suggestions).toEqual([]);
		});
	});

	it('should update address and fetch location on selectAddress', () => {
		const mockAddressCandidate: AddressCandidate = {
			address: '123 Mock St',
			location: { x: 123.45, y: 678.9 }
		} as unknown as AddressCandidate;

		const mockSuggestion: EsriSuggestionResult = { text: 'Selected Address', magicKey: 'key' };

		jest.spyOn(esriLocatorService, 'findLocationBasedOnSuggestion').mockReturnValue(of([mockAddressCandidate]));

		component.selectAddress(mockSuggestion);

		expect(component.form.get('address')?.value).toBe('Selected Address');
		expect(component.suggestions).toEqual([]);
		expect(component.selectedLocation).toEqual(mockAddressCandidate);
	});

	it('should handle error during location fetch in selectAddress', () => {
		const mockSuggestion: EsriSuggestionResult = { text: 'Selected Address', magicKey: 'key' };

		jest.spyOn(esriLocatorService, 'findLocationBasedOnSuggestion').mockReturnValue(
			throwError(() => new Error('Error'))
		);

		component.selectAddress(mockSuggestion);

		expect(component.form.get('address')?.value).toBe('Selected Address');
		expect(component.suggestions).toEqual([]);
		expect(component.selectedLocation).toBeNull();
	});

	describe('isDisabled', () => {
		test.each([
			[{ companyName: '', kvk: '', category: '', address: '', contactEmail: '', website: '' }, null, true],
			[
				{ companyName: '', kvk: '', category: '', address: '', contactEmail: '', website: '' },
				{ location: { x: 0, y: 0 } } as any,
				true
			],
			[
				{
					companyName: 'Valid Company',
					kvk: '12345678',
					category: 'Category 1',
					address: 'Valid Address',
					contactEmail: 'domain@example.com',
					website: 'https://valid.url'
				},
				null,
				true
			],
			[
				{
					companyName: 'Valid Company',
					kvk: '12345678',
					category: 'Category 1',
					address: 'Valid Address',
					contactEmail: 'domain@example.com',
					website: 'https://valid.url'
				},
				{ location: { x: 0, y: 0 } } as any,
				false
			]
		])(
			'should return %s when form is %p and selectedLocation is %p',
			(formValue, selectedLocation, expectedIsDisabled) => {
				component.form.setValue(formValue);
				component.selectedLocation = selectedLocation;
				expect(component.isDisabled).toBe(expectedIsDisabled);
			}
		);
	});

	it('should remove non-digit characters from KVK number input', () => {
		const inputEvent = new Event('input');
		const inputElement = document.createElement('input');
		inputElement.value = 'abc123def';

		Object.defineProperty(inputEvent, 'target', { value: inputElement });

		component.onKvkInput(inputEvent);

		expect(inputElement.value).toBe('123');
	});

	describe('registerMerchant', () => {
		it('should close dialog with ALREADY_REGISTERED_CODE when registration fails with ALREADY_REGISTERED_CODE', () => {
			const mockError = { error: { message: ALREADY_REGISTERED_CODE } };
			jest.spyOn(merchantService, 'registerMerchant').mockReturnValue(throwError(() => mockError));

			component['registerMerchant']();

			expect(matDialogRefStub.close).toHaveBeenCalledWith(ALREADY_REGISTERED_CODE);
		});
	});

	describe('performAction', () => {
		it('should call approveMerchant if isApprovalDialog is true', () => {
			component.merchantDialogType = MerchantDialogType.APPROVAL;
			const approveMerchantSpy = jest.spyOn(component as any, 'approveMerchant');

			component.performAction();

			expect(approveMerchantSpy).toHaveBeenCalled();
		});

		it('should call registerMerchant if isApprovalDialog is false', () => {
			component.merchantDialogType = MerchantDialogType.REGISTRATION;
			const registerMerchantSpy = jest.spyOn(component as any, 'registerMerchant');

			component.performAction();

			expect(registerMerchantSpy).toHaveBeenCalled();
		});
	});

	describe('handleApprovalDialogValues', () => {
		it('should return "-" if formControl is WEBSITE and value is null', () => {
			const field: FormField = { formControl: ColumnType.WEBSITE } as FormField;
			const result = component['handleApprovalDialogValues'](field, null);

			expect(result).toBe('-');
		});

		it('should return translated value if formControl is CATEGORY and value is not null', () => {
			const field: FormField = { formControl: ColumnType.CATEGORY } as FormField;
			const inputValue = 'someCategory';
			const translatedValue = 'Translated Category';

			jest.spyOn(translateService, 'instant').mockReturnValue(translatedValue);

			const result = component['handleApprovalDialogValues'](field, inputValue);

			expect(result).toBe(inputValue);
		});

		it('should return the value for any other formControl', () => {
			const field: FormField = { formControl: 'someOtherControl' } as FormField;
			const inputValue = 'someValue';

			const result = component['handleApprovalDialogValues'](field, inputValue);

			expect(result).toBe(inputValue);
		});
	});

	it('should return an empty array if isApprovalDialog is true', () => {
		component.merchantDialogType = MerchantDialogType.APPROVAL;

		const formField: FormField = {
			formControl: 'companyName',
			labelKey: 'label.companyName',
			fieldType: 'input',
			required: true,
			isReadOnly: false
		} as FormField;
		const validators = component['getValidators'](formField);

		expect(validators).toEqual([]);
	});

	it('should return required and pattern validators if isApprovalDialog is false and field has required and pattern', () => {
		component.merchantDialogType = MerchantDialogType.REGISTRATION;

		const formField: FormField = {
			formControl: 'kvk',
			labelKey: 'label.kvk',
			fieldType: 'input',
			required: true,
			isReadOnly: false,
			pattern: '\\d+',
			requiredMessage: 'Field is required',
			patternMessage: 'Must be a number'
		} as FormField;
		const validators = component['getValidators'](formField);

		expect(validators.length).toBe(2);
		expect(validators[0]).toBe(Validators.required);
	});

	it('should return only required validator if isApprovalDialog is false and field is required but has no pattern', () => {
		component.merchantDialogType = MerchantDialogType.REGISTRATION;

		const formField: FormField = {
			formControl: 'address',
			labelKey: 'label.address',
			fieldType: 'input',
			required: true,
			isReadOnly: false
		} as FormField;
		const validators = component['getValidators'](formField);

		expect(validators.length).toBe(1);
		expect(validators[0]).toBe(Validators.required);
	});

	it('should return only pattern validator if isApprovalDialog is false and field has pattern but is not required', () => {
		component.merchantDialogType = MerchantDialogType.REGISTRATION;

		const formField: FormField = {
			formControl: 'website',
			labelKey: 'label.website',
			fieldType: 'input',
			required: false,
			isReadOnly: false,
			pattern: 'https?://.+'
		} as FormField;
		const validators = component['getValidators'](formField);

		expect(validators.length).toBe(1);
	});
});
