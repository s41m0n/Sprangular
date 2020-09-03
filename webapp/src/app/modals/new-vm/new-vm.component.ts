import {Component, Inject, OnInit} from '@angular/core';
import {MAT_DIALOG_DATA, MatDialogRef} from '@angular/material/dialog';

@Component({
  selector: 'app-new-vm',
  templateUrl: './new-vm.component.html',
  styleUrls: ['./new-vm.component.css']
})
export class NewVmComponent implements OnInit {

  vmName = '';
  team = '';
  vmPath = '';
  vCpu = 1;
  ram = 0.5;
  disk = 2;

  constructor(
      public dialogRef: MatDialogRef<NewVmComponent>,
      @Inject(MAT_DIALOG_DATA) public data: any) { }

  ngOnInit(): void {
  }

  onNoClick(): void {
    this.dialogRef.close({
        vmName: this.vmName,
        vmTeam: this.team,
        vmPath: this.vmPath,
        vmVCpu: this.vCpu,
        vmRam:  this.ram,
        vmDisk: this.disk
    });
  }

  getSliderTickInterval(): number | 'auto' { return 'auto'; }

  enabled(): boolean {
    return [this.vmName, this.team, this.vmPath, this.vCpu, this.ram, this.disk].every(el => el !== undefined && el !== '');
  }
}
