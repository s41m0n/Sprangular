/**
 * Model for Team resource
 * 
 * @param(id)   the id of the team
 * @param(name) the name of the team
 */
export class Team {
    id: number;
    name: string;

    constructor(id: number, name: string){
        this.id = id;
        this.name = name;
    }
  }