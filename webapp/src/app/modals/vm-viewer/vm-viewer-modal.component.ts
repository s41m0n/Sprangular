import {Component, Inject, Input} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogRef} from '@angular/material/dialog';

@Component({
  selector: 'app-vm-options-modal',
  templateUrl: './vm-viewer-modal.component.html'
})
export class VmViewerModalComponent{

  id = '';
  imageSrc = '';
  
  constructor(
      public dialogRef: MatDialogRef<VmViewerModalComponent>,
      @Inject(MAT_DIALOG_DATA) public data: any) {
        this.imageSrc = data.imageSrc;
        this.id = data.id;
  }

  onNoClick(): void {
    this.dialogRef.close(this.data);
  }

  save() {
    alert("saved");
  }

  modify() {
    alert("modified");
  }
}
