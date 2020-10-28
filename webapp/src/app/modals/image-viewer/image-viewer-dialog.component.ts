import { Component, Inject, Input } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';

@Component({
  selector: 'app-image-viewer-dialog',
  templateUrl: './image-viewer-dialog.component.html',
})
export class ImageViewerDialogComponent {
  title = '';
  imageSrc = '';

  constructor(
    public dialogRef: MatDialogRef<ImageViewerDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: any
  ) {
    this.imageSrc = data.imageSrc;
    this.title = data.title;
  }

  onNoClick(): void {
    this.dialogRef.close(this.data);
  }

  save() {
    alert('saved');
  }

  modify() {
    alert('modified');
  }
}
