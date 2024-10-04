import { ComponentFixture, TestBed } from '@angular/core/testing';
import { MatSnackBarRef, MAT_SNACK_BAR_DATA } from '@angular/material/snack-bar';
import { CustomSnackbarComponent } from './custom-snackbar.component';
import { SnackbarType } from '../../_enums/snackbar-type.enum';
import { NO_ERRORS_SCHEMA } from '@angular/core';

describe('CustomSnackbarComponent', () => {
  let component: CustomSnackbarComponent;
  let fixture: ComponentFixture<CustomSnackbarComponent>;
  let mockSnackBarRef: jest.Mocked<MatSnackBarRef<CustomSnackbarComponent>>;

  beforeEach(() => {
    mockSnackBarRef = {
      dismiss: jest.fn()
    } as unknown as jest.Mocked<MatSnackBarRef<CustomSnackbarComponent>>;

    TestBed.configureTestingModule({
      declarations: [CustomSnackbarComponent],
      providers: [
        { provide: MatSnackBarRef, useValue: mockSnackBarRef },
        { provide: MAT_SNACK_BAR_DATA, useValue: { message: 'Test message', type: SnackbarType.SUCCESS } }
      ],
      schemas: [NO_ERRORS_SCHEMA]
    }).compileComponents();

    fixture = TestBed.createComponent(CustomSnackbarComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create the component', () => {
    expect(component).toBeTruthy();
  });

  it('should dismiss the snackbar when dismiss method is called', () => {
    component.dismiss();
    expect(mockSnackBarRef.dismiss).toHaveBeenCalled();
  });

  test.each([
    { type: SnackbarType.SUCCESS, expectedIcon: 'check_circle' },
    { type: SnackbarType.INFO, expectedIcon: 'info' },
    { type: SnackbarType.ERROR, expectedIcon: 'warning' },
    { type: SnackbarType.WARNING, expectedIcon: 'error' }
  ])('should return the correct icon for $type', ({ type, expectedIcon }) => {
    const icon = component.getIcon(type);
    expect(icon).toBe(expectedIcon);
  });

  it('should return an empty string for unknown SnackbarType', () => {
    const icon = component.getIcon(-1 as unknown as SnackbarType);
    expect(icon).toBe('');
  });
});
