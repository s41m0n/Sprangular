import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';

import { AppComponent } from './app.component';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';

import { MatToolbarModule } from '@angular/material/toolbar';
import { MatIconModule } from '@angular/material/icon';
import { MatSidenavModule } from '@angular/material/sidenav';
import { MatTabsModule } from '@angular/material/tabs';
import { MatListModule } from '@angular/material/list';
import { MatTableModule } from '@angular/material/table';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatButtonModule } from '@angular/material/button';
import { MatInputModule } from '@angular/material/input';
import { MaterialFileInputModule } from 'ngx-material-file-input';
import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import { MatAutocompleteModule } from '@angular/material/autocomplete';
import { MatSortModule } from '@angular/material/sort';
import { MatPaginatorModule } from '@angular/material/paginator';
import { TabStudentsComponent } from './professor/students/tab-students.component';
import { TabStudentsContComponent } from './professor/students/tab-students.container';
import { TabProfessorAssignmentsComponent } from './professor/assignments/tab-assignments.component';
import { TabProfessorAssignmentsContComponent } from './professor/assignments/tab-assignments.container';
import { TabProfessorVmsContComponent } from './professor/vms/tab-vms.container';
import { TabProfessorVmsComponent } from './professor/vms/tab-vms.component';
import { AppRoutingModule } from './app-routing-module';
import { HTTP_INTERCEPTORS, HttpClientModule } from '@angular/common/http';
import { MatDialogModule } from '@angular/material/dialog';
import { LoginDialogComponent } from './modals/login/login-dialog.component';
import { MatCardModule } from '@angular/material/card';
import { FlexLayoutModule } from '@angular/flex-layout';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { AuthInterceptor } from './helpers/auth.interceptor';
import { ErrorInterceptor } from './helpers/error.interceptor';
import { ToastrModule } from 'ngx-toastr';
import { ProfessorComponent } from './professor/professor.component';
import { HomeComponent } from './home/home.component';
import { PageNotFoundComponent } from './fallback/page-not-found.component';

import { StudentComponent } from './student/student.component';
import { TabTeamComponent } from './student/team/tab-team.component';
import { TabNoTeamComponent } from './student/team/tab-no-team.component';
import { TabTeamContComponent } from './student/team/tab-team.container';
import { TabStudentVmsComponent } from './student/vms/tab-vms.component';
import { TabStudentVmsContComponent } from './student/vms/tab-vms.container';
import { TabStudentAssignmentsComponent } from './student/assignments/tab-assignments.component';
import { TabStudentAssignmentsContComponent } from './student/assignments/tab-assignments.container';
import { TabExpandendAssignmentComponent } from './student/assignments/tab-expandend-assignment.component';

import { MatMenuModule } from '@angular/material/menu';
import { VmOptionsModalComponent } from './modals/vm-options-modal/vm-options-modal.component';
import { MatSliderModule } from '@angular/material/slider';
import { NewVmComponent } from './modals/new-vm/new-vm.component';
import { MatSelectModule } from '@angular/material/select';
import { NewAssignmentUploadComponent } from './modals/new-assignment-upload/new-assignment-upload.component';

import { RegisterDialogComponent } from './modals/register/register-dialog.component';
import { MatNativeDateModule } from '@angular/material/core';
import { VmOwnerModalComponent } from './modals/vm-owner/vm-owner-modal.component';
import { VmViewerModalComponent } from './modals/vm-viewer/vm-viewer-modal.component';
import { NewCourseComponent } from './modals/new-course/new-course-dialog.component';
import { MatButtonToggleModule } from '@angular/material/button-toggle';
import { UserConfirmComponent } from './user/user-confirm.component';

@NgModule({
  declarations: [
    AppComponent,
    LoginDialogComponent,
    HomeComponent,
    PageNotFoundComponent,
    StudentComponent,
    TabStudentVmsComponent,
    TabStudentVmsContComponent,
    TabStudentAssignmentsComponent,
    TabStudentAssignmentsContComponent,
    TabTeamComponent,
    TabNoTeamComponent,
    TabTeamContComponent,
    ProfessorComponent,
    TabStudentsComponent,
    TabStudentsContComponent,
    TabProfessorAssignmentsComponent,
    TabProfessorAssignmentsContComponent,
    TabProfessorVmsComponent,
    TabProfessorVmsContComponent,
    VmOptionsModalComponent,
    NewVmComponent,
    NewAssignmentUploadComponent,
    RegisterDialogComponent,
    VmOwnerModalComponent,
    VmViewerModalComponent,
    NewCourseComponent,
    TabExpandendAssignmentComponent,
    UserConfirmComponent,
  ],
  imports: [
    BrowserModule,
    BrowserAnimationsModule,
    MatToolbarModule,
    MatIconModule,
    MatSidenavModule,
    MatTabsModule,
    MatListModule,
    MatTableModule,
    MatCheckboxModule,
    MatButtonModule,
    MatInputModule,
    MaterialFileInputModule,
    ReactiveFormsModule,
    MatAutocompleteModule,
    MatSortModule,
    MatPaginatorModule,
    MatDialogModule,
    MatCardModule,
    FlexLayoutModule,
    AppRoutingModule,
    HttpClientModule,
    MatProgressSpinnerModule,
    ToastrModule.forRoot({
      progressBar: true,
      timeOut: 3000,
      preventDuplicates: true,
    }),
    MatMenuModule,
    FormsModule,
    MatSliderModule,
    MatSelectModule,
    MatDatepickerModule,
    MatNativeDateModule,
    MatButtonToggleModule,
  ],
  entryComponents: [LoginDialogComponent, RegisterDialogComponent],
  providers: [
    MatDatepickerModule,
    { provide: HTTP_INTERCEPTORS, useClass: AuthInterceptor, multi: true },
    { provide: HTTP_INTERCEPTORS, useClass: ErrorInterceptor, multi: true },
  ],
  bootstrap: [AppComponent],
})
export class AppModule {}
