export class Intent {
	intent_no: number;
	intent_context: string;
	intent_shortname: string;
	intent_x: number;	
	intent_y: number;
	intent_h: number;
	intent_w: number;
	static newIntent(no,name,shortname,x,y,h,w){
		let req = new Intent()
		req.intent_no=no
		req.intent_context=name
		req.intent_shortname = shortname;
		req.intent_x=x
		req.intent_y=y
		req.intent_h=h
		req.intent_w=w
		return req
	}
	static newIntentWithOld(old,name,shortname){
		let req = new Intent()
		req.intent_no = old.intent_no
		req.intent_context = name
		req.intent_shortname = shortname;
		req.intent_x = old.intent_x
		req.intent_y = old.intent_y
		req.intent_h = old.intent_h
		req.intent_w = old.intent_w
		return req
	}
	static copyIntent(old){
		let req = new Intent()
		req.intent_no = old.intent_no
		req.intent_context = old.intent_context
		req.intent_shortname = old.intent_shortname ? old.intent_shortname : getShortname(old.name);
		req.intent_x = old.intent_x
		req.intent_y = old.intent_y
		req.intent_h = old.intent_h
		req.intent_w = old.intent_w
		return req
	}
	getNo(){return this.intent_no}	
	setNo(no){this.intent_no=no}
	getName(){return this.intent_context}	
    setName(name){this.intent_context=name}
	getShortName(){
		return this.intent_shortname
	}	
    setShortName(shortName){this.intent_shortname=shortName}
	getX(){return this.intent_x}
	setX(x){this.intent_x=x}
    getY(){return this.intent_y}
    setY(y){this.intent_y=y}
    getH(){return this.intent_h}
    setH(h){this.intent_h=h}
	getW(){return this.intent_w}
	setW(w){this.intent_w=w}
}
function getShortname(name: string) {
	return name.replace(/\s+/g, "");
  }