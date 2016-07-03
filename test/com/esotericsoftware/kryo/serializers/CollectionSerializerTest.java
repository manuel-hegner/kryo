package com.esotericsoftware.kryo.serializers;

import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.function.IntFunction;

import org.junit.Test;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

import junit.framework.Assert;

public class CollectionSerializerTest {

	@Test
	public void test() throws Exception {
		Kryo k = new Kryo();
		k.register(ArrayList.class, new CollectionSerializer());
		Kryo k1 = new Kryo();
		k1.register(ArrayList.class, new NewCollectionSerializer());
		
		System.out.print("| List Size");
		for(Mode mode:Mode.values()) {
			System.out.print(" | "+mode.name());
		}
		System.out.println(" |");
		System.out.print("| ---:");
		for(Mode mode:Mode.values()) {
			System.out.print(" | ---:");
		}
		System.out.println(" |");
		
		for(int size:new int[]{1,2,3,4,5,6,7,8,9,10,100,1000,10000,10000,100000}) {
			System.out.print("| "+size);
			for(Mode mode:Mode.values()) {
				ArrayList<Object> l=new ArrayList<>();
				for(int i=0;i<size;i++)
					l.add(mode.value(i));

				System.out.print(" | "+(countObjectBytes(k1,l)*100/countObjectBytes(k,l)-100)+"%");
				
				long normalTime=-System.nanoTime();
				for(int i=0;i<1000;i++) {
					writeReadObject(k, l);
				}
				normalTime+=System.nanoTime();
				long newTime=-System.nanoTime();
				for(int i=0;i<1000;i++) {
					writeReadObject(k1, l);
				}
				newTime+=System.nanoTime();
				//do it again to counter JIT optimizations
				normalTime=-System.nanoTime();
				for(int i=0;i<1000;i++) {
					writeReadObject(k, l);
				}
				normalTime+=System.nanoTime();
				newTime=-System.nanoTime();
				for(int i=0;i<1000;i++) {
					writeReadObject(k1, l);
				}
				newTime+=System.nanoTime();
				System.out.print(" / "+(newTime*100/normalTime-100)+"%");
			}
			System.out.println(" |");
		}
	}
	
	private Object writeReadObject (Kryo k, Object o) {
		byte[] bytes = getObjectBytes(k, o);
		Input in = new Input(bytes);
		return k.readObject(in, o.getClass());
	}

	private int countObjectBytes (Kryo k, Object o) {
		
		return getObjectBytes(k, o).length;
	}
	
	private byte[] getObjectBytes (Kryo k, Object o) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try(Output out = new Output(baos)) {
			k.writeObject(out, o);
		}
		return baos.toByteArray();
	}

	private static final class ElementA {
		private int i=4;

		public ElementA () {}
		public ElementA (int i) {
			this.i=i;
		}

		public int getI () {
			return i;
		}

		public void setI (int i) {
			this.i = i;
		}

		@Override
		public int hashCode () {
			final int prime = 31;
			int result = 1;
			result = prime * result + i;
			return result;
		}

		@Override
		public boolean equals (Object obj) {
			if (this == obj) return true;
			if (obj == null) return false;
			if (getClass() != obj.getClass()) return false;
			ElementA other = (ElementA)obj;
			if (i != other.i) return false;
			return true;
		}
		
		@Override
		public String toString () {
			return "A["+i+"]";
		}
	}
	private static final class ElementB {
		private int i=9;

		public ElementB () {}
		public ElementB (int i) {
			this.i=i;
		}

		public int getI () {
			return i;
		}

		public void setI (int i) {
			this.i = i;
		}

		@Override
		public int hashCode () {
			final int prime = 31;
			int result = 1;
			result = prime * result + i;
			return result;
		}

		@Override
		public boolean equals (Object obj) {
			if (this == obj) return true;
			if (obj == null) return false;
			if (getClass() != obj.getClass()) return false;
			ElementB other = (ElementB)obj;
			if (i != other.i) return false;
			return true;
		}
		
		@Override
		public String toString () {
			return "B["+i+"]";
		}
	}
	
	private static enum Mode {
		
		SAME_CLASS_NO_NULL {
			@Override
			public Object value (int id) {
				return new ElementA(id);
			}
		},
		SAME_CLASS_NULL {
			@Override
			public Object value (int id) {
				return id%2==0 ? new ElementA(id) : null;
			}
		},
		MIXED_CLASS_NO_NULL {
			@Override
			public Object value (int id) {
				return id%2==0 ? new ElementA(id) : new ElementB(id);
			}
		},
		MIXED_CLASS_NULL {
			@Override
			public Object value (int id) {
				if(id%3==0)
					return new ElementA(id);
				else if(id%3==1)
					return new ElementB(id);
				else
					return null;
			}
		},
		ONLY_NULL {
			@Override
			public Object value (int id) {
				return null;
			}
		};
		
		public abstract Object value(int id);
	}
}
