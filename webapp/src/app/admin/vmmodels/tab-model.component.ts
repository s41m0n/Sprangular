import { Component, OnInit, Input, Output, EventEmitter, OnDestroy } from '@angular/core';
import { FormControl } from '@angular/forms';
import { Observable, Subject } from 'rxjs';
import { debounceTime, distinctUntilChanged, takeUntil, tap} from 'rxjs/operators';

import { MatDialog } from '@angular/material/dialog';
import { VmModel } from 'src/app/models/vm-model.model';

/**
 * TabProfessorsComponent
 * 
 * It represents the view for the admin course
 */
@Component({
  selector: 'app-tab-admin-vm-model-comp',
  templateUrl: './tab-model.component.html'
})
export class TabAdminVmModelComponent implements OnInit, OnDestroy{

  assignVmModelControl = new FormControl();                              //Form control to input the user to be enrolled
  private destroy$: Subject<boolean> = new Subject<boolean>();        //Private subject to perform the unsubscriptions when the component is destroyed
  @Output() searchVmModelEvent = new EventEmitter<string>();         //Event emitter for the search students (autocompletions)
  @Output() assignVmModelEvent = new EventEmitter<VmModel>();         //Event emitter for the search students (autocompletions)
  @Input() filteredVmModel : Observable<VmModel[]>;                  //List of students matching search criteria
  @Input() vmModel : VmModel;

  constructor(public dialog: MatDialog) {}

  ngOnInit() {
    /** Setting filter to autocomplete */
    this.assignVmModelControl.valueChanges
    .pipe(
      takeUntil(this.destroy$),
      // wait 300ms after each keystroke before considering the term
      debounceTime(300),
      // ignore new term if same as previous term
      distinctUntilChanged(),
    ).subscribe((name: string) => this.searchVmModelEvent.emit(name));
  }

  ngOnDestroy() {
    /** Destroying subscription */
    this.destroy$.next(true);
    this.destroy$.unsubscribe();
  }

  assignVmModelToCourse() {
    this.assignVmModelEvent.emit(this.assignVmModelControl.value);
    this.assignVmModelControl.setValue('');
  }

  /** Function to set the value displayed in input and mat-options */
  displayFn(model: VmModel): string{
    return model? VmModel.displayFn(model) : '';
  }
}
