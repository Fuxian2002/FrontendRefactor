import { Phenomenon } from './Phenomenon';

export class Node {
  node_no: number;
  node_type: string;
  node_x: number;
  node_y: number;
  node_fromList: Node[];
  node_toList: Node[];
  pre_condition: Phenomenon;
  post_condition: Phenomenon;
}
