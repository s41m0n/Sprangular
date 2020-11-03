/** Role enum to:
 *    - temporary bind a role to the returned JWT token (Json-Server cannot set personal claims)
 *    - set privileges to routes
 */
export enum Role {
  Student = 'ROLE_STUDENT',
  Professor = 'ROLE_PROFESSOR',
  Admin = 'ROLE_ADMIN',
}
