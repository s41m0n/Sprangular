/** Role enum to:
 *    - temporary bind a role to the returned JWT token (Json-Server cannot set personal claims)
 *    - set privileges to routes 
 */
export enum Role {
    Student = 'student',
    Professor = 'professor',
    Admin = 'admin'
  }