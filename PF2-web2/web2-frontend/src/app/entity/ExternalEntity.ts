// import { IntentPhenomenon } from "./IntentPhenomenon";
import { Phenomenon } from './Phenomenon';
import { Node } from "./Node";
export class ExternalEntity extends Node {
  externalentity_no: number;
  externalentity_name: string;
  externalentity_shortname: string;
  externalentity_x: number;
  externalentity_y: number;
  externalentity_h: number;
  externalentity_w: number;
  // phes: IntentPhenomenon[];
  phes: Phenomenon[];
  static newExternalEntity(no, name, shortName, x, y, w, h) {
    let pd = new ExternalEntity();
    pd.externalentity_no = no;
    pd.externalentity_name = name;
    pd.externalentity_shortname = shortName;
    pd.externalentity_x = x;
    pd.externalentity_y = y;
    pd.externalentity_h = h;
    pd.externalentity_w = w;
    return pd;
  }

  static newExternalEntityWithOld(old, name, shortName) {
    let pd = new ExternalEntity();
    pd.externalentity_no = old.externalentity_no;
    pd.externalentity_x = old.externalentity_x;
    pd.externalentity_y = old.externalentity_y;
    pd.externalentity_h = old.externalentity_h;
    pd.externalentity_w = old.externalentity_w;

    pd.externalentity_name = name;
    pd.externalentity_shortname = shortName;
    return pd;
  }

  static copyExternalEntity(old) {
    let pd = new ExternalEntity();
    pd.externalentity_no = old.externalentity_no;
    pd.externalentity_x = old.externalentity_x;
    pd.externalentity_y = old.externalentity_y;
    pd.externalentity_h = old.externalentity_h;
    pd.externalentity_w = old.externalentity_w;

    pd.externalentity_name = old.externalentity_name;
    pd.externalentity_shortname = old.externalentity_shortname;
    pd.phes = new Array<Phenomenon>();
    if(old.phes!=null){
      // pd.phes = new Array<Phenomenon>();
      for(let p of old.phes){
        let ph=new Phenomenon();
        ph.phenomenon_no=p.phenomenon_no;
        ph.phenomenon_name=p.phenomenon_name;
        ph.phenomenon_type=p.phenomenon_type;
        ph.phenomenon_from=p.phenomenon_from;
        ph.phenomenon_to=p.phenomenon_to;
        pd.phes.push(ph);
      }
    }
    
    return pd;
  }
  getNo() {
    return this.externalentity_no;
  }
  getName() {
    return this.externalentity_name;
  }
  getShortName() {
    return this.externalentity_shortname;
  }
  getX() {
    return this.externalentity_x;
  }
  getY() {
    return this.externalentity_y;
  }
  getH() {
    return this.externalentity_h;
  }
  getW() {
    return this.externalentity_w;
  }
  setNo(no) {
    this.externalentity_no = no;
  }
  setName(name) {
    this.externalentity_name = name;
  }
  setShortName(shortName) {
    this.externalentity_shortname = shortName;
  }
  setX(x) {
    this.externalentity_x = x;
  }
  setY(y) {
    this.externalentity_y = y;
  }
  setH(h) {
    this.externalentity_h = h;
  }
  setW(w) {
    this.externalentity_w = w;
  }
}
