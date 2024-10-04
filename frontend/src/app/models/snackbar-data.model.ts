import { SnackbarType } from "../_enums/snackbar-type.enum";

export class SnackbarData {
    public message: string;
    public type: SnackbarType;

    constructor(message: string, type: SnackbarType) {
        this.message = message;
        this.type = type;
    }
}