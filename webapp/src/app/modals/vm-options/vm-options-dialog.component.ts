import { Component, Inject, OnInit } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {ToastrService} from 'ngx-toastr';
import {first} from 'rxjs/operators';
import {VmService} from '../../services/vm.service';

@Component({
  selector: 'app-vm-options',
  templateUrl: './vm-options-dialog.component.html',
  styleUrls: ['./vm-options-dialog.component.css'],
})
export class VmOptionsDialogComponent implements OnInit {
  form: FormGroup;

  constructor(
      private fb: FormBuilder,
      private toastrService: ToastrService,
      private vmService: VmService,
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

  onSubmit() {
    if (this.form.invalid) {
      return;
    }

    this.vmService
        .updateVm(this.data.vmId, this.form.get('vCpu').value, this.form.get('ram').value, this.form.get('disk').value)
        .pipe(first())
        .subscribe((res) => {
          if (res) {
            this.dialogRef.close(res);
          }
        });
  }

  compareWithCurrentValue(value: number, field: string) {
    const currentField = 'current' + this.capitalizeFirstLetter(field);
    const maxField     = 'max' + this.capitalizeFirstLetter(field);
    if (value + this.data[currentField] - this.data[field] > this.data[maxField]) {
      this.form.get(field).setValue(this.data[maxField] - this.data[currentField] + this.data[field]);
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
