import { Role } from "../enums/roles.enum";

export class LoginRequestDto {
    public email: string;
    public password: string;
    public rememberMe: boolean;
    public reCaptchaResponse: string;
    public role: Role;
}