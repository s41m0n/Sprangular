// This file can be replaced during build by using the `fileReplacements` array.
// `ng build --prod` replaces `environment.ts` with `environment.prod.ts`.
// The list of file replacements can be found in `angular.json`.

import { HttpHeaders } from '@angular/common/http';

const BASE_API = '/API';

export const environment = {
  production: false,
  base_api_url: BASE_API,
  base_vms_url: `${BASE_API}/vms`,
  base_courses_url: `${BASE_API}/courses`,
  base_students_url: `${BASE_API}/students`,
  base_professors_url: `${BASE_API}/professors`,
  base_teams_url: `${BASE_API}/teams`,
  base_assignments_url: `${BASE_API}/assignments`,
  base_assignmentSolutions_url: `${BASE_API}/assignmentSolutions`,
  login_url: `${BASE_API}/authentication/login`,
  register_url: `${BASE_API}/authentication/register`,
  confirm_url: `${BASE_API}/authentication/`,
  base_http_headers: {
    headers: new HttpHeaders({ 'Content-Type': 'application/json' }),
  }, // Header to be used in POST/PUT
};

/*
 * For easier debugging in development mode, you can import the following file
 * to ignore zone related error stack frames such as `zone.run`, `zoneDelegate.invokeTask`.
 *
 * This import should be commented out in production mode because it will have a negative impact
 * on performance if an error is thrown.
 */
// import 'zone.js/dist/zone-error';  // Included with Angular CLI.
