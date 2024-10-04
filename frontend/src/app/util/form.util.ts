import { AbstractControl, FormGroup, ValidationErrors, ValidatorFn } from "@angular/forms";
import { RegexUtil } from "./regex.util";

export class FormUtil {
    public static hasRequiredError(form: FormGroup, fieldName: string): boolean {
        const control = form.get(fieldName);
        return control?.hasError('required') ?? false;
    }

    public static hasPatternError(form: FormGroup, fieldName: string): boolean {
        const control = form.get(fieldName);
        return control?.hasError('pattern') ?? false;
    }

    public static validatePassword(control: AbstractControl) {
        const text = control.value;
        const strongPassword = RegexUtil.passwordRegexPattern;

        return !strongPassword.test(text) ? { validPassword: true } : null;
    }

    public static confirmPasswordValidator: ValidatorFn = (control: AbstractControl): ValidationErrors | null => {
        const password = control.parent?.get('password');
        return password && control.value !== password.value ? { fieldsMismatch: true } : null;
    }

}