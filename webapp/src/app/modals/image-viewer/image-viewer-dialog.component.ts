import { Component, Inject } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import {DomSanitizer, SafeUrl} from '@angular/platform-browser';

@Component({
  selector: 'app-image-viewer-dialog',
  templateUrl: './image-viewer-dialog.component.html',
})
export class ImageViewerDialogComponent {
  title = '';
  imageSrc: SafeUrl;
  downloadable = false;

  constructor(
    public dialogRef: MatDialogRef<ImageViewerDialogComponent>,
    private sanitizer: DomSanitizer,
    @Inject(MAT_DIALOG_DATA) public data: any
  ) {
    this.imageSrc = this.sanitizer.bypassSecurityTrustUrl(data.imageSrc);
    this.title = data.title;
    this.downloadable = data.downloadable;
  }

  downloadDocument() {
    const a: any = document.createElement('a');
    a.href = this.data.imageSrc;
    a.download = this.data.dl_name + '.png';
    document.body.appendChild(a);
    a.style = 'display: none';
    a.click();
    a.remove();
  }
}
