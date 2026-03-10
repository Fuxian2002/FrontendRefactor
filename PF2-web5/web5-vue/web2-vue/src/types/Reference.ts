import { Line } from './Line';
import { RequirementPhenomenon } from './RequirementPhenomenon';

export class Reference extends Line {
  reference_no: number;
  reference_name: string;
  reference_description: string;
  reference_from: string;
  reference_to: string;
  reference_x1: number;
  reference_y1: number;
  reference_x2: number;
  reference_y2: number;
  phenomenonList: RequirementPhenomenon[];

  static newReference(no: number, name: string, description: string, from: string, to: string, phenomenonList: RequirementPhenomenon[], x1: number, y1: number, x2: number, y2: number) {
    let ref = new Reference();
    ref.reference_no = no;
    ref.reference_name = name;
    ref.reference_description = description;
    ref.reference_from = from;
    ref.reference_to = to;
    ref.phenomenonList = phenomenonList;
    ref.reference_x1 = x1;
    ref.reference_y1 = y1;
    ref.reference_x2 = x2;
    ref.reference_y2 = y2;
    return ref;
  }

  static newReferenceWithOld(old: Reference, phenomenonList: RequirementPhenomenon[], description: string) {
    let ref = new Reference();
    ref.reference_no = old.reference_no;
    ref.reference_name = old.reference_name;
    ref.reference_description = description;
    ref.reference_from = old.reference_from;
    ref.reference_to = old.reference_to;
    ref.phenomenonList = phenomenonList;
    ref.reference_x1 = old.reference_x1;
    ref.reference_y1 = old.reference_y1;
    ref.reference_x2 = old.reference_x2;
    ref.reference_y2 = old.reference_y2;
    return ref;
  }

  static getDescription1(name: string, pheList: RequirementPhenomenon[]) {
    // a:M!{on},P!{off}
    let s = "";
    s = s + name + ":";
    let desList: [string, string][] = [];
    for (let phe of pheList) {
      let flag = false;
      for (let des of desList) {
        if (phe.phenomenon_from == des[0]) {
          des.push(phe.phenomenon_name);
          flag = true;
          break;
        }
      }
      if (!flag) {
        desList.push([phe.phenomenon_from, phe.phenomenon_name]);
      }
    }
    for (let des of desList) {
      s += des[0] + "!{";
      for (let item of des.slice(1)) {
        s += item + ",";
      }
      s = s.slice(0, -1);
      s += "},";
    }
    s = s.slice(0, -1);
    return s;
  }

  static copyReference(old: Reference) {
    let ref = new Reference();
    ref.reference_no = old.reference_no;
    ref.reference_name = old.reference_name;
    ref.reference_description = old.phenomenonList == undefined ? old.reference_description :
      Reference.getDescription1(ref.reference_name, old.phenomenonList);
    ref.reference_from = old.reference_from;
    ref.reference_to = old.reference_to;
    ref.phenomenonList = old.phenomenonList == undefined ? new Array<RequirementPhenomenon>() : old.phenomenonList;
    ref.reference_x1 = old.reference_x1;
    ref.reference_y1 = old.reference_y1;
    ref.reference_x2 = old.reference_x2;
    ref.reference_y2 = old.reference_y2;
    return ref;
  }

  getNo() { return this.reference_no; }
  setNo(no: number) { this.reference_no = no; }

  getName() { return this.reference_name; }
  setName(name: string) { this.reference_name = name; }

  getDescription() { return this.reference_description; }
  setDescription(description: string) { this.reference_description = description; }

  getFrom() { return this.reference_from; }
}
