import { TestBed } from "@angular/core/testing";
import { CaptchaService } from "./captcha.service";
import { CaptchaStatus } from "../_enums/captcha.enum";
import { take } from "rxjs";

describe('CaptchaService', () => {
    let captchaService: CaptchaService;

    beforeEach(() => {
        TestBed.configureTestingModule({
            providers: [CaptchaService],
        });

        captchaService = TestBed.inject(CaptchaService);
    });

    it('should be created', () => {
        expect(captchaService).toBeTruthy();
    });

    it('should emit captcha status when calling displayCaptchaObservable', (done) => {
        const expectedStatus: CaptchaStatus = CaptchaStatus.CREATED;

        captchaService.displayCaptchaObservable.pipe(take(1)).subscribe((status) => {
            expect(status).toEqual(expectedStatus);
            done();
        });

        captchaService.displayCaptchaSubject.next(expectedStatus);
    });
});