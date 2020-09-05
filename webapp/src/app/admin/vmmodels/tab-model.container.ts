import { Component, OnInit, OnDestroy } from '@angular/core';
import { VmModel } from 'src/app/models/vm-model.model';
import { Observable, Subject } from 'rxjs';
import { Course } from 'src/app/models/course.model';
import { takeUntil, switchMap, first, tap } from 'rxjs/operators';
import { CourseService } from 'src/app/services/course.service';
import { VmService } from 'src/app/services/vm.service';

/**
 * TabAdminProfessorContainer
 * 
 * It displays the professors list
 */
@Component({
  selector: 'app-tab-admin-vm-model-cont',
  templateUrl: './tab-model.container.html'
})
export class TabAdminVmModelContainer implements OnInit, OnDestroy{
  model : VmModel = null;
  private course : Course;                                      //The current selected course
  filteredModels : Observable<VmModel[]>;                     //The list of students matching a criteria
  private searchTerms = new Subject<string>();                  //The search criteria emitter
  private destroy$: Subject<boolean> = new Subject<boolean>();  //Private subject to perform the unsubscriptions when the component is destroyed

  constructor(private courseService : CourseService,
    private vmService: VmService) {}

  ngOnInit(): void {
    //Subscribe to current course
    this.courseService.currentCourseSubject.asObservable().pipe(takeUntil(this.destroy$)).subscribe(course => {
      this.course = course;
      if(this.course) this.vmService.getModelForCourse(course).pipe(first()).subscribe(model => this.model = model);
      else this.model = null;
    });
    //Subscribe to the search terms emitter
    this.filteredModels = this.searchTerms.pipe(
      takeUntil(this.destroy$),
      // switch to new search observable each time the term changes
      switchMap((name: string) => this.vmService.searchModels(name)),
    );
  }

  ngOnDestroy() {
    /** Destroying subscription */
    this.destroy$.next(true);
    this.destroy$.unsubscribe();
  }

  public searchModels(name: string): void {
    this.searchTerms.next(name);
  }

  public assignModel(model: VmModel) : void{
    this.vmService.assignVmModelToCourse(model, this.course).pipe(first()).subscribe(model => {
      if(model) this.vmService.getModelForCourse(this.course).pipe(first()).subscribe(model => this.model = model);
    });
  }
}