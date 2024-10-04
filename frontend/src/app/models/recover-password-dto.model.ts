export class RecoverPasswordDto {
	public email: string;
	public reCaptchaResponse: string;

	constructor(email: string, reCaptchaResponse: string){
		this.email = email;
		this.reCaptchaResponse = reCaptchaResponse;
	}
}