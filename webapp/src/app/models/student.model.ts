/**
 * Model for Student resource
 *
 * @param(id)        the id of the student
 * @param(email)     the email of the student
 * @param(name)      the name of the student
 * @param(surname)   the surname of the student
 * @param(courseId)  the id of the course the student is enrolled to (0 if none)
 * @param(teamId)    the id of the team the student belongs to (0 if none)
 * @param(team)?     the resolved team object (if any)
 * @param(course)?   the resolved course object (if any)
 */
export class Student {
  id: string;
  email: string;
  name: string;
  surname: string;

  constructor(
    id: string = '',
    email: string = '',
    name: string = '',
    surname: string = '',
  ) {
    this.id = id;
    this.email = email;
    this.name = name;
    this.surname = surname;
  }

  /**
   * Static method to return a user-friendly string representation of the student
   *
   * @param student the student to be print
   */
  static displayFn(student: Student): string {
    return student.surname + ' ' + student.name + ' (' + student.id + ')';
  }
}
