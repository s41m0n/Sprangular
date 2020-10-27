import { Component, Inject, OnInit } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';

@Component({
  selector: 'app-vm-options-modal',
  templateUrl: './vm-options-dialog.component.html',
  styleUrls: ['./vm-options-dialog.component.css'],
})
export class VmOptionsDialogComponent implements OnInit {
  constructor(
    public dialogRef: MatDialogRef<VmOptionsDialogComponent>,
    @Inject(MAT_DIALOG_DATA) public data: any
  ) {}

  ngOnInit(): void {}

  onNoClick(): void {
    this.dialogRef.close(this.data);
  }

  getSliderTickInterval(): number | 'auto' {
    return 'auto';
  }
}
