package com.esotericsoftware.kryo.util;

public class FlagAndInt {
	private final int intValue;
	private final boolean flag;
	
	public FlagAndInt (int intValue, boolean flag) {
		this.intValue = intValue;
		this.flag = flag;
	}
	public int getIntValue () {
		return intValue;
	}
	public boolean isFlag () {
		return flag;
	}
	@Override
	public int hashCode () {
		final int prime = 31;
		int result = 1;
		result = prime * result + (flag ? 1231 : 1237);
		result = prime * result + intValue;
		return result;
	}
	@Override
	public boolean equals (Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		FlagAndInt other = (FlagAndInt)obj;
		if (flag != other.flag) return false;
		if (intValue != other.intValue) return false;
		return true;
	}
	@Override
	public String toString () {
		return "FlagAndInt [intValue=" + intValue + ", flag=" + flag + "]";
	}
}
