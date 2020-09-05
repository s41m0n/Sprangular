import {Component, OnInit} from '@angular/core';
import { MatDialogRef} from '@angular/material/dialog';
import { FormGroup, Validators, FormBuilder } from '@angular/forms';
import { AuthService } from 'src/app/services/auth.service';
import { first } from 'rxjs/operators';

@Component({
  selector: 'app-register-dialog',
  templateUrl: './register-dialog.component.html',
  styleUrls: ['./register-dialog.component.css']
})
export class RegisterDialogComponent implements OnInit {

  public form : FormGroup;
  public registerInvalid : boolean = false;
  
  constructor(private _fb : FormBuilder,
    private _authService : AuthService,
    public dialogRef: MatDialogRef<RegisterDialogComponent>) {}

	ngOnInit(): void {
		this.form = this._fb.group({
			name: ['', Validators.pattern('^[A-Za-z]{1,32}$')],
      surname: ['', Validators.pattern('^[A-Za-z]{1,32}$')],
      email: ['', Validators.email],
      id: ['', Validators.pattern('^(a|s|d)[0-9]+$')],
      pic: [''],
			password: ['', Validators.pattern('^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[a-zA-Z]).{8,15}$')]
		});
	}

  onSubmit() {
    if (this.form.invalid)
      return;
    this._authService.register(this.form.get('email').value, this.form.get('name').value, this.form.get('surname').value, this.form.get('id').value, this.form.get('password').value, this.form.get('pic').value)
      .pipe(first())
      .subscribe(
        () => this.dialogRef.close(true),
        () => this.registerInvalid = true
      );
  }
}
