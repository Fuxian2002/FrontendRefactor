export class Requirement {
  requirement_no: number;
  requirement_context: string;
  requirement_x: number;
  requirement_y: number;
  requirement_h: number;
  requirement_w: number;

  static newRequirement(no: number, name: string, x: number, y: number, h: number, w: number) {
    let req = new Requirement();
    req.requirement_no = no;
    req.requirement_context = name;
    req.requirement_x = x;
    req.requirement_y = y;
    req.requirement_h = h;
    req.requirement_w = w;
    return req;
  }

  static newRequirementWithOld(old: Requirement, name: string) {
    let req = new Requirement();
    req.requirement_no = old.requirement_no;
    req.requirement_context = name;
    req.requirement_x = old.requirement_x;
    req.requirement_y = old.requirement_y;
    req.requirement_h = old.requirement_h;
    req.requirement_w = old.requirement_w;
    return req;
  }

  static copyRequirement(old: Requirement) {
    let req = new Requirement();
    req.requirement_no = old.requirement_no;
    req.requirement_context = old.requirement_context;
    req.requirement_x = old.requirement_x;
    req.requirement_y = old.requirement_y;
    req.requirement_h = old.requirement_h;
    req.requirement_w = old.requirement_w;
    return req;
  }

  getNo() { return this.requirement_no; }
  setNo(no: number) { this.requirement_no = no; }
  getName() { return this.requirement_context; }
  setName(name: string) { this.requirement_context = name; }
  getShortName() { return this.requirement_context; }
  setShortName(shortName: string) { this.requirement_context = shortName; }
  getX() { return this.requirement_x; }
  setX(x: number) { this.requirement_x = x; }
  getY() { return this.requirement_y; }
  setY(y: number) { this.requirement_y = y; }
  getH() { return this.requirement_h; }
  setH(h: number) { this.requirement_h = h; }
  getW() { return this.requirement_w; }
  setW(w: number) { this.requirement_w = w; }
}
