import { Component } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

/** HomeComponent 
 * 
 *  It shows the user a basic homepage
 */
@Component({
  selector: 'app-home',
  templateUrl: './home.component.html'
})
export class HomeComponent {
  path: string;
  constructor(private route: ActivatedRoute) {}
}