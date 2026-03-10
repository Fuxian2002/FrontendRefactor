import {Project} from "./Project";

export class ProjectRecord {
  id: string;  // 项目记录ID，唯一标识一个项目记录
  strategyId: string;  // 使用的解耦策略ID
  project: Project;
}
