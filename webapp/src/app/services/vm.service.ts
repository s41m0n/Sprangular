import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { HttpClient } from '@angular/common/http';
import { ToastrService } from 'ngx-toastr';
import { catchError, tap } from 'rxjs/operators';
import { environment } from 'src/environments/environment';
import { VM } from '../models/vm.model';
import { TeamService } from './team.service';
import { handleError } from '../helpers/handle.error';
import {VmStudentDetails} from '../models/vm-student-details.model';

/** Vm service
 *
 *  This service is responsible of all the interaction with vms resources through Rest api.
 */
@Injectable({
    providedIn: 'root',
})
export class VmService {
    constructor(
        private http: HttpClient,
        private toastrService: ToastrService,
        private teamService: TeamService
    ) {}

    /**
     *
     * @param vmDTO The vm to create
     * @param teamId The team id
     */
    public createVmForTeam(
        vmDTO: VM,
        teamId: number = this.teamService.currentTeamSubject.value.id
    ): Observable<VM> {
        return this.http
            .post<VM>(
                `${environment.base_teams_url}/${teamId}/vms`,
                vmDTO,
                environment.base_http_headers
            )
            .pipe(
                tap((x) =>
                    this.toastrService.success(
                        `Created VM ${x.id} for team ${teamId}`,
                        'Congratulations 😃'
                    )
                ),
                catchError(
                    handleError<VM>(
                        this.toastrService,
                        `createVmForTeam(${teamId})`,
                        null
                    )
                )
            );
    }

    /**
     *
     * @param teamId The team id
     */
    public getTeamVms(
        teamId: number = this.teamService.currentTeamSubject.value.id
    ): Observable<VmStudentDetails[]> {
        return this.http
            .get<VmStudentDetails[]>(`${environment.base_teams_url}/${teamId}/vms`)
            .pipe(
                tap(() => console.log(`fetched team ${teamId} vms - getTeamVms()`)),
                catchError(
                    handleError<VmStudentDetails[]>(this.toastrService, `getTeamVms(${teamId}`)
                )
            );
    }

    /**
     *
     * @param vmId The vm id
     * @param vmActive the actual status
     */
    public triggerVm(vmId: number, vmActive: boolean): Observable<VM> {
        // If the vm is active, request the turn off, request the turn on otherwise
        const active = vmActive ? 'false' : 'true';
        return this.http
            .put<VM>(
                `${environment.base_vms_url}/${vmId}/trigger`,
                { active },
                environment.base_http_headers
            )
            .pipe(
                tap(() => console.log(`triggered vm ${vmId} - triggerVm()`)),
                catchError(handleError<VM>(this.toastrService, `triggerVm(${vmId})`))
            );
    }

    /**
     *
     * @param vmId The vm id
     * @param studentId The student id
     * @param owner True if it is owner
     */
    public editOwner(vmId: number, studentId: string, owner: boolean): Observable<VM> {
        return this.http
            .post<VM>(
                `${environment.base_vms_url}/${vmId}/editOwner`,
                { studentId },
                environment.base_http_headers
            )
            .pipe(
                tap(() => {
                    if (owner) {
                        this.toastrService.info(
                            `Correctly remove ${studentId} as owner`,
                            'Congratulations 😃'
                        );
                    } else {
                        this.toastrService.success(
                            `Correctly add ${studentId} as owner`,
                            'Congratulations 😃'
                        );
                    }
                }),
                catchError(
                    handleError<VM>(this.toastrService, `addOwner(${vmId}, ${studentId})`)
                )
            );
    }

    /**
     *
     * @param vmId The vm id
     */
    public getInstance(vmId: number): Observable<any> {
        return this.http
            .get(`${environment.base_vms_url}/${vmId}/instance`, {
                responseType: 'blob',
            })
            .pipe(
                tap(() =>
                    console.log(`returned instance for vm ${vmId} - getInstance()`)
                ),
                catchError(handleError<any>(this.toastrService, `getInstance(${vmId})`))
            );
    }

    /**
     *
     * @param vmId The vm id
     */
    public removeVm(vmId: number) {
        return this.http.delete(`${environment.base_vms_url}/${vmId}`).pipe(
            tap(() => console.log(`removed vm ${vmId} - removeVm()`)),
            catchError(handleError(this.toastrService, `removeVm(${vmId})`))
        );
    }

    /**
     *
     * @param vmId The vm id
     * @param vcpu The vcpu requested value
     * @param ram The ram requested value
     * @param diskStorage The disk storage requested value
     */
    public updateVm(vmId: number, vcpu: string, ram: string, diskStorage: string) {
        return this.http
            .put<VM>(
                `${environment.base_vms_url}/${vmId}`,
                {vcpu, ram, diskStorage},
                environment.base_http_headers
            ).pipe(
                tap((vm: VM) => {
                    this.toastrService.success(
                        `Vm ${vm.name} successfully updated!`,
                        'Awesome 😃'
                    );
                }),
                catchError(
                    handleError<VM>(
                        this.toastrService,
                        `updateVm(${vmId})`
                    )
                )
            );
    }
}
