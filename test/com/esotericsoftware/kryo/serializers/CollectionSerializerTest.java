package com.esotericsoftware.kryo.serializers;

import static org.junit.Assert.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

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
		
		ArrayList<Object> l=new ArrayList<>();
		for(int i=0;i<40;i++) {
			System.out.println("All equal length "+i+"\t\t"+ 					(countObjectBytes(k,l)-countObjectBytes(k1,l)));
			//System.out.println(Arrays.toString(getObjectBytes(k,l)));
			//System.out.println(Arrays.toString(getObjectBytes(k1,l)));
			Assert.assertEquals(writeReadObject(k,l), writeReadObject(k1,l));
			l.add(new ElementA(i));
		}
		
		l.clear();
		for(int i=0;i<40;i++) {
			System.out.println("All null length "+i+"\t\t"+ 					(countObjectBytes(k,l)-countObjectBytes(k1,l)));
			l.add(null);
		}
		
		l.clear();
		for(int i=0;i<40;i++) {
			System.out.println("mixed null length "+i+"\t\t"+ 					(countObjectBytes(k,l)-countObjectBytes(k1,l)));
			l.add(i%2==0 ? null : new ElementA(i));
		}
		
		l.clear();
		for(int i=0;i<40;i++) {
			System.out.println("non-null mixed classes length "+i+"\t\t"+ 	(countObjectBytes(k,l)-countObjectBytes(k1,l)));
			l.add(i%2==0 ? new ElementB(i) : new ElementA(i));
		}
		
		l.clear();
		for(int i=0;i<40;i++) {
			System.out.println("mixed null mixed classes length "+i+"\t\t"+(countObjectBytes(k,l)-countObjectBytes(k1,l)));
			l.add(i%3==0 ? new ElementB(i) : (i%3==1 ? new ElementA(i) : null));
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
}
