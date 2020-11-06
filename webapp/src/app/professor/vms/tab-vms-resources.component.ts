import {Component, Input} from '@angular/core';
import {Resource} from '../../models/resource.model';

@Component({
    selector: 'app-tab-professor-team-resources',
    templateUrl: './tab-vms-resources.component.html',
    styleUrls: ['./tab-vms-resources.component.css'],
})
export class TabProfessorVmsTeamResourcesComponent {
    resources = [];

    @Input() set teamResources(rs: Resource[]) {
        rs.forEach(r => r.mappedValue = this.mapTo100(r.value, r.maxValue));
        this.resources = rs;
    }

    mapTo100(value, maxValue) {
        if (value === 0) {
            return 0;
        }
        const result = (value / maxValue) * 100;
        if (result > 100) {
            return 100;
        } else {
            return result;
        }
    }

    color(value) {
        switch (true) {
            case (value < 50):
                return['#78C000', '#C7E596'];
            case (value < 99):
                return['#eec11e', '#e5bf96'];
            default:
                return['#d41b1b', '#e59696'];
        }
    }
}
