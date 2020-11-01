import { Component, Inject, OnInit } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {ToastrService} from 'ngx-toastr';
import {Subject} from 'rxjs';
import {first} from 'rxjs/operators';
import {VmService} from '../../services/vm.service';

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

  editVmSpec() {
    if (this.form.invalid) {
      return;
    }
    const formData = new FormData();
    formData.append('vCpu', this.form.get('vCpu').value);
    formData.append('ram', this.form.get('ram').value);
    formData.append('diskStorage', this.form.get('disk').value);

    this.vmService
        .updateVm(this.data.vmId, formData)
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
      value = this.data[maxField] - this.data[currentField] + this.data[field];
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
