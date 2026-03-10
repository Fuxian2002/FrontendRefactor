import { CtrlNode } from "./CtrlNode";
import { Line } from "./Line";
import { Node } from "./Node";

export class ScenarioGraph {
    title: string;
    requirement: string; // Add requirement field
    intNodeList: Node[];
    ctrlNodeList: CtrlNode[];
    lineList: Line[];
}