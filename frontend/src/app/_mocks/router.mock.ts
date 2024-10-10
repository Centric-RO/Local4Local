import { Injectable } from '@angular/core';

@Injectable()
export class MockRouter {
    // eslint-disable-next-line @typescript-eslint/no-unused-vars
    navigateByUrl(url: string): Promise<boolean> {
        return Promise.resolve(true);
    }

    navigate(): Promise<boolean> {
        return Promise.resolve(true);
    }
}