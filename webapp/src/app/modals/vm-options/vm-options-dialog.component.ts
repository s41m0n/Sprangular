import { Component, Inject, OnInit } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import {FormBuilder, FormGroup, Validators} from "@angular/forms";
import {ToastrService} from "ngx-toastr";
import {Subject} from "rxjs";

@Component({
  selector: 'app-vm-options',
  templateUrl: './vm-options-dialog.component.html',
  styleUrls: ['./vm-options-dialog.component.css'],
})
export class VmOptionsDialogComponent implements OnInit {
  form: FormGroup;
  private destroy$: Subject<boolean> = new Subject<boolean>();

  constructor(
      private fb: FormBuilder,
      private toastrService: ToastrService,
      public dialogRef: MatDialogRef<VmOptionsDialogComponent>,
      @Inject(MAT_DIALOG_DATA) public data: any
  ) {}

  ngOnInit() {
    this.form = this.fb.group({
      vCpu: [this.data.vCpu, [Validators.min(1), Validators.max(4)]],
      ram: [this.data.ram, [Validators.min(1), Validators.max(8)]],
      disk: [this.data.disk, [Validators.min(1), Validators.max(10)]]
    });
  }

  onNoClick(): void {
    this.dialogRef.close(this.data);
  }

  getSliderTickInterval(): number | 'auto' {
    return 'auto';
  }

  compareWithCurrentValue(value: number, field: string) {
    const currentField = 'max' + this.capitalizeFirstLetter(field);
    if (value > this.data[currentField]) {
      this.form.get(field).setValue(this.data[currentField]);
      value = this.data[currentField];
      this.toastrService.info(
          `It violates team configuration`,
          'Ops! Invalid value 😅'
      );
    }
  }

  capitalizeFirstLetter(word: string) {
    return word.charAt(0).toUpperCase() + word.slice(1);
  }
}
