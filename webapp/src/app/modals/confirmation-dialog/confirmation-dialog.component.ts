import {MatDialogRef} from '@angular/material/dialog';
import {Component} from '@angular/core';

@Component({
  selector: 'app-confirmation-dialog',
  templateUrl: './confirmation-dialog.component.html',
  styleUrls: ['./confirmation-dialog.component.css']
})
export class ConfirmationDialogComponent {
  confirmMessage: string;

  constructor(public dialogRef: MatDialogRef<ConfirmationDialogComponent>) {
  }
}
