import { Component, Input } from '@angular/core';

/** PageNotFoundComponent
 * 
 *  Display a 404-NotFound custom error page.
 *  What => a personalized error message related to a resourse not found (ex /professor/courses/XXXX  whenre XXXX does not exist)
 */
@Component({
  selector: 'app-page-not-found',
  templateUrl: './page-not-found.component.html'
})
export class PageNotFoundComponent {

  @Input() what : string;
}