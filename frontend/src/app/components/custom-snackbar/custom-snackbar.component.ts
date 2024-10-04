import { Component, inject, Inject } from '@angular/core';
import { MAT_SNACK_BAR_DATA, MatSnackBarRef } from '@angular/material/snack-bar';
import { SnackbarType } from '../../_enums/snackbar-type.enum';
import { SnackbarData } from '../../models/snackbar-data.model';

@Component({
  selector: 'app-custom-snackbar',
  templateUrl: './custom-snackbar.component.html',
  styleUrl: './custom-snackbar.component.scss'
})
export class CustomSnackbarComponent {

  public snackBarRef = inject(MatSnackBarRef<CustomSnackbarComponent>);
  constructor(
    @Inject(MAT_SNACK_BAR_DATA) public data: SnackbarData
  ) { }

  public dismiss(): void {
    this.snackBarRef.dismiss();
  }

  public getIcon(type: SnackbarType): string {
    switch (type) {
      case SnackbarType.SUCCESS:
        return 'check_circle';
      case SnackbarType.INFO:
        return 'info';
      case SnackbarType.ERROR:
        return 'warning';
      case SnackbarType.WARNING:
        return 'error';
      default:
        return '';
    }
  }
}
