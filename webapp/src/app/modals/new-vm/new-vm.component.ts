import {Component, OnInit} from '@angular/core';
import {MatDialogRef} from '@angular/material/dialog';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {VmService} from 'src/app/services/vm.service';
import {TeamService} from 'src/app/services/team.service';
import {VM} from 'src/app/models/vm.model';
import {first} from 'rxjs/operators';

@Component({
  selector: 'app-new-vm',
  templateUrl: './new-vm.component.html',
  styleUrls: ['./new-vm.component.css']
})
export class NewVmComponent implements OnInit {

  form: FormGroup;
  vmInvalid = false;

  constructor(private fb: FormBuilder,
              private vmService: VmService,
              private teamService: TeamService,
              public dialogRef: MatDialogRef<NewVmComponent>) {
  }

  ngOnInit(): void {
    this.form = this.fb.group({
      name: ['', Validators.pattern('^[A-Za-z0-9]{1,32}$')],
      path: ['', Validators.pattern('^[a-z0-9\-]{1,32}$')],
      cpu: [2, [Validators.min(1), Validators.max(6)]],
      ram: [2, [Validators.min(1), Validators.max(4)]],
      disk: [5, [Validators.min(1), Validators.max(5)]],
    });
  }

  pickle(name: string, value: number): void {
    this.form[name].value = value;
    alert(value);
  }

  onSubmit() {
    if (this.form.invalid) {
      return;
    }
    const vm = new VM(0, this.form.get('name').value, this.form.get('path').value, new Date().toString(), this.form.get('cpu').value,
        this.form.get('ram').value, this.form.get('disk').value);
    delete vm.id;
    this.vmService.createVmForTeam(vm, this.teamService.currentTeamSubject.value)
        .pipe(first())
        .subscribe(
            () => this.dialogRef.close(true),
            () => this.vmInvalid = true
        );
  }
}
