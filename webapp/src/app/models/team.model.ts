import { Student } from './student.model';

/**
 * Model for Team resource
 *
 * @param(id)   the id of the team
 * @param(name) the name of the team
 * @param(courseId)   the id of the course
 */
export class Team {
  id: number;
  name: string;
  members: Student[];
  courseId: number;
  active: boolean;
  maxVCpu: number;
  maxDiskStorage: number;
  maxRam: number;
  maxActiveInstances: number;
  maxTotalInstances: number;

  constructor(
    id: number,
    name: string,
    courseId: number,
    active: boolean,
    maxVCpu: number,
    maxDiskStorage: number,
    maxRam: number,
    maxActiveInstances: number,
    maxTotalInstances: number,
    members: Student[]
  ) {
    this.id = id;
    this.name = name;
    this.courseId = courseId;
    this.active = active;
    this.maxVCpu = maxVCpu;
    this.maxDiskStorage = maxDiskStorage;
    this.maxRam = maxRam;
    this.maxActiveInstances = maxActiveInstances;
    this.maxTotalInstances = maxTotalInstances;
    this.members = members;
  }
}
