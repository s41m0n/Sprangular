import {Component, OnInit} from '@angular/core';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {AuthService} from '../../services/auth.service';
import {first} from 'rxjs/operators';
import {MatDialogRef} from '@angular/material/dialog';

/** LoginDialogComponent
 *
 *  Class to represent a login dialog window
 */
@Component({
  selector: 'app-login-dialog',
  templateUrl: './login-dialog.component.html',
  styleUrls: ['./login-dialog.component.css']
})
export class LoginDialogComponent implements OnInit {
  form: FormGroup;
  loginInvalid = false;                   // Variable to let the forum know there are errors

  constructor(private fb: FormBuilder,
              private authService: AuthService,
              public dialogRef: MatDialogRef<LoginDialogComponent>) {
  }

  /** Create a Form with email-password input and validators.
   *  The password must:
   *    - contain at least 1 lowercase
   *    - contain at least 1 uppercase
   *    - contain at least 1 digit
   *    - be within 8-15 characters
   */
  ngOnInit(): void {
    this.form = this.fb.group({
      id: [''],
      password: ['']
    });
  }

  /** Function to submit the form.
   *
   *  If the form is valid, then it performs authentication.
   *  In case authentication succeeded, close the window with a valid return value,
   *  otherwise the form will display the errors
   */
  onSubmit() {
    if (this.form.invalid) {
      return;
    }
    const email = this.form.get('id').value;
    const password = this.form.get('password').value;
    this.authService.login(email, password)
        .pipe(first())
        .subscribe(
            () => this.dialogRef.close(true),
            () => this.loginInvalid = true
        );
  }
}
