package com.esotericsoftware.kryo.util;

import java.util.Collection;

public class CollectionInformation {
	public static final CollectionInformation NO_SPECIAL_CASE = new CollectionInformation((byte)0x5);
	private Class<?> singularClass=null;
	private boolean multipleClasses=false;
	private boolean allNull=true;
	private boolean anyNull=false;
	
	public CollectionInformation () {}
	
	/**
	 * Recreates the boolean flags from the given byte. This will not set the singularClass field.
	 * @param flagByte
	 */
	public CollectionInformation (byte flagByte) {
		multipleClasses=(flagByte&1)==1;
		allNull=(flagByte&2)==2;
		anyNull=(flagByte&4)==4;
	}
	public Class<?> getSingularClass () {
		return singularClass;
	}
	public void setSingularClass (Class<?> singularClass) {
		this.singularClass = singularClass;
	}
	public boolean isAllNull () {
		return allNull;
	}
	public void setAllNull (boolean allNull) {
		this.allNull = allNull;
	}
	public boolean isAnyNull () {
		return anyNull;
	}
	public void setAnyNull (boolean anyNull) {
		this.anyNull = anyNull;
	}
	public boolean isMultipleClasses () {
		return multipleClasses;
	}
	public void setMultipleClasses (boolean multipleClasses) {
		this.multipleClasses = multipleClasses;
	}
	
	public static CollectionInformation gather (Collection collection) {
		CollectionInformation info = new CollectionInformation();
		
		for(Object o:collection) {
			if(o == null) {
				info.anyNull = true;
				if(info.multipleClasses && !info.allNull) //worst case reached
					return info;
			}
			else {
				info.allNull=false;
				
				if(!info.multipleClasses) {
					if(info.singularClass==null)
						info.singularClass=o.getClass();
					else {
						if(info.singularClass!=o.getClass()) {
							info.singularClass=null;
							info.multipleClasses=true;
							if(info.anyNull)
								return info;	//worst case reached
						}
					}
				}
			}
		}
		return info;
	}
	
	public byte toByte () {
		return (byte) (
				(multipleClasses?1:0)
				|(allNull?2:0)
				|(anyNull?4:0)
		);
	}

	@Override
	public int hashCode () {
		final int prime = 31;
		int result = 1;
		result = prime * result + (allNull ? 1231 : 1237);
		result = prime * result + (anyNull ? 1231 : 1237);
		result = prime * result + (multipleClasses ? 1231 : 1237);
		result = prime * result + ((singularClass == null) ? 0 : singularClass.hashCode());
		return result;
	}

	@Override
	public boolean equals (Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		CollectionInformation other = (CollectionInformation)obj;
		if (allNull != other.allNull) return false;
		if (anyNull != other.anyNull) return false;
		if (multipleClasses != other.multipleClasses) return false;
		if (singularClass == null) {
			if (other.singularClass != null) return false;
		} else if (!singularClass.equals(other.singularClass)) return false;
		return true;
	}

	@Override
	public String toString () {
		return "CollectionInformation [singularClass=" + singularClass + ", multipleClasses=" + multipleClasses + ", allNull="
			+ allNull + ", anyNull=" + anyNull + "]";
	}
}
