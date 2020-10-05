import { Student } from './student.model';

export class TeamProposal {
  name: string;
  students: Student[];
  deadline: string; // ISO string to parse in Java

  constructor(name: string, students: Student[]) {
    this.name = name;
    this.students = students;
  }
}
