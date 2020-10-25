import { Component, Inject, OnInit } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';

@Component({
  selector: 'app-vm-options-modal',
  templateUrl: './vm-options-modal.component.html',
  styleUrls: ['./vm-options-modal.component.css'],
})
export class VmOptionsModalComponent implements OnInit {
  constructor(
    public dialogRef: MatDialogRef<VmOptionsModalComponent>,
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
