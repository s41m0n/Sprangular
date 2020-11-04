import { Component, OnInit } from '@angular/core';
import { MatDialogRef } from '@angular/material/dialog';
import { Validators, FormGroup, FormBuilder } from '@angular/forms';
import { AuthService } from 'src/app/services/auth.service';
import { first } from 'rxjs/operators';
import { FileInput } from 'ngx-material-file-input';

@Component({
  selector: 'app-register-dialog',
  templateUrl: './register-dialog.component.html',
  styleUrls: ['./register-dialog.component.css'],
})
export class RegisterDialogComponent implements OnInit {
  public form: FormGroup;

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    public dialogRef: MatDialogRef<RegisterDialogComponent>
  ) {}

  ngOnInit(): void {
    this.form = this.fb.group(
      {
        name: ['', Validators.pattern('^[A-Za-z]{1,32}$')],
        surname: ['', Validators.pattern('^[A-Za-z,.\'-]{1,32}$')],
        email: ['', Validators.email],
        id: ['', Validators.pattern('^(s|d)[0-9]+$')],
        pic: [''],
        password: [
          '',
          Validators.pattern(
            '^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[a-zA-Z]).{8,}$'
          ),
        ],
        passwordConfirm: [''],
      },
      {
        validator: this.checkIfMatchingPasswords('password', 'passwordConfirm'),
      }
    );
  }

  onSubmit() {
    if (this.form.invalid) {
      return;
    }
    const formData = new FormData();
    formData.append('id', this.form.get('id').value);
    formData.append('email', this.form.get('email').value);
    formData.append('name', this.form.get('name').value);
    formData.append('surname', this.form.get('surname').value);
    formData.append('password', this.form.get('password').value);
    const fileInput: FileInput = this.form.get('pic').value;
    formData.append('photo', fileInput.files[0]);

    this.authService
      .register(formData)
      .pipe(first())
      .subscribe((res) => {
        if (res) {
          this.dialogRef.close(true);
        }
      });
  }

  checkIfMatchingPasswords(
    passwordKey: string,
    passwordConfirmationKey: string
  ) {
    return (group: FormGroup) => {
      const passwordInput = group.controls[passwordKey];
      const passwordConfirmationInput = group.controls[passwordConfirmationKey];
      if (passwordInput.value !== passwordConfirmationInput.value) {
        return passwordConfirmationInput.setErrors({ notEquivalent: true });
      } else {
        return passwordConfirmationInput.setErrors(null);
      }
    };
  }
}
