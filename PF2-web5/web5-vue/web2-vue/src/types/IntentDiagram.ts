import { ExternalEntity } from './ExternalEntity';
import { Intent } from './Intent';
import { Interface } from './Interface';
import { Reference } from './Reference';
import { Constraint } from './Constraint';
import { ESystem } from './ESystem';

export class IntentDiagram {
  title: string;
  system: ESystem;
  externalEntityList: ExternalEntity[];
  intentList: Intent[];
  interfaceList: Interface[];
  referenceList: Reference[];
  constraintList: Constraint[];
}
