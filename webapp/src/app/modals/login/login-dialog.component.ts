import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup } from '@angular/forms';
import { AuthService } from '../../services/auth.service';
import { first } from 'rxjs/operators';
import { MatDialogRef } from '@angular/material/dialog';

/** LoginDialogComponent
 *
 *  Class to represent a login dialog window
 */
@Component({
  selector: 'app-login-dialog',
  templateUrl: './login-dialog.component.html',
  styleUrls: ['./login-dialog.component.css'],
})
export class LoginDialogComponent implements OnInit {
  form: FormGroup;

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    public dialogRef: MatDialogRef<LoginDialogComponent>
  ) {}

  ngOnInit(): void {
    this.form = this.fb.group({
      id: [''],
      password: [''],
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
    this.authService
      .login(email, password)
      .pipe(first())
      .subscribe((res) => {
        if (res) {
          this.dialogRef.close(true);
        }
      });
  }
}
