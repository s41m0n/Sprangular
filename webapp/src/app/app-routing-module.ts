import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { TabStudentsContainer } from './professor/students/tab-students.container';
import { PageNotFoundComponent } from './fallback/page-not-found.component';
import { HomeComponent } from './home/home.component';
import { TabProfessorVmsContainer } from './professor/vms/tab-vms.container';
import { AuthGuard } from './helpers/auth.guard';
import { Role } from './models/role.model';
import { ProfessorComponent } from './professor/professor.component';
import { TabProfessorAssignmentsContainer } from './professor/assignments/tab-assignments.container';
import { StudentComponent } from './student/student.component';
import { TabTeamContainer } from './student/team/tab-team.container';
import { TabStudentVmsContainer } from './student/vms/tab-vms.container';
import { TabStudentAssignmentsContainer } from './student/assignments/tab-assignments.container';
import { AdminComponent } from './admin/admin.component'; 
import { TabAdminProfessorsContainer } from './admin/professors/tab-professors.container';
import { TabAdminVmModelsContainer } from './admin/models/tab-models.container';

//Supported and admitted routes by now
//Could do multiple and sub-forRoot, but since we have few routes there's no need now
const routes = [
  { path: '',   component: HomeComponent },
  { 
    path:'professor/course/:coursename',
    component: ProfessorComponent,
    canActivate: [AuthGuard],
    data: { roles: [Role.Admin, Role.Professor]},
    children: [
      { path:'', pathMatch:'full', redirectTo:'students'},
      { path:'students', component: TabStudentsContainer},
      { path:'vms', component:  TabProfessorVmsContainer},
      { path:'assignments', component: TabProfessorAssignmentsContainer}
    ]
  },
  { 
    path:'student/course/:coursename',
    component: StudentComponent,
    canActivate: [AuthGuard],
    data: { roles: [Role.Student]},
    children: [
      { path:'', pathMatch:'full', redirectTo:'teams'},
      { path:'teams', component: TabTeamContainer},
      { path:'vms', component:  TabStudentVmsContainer},
      { path:'assignments', component: TabStudentAssignmentsContainer}
    ]
  },
  {
    path:'admin', /* to finish /admin/course/X... */
    component: AdminComponent,
    canActivate: [AuthGuard],
    data: { roles: [Role.Admin]},
    children: [
      { path:'', pathMatch:'full', redirectTo:'professors'},
      { path:'professors', component: TabAdminProfessorsContainer},
      { path:'models', component: TabAdminVmModelsContainer}
    ]
  },
  { path: '**', component: PageNotFoundComponent},
];

export const appRouting = RouterModule.forRoot(routes);
@NgModule({
  imports: [
    RouterModule.forRoot(routes, { enableTracing: false }),
    CommonModule
  ],
  exports: [ RouterModule ],
  declarations: []
})
export class AppRoutingModule { }