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

  // TODO - questi campi in più del costruttore ci servono? Nel caso rifattorizzare togliendoli
  constructor(
    acronym: string,
    name: string = '',
    path: string = '',
    professorId: number = 0
  ) {
    this.acronym = acronym;
    this.name = name;
  }
}
