package com.example.demo.bean;

public class RequirementPhenomenon extends Phenomenon {
	private int phenomenon_requirement;
	private String phenomenon_constraint;
	
	public int getPhenomenon_requirement() {
		return phenomenon_requirement;
	}
	public void setPhenomenon_requirement(int phenomenon_requirement) {
		this.phenomenon_requirement = phenomenon_requirement;
	}
	public String getPhenomenon_constraint() {
		return phenomenon_constraint;
	}
	public void setPhenomenon_constraint(String phenomenon_constraint) {
		this.phenomenon_constraint = phenomenon_constraint;
	}

	public boolean equals(Object obj) {
		if(this == obj) return true;
		if(obj == null || obj.getClass() != this.getClass()) {
			return false;
		}
		RequirementPhenomenon phe = (RequirementPhenomenon) obj;
		return phe.getPhenomenon_no() == this.getPhenomenon_no()
				&& phe.getPhenomenon_from().equals(this.getPhenomenon_from())
				&& phe.getPhenomenon_to().equals(this.getPhenomenon_to())
				&& phe.getPhenomenon_type().equals(this.getPhenomenon_type());
	}
	
}
