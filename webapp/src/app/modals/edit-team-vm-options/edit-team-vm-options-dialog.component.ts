import {Component, Inject, OnDestroy, OnInit} from '@angular/core';
import {AbstractControl, FormBuilder, FormGroup, Validators} from '@angular/forms';
import {Team} from '../../models/team.model';
import {TeamService} from '../../services/team.service';
import {MAT_DIALOG_DATA, MatDialog, MatDialogRef} from '@angular/material/dialog';
import {Subject} from 'rxjs';
import {ToastrService} from 'ngx-toastr';

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
        private toastrService: ToastrService,
        public dialogRef: MatDialogRef<EditTeamVmOptionsDialogComponent>,
        public dialog: MatDialog,
        @Inject(MAT_DIALOG_DATA) public data: any
    ) {}

    ngOnInit() {
        this.form = this.fb.group({
            maxTotalInstances: [this.data.maxTotalInstances, [Validators.min(1), Validators.max(10)]],
            maxActiveInstances: [this.data.maxActiveInstances, [Validators.min(1), Validators.max(10)]],
            maxVCpu: [this.data.maxVCpu, [Validators.min(1), Validators.max(4)]],
            maxRam: [this.data.maxRam, [Validators.min(1), Validators.max(8)]],
            maxDiskStorage: [this.data.maxDiskStorage, [Validators.min(1), Validators.max(10)]]
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

    compareWithCurrentValue(value: number, field: string) {
        const currentField = 'current' + this.capitalizeFirstLetter(field);
        if (value < this.data[currentField]) {
            this.form.get(field).setValue(this.data[currentField]);
            value = this.data[currentField];
            this.toastrService.info(
                `There is a vm that violates this new value`,
                'Ops! Invalid value 😅'
            );
        }
        if (field === 'maxTotalInstances') {
            this.adaptMin(value);
        } else if (field === 'maxActiveInstances') {
            this.adaptMax(value);
        }
    }

    capitalizeFirstLetter(word: string) {
        return word.charAt(0).toUpperCase() + word.slice(1);
    }

    getSliderTickInterval(): number | 'auto' {
        return 'auto';
    }

}
