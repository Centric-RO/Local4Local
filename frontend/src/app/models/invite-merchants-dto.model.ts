export class InviteMerchantsDto {
    public emails: string[];
    public message: string;

    constructor(emails: string[], message: string) {
        this.emails = emails;
        this.message = message;
    }
}