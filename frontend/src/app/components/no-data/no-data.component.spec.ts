import { ComponentFixture, TestBed } from '@angular/core/testing';

import { NoDataComponent } from './no-data.component';
import { TranslateModule, TranslateService } from '@ngx-translate/core';

describe('NoDataComponent', () => {
    let component: NoDataComponent;
    let fixture: ComponentFixture<NoDataComponent>;

    beforeEach(async () => {
        await TestBed.configureTestingModule({
            declarations: [NoDataComponent],
            imports: [
                TranslateModule.forRoot()],
            providers: [
                TranslateService
            ],
        })
            .compileComponents();

        fixture = TestBed.createComponent(NoDataComponent);
        component = fixture.componentInstance;
        fixture.detectChanges();
    });

    it('should create', () => {
        expect(component).toBeTruthy();
    });
});
