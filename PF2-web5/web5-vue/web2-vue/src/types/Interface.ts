import { Line } from './Line';
import { Phenomenon } from './Phenomenon';

export class Interface extends Line {
  interface_no: number;
  interface_name: string;
  interface_description: string;
  interface_from: string;
  interface_to: string;
  phenomenonList: Phenomenon[];
  interface_x1: number;
  interface_y1: number;
  interface_x2: number;
  interface_y2: number;

  static newInterface(no: number, name: string, description: string, from: string, to: string, phe: Phenomenon[], x1: number, y1: number, x2: number, y2: number) {
    let int = new Interface();
    int.interface_no = no;
    int.interface_name = name;
    int.interface_description = description;
    int.interface_from = from;
    int.interface_to = to;
    int.phenomenonList = phe;
    int.interface_x1 = x1;
    int.interface_y1 = y1;
    int.interface_x2 = x2;
    int.interface_y2 = y2;
    return int;
  }

  static newInterfaceWithOld(old: Interface, phenomenonList: Phenomenon[], description: string) {
    let int = new Interface();
    int.interface_no = old.interface_no;
    int.interface_name = old.interface_name;
    int.interface_description = description;
    int.interface_from = old.interface_from;
    int.interface_to = old.interface_to;
    int.phenomenonList = phenomenonList;
    int.interface_x1 = old.interface_x1;
    int.interface_y1 = old.interface_y1;
    int.interface_x2 = old.interface_x2;
    int.interface_y2 = old.interface_y2;
    return int;
  }

  static getDescription1(name: string, pheList: Phenomenon[]) {
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

  static copyInterface(old: Interface) {
    let int = new Interface();
    int.interface_no = old.interface_no;
    int.interface_name = old.interface_name;
    int.interface_from = old.interface_from;
    int.interface_to = old.interface_to;
    if (old.phenomenonList != undefined) {
      int.phenomenonList = old.phenomenonList;
      int.interface_description = Interface.getDescription1(int.interface_name, old.phenomenonList);
    } else {
      int.phenomenonList = new Array<Phenomenon>();
    }
    int.interface_description = Interface.getDescription1(int.interface_name, int.phenomenonList);
    int.interface_x1 = old.interface_x1;
    int.interface_y1 = old.interface_y1;
    int.interface_x2 = old.interface_x2;
    int.interface_y2 = old.interface_y2;
    return int;
  }

  getNo() { return this.interface_no; }
  setNo(no: number) { this.interface_no = no; }

  getName() { return this.interface_name; }
  setName(name: string) { this.interface_name = name; }

  getDescription() { return this.interface_description; }
}
