import { Injectable, EventEmitter } from '@angular/core';
import { Router, NavigationEnd } from '@angular/router';
import { filter } from 'rxjs/operators';
import { Course } from '../models/course.model';
import { Subject, BehaviorSubject } from 'rxjs';

/** BroadcasterService service
 * 
 *  This service is responsible of send message in broadcast to all those who subscribed to
 *  a particular event/variable.
 *  It is extremely useful for:
 *    - communicating with ancestors/nephews who have to display information concerning a shared resource
 *    - signalling component of some events (like NotFound) in order to display the correct sub-view
 */
@Injectable({
  providedIn: 'root'
})
export class BroadcasterService {

    private notFoundEvent: EventEmitter<string> = new EventEmitter();         //Not found broadcast event
    private course:BehaviorSubject<Course> = new BehaviorSubject<Course>(undefined);  //Course subject, the actual selected/requested one

    constructor(private router: Router) {
      //Register to router NavigationEnd to restore all NotFound listeners
      this.router.events.pipe(filter(x => x instanceof NavigationEnd)).subscribe(() => this.notFoundEvent.emit(null));
    }
    
    /**
     * Function to "subscribe" to NotFound event emitter
     * 
     * @returns a readonly version of the NotFound event emitter
     */
    subscribeNotFound() : Readonly<EventEmitter<string>>{
      return this.notFoundEvent;
    }

    /**
     * Function to send broacast a NotFound event
     * 
     * @param(what) a string containing the error cause, null if want to restore NotFound listeners
     */
    broadcastNotFound(what :string = null) {
      this.notFoundEvent.emit(what);
    }

    /**
     * Function to "subscribe" to Course subject
     * 
     * @returns a readonly version of the Course subject
     */
    subscribeCourse() : Readonly<Subject<Course>> {
      return this.course;
    }

    /**
     * Function to send broacast a new Course loaded event
     * 
     * @param(course) the Course loaded
     */
    broadcastCourse(course : Course) {
      this.course.next(course);
    }

    /**
     * Function to retrieve the current Course loaded value
     * 
     * @returns the current course value
     */
    getCurrentCourseValue() : Course{
      return this.course.value;
    }
}