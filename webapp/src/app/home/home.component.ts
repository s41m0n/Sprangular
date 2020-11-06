import {Component} from '@angular/core';

/** HomeComponent
 *
 *  It shows the user a basic homepage
 */
@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.css']
})
export class HomeComponent {
  path: string;
}
