import { Injectable } from '@angular/core';
import { Observable, Subject } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class AppLoaderService {
	public loaderSubject = new Subject<boolean>();
	public loaderStateObservable$: Observable<boolean> = this.loaderSubject.asObservable();

	public loaderShow(value: boolean) {
		this.loaderSubject.next(value);
	}
}

