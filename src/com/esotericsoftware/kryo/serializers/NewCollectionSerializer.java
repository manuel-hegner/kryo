/* Copyright (c) 2008, Nathan Sweet
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following
 * conditions are met:
 * 
 * - Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
 * - Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following
 * disclaimer in the documentation and/or other materials provided with the distribution.
 * - Neither the name of Esoteric Software nor the names of its contributors may be used to endorse or promote products derived
 * from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING,
 * BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT
 * SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE. */

package com.esotericsoftware.kryo.serializers;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Registration;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

/** Serializes objects that implement the {@link Collection} interface.
 * <p>
 * With the default constructor, a collection requires a 1-3 byte header and an extra 2-3 bytes is written for each element in the
 * collection. The alternate constructor can be used to improve efficiency to match that of using an array instead of a
 * collection.
 * @author Nathan Sweet <misc@n4te.com> */
public class NewCollectionSerializer extends Serializer<Collection> {
	private boolean elementsCanBeNull = true;
	private Serializer serializer;
	private Class elementClass;
	private Class genericType;

	public NewCollectionSerializer () {
	}

	/** @see #setElementClass(Class, Serializer) */
	public NewCollectionSerializer (Class elementClass, Serializer serializer) {
		setElementClass(elementClass, serializer);
	}

	/** @see #setElementClass(Class, Serializer)
	 * @see #setElementsCanBeNull(boolean) */
	public NewCollectionSerializer (Class elementClass, Serializer serializer, boolean elementsCanBeNull) {
		setElementClass(elementClass, serializer);
		this.elementsCanBeNull = elementsCanBeNull;
	}

	/** @param elementsCanBeNull False if all elements are not null. This saves 1 byte per element if elementClass is set. True if it
	 *           is not known (default). */
	public void setElementsCanBeNull (boolean elementsCanBeNull) {
		this.elementsCanBeNull = elementsCanBeNull;
	}

	/** @param elementClass The concrete class of each element. This saves 1-2 bytes per element. Set to null if the class is not
	 *           known or varies per element (default).
	 * @param serializer The serializer to use for each element. */
	public void setElementClass (Class elementClass, Serializer serializer) {
		this.elementClass = elementClass;
		this.serializer = serializer;
	}

	public void setGenerics (Kryo kryo, Class[] generics) {
		genericType = null;
		if (generics != null && generics.length > 0) {
			if (kryo.isFinal(generics[0])) genericType = generics[0];
		}
	}

	public void write (Kryo kryo, Output output, Collection collection) {
		int length = collection.size();
		
		if(collection.size()>3) {
			CollectionInfo info = collectInfo(collection);
			writeSpecial(kryo, output, collection, length, info);
		}
		else
			writeGeneric(kryo, output, collection, length);
	}

	private void writeSpecial (Kryo kryo, Output output, Collection collection, int length, CollectionInfo info) {
		if((info.singularClass==null && !info.allNull) || //no special case at all
			(info.allNull && length<5) || (!info.allNull && info.anyNull && length<8)) { //special case but meta information is too expensive if small collection
			writeGeneric(kryo, output, collection, length);
		}
		else {
			output.writeVarInt(-length, true);
			output.writeByte((info.allNull?1:0) | (info.anyNull?2:0) | (info.singularClass!=null?4:0));	//write special info
			
			if(!info.allNull) {
				Serializer serializer = this.serializer;
				if (genericType != null) {
					if (serializer == null) serializer = kryo.getSerializer(genericType);
					genericType = null;
				}
				if(info.singularClass!=null) {
					if (serializer == null) {
						serializer = kryo.getSerializer(info.singularClass);
						kryo.writeClass(output, info.singularClass);
					}
				}
				if (serializer != null) {
					if (elementsCanBeNull && info.anyNull) {
						for (Object element : collection)
							kryo.writeObjectOrNull(output, element, serializer);
					} else {
						for (Object element : collection)
							kryo.writeObject(output, element, serializer);
					}
				} else {
					for (Object element : collection)
						kryo.writeClassAndObject(output, element);
				}
			}
		}
	}

	private void writeGeneric (Kryo kryo, Output output, Collection collection, int length) {
		output.writeVarInt(length, true);
		Serializer serializer = this.serializer;
		if (genericType != null) {
			if (serializer == null) serializer = kryo.getSerializer(genericType);
			genericType = null;
		}
		if (serializer != null) {
			if (elementsCanBeNull) {
				for (Object element : collection)
					kryo.writeObjectOrNull(output, element, serializer);
			} else {
				for (Object element : collection)
					kryo.writeObject(output, element, serializer);
			}
		} else {
			for (Object element : collection)
				kryo.writeClassAndObject(output, element);
		}
	}

	private CollectionInfo collectInfo (Collection collection) {
		CollectionInfo info = new CollectionInfo();
		for(Object o:collection)
			info.add(o);
		return info;
	}

	/** Used by {@link #read(Kryo, Input, Class)} to create the new object. This can be overridden to customize object creation, eg
	 * to call a constructor with arguments. The default implementation uses {@link Kryo#newInstance(Class)}. */
	protected Collection create (Kryo kryo, Input input, Class<Collection> type) {
		return kryo.newInstance(type);
	}

	public Collection read (Kryo kryo, Input input, Class<Collection> type) {
		Collection collection = create(kryo, input, type);
		kryo.reference(collection);
		int length = input.readVarInt(true);
		if(length>=0)
			return readGeneric(kryo, input, type, collection, length);
		else
			return readSpecial(kryo, input, type, collection, -length);
	}

	private Collection readSpecial (Kryo kryo, Input input, Class<Collection> type, Collection collection, int length) {
		
		//read metadata
		byte meta=input.readByte();
		boolean allNull = (meta & 1) == 1;
		boolean anyNull = (meta & 2) == 2;
		boolean singularClass = (meta & 4) == 4;
		
		if(allNull)
			collection.addAll(Arrays.asList(new Object[length]));
		else {
			if (collection instanceof ArrayList) ((ArrayList)collection).ensureCapacity(length);
			Class elementClass = this.elementClass;
			Serializer serializer = this.serializer;
			if (genericType != null) {
				if (serializer == null) {
					elementClass = genericType;
					serializer = kryo.getSerializer(genericType);
				}
				genericType = null;
			}
			if(serializer==null && singularClass) {
				Registration elements = kryo.readClass(input);
				elementClass = elements.getType();
				serializer = elements.getSerializer();
			}
			if (serializer != null) {
				if (elementsCanBeNull && anyNull) {
					for (int i = 0; i < length; i++)
						collection.add(kryo.readObjectOrNull(input, elementClass, serializer));
				} else {
					for (int i = 0; i < length; i++)
						collection.add(kryo.readObject(input, elementClass, serializer));
				}
			} else {
				for (int i = 0; i < length; i++)
					collection.add(kryo.readClassAndObject(input));
			}
		}
		return collection;
	}
	
	private Collection readGeneric (Kryo kryo, Input input, Class<Collection> type, Collection collection, int length) {
		if (collection instanceof ArrayList) ((ArrayList)collection).ensureCapacity(length);
		Class elementClass = this.elementClass;
		Serializer serializer = this.serializer;
		if (genericType != null) {
			if (serializer == null) {
				elementClass = genericType;
				serializer = kryo.getSerializer(genericType);
			}
			genericType = null;
		}
		if (serializer != null) {
			if (elementsCanBeNull) {
				for (int i = 0; i < length; i++)
					collection.add(kryo.readObjectOrNull(input, elementClass, serializer));
			} else {
				for (int i = 0; i < length; i++)
					collection.add(kryo.readObject(input, elementClass, serializer));
			}
		} else {
			for (int i = 0; i < length; i++)
				collection.add(kryo.readClassAndObject(input));
		}
		return collection;
	}

	/** Used by {@link #copy(Kryo, Collection)} to create the new object. This can be overridden to customize object creation, eg to
	 * call a constructor with arguments. The default implementation uses {@link Kryo#newInstance(Class)}. */
	protected Collection createCopy (Kryo kryo, Collection original) {
		return kryo.newInstance(original.getClass());
	}

	public Collection copy (Kryo kryo, Collection original) {
		Collection copy = createCopy(kryo, original);
		kryo.reference(copy);
		for (Object element : original)
			copy.add(kryo.copy(element));
		return copy;
	}

	/**
	 * Used to annotate fields that are collections with specific Kryo serializers
	 * for their values.
	 */
	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.FIELD)
	public @interface BindCollection {
	    /**
	     * Serializer to be used for values
	     * 
	     * @return the class<? extends Serializer> used for values serialization
	     */
	    @SuppressWarnings("rawtypes")
	    Class<? extends Serializer> elementSerializer() default Serializer.class;
	    
	    /**
	     * Class used for elements
	     * 
	     * @return the class used for elements
	     */
	    Class<?> elementClass() default Object.class;
	    
	    /**
	     * Indicates if elements can be null
	     * 
	     * @return true, if elements can be null
	     */
	    boolean elementsCanBeNull() default true;
	}
	
	private static final class CollectionInfo {
		Class<?> singularClass=null;
		boolean allNull=true;
		boolean anyNull=false;
		
		public CollectionInfo () {}

		public void add(Object o) {
			if(o == null)
				anyNull = true;
			else {
				if(!o.getClass().equals(singularClass)) {
					if(allNull)
						singularClass=o.getClass();
					else
						singularClass=null;
				}
				allNull=false;
			}
		}
	}
}
