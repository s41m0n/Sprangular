import { Component, Input } from '@angular/core';
import { Student } from 'src/app/models/student.model';
import { MatTableDataSource } from '@angular/material/table';
import { Team } from 'src/app/models/team.model';

/**
 * TabTeamComponent
 *
 * It represents the view for the team tab
 */
@Component({
  selector: 'app-tab-team',
  templateUrl: './tab-team.component.html',
})
export class TabTeamComponent {
  dataSource = new MatTableDataSource<Student>(); // Table datasource dynamically modified
  colsToDisplay = ['id', 'name', 'surname']; // Columns to be displayed in the table
  name = '';

  @Input() set team(team: Team) {
    this.name = team.name;
    this.dataSource.data = team.members;
  }
}
