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
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatButtonModule } from '@angular/material/button';
import { MatInputModule } from '@angular/material/input';
import { ReactiveFormsModule } from '@angular/forms';
import { MatAutocompleteModule } from '@angular/material/autocomplete'; 
import { MatSortModule } from '@angular/material/sort';
import { MatPaginatorModule } from '@angular/material/paginator';
import { TabStudentsComponent } from './professor/students/tab-students.component';
import { TabStudentsContainer } from './professor/students/tab-students.container';
import { TabProfessorAssignmentsComponent} from './professor/assignments/tab-assignments.component';
import { TabProfessorAssignmentsContainer} from './professor/assignments/tab-assignments.container';
import { TabProfessorVmsContainer } from './professor/vms/tab-vms.container';
import { TabProfessorVmsComponent } from './professor/vms/tab-vms.component';
import { AppRoutingModule } from './app-routing-module';
import { HttpClientModule, HTTP_INTERCEPTORS }    from '@angular/common/http';
import { MatDialogModule } from '@angular/material/dialog';
import { LoginDialogComponent } from './login/login-dialog.component';
import { MatCardModule } from '@angular/material/card';
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
import { TabTeamContainer } from './student/team/tab-team.container';
import { TabStudentVmsComponent } from './student/vms/tab-vms.component';
import { TabStudentVmsContainer } from './student/vms/tab-vms.container';
import { TabStudentAssignmentsComponent } from './student/assignments/tab-assignments.component';
import { TabStudentAssignmentsContainer } from './student/assignments/tab-assignments.container';

import { TabAdminProfessorsContainer } from './admin/professors/tab-professors.container';
import { AdminComponent } from './admin/admin.component';
import { TabAdminVmModelsContainer } from './admin/models/tab-models.container';

@NgModule({
  declarations: [
    AppComponent,
    LoginDialogComponent,
    HomeComponent,
    PageNotFoundComponent,
    StudentComponent,
    TabStudentVmsComponent,
    TabStudentVmsContainer,
    TabStudentAssignmentsComponent,
    TabStudentAssignmentsContainer,
    TabTeamComponent,
    TabNoTeamComponent,
    TabTeamContainer,
    ProfessorComponent,
    TabStudentsComponent,
    TabStudentsContainer,
    TabProfessorAssignmentsComponent,
    TabProfessorAssignmentsContainer,
    TabProfessorVmsComponent,
    TabProfessorVmsContainer,
    AdminComponent,
    TabAdminProfessorsContainer,
    TabAdminVmModelsContainer
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
    ReactiveFormsModule,
    MatAutocompleteModule,
    MatSortModule,
    MatPaginatorModule,
    MatDialogModule,
    MatCardModule,
    AppRoutingModule,
    HttpClientModule,
    MatProgressSpinnerModule,
    ToastrModule.forRoot({
      progressBar: true,
      timeOut: 3000,
      preventDuplicates: true
    })
  ],
  entryComponents: [
    LoginDialogComponent
  ],
  providers: [
    { provide: HTTP_INTERCEPTORS, useClass: AuthInterceptor, multi: true },
    { provide: HTTP_INTERCEPTORS, useClass: ErrorInterceptor, multi: true },
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
