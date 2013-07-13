package com.kactech.otj.andro;

import java.io.Serializable;

@SuppressWarnings("serial")
public class Transaction implements Serializable {
	Long _id;
	Long date;
	String account;
	Long amount;

	public Transaction() {
	}

	public Transaction(Long _id, Long date, String account, Long amount) {
		super();
		this._id = _id;
		this.date = date;
		this.account = account;
		this.amount = amount;
	}

	public Long getId() {
		return _id;
	}

	protected void set_id(Long _id) {
		this._id = _id;
	}

	public Long getDate() {
		return date;
	}

	public void setDate(Long date) {
		this.date = date;
	}

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public Long getAmount() {
		return amount;
	}

	public void setAmount(Long amount) {
		this.amount = amount;
	}

	@Override
	public String toString() {
		return "Transaction [_id=" + _id + ", date=" + date + ", account=" + account + ", amount=" + amount + "]";
	}

}
