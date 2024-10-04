export class ChangePasswordDto {
    public token: string;
    public password: string;

    constructor(token: string, password: string) {
        this.token = token;
        this.password = password;
    }
}