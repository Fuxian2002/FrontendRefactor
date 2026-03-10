package com.example.demo.LSP.bean;

public class TempMessage {
	int messageId;
	int frameId;
	String frame;

	public TempMessage(int messageId, int frameId, String frame) {
		super();
		this.messageId = messageId;
		this.frameId = frameId;
		this.frame = frame;
	}

	public int getMessageId() {
		return messageId;
	}

	public void setMessageId(int messageId) {
		this.messageId = messageId;
	}

	public int getFrameId() {
		return frameId;
	}

	public void setFrameId(int frameId) {
		this.frameId = frameId;
	}

	public String getFrame() {
		return frame;
	}

	public void setFrame(String frame) {
		this.frame = frame;
	}

}
