import { FormControl, FormGroupDirective } from '@angular/forms';
import { L4LErrorStateMatcher } from './error-state-matcher';

describe('L4LErrorStateMatcher', () => {
    let matcher: L4LErrorStateMatcher;
    let formGroupDirective: FormGroupDirective;

    beforeEach(() => {
        matcher = new L4LErrorStateMatcher();
        formGroupDirective = {
            submitted: false
        } as unknown as FormGroupDirective;
    });

    describe('isErrorState', () => {
        test.each([
            [false, true, false, false, false],
            [false, false, true, false, false],
            [false, false, false, true, false],
            [true, true, true, false, false],
            [false, false, true, true, false],
            [false, false, false, false, false],
            [true, true, true, true, true],
        ])(
            'should return %p for control invalid: %p, dirty: %p, touched: %p, form submitted: %p',
            (expectedErrorState, controlInvalid, controlDirty, controlTouched, formSubmitted) => {
                const control = new FormControl('');
                if (controlInvalid) {
                    control.setErrors({ invalid: true });
                } else {
                    control.setErrors(null);
                }
                if (controlDirty) {
                    control.markAsDirty();
                }
                if (controlTouched) {
                    control.markAsTouched();
                }

                formGroupDirective['submitted'] = formSubmitted;

                expect(matcher.isErrorState(control, formGroupDirective)).toBe(expectedErrorState);
            }
        );
    });
});
