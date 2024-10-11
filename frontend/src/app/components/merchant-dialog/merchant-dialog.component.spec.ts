import { ComponentFixture, TestBed } from '@angular/core/testing';
import { FormBuilder } from '@angular/forms';
import { MatDialog, MatDialogRef } from '@angular/material/dialog';
import { TranslateModule } from '@ngx-translate/core';
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

const matDialogRefStub = {
    close: jest.fn()
};

const merchantServiceStub = {
    registerMerchant: jest.fn().mockReturnValue(of({}))
};

const matDialogStub = {
    open: jest.fn()
};

describe('MerchantDialogComponent', () => {
    let component: MerchantDialogComponent;
    let fixture: ComponentFixture<MerchantDialogComponent>;
    let esriLocatorService: EsriLocatorService;
    let merchantService: MerchantService;

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
                CategoryService
            ],
            schemas: [NO_ERRORS_SCHEMA],
            imports: [TranslateModule.forRoot(), MatAutocomplete, HttpClientTestingModule],
        }).compileComponents();

        fixture = TestBed.createComponent(MerchantDialogComponent);
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
            ['kvkNumber', 'required'],
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
            [{ companyName: '', kvkNumber: '', category: '', address: '', contactEmail: '', website: '' }, true],
            [{ companyName: 'Valid Company', kvkNumber: '12345678', category: 'Category 1', address: 'Valid Address', contactEmail: 'domain@example.com', website: 'https://valid.url' }, false]
        ])('should mark form as %s when form data is %s', (formValue, expectedValidity) => {
            component.form.setValue(formValue);
            component.registerMerchant();
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
            location: { x: 123.45, y: 678.90 }
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

        jest.spyOn(esriLocatorService, 'findLocationBasedOnSuggestion').mockReturnValue(throwError(() => new Error('Error')));

        component.selectAddress(mockSuggestion);

        expect(component.form.get('address')?.value).toBe('Selected Address');
        expect(component.suggestions).toEqual([]);
        expect(component.selectedLocation).toBeNull();
    });

    describe('isDisabled', () => {
        test.each([
            [{ companyName: '', kvkNumber: '', category: '', address: '', contactEmail: '', website: '' }, null, true],
            [{ companyName: '', kvkNumber: '', category: '', address: '', contactEmail: '', website: '' }, { location: { x: 0, y: 0 } } as any, true],
            [{ companyName: 'Valid Company', kvkNumber: '12345678', category: 'Category 1', address: 'Valid Address', contactEmail: 'domain@example.com', website: 'https://valid.url' }, null, true],
            [{ companyName: 'Valid Company', kvkNumber: '12345678', category: 'Category 1', address: 'Valid Address', contactEmail: 'domain@example.com', website: 'https://valid.url' }, { location: { x: 0, y: 0 } } as any, false]
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

            component.registerMerchant();

            expect(matDialogRefStub.close).toHaveBeenCalledWith(ALREADY_REGISTERED_CODE);
        });
    });
});
