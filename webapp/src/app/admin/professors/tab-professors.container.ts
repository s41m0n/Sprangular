import { Component, OnInit, OnDestroy } from '@angular/core';
import { Course } from 'src/app/models/course.model';
import { Observable, Subject } from 'rxjs';
import { Professor } from 'src/app/models/professor.model';
import { CourseService } from 'src/app/services/course.service';
import { ProfessorService } from 'src/app/services/professor.service';
import { takeUntil, switchMap, first, finalize } from 'rxjs/operators';

/**
 * TabAdminProfessorContainer
 * 
 * It displays the professors list
 */
@Component({
  selector: 'app-tab-admin-professors-cont',
  templateUrl: './tab-professors.container.html'
})
export class TabAdminProfessorsContainer implements OnInit, OnDestroy{

  private course : Course;                                      //The current selected course
  courseProfessors: Professor[] = [];                             //The current enrolled list
  filteredProfessors : Observable<Professor[]>;                     //The list of students matching a criteria
  private searchTerms = new Subject<string>();                  //The search criteria emitter
  private destroy$: Subject<boolean> = new Subject<boolean>();  //Private subject to perform the unsubscriptions when the component is destroyed

  constructor(private professorService : ProfessorService,
    private courseService: CourseService) {}

  ngOnInit(): void {
    //Subscribe to current course
    this.courseService.currentCourseSubject.asObservable().pipe(takeUntil(this.destroy$)).subscribe(course => {
      this.course = course;
      this.refreshOwners();
    });
    //Subscribe to the search terms emitter
    this.filteredProfessors = this.searchTerms.pipe(
      takeUntil(this.destroy$),
      // switch to new search observable each time the term changes
      switchMap((name: string) => this.professorService.searchProfessors(name)),
    );
  }

  ngOnDestroy() {
    /** Destroying subscription */
    this.destroy$.next(true);
    this.destroy$.unsubscribe();
  }

  
  removeProfessorsFromCourse(professors: Professor[]) {
    this.courseService.removeProfessorsFromCourse(professors, this.course).pipe(first(), finalize(() => this.refreshOwners())).subscribe();
  }

  assignProfessorsToCourse(professors: Professor[]) {
    this.courseService.assignProfessorsToCourse(professors, this.course).pipe(first(),finalize(() => this.refreshOwners())).subscribe();
  }

  /**
   * Function to push new search terms
   *
   * @param(name) the new search terms 
   */
  searchProfessors(name: string): void {
    this.searchTerms.next(name);
  }

  /** Private function to refresh the list of professors for the course*/
  private refreshOwners() {
    //Check if already received the current course
    if(!this.course) {
      this.courseProfessors = [];
      return;
    }
    this.courseService.getCourseProfessors(this.course).pipe(first()).subscribe(professors => this.courseProfessors = professors);
  }

}