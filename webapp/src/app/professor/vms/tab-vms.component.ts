import {
  AfterViewInit,
  Component,
  EventEmitter,
  Input,
  Output,
} from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import {VmProfessorDetails} from '../../models/vm-professor-details.model';
import {Resource} from '../../models/resource.model';

/**
 * VmsComponent
 *
 * It represents the view for the VMs tab
 */
@Component({
  selector: 'app-tab-professor-vms',
  templateUrl: './tab-vms.component.html',
  styleUrls: ['./tab-vms.component.css'],
})
export class TabProfessorVmsComponent implements AfterViewInit {
  dataSources: VmProfessorDetails[];

  @Input() set vms(vms: VmProfessorDetails[]) {
    vms.forEach(vm => vm.resources = this.availableTeamResources(vm));
    this.dataSources = vms;
  }
  @Output() triggerVmEvent = new EventEmitter<{teamId: number, vmId: number}>();
  @Output() refreshVmList = new EventEmitter();

  constructor(public dialog: MatDialog) {}

  ngAfterViewInit() {}

  triggerVm(teamId: number, vmId: number) {
    this.triggerVmEvent.emit({teamId, vmId});
  }

  availableTeamResources(vpd: VmProfessorDetails) {
    return [
      new Resource(
          '#VMs',
          vpd.team.maxTotalInstances,
          vpd.vms ? vpd.vms.length : 0
      ),
      new Resource(
          '#Actives',
          vpd.team.maxActiveInstances,
          vpd.vms ? vpd.vms.filter((vm) => vm.active).length : 0
      ),
      new Resource(
          'VCpus',
          vpd.team.maxVCpu,
          vpd.vms ? vpd.vms.map(vm => vm.vcpu).reduce((acc, val) => acc + val, 0) : 0
      ),
      new Resource(
          'Ram',
          vpd.team.maxRam,
          vpd.vms ? vpd.vms.map(vm => vm.ram).reduce((acc, val) => acc + val, 0) : 0
      ),
      new Resource(
          'DiskGB',
          vpd.team.maxDiskStorage,
          vpd.vms ? vpd.vms.map(vm => vm.diskStorage).reduce((acc, val) => acc + val, 0) : 0
      )
    ];
  }
}
