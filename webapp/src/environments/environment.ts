// This file can be replaced during build by using the `fileReplacements` array.
// `ng build --prod` replaces `environment.ts` with `environment.prod.ts`.
// The list of file replacements can be found in `angular.json`.

import { HttpHeaders } from '@angular/common/http';

let base_api = '/api'

export const environment = {
  production: false,
  base_api_url: base_api,
  base_courses_url: `${base_api}/courses`,
  base_students_url: `${base_api}/students`,
  base_professors_url: `${base_api}/professors`,
  base_teams_url: `${base_api}/teams`,
  base_vms_url: `${base_api}/vms`,
  base_vm_models_url: `${base_api}/vmmodels`,
  base_assignments_url: `${base_api}/assignments`,
  login_url: `${base_api}/login`,
  register_url: `${base_api}/register`,
  base_http_headers: {headers: new HttpHeaders({ 'Content-Type': 'application/json' })} //Header to be used in POST/PUT
};

/*
 * For easier debugging in development mode, you can import the following file
 * to ignore zone related error stack frames such as `zone.run`, `zoneDelegate.invokeTask`.
 *
 * This import should be commented out in production mode because it will have a negative impact
 * on performance if an error is thrown.
 */
// import 'zone.js/dist/zone-error';  // Included with Angular CLI.
