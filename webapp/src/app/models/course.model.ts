import { Professor } from './professor.model';

/**
 * Model for Course resource
 *
 * @param(id)   the id of the course
 * @param(name) the name of the course
 * @param(path) the path of the course (remote path)
 */
export class Course {
  acronym: string; // id
  name: string;
  teamMinSize: number;
  teamMaxSize: number;
  enabled: boolean;

  constructor(
    acronym: string,
    name: string,
    teamMinSize: number,
    teamMaxSize: number,
    enabled: boolean
  ) {
    this.acronym = acronym;
    this.name = name;
    this.teamMinSize = teamMinSize;
    this.teamMaxSize = teamMaxSize;
    this.enabled = enabled;
  }
}
