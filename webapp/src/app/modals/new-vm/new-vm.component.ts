import { Component, OnInit } from '@angular/core';
import { MatDialogRef } from '@angular/material/dialog';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { VmService } from 'src/app/services/vm.service';
import { VM } from 'src/app/models/vm.model';
import { first } from 'rxjs/operators';

@Component({
  selector: 'app-new-vm',
  templateUrl: './new-vm.component.html',
  styleUrls: ['./new-vm.component.css'],
})
export class NewVmDialogComponent implements OnInit {
  form: FormGroup;
  vmInvalid = false;

  constructor(
    private fb: FormBuilder,
    private vmService: VmService,
    public dialogRef: MatDialogRef<NewVmDialogComponent>
  ) {}

  ngOnInit(): void {
    this.form = this.fb.group({
      name: ['', Validators.min(1)],
      vCpu: [2, [Validators.min(1), Validators.max(6)]],
      ram: [2, [Validators.min(1), Validators.max(6)]],
      diskStorage: [5, [Validators.min(1), Validators.max(20)]],
    });
  }

  onSubmit() {
    if (this.form.invalid) {
      return;
    }
    const vm = new VM(
      0,
      this.form.get('name').value,
      this.form.get('vCpu').value,
      this.form.get('ram').value,
      this.form.get('diskStorage').value
    );
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
