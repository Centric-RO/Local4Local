import { Injectable } from '@angular/core';
import { Observable, Subject } from 'rxjs';
import { CaptchaStatus } from '../_enums/captcha.enum';

@Injectable({
	providedIn: 'root'
})
export class CaptchaService {

	public displayCaptchaSubject = new Subject<CaptchaStatus>();

	public get displayCaptchaObservable(): Observable<CaptchaStatus> {
		return this.displayCaptchaSubject.asObservable();
	}
}
