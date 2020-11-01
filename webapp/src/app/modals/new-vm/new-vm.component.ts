import {Component, Inject, OnInit} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogRef} from '@angular/material/dialog';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { VmService } from 'src/app/services/vm.service';
import { VM } from 'src/app/models/vm.model';
import { first } from 'rxjs/operators';
import {ToastrService} from 'ngx-toastr';
import {Subject} from 'rxjs';

@Component({
  selector: 'app-new-vm',
  templateUrl: './new-vm.component.html',
  styleUrls: ['./new-vm.component.css'],
})
export class NewVmDialogComponent implements OnInit {
  form: FormGroup;
  private destroy$: Subject<boolean> = new Subject<boolean>();

  constructor(
      private fb: FormBuilder,
      private vmService: VmService,
      private toastrService: ToastrService,
      public dialogRef: MatDialogRef<NewVmDialogComponent>,
      @Inject(MAT_DIALOG_DATA) public data: any
  ) {}

  ngOnInit(): void {
    this.form = this.fb.group({
      name: ['', [Validators.minLength(1), Validators.maxLength(256)]],
      vCpu: [1, [Validators.min(1), Validators.max(4)]],
      ram: [1, [Validators.min(1), Validators.max(8)]],
      disk: [1, [Validators.min(1), Validators.max(10)]],
      active: [false]
    });
  }

  compareWithCurrentValue(value: number, field: string) {
    const currentField = 'current' + this.capitalizeFirstLetter(field);
    const maxField     = 'max' + this.capitalizeFirstLetter(field);
    if (value + this.data[currentField] > this.data[maxField]) {
      this.form.get(field).setValue(this.data[maxField] - this.data[currentField]);
      this.toastrService.info(
          `It violates team configuration`,
          'Ops! Invalid value 😅'
      );
    }
  }

  capitalizeFirstLetter(word: string) {
    return word.charAt(0).toUpperCase() + word.slice(1);
  }

  checkActivation() {
    if (this.data.currentActiveInstances + 1 > this.data.maxActiveInstances) {
      this.toastrService.info(
          `It violates team configuration`,
          'Ops! Invalid value 😅'
      );
      this.form.get('active').setValue(false);
    }
  }

  createNewVm() {
    if (this.form.invalid) {
      return;
    }
    const vm = new VM(
        0,
        this.form.get('name').value,
        this.form.get('vCpu').value,
        this.form.get('ram').value,
        this.form.get('disk').value,
        this.form.get('active').value,
        {},
        []
    );

    console.log(vm);

    this.vmService
        .createVmForTeam(vm)
        .pipe(first())
        .subscribe((res) => {
          if (res) {
            this.dialogRef.close(true);
          }
        });
  }
}
