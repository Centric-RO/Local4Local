import { ComponentFixture, TestBed } from '@angular/core/testing';
import { GenericDialogComponent } from './generic-dialog.component';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { TranslateModule } from '@ngx-translate/core';
import { NO_ERRORS_SCHEMA } from '@angular/core';

const matDialogRefStub = {
    close: jest.fn()
};

const matDialogDataStub = {};

describe('GenericDialogComponent', () => {
    let component: GenericDialogComponent;
    let fixture: ComponentFixture<GenericDialogComponent>;
    let dialogRef: MatDialogRef<GenericDialogComponent>;

    beforeEach(async () => {
        await TestBed.configureTestingModule({
            declarations: [GenericDialogComponent],
            providers: [
                { provide: MatDialogRef, useValue: matDialogRefStub },
                { provide: MAT_DIALOG_DATA, useValue: matDialogDataStub }
            ],
            schemas: [NO_ERRORS_SCHEMA],
            imports: [TranslateModule.forRoot()]
        })
            .compileComponents();

        fixture = TestBed.createComponent(GenericDialogComponent);
        component = fixture.componentInstance;
        dialogRef = TestBed.inject(MatDialogRef);
        fixture.detectChanges();
    });

    it('should create', () => {
        expect(component).toBeTruthy();
    });

    it('should call dialogRef.close with false when close() is called', () => {
        component.close();
        expect(dialogRef.close).toHaveBeenCalledWith(false);
    });

    it('should call dialogRef.close with true when accept() is called', () => {
        component.accept();
        expect(dialogRef.close).toHaveBeenCalledWith(true);
    });
});
