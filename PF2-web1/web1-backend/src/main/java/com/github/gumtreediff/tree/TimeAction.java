package com.github.gumtreediff.tree;

import com.github.gumtreediff.actions.model.Action;;

public class TimeAction {
	private long time;
	private Action action;

	public TimeAction(long time, Action action) {
		super();
		this.time = time;
		this.action = action;
	}

	public long getTime() {
		return time;
	}

	public void setTime(long time) {
		this.time = time;
	}

	public Action getAction() {
		return action;
	}

	public void setAction(Action action) {
		this.action = action;
	}

}