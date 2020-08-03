package com.yc.bean;

import java.io.Serializable;

public class Resorder implements Serializable {
	private static final long serialVersionUID = -2016543110735258001L;
	private int roid;
	private int userid;
	private String address;
	private String tel;
	private String ordertime;
	private String deliverytime;
	private String ps;
	private int status;

	public int getRoid() {
		return roid;
	}

	public void setRoid(int roid) {
		this.roid = roid;
	}



	public int getUserid() {
		return userid;
	}

	public void setUserid(int userid) {
		this.userid = userid;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getTel() {
		return tel;
	}

	public void setTel(String tel) {
		this.tel = tel;
	}

	public String getOrdertime() {
		return ordertime;
	}

	public void setOrdertime(String ordertime) {
		this.ordertime = ordertime;
	}

	public String getDeliverytime() {
		return deliverytime;
	}

	public void setDeliverytime(String deliverytime) {
		this.deliverytime = deliverytime;
	}

	public String getPs() {
		return ps;
	}

	public void setPs(String ps) {
		this.ps = ps;
	}

	public int getStatus() {
		return status;
	}

	public void setStatus(int status) {
		this.status = status;
	}

	@Override
	public String toString() {
		return "Resorder [roid=" + roid + ", userid=" + userid + ", address=" + address + ", tel=" + tel
				+ ", ordertime=" + ordertime + ", deliverytime=" + deliverytime + ", ps=" + ps + ", status=" + status
				+ "]";
	}

}
