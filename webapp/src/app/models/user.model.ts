/**
 * Model for User resource
 *
 * It actually maps the JWT token to that resource, filling those value with the claims in the token
 * (which by now are not all filled, so some property like ROLE has to be manually set)
 *
 * @param(email)        the email of the user
 * @param(accessToken)  the current accessToken for the user
 * @param(role)         the role of the user
 */
export class User {
  id: string;
  accessToken: string;
  roles: string[];

  constructor(
    id: string = null,
    accessToken: string = null,
    roles: string[] = null
  ) {
    this.id = id;
    this.accessToken = accessToken;
    this.roles = roles;
  }

  /**
   * Static generic utility function to return expire date from a token
   *
   * @param(accessToken) the token to be parsed
   */
  static getTokenExpireTime(accessToken: string): number {
    return JSON.parse(atob(accessToken.split('.')[1])).exp;
  }
}
