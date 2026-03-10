import { Machine } from './Machine';
import { ProblemDomain } from './ProblemDomain';
import { Requirement } from './Requirement';
import { Interface } from './Interface';
import { Constraint } from './Constraint';
import { Reference } from './Reference';

export class SubProblemDiagram {
  title: string;	// ?????
  machine: Machine;	// ????
  problemDomainList: ProblemDomain[];	// ????????
  requirement: Requirement;	// ????
  interfaceList: Interface[];	// ????????
  constraintList: Constraint[];	// ???????
  referenceList: Reference[];	// ????????
}
