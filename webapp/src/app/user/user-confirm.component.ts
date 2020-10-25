import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { AuthService } from '../services/auth.service';
import { first } from 'rxjs/operators';

@Component({
  selector: 'app-user-confirm',
  templateUrl: './user-confirm.component.html',
})
export class UserConfirmComponent implements OnInit {
  constructor(
    private route: ActivatedRoute,
    private authService: AuthService,
    private router: Router
  ) {}
  id: string;
  token: string;

  ngOnInit(): void {
    this.id = this.route.snapshot.paramMap.get('userId');
    this.token = this.route.snapshot.paramMap.get('confirmToken');
    this.authService
      .confirmEmail(this.id, this.token)
      .pipe(first())
      .subscribe(() => {
        this.router.navigate(['/home']);
      });
  }
}
