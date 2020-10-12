import {Component, OnInit} from '@angular/core';
import {MatDialogRef} from '@angular/material/dialog';
import {FormBuilder, FormGroup, Validators} from '@angular/forms';
import {AuthService} from 'src/app/services/auth.service';
import {first} from 'rxjs/operators';
import {FileInput} from 'ngx-material-file-input';

@Component({
  selector: 'app-register-dialog',
  templateUrl: './register-dialog.component.html',
  styleUrls: ['./register-dialog.component.css']
})
export class RegisterDialogComponent implements OnInit {

  public form: FormGroup;
  public registerInvalid = false;

  constructor(private fb: FormBuilder,
              private authService: AuthService,
              public dialogRef: MatDialogRef<RegisterDialogComponent>) {
  }

  ngOnInit(): void {
    this.form = this.fb.group({
      name: ['', Validators.pattern('^[A-Za-z]{1,32}$')],
      surname: ['', Validators.pattern('^[A-Za-z]{1,32}$')],
      email: ['', Validators.email],
      id: ['', Validators.pattern('^(a|s|d)[0-9]+$')],
      pic: [''],
      password: ['', Validators.pattern('^(?=.*\\d)(?=.*[a-z])(?=.*[A-Z])(?=.*[a-zA-Z]).{8,}$')]
    });
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

    this.authService.register(formData)
        .pipe(first())
        .subscribe(
            () => this.dialogRef.close(true),
            () => this.registerInvalid = true
        );
  }
}
