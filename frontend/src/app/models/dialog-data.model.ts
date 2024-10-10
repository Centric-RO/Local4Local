export class ModalData {
    public title: string;
    public mainContent: string;
    public secondaryContent: string;
    public cancelButtonText: string;
    public acceptButtonText: string;
    public disableClosing: boolean;
    public imageName: string;
    public shouldDisplayActionButton: boolean;

    constructor(title: string, mainContent: string, secondaryContent: string, cancelButtonText: string, acceptButtonText: string, disableClosing: boolean, imageName: string, shouldDisplayActionButton: boolean) {
        this.title = title;
        this.mainContent = mainContent;
        this.secondaryContent = secondaryContent;
        this.cancelButtonText = cancelButtonText;
        this.acceptButtonText = acceptButtonText;
        this.disableClosing = disableClosing;
        this.imageName = imageName;
        this.shouldDisplayActionButton = shouldDisplayActionButton;
    }
}