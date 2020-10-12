import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { TabStudentsContComponent } from './professor/students/tab-students.container';
import { PageNotFoundComponent } from './fallback/page-not-found.component';
import { HomeComponent } from './home/home.component';
import { TabProfessorVmsContComponent } from './professor/vms/tab-vms.container';
import { AuthGuard } from './helpers/auth.guard';
import { Role } from './models/role.model';
import { ProfessorComponent } from './professor/professor.component';
import { TabProfessorAssignmentsContComponent } from './professor/assignments/tab-assignments.container';
import { StudentComponent } from './student/student.component';
import { TabTeamContComponent } from './student/team/tab-team.container';
import { TabStudentVmsContComponent } from './student/vms/tab-vms.container';
import { TabStudentAssignmentsContComponent } from './student/assignments/tab-assignments.container';

// Supported and admitted routes by now
// Could do multiple and sub-forRoot, but since we have few routes there's no need now
const routes = [
  { path: 'home', component: HomeComponent },
  {path: '',   redirectTo: '/home', pathMatch: 'full' },
  {
    path: 'professor/course/:coursename',
    component: ProfessorComponent,
    canActivate: [AuthGuard],
    data: { roles: [Role.Admin, Role.Professor] },
    children: [
      { path: '', pathMatch: 'full', redirectTo: 'students' },
      { path: 'students', component: TabStudentsContComponent },
      { path: 'vms', component: TabProfessorVmsContComponent },
      { path: 'assignments', component: TabProfessorAssignmentsContComponent },
    ],
  },
  {
    path: 'student/course/:coursename',
    component: StudentComponent,
    canActivate: [AuthGuard],
    data: { roles: [Role.Student] },
    children: [
      { path: '', pathMatch: 'full', redirectTo: 'teams' },
      { path: 'teams', component: TabTeamContComponent },
      { path: 'vms', component: TabStudentVmsContComponent },
      { path: 'assignments', component: TabStudentAssignmentsContComponent },
    ],
  },
  { path: '**', component: PageNotFoundComponent },
];

export const appRouting = RouterModule.forRoot(routes);

@NgModule({
  imports: [
    RouterModule.forRoot(routes, { enableTracing: false }),
    CommonModule,
  ],
  exports: [RouterModule],
  declarations: [],
})
export class AppRoutingModule {}
