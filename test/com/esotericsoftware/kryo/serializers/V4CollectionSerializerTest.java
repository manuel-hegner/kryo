package com.esotericsoftware.kryo.serializers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.TreeSet;

import org.junit.Before;
import org.junit.Test;

import com.esotericsoftware.kryo.KryoTestCase;
import com.esotericsoftware.kryo.serializers.v4.V4CollectionSerializer;

public class V4CollectionSerializerTest extends KryoTestCase {
	
	@Before
	@Override
	protected void setUp () throws Exception {
		super.setUp();
		
		kryo.register(ArrayList.class, new V4CollectionSerializer());
		kryo.register(LinkedList.class, new V4CollectionSerializer());
		kryo.register(HashSet.class, new V4CollectionSerializer());
	}
	
	@Test
	public void testSameTypeNoNull() {
		List<Integer> l = Arrays.asList(5,3,835,3853,384,384,34,9385,2394,364923,52935,923,5923,593,59135);
		
		roundTrip(35, 64, new ArrayList<>(l));
		roundTrip(35, 64, new LinkedList<>(l));
		roundTrip(33, 60, new HashSet<>(l));
	}
	
	@Test
	public void testSameTypeNull() {
		List<Integer> l = Arrays.asList(5,null,835,3853,384,384,34,null,2394,364923,null,923,5923,593,59135);
		
		roundTrip(43, 67, new ArrayList<>(l));
		roundTrip(43, 67, new LinkedList<>(l));
		roundTrip(38, 60, new HashSet<>(l));
	}
	
	@Test
	public void testDifferentTypesNoNull() {
		List<Number> l = Arrays.asList(5,3,835,3853.5D,384,384,34F,9385,2394,364923,52935,923,5923,593,59135);
		
		roundTrip(57, 81, new ArrayList<>(l));
		roundTrip(57, 81, new LinkedList<>(l));
		roundTrip(54, 76, new HashSet<>(l));
	}
	
	@Test
	public void testDifferentTypesNull() {
		List<Number> l = Arrays.asList(5,null,835,3853.5D,384,384,34F,null,2394,364923,null,923,5923,593,59135);
		
		roundTrip(50, 69, new ArrayList<>(l));
		roundTrip(50, 69, new LinkedList<>(l));
		roundTrip(45, 62, new HashSet<>(l));
	}
	
	@Test
	public void testOnlyNull() {
		List<Integer> l = Arrays.asList(null,null,null,null,null,null,null,null,null,null,null,null,null,null);
		
		roundTrip(3, 3, new ArrayList<>(l));
		roundTrip(3, 3, new LinkedList<>(l));
		roundTrip(3, 3, new HashSet<>(l));
	}
}
