import {VM} from './vm.model';
import {Student} from './student.model';

export class VmStudentDetails {
  vm: VM;
  owners: Student[];

  constructor(vm: VM,
              owners: Student[]) {
    this.vm = vm;
    this.owners = owners;
  }
}
