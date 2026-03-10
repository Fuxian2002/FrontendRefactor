export class Machine {
  machine_name: string;
  machine_shortName: string;
  machine_x: number;
  machine_y: number;
  machine_h: number;
  machine_w: number;

  static newMachine(name: string, shortName: string, x: number, y: number, w: number, h: number) {
    let machine = new Machine();
    machine.machine_name = name;
    machine.machine_shortName = shortName;
    machine.machine_x = x;
    machine.machine_y = y;
    machine.machine_h = h;
    machine.machine_w = w;
    return machine;
  }

  static newMachineWithOld(old: Machine, name: string, shortName: string) {
    let machine = new Machine();
    machine.machine_name = name;
    machine.machine_shortName = shortName;
    machine.machine_x = old.machine_x;
    machine.machine_y = old.machine_y;
    machine.machine_h = old.machine_h;
    machine.machine_w = old.machine_w;
    return machine;
  }

  getName() { return this.machine_name; }
  setName(name: string) { this.machine_name = name; }

  getShortName() { return this.machine_shortName; }
  setShortName(shortName: string) { this.machine_shortName = shortName; }

  getX() { return this.machine_x; }
  getY() { return this.machine_y; }
  getH() { return this.machine_h; }
  getW() { return this.machine_w; }

  setX(x: number) { this.machine_x = x; }
  setY(y: number) { this.machine_y = y; }
  setH(h: number) { this.machine_h = h; }
  setW(w: number) { this.machine_w = w; }
}
