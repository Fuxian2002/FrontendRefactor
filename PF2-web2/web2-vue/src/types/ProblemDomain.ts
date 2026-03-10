import { Phenomenon } from "./Phenomenon";
import { Node } from "./Node";

export class ProblemDomain extends Node {
  problemdomain_no: number;
  problemdomain_name: string;
  problemdomain_shortname: string;
  problemdomain_type: string;
  problemdomain_property: string;
  problemdomain_x: number;
  problemdomain_y: number;
  problemdomain_h: number;
  problemdomain_w: number;
  phes: Phenomenon[];

  static newProblemDomain(no: number, name: string, shortName: string, type: string, property: string, x: number, y: number, w: number, h: number) {
    let pd = new ProblemDomain();
    pd.problemdomain_no = no;
    pd.problemdomain_name = name;
    pd.problemdomain_shortname = shortName;
    pd.problemdomain_type = type;
    pd.problemdomain_property = property;
    pd.problemdomain_x = x;
    pd.problemdomain_y = y;
    pd.problemdomain_h = h;
    pd.problemdomain_w = w;
    return pd;
  }

  static newProblemDomainWithOld(old: ProblemDomain, name: string, shortName: string, type: string, property: string) {
    let pd = new ProblemDomain();
    pd.problemdomain_no = old.problemdomain_no;
    pd.problemdomain_x = old.problemdomain_x;
    pd.problemdomain_y = old.problemdomain_y;
    pd.problemdomain_h = old.problemdomain_h;
    pd.problemdomain_w = old.problemdomain_w;

    pd.problemdomain_name = name;
    pd.problemdomain_shortname = shortName;
    pd.problemdomain_type = type;
    pd.problemdomain_property = property;
    return pd;
  }

  static copyProblemDomain(old: ProblemDomain) {
    let pd = new ProblemDomain();
    pd.problemdomain_no = old.problemdomain_no;
    pd.problemdomain_x = old.problemdomain_x;
    pd.problemdomain_y = old.problemdomain_y;
    pd.problemdomain_h = old.problemdomain_h;
    pd.problemdomain_w = old.problemdomain_w;

    pd.problemdomain_name = old.problemdomain_name;
    pd.problemdomain_shortname = old.problemdomain_shortname;
    pd.problemdomain_type = old.problemdomain_type;
    pd.problemdomain_property = old.problemdomain_property;
    return pd;
  }

  getNo() { return this.problemdomain_no; }
  getName() { return this.problemdomain_name; }
  getShortName() { return this.problemdomain_shortname; }
  getX() { return this.problemdomain_x; }
  getY() { return this.problemdomain_y; }
  getH() { return this.problemdomain_h; }
  getW() { return this.problemdomain_w; }
  getProperty() { return this.problemdomain_property; }
  getType() { return this.problemdomain_type; }

  setNo(no: number) { this.problemdomain_no = no; }
  setName(name: string) { this.problemdomain_name = name; }
  setShortName(shortName: string) { this.problemdomain_shortname = shortName; }
  setX(x: number) { this.problemdomain_x = x; }
  setY(y: number) { this.problemdomain_y = y; }
  setH(h: number) { this.problemdomain_h = h; }
  setW(w: number) { this.problemdomain_w = w; }
}
