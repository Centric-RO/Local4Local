import { MatDialogConfig } from '@angular/material/dialog';
import { ModalData } from '../models/dialog-data.model';

export class CustomDialogConfigUtil {
	static MESSAGE_MODAL_CONFIG: MatDialogConfig = {
		width: '600px',
		disableClose: false,
		autoFocus: true,
		data: {
			title: '',
			mainContent: '',
			secondaryContent: '',
			disableClosing: true,
			cancelButtonText: '',
			acceptButtonText: '',
			imageName: '',
			shouldDisplayActionButton: true
		}
	};

	public static createMessageModal(successModal: ModalData): MatDialogConfig {
		const config: MatDialogConfig = structuredClone(this.MESSAGE_MODAL_CONFIG);
		config.data.title = successModal.title;
		config.data.mainContent = successModal.mainContent;
		config.data.secondaryContent = successModal.secondaryContent;
		config.data.cancelButtonText = successModal.cancelButtonText;
		config.data.acceptButtonText = successModal.acceptButtonText;
		config.data.disableClosing = successModal.disableClosing;
		config.data.shouldDisplayActionButton = successModal.shouldDisplayActionButton;
		config.data.imageName = successModal.imageName;
		return config;
	}

}
