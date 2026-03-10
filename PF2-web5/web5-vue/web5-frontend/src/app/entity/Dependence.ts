export class Dependence {
  // Target依赖Source，即Source要先于Target发生
  source: string; //被依赖方
  data: string[];  // 依赖数据/设备
  target: string;  // 依赖方
}
