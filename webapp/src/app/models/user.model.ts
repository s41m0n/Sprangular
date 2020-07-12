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
    email : string;
    accessToken: string;
    role : string;

    constructor(email: string = null, accessToken: string = null, role: string = null){
        this.email = email;
        this.accessToken = accessToken;
        this.role = role;
    }

    /**
     * Static generic utility function to return expire date from a token
     * 
     * @param(accessToken) the token to be parsed
     */
    static getTokenExpireTime(accessToken : string) : number {
        return JSON.parse(atob(accessToken.split('.')[1])).exp;
    }
  }