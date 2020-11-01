import { Component, OnInit } from '@angular/core';
import { VM } from '../../models/vm.model';
import { first, takeUntil } from 'rxjs/operators';
import { TeamService } from 'src/app/services/team.service';
import { MatDialog } from '@angular/material/dialog';
import { NewVmDialogComponent } from 'src/app/modals/new-vm/new-vm.component';
import { VmService } from 'src/app/services/vm.service';
import { Subject } from 'rxjs';
import { ImageViewerDialogComponent } from '../../modals/image-viewer/image-viewer-dialog.component';
import { DomSanitizer } from '@angular/platform-browser';

/**
 * VmsContainer
 *
 * It displays the Vms view (WIP)
 */
@Component({
    selector: 'app-tab-student-vms-cont',
    templateUrl: './tab-vms.container.html',
    styleUrls: ['./tab-vms.container.css'],
})
export class TabStudentVmsContComponent implements OnInit {
    vms: VM[] = null; // The current vms
    private destroy$: Subject<boolean> = new Subject<boolean>();
    inTeam$: boolean;
    team;

    constructor(
        public dialog: MatDialog,
        private teamService: TeamService,
        private vmService: VmService,
        private sanitizer: DomSanitizer
    ) {}

    ngOnInit(): void {
        this.teamService.currentTeamSubject
            .asObservable()
            .pipe(takeUntil(this.destroy$))
            .subscribe((t) => {
                this.inTeam$ = t !== null;
                if (this.inTeam$) {
                    this.refreshVMs();
                }
            });
    }

    /** Private function to refresh the list of vms */
    refreshVMs() {
        // Check if already received the current course
        if (!this.teamService.currentTeamSubject.value) {
            this.vms = null;
            return;
        }
        this.vmService
            .getTeamVms()
            .pipe(first())
            .subscribe((vms) => {
                this.vms = vms;
                if (vms.length > 0) {
                    this.team = vms[0].team;
                }
            });
    }

    turnVm(vmId: number) {
        this.vmService
            .triggerVm(vmId, this.vms.find((vm) => vm.id === vmId).active)
            .pipe(first())
            .subscribe((_) => this.refreshVMs());
    }

    addOwner(object: any) {
        this.vmService
            .addOwner(object.vmId, object.studentId)
            .pipe(first())
            .subscribe((_) => this.refreshVMs());
    }

    connect(vmId: number) {
        this.vmService
            .getInstance(vmId)
            .pipe(first())
            .subscribe((instance) => {
                if (!instance) {
                    return;
                }
                const url = URL.createObjectURL(instance);
                const dialogRef = this.dialog.open(ImageViewerDialogComponent, {
                    data: {
                        id: vmId,
                        imageSrc: this.sanitizer.bypassSecurityTrustUrl(url),
                    },
                });
                dialogRef.afterClosed().subscribe(() => {
                    URL.revokeObjectURL(url);
                });
            });
    }

    newVm() {
        const dialogRef = this.dialog.open(NewVmDialogComponent, {
            data: {
                currentActiveInstances: this.vms.filter((vm) => vm.active).length,
                maxActiveInstances: this.team.maxActiveInstances,
                currentVCpu: this.vms.map(vm => vm.vcpu).reduce((acc, val) => acc + val, 0),
                maxVCpu: this.team.maxVCpu,
                currentRam: this.vms.map(vm => vm.ram).reduce((acc, val) => acc + val, 0),
                maxRam: this.team.maxRam,
                currentDisk: this.vms.map(vm => vm.diskStorage).reduce((acc, val) => acc + val, 0),
                maxDisk: this.team.maxDiskStorage
            }
        });

        dialogRef
            .afterClosed()
            .pipe(first())
            .subscribe((result) => {
                if (result) {
                    this.refreshVMs();
                }
            });
    }

    checkResourceAvailability() {
        if (!this.vms) {
            return true;
        }
        if (this.vms.length >= this.team.maxTotalInstances) {
            return true;
        }

        const currentMaxVCpu = this.vms.map(vm => vm.vcpu).reduce((acc, val) => acc + val, 0);
        const currentMaxRam = this.vms.map(vm => vm.ram).reduce((acc, val) => acc + val, 0);
        const currentMaxDiskStorage = this.vms.map(vm => vm.diskStorage).reduce((acc, val) => acc + val, 0);

        return !(currentMaxVCpu < this.team.maxVCpu &&
            currentMaxRam < this.team.maxRam &&
            currentMaxDiskStorage < this.team.maxDiskStorage);
    }
}
