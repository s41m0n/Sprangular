import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { StudentsContainer } from './professor/students/students.container';
import { PageNotFoundComponent } from './fallback/page-not-found.component';
import { HomeComponent } from './home/home.component';
import { VmsContainer } from './professor/vms/vms.container';
import { AuthGuard } from './helpers/auth.guard';
import { Role } from './models/role.model';
import { ProfessorComponent } from './professor/professor.component';
import { AssignmentsContainer } from './professor/assignments/assignments.container';

//Supported and admitted routes by now
//Could do multiple and sub-forRoot, but since we have few routes there's no need now
const routes = [
  { path: '',   component: HomeComponent },
  { 
    path:'professor/courses/:coursename',
    component: ProfessorComponent,
    canActivate: [AuthGuard],
    data: { roles: [Role.Admin, Role.Professor]},
    children: [
      { path:'students', component: StudentsContainer},
      { path:'vms', component:  VmsContainer},
      { path:'assignments', component: AssignmentsContainer}
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