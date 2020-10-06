import { Component } from '@angular/core';
import { VM } from '../../models/vm.model';
import { CourseService } from '../../services/course.service';
import { first } from 'rxjs/operators';
import { VmService } from 'src/app/services/vm.service';

/**
 * VmsContainer
 *
 * It displays the Vms view (WIP)
 */
@Component({
  selector: 'app-tab-professor-vms-cont',
  templateUrl: './tab-vms.container.html'
})
export class TabProfessorVmsContComponent {

  vms: VM[] = [];                             // The current vms

  constructor(private courseService: CourseService, private vmService: VmService) {
    this.refreshVmList();
  }

  refreshVmList() {
    this.courseService.getCourseVMs(this.courseService.currentCourseSubject.value).pipe(first()).subscribe(vms => this.vms = vms);    
  }

  wipeVm(vmId: number) {
    this.vmService.removeVm(vmId).pipe(first()).subscribe(x => this.refreshVmList);
  }

  connect(vmId: number) {
    this.vmService.getInstance(vmId).pipe(first()).subscribe(instance => console.log(instance));
  }
}

