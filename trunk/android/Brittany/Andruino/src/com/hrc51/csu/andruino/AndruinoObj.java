package com.hrc51.csu.andruino;

public class AndruinoObj {
	private int did;
	private int id;
	private String label;
	private int ddr;
	private int pin;
	private int value;
	private String ts_value;
	
	public AndruinoObj(int did, int id, String label, int ddr, int pin, int value, String ts_value)
	{
		this.did = did;
		this.id = id;
		this.label = label;
		this.ddr = ddr;
		this.pin = pin;
		this.value = value;
		this.ts_value = ts_value;
	}

	public int getDid() {
		return did;
	}

	public void setDid(int did) {
		this.did = did;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public int getDdr() {
		return ddr;
	}

	public void setDdr(int ddr) {
		this.ddr = ddr;
	}

	public int getPin() {
		return pin;
	}

	public void setPin(int pin) {
		this.pin = pin;
	}

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}

	public String getTs_value() {
		return ts_value;
	}

	public void setTs_value(String ts_value) {
		this.ts_value = ts_value;
	}
	
}
