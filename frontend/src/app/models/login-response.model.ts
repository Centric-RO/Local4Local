
export class LoginResponseDto {
    public expirationDate: Date;
    public role: string;
    public rememberMe: boolean;

    constructor(expirationDate: Date, role: string, rememberMe: boolean) {
        this.expirationDate = expirationDate;
        this.role = role;
        this.rememberMe = rememberMe;
    }

}