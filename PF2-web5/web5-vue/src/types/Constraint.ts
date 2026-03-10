import { Line } from './Line';
import { RequirementPhenomenon } from './RequirementPhenomenon';

export class Constraint extends Line {
  constraint_no: number;
  constraint_name: string;
  constraint_description: string;
  constraint_from: string;
  constraint_to: string;
  constraint_x1: number;
  constraint_y1: number;
  constraint_x2: number;
  constraint_y2: number;
  phenomenonList: RequirementPhenomenon[];

  static newConstraint(no: number, name: string, description: string, from: string, to: string, phe: RequirementPhenomenon[], x1: number, y1: number, x2: number, y2: number) {
    let con = new Constraint();
    con.constraint_no = no;
    con.constraint_name = name;
    con.constraint_description = description;
    con.constraint_from = from;
    con.constraint_to = to;
    con.phenomenonList = phe;
    con.constraint_x1 = x1;
    con.constraint_y1 = y1;
    con.constraint_x2 = x2;
    con.constraint_y2 = y2;
    return con;
  }

  static newConstraintWithOld(old: Constraint, phenomenonList: RequirementPhenomenon[], description: string) {
    let con = new Constraint();
    con.constraint_no = old.constraint_no;
    con.constraint_name = old.constraint_name;
    con.constraint_from = old.constraint_from;
    con.constraint_to = old.constraint_to;
    con.constraint_x1 = old.constraint_x1;
    con.constraint_y1 = old.constraint_y1;
    con.constraint_x2 = old.constraint_x2;
    con.constraint_y2 = old.constraint_y2;
    con.constraint_description = description;
    con.phenomenonList = phenomenonList;
    return con;
  }

  static copyConstraint(old: Constraint) {
    let con = new Constraint();
    con.constraint_no = old.constraint_no;
    con.constraint_name = old.constraint_name;
    con.constraint_from = old.constraint_from;
    con.constraint_to = old.constraint_to;
    con.constraint_x1 = old.constraint_x1;
    con.constraint_y1 = old.constraint_y1;
    con.constraint_x2 = old.constraint_x2;
    con.constraint_y2 = old.constraint_y2;
    con.constraint_description = old.phenomenonList == undefined ? old.constraint_description : Constraint.getDescription1(con.constraint_name, old.phenomenonList);
    con.phenomenonList = old.phenomenonList == undefined ? new Array<RequirementPhenomenon>() : old.phenomenonList;
    return con;
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

  getNo() { return this.constraint_no; }
  setNo(no: number) { this.constraint_no = no; }

  getName() { return this.constraint_name; }
  setName(name: string) { this.constraint_name = name; }

  getDescription() { return this.constraint_description; }
  setDescription(description: string) { this.constraint_description = description; }

  getFrom() { return this.constraint_from; }
}
