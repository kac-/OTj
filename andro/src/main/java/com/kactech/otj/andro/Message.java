package com.kactech.otj.andro;

public class Message {
	Long _id;
	Long date;
	String nym;
	String subject;
	String text;
	Boolean read;

	public Message() {
		// TODO Auto-generated constructor stub
	}

	public Message(Long _id, Long date, String nym, String subject, String text, Boolean read) {
		super();
		this._id = _id;
		this.date = date;
		this.nym = nym;
		this.subject = subject;
		this.text = text;
		this.read = read;
	}

	@Override
	public String toString() {
		return "Message [_id=" + _id + ", date=" + date + ", nym=" + nym + ", subject=" + subject + ", text=" + text
				+ ", read=" + read + "]";
	}

}
