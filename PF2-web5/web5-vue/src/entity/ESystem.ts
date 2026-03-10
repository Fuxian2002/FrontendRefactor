export class ESystem{
	system_name: string;	//��������
	system_shortName: string;	//������д
	system_x: number;	//λ����Ϣ
	system_y: number;
	system_h: number;
	system_w: number;
	static newSystem(name,shortName,x,y,w,h){
		let  system =  new ESystem()
		system.system_name=name
		system.system_shortName=shortName
		system.system_x=x
		system.system_y=y
		system.system_h=h
		system.system_w=w
		return system
	}
	static newSystemWithOld(old:ESystem, name,shortName){
		console.log(old)
		let  system =  new ESystem()
		system.system_name=name
		system.system_shortName=shortName
		system.system_x=old.system_x
		system.system_y=old.system_y
		system.system_h=old.system_h
		system.system_w=old.system_w
		return system
	}
	static copySystem(old:ESystem) {
		if (old == null) return null;
		let  system =  new ESystem()
		system.system_name = old.system_name;
		system.system_shortName = old.system_shortName;
		// system.system_shortName = old.system_shortName == "S" ? "S1" : old.system_shortName; //for text syn,M is a keyword
		system.system_x=old.system_x
		system.system_y=old.system_y
		system.system_h=old.system_h
		system.system_w=old.system_w
		return system
	  }
	getName(){return this.system_name}
	setName(name){this.system_name=name}

	getShortName(){return this.system_shortName}
	setShortName(shortName){this.system_shortName=shortName}
	
	getX(){return this.system_x}
    getY(){return this.system_y}
    getH(){return this.system_h}
	getW(){return this.system_w}
	
    setX(x){this.system_x=x}
    setY(y){this.system_y=y}
    setH(h){this.system_h=h}
	setW(w){this.system_w=w}
}