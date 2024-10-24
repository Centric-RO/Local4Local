export class InvitationDto {
    public email: string;
    public sendingDate: Date;

    constructor(email: string, sendingDate: Date) {
        this.email = email;
        this.sendingDate = sendingDate;
    }
}