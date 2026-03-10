import { Node } from './Node';
import { CtrlNode } from './CtrlNode';
import { Line } from './Line';

export class ScenarioGraph {
	title: string;	//?????
	requirement: string; //?????????
	intNodeList: Node[];	//????ÿÿ?
	ctrlNodeList: CtrlNode[];	//????ÿÿ?
	lineList: Line[];	//???ÿÿ?
}