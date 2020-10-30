import {Component, Inject, OnDestroy, OnInit} from '@angular/core';
import {AbstractControl, FormBuilder, FormGroup, Validators} from '@angular/forms';
import {Team} from '../../models/team.model';
import {TeamService} from '../../services/team.service';
import {MAT_DIALOG_DATA, MatDialog, MatDialogRef} from '@angular/material/dialog';
import {Subject} from 'rxjs';

@Component({
    selector: 'app-edit-team-vm-options-dialog',
    templateUrl: './edit-team-vm-options-dialog.component.html',
    styleUrls: ['./edit-team-vm-options-dialog.component.css'],
})
export class EditTeamVmOptionsDialogComponent implements OnInit, OnDestroy {
    form: FormGroup;
    private destroy$: Subject<boolean> = new Subject<boolean>();

    constructor(
        private fb: FormBuilder,
        private teamService: TeamService,
        public dialogRef: MatDialogRef<EditTeamVmOptionsDialogComponent>,
        public dialog: MatDialog,
        @Inject(MAT_DIALOG_DATA) public data: any
    ) {}

    ngOnInit() {
        console.log(this.data);
        this.form = this.fb.group({
            maxTotalInstances: [this.data.maxInstances, [Validators.min(1), Validators.max(10)]],
            maxActiveInstances: [this.data.maxActive, [Validators.min(1), Validators.max(10)]],
            maxVCpu: [this.data.vCpu, [Validators.min(1), Validators.max(4)]],
            maxRam: [this.data.ram, [Validators.min(1), Validators.max(8)]],
            maxDisk: [this.data.disk, [Validators.min(1), Validators.max(10)]]
        });
    }

    editTeamSpec() {
        if (this.form.invalid) {
            console.log('Invalid form');
        } else {
            console.log('Form valid! :)');
        }
    }

    ngOnDestroy() {
        this.destroy$.next(true);
        this.destroy$.unsubscribe();
    }

    adaptMin(value: number) {
        if (
            this.form.get('maxActiveInstances').value > this.form.get('maxTotalInstances').value
        ) {
            this.form.get('maxActiveInstances').setValue(value);
        }
    }

    adaptMax(value: number) {
        if (
            this.form.get('maxActiveInstances').value > this.form.get('maxTotalInstances').value
        ) {
            this.form.get('maxTotalInstances').setValue(value);
        }
    }

    getSliderTickInterval(): number | 'auto' {
        return 'auto';
    }

}
