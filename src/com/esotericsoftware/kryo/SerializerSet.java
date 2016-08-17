package com.esotericsoftware.kryo;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Currency;
import java.util.Date;
import java.util.EnumSet;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.TreeMap;
import java.util.TreeSet;

import com.esotericsoftware.kryo.serializers.CollectionSerializer;
import com.esotericsoftware.kryo.serializers.MapSerializer;
import com.esotericsoftware.kryo.serializers.OptionalSerializers;
import com.esotericsoftware.kryo.serializers.TimeSerializers;
import com.esotericsoftware.kryo.serializers.v4.V4CollectionSerializer;
import com.esotericsoftware.kryo.serializers.DefaultArraySerializers.BooleanArraySerializer;
import com.esotericsoftware.kryo.serializers.DefaultArraySerializers.ByteArraySerializer;
import com.esotericsoftware.kryo.serializers.DefaultArraySerializers.CharArraySerializer;
import com.esotericsoftware.kryo.serializers.DefaultArraySerializers.DoubleArraySerializer;
import com.esotericsoftware.kryo.serializers.DefaultArraySerializers.FloatArraySerializer;
import com.esotericsoftware.kryo.serializers.DefaultArraySerializers.IntArraySerializer;
import com.esotericsoftware.kryo.serializers.DefaultArraySerializers.LongArraySerializer;
import com.esotericsoftware.kryo.serializers.DefaultArraySerializers.ObjectArraySerializer;
import com.esotericsoftware.kryo.serializers.DefaultArraySerializers.ShortArraySerializer;
import com.esotericsoftware.kryo.serializers.DefaultArraySerializers.StringArraySerializer;
import com.esotericsoftware.kryo.serializers.DefaultSerializers.BigDecimalSerializer;
import com.esotericsoftware.kryo.serializers.DefaultSerializers.BigIntegerSerializer;
import com.esotericsoftware.kryo.serializers.DefaultSerializers.CalendarSerializer;
import com.esotericsoftware.kryo.serializers.DefaultSerializers.CharsetSerializer;
import com.esotericsoftware.kryo.serializers.DefaultSerializers.ClassSerializer;
import com.esotericsoftware.kryo.serializers.DefaultSerializers.CollectionsEmptyListSerializer;
import com.esotericsoftware.kryo.serializers.DefaultSerializers.CollectionsEmptyMapSerializer;
import com.esotericsoftware.kryo.serializers.DefaultSerializers.CollectionsEmptySetSerializer;
import com.esotericsoftware.kryo.serializers.DefaultSerializers.CollectionsSingletonListSerializer;
import com.esotericsoftware.kryo.serializers.DefaultSerializers.CollectionsSingletonMapSerializer;
import com.esotericsoftware.kryo.serializers.DefaultSerializers.CollectionsSingletonSetSerializer;
import com.esotericsoftware.kryo.serializers.DefaultSerializers.CurrencySerializer;
import com.esotericsoftware.kryo.serializers.DefaultSerializers.DateSerializer;
import com.esotericsoftware.kryo.serializers.DefaultSerializers.EnumSerializer;
import com.esotericsoftware.kryo.serializers.DefaultSerializers.EnumSetSerializer;
import com.esotericsoftware.kryo.serializers.DefaultSerializers.KryoSerializableSerializer;
import com.esotericsoftware.kryo.serializers.DefaultSerializers.LocaleSerializer;
import com.esotericsoftware.kryo.serializers.DefaultSerializers.StringBufferSerializer;
import com.esotericsoftware.kryo.serializers.DefaultSerializers.StringBuilderSerializer;
import com.esotericsoftware.kryo.serializers.DefaultSerializers.TimeZoneSerializer;
import com.esotericsoftware.kryo.serializers.DefaultSerializers.TreeMapSerializer;
import com.esotericsoftware.kryo.serializers.DefaultSerializers.TreeSetSerializer;
import com.esotericsoftware.kryo.serializers.DefaultSerializers.URLSerializer;

public enum SerializerSet {
	V3 {
		@Override
		public void addDefaultSerializers(Kryo kryo) {
			kryo.addDefaultSerializer(byte[].class, ByteArraySerializer.class);
			kryo.addDefaultSerializer(char[].class, CharArraySerializer.class);
			kryo.addDefaultSerializer(short[].class, ShortArraySerializer.class);
			kryo.addDefaultSerializer(int[].class, IntArraySerializer.class);
			kryo.addDefaultSerializer(long[].class, LongArraySerializer.class);
			kryo.addDefaultSerializer(float[].class, FloatArraySerializer.class);
			kryo.addDefaultSerializer(double[].class, DoubleArraySerializer.class);
			kryo.addDefaultSerializer(boolean[].class, BooleanArraySerializer.class);
			kryo.addDefaultSerializer(String[].class, StringArraySerializer.class);
			kryo.addDefaultSerializer(Object[].class, ObjectArraySerializer.class);
			kryo.addDefaultSerializer(KryoSerializable.class, KryoSerializableSerializer.class);
			kryo.addDefaultSerializer(BigInteger.class, BigIntegerSerializer.class);
			kryo.addDefaultSerializer(BigDecimal.class, BigDecimalSerializer.class);
			kryo.addDefaultSerializer(Class.class, ClassSerializer.class);
			kryo.addDefaultSerializer(Date.class, DateSerializer.class);
			kryo.addDefaultSerializer(Enum.class, EnumSerializer.class);
			kryo.addDefaultSerializer(EnumSet.class, EnumSetSerializer.class);
			kryo.addDefaultSerializer(Currency.class, CurrencySerializer.class);
			kryo.addDefaultSerializer(StringBuffer.class, StringBufferSerializer.class);
			kryo.addDefaultSerializer(StringBuilder.class, StringBuilderSerializer.class);
			kryo.addDefaultSerializer(Collections.EMPTY_LIST.getClass(), CollectionsEmptyListSerializer.class);
			kryo.addDefaultSerializer(Collections.EMPTY_MAP.getClass(), CollectionsEmptyMapSerializer.class);
			kryo.addDefaultSerializer(Collections.EMPTY_SET.getClass(), CollectionsEmptySetSerializer.class);
			kryo.addDefaultSerializer(Collections.singletonList(null).getClass(), CollectionsSingletonListSerializer.class);
			kryo.addDefaultSerializer(Collections.singletonMap(null, null).getClass(), CollectionsSingletonMapSerializer.class);
			kryo.addDefaultSerializer(Collections.singleton(null).getClass(), CollectionsSingletonSetSerializer.class);
			kryo.addDefaultSerializer(TreeSet.class, TreeSetSerializer.class);
			kryo.addDefaultSerializer(Collection.class, CollectionSerializer.class);
			kryo.addDefaultSerializer(TreeMap.class, TreeMapSerializer.class);
			kryo.addDefaultSerializer(Map.class, MapSerializer.class);
			kryo.addDefaultSerializer(TimeZone.class, TimeZoneSerializer.class);
			kryo.addDefaultSerializer(Calendar.class, CalendarSerializer.class);
			kryo.addDefaultSerializer(Locale.class, LocaleSerializer.class);
			kryo.addDefaultSerializer(Charset.class, CharsetSerializer.class);
			kryo.addDefaultSerializer(URL.class, URLSerializer.class);
			OptionalSerializers.addDefaultSerializers(kryo);
			TimeSerializers.addDefaultSerializers(kryo);
		}
	},
	V4 {

		@Override
		public void addDefaultSerializers(Kryo kryo) {
			kryo.addDefaultSerializer(byte[].class, ByteArraySerializer.class);
			kryo.addDefaultSerializer(char[].class, CharArraySerializer.class);
			kryo.addDefaultSerializer(short[].class, ShortArraySerializer.class);
			kryo.addDefaultSerializer(int[].class, IntArraySerializer.class);
			kryo.addDefaultSerializer(long[].class, LongArraySerializer.class);
			kryo.addDefaultSerializer(float[].class, FloatArraySerializer.class);
			kryo.addDefaultSerializer(double[].class, DoubleArraySerializer.class);
			kryo.addDefaultSerializer(boolean[].class, BooleanArraySerializer.class);
			kryo.addDefaultSerializer(String[].class, StringArraySerializer.class);
			kryo.addDefaultSerializer(Object[].class, ObjectArraySerializer.class);
			kryo.addDefaultSerializer(KryoSerializable.class, KryoSerializableSerializer.class);
			kryo.addDefaultSerializer(BigInteger.class, BigIntegerSerializer.class);
			kryo.addDefaultSerializer(BigDecimal.class, BigDecimalSerializer.class);
			kryo.addDefaultSerializer(Class.class, ClassSerializer.class);
			kryo.addDefaultSerializer(Date.class, DateSerializer.class);
			kryo.addDefaultSerializer(Enum.class, EnumSerializer.class);
			kryo.addDefaultSerializer(EnumSet.class, EnumSetSerializer.class);
			kryo.addDefaultSerializer(Currency.class, CurrencySerializer.class);
			kryo.addDefaultSerializer(StringBuffer.class, StringBufferSerializer.class);
			kryo.addDefaultSerializer(StringBuilder.class, StringBuilderSerializer.class);
			kryo.addDefaultSerializer(Collections.EMPTY_LIST.getClass(), CollectionsEmptyListSerializer.class);
			kryo.addDefaultSerializer(Collections.EMPTY_MAP.getClass(), CollectionsEmptyMapSerializer.class);
			kryo.addDefaultSerializer(Collections.EMPTY_SET.getClass(), CollectionsEmptySetSerializer.class);
			kryo.addDefaultSerializer(Collections.singletonList(null).getClass(), CollectionsSingletonListSerializer.class);
			kryo.addDefaultSerializer(Collections.singletonMap(null, null).getClass(), CollectionsSingletonMapSerializer.class);
			kryo.addDefaultSerializer(Collections.singleton(null).getClass(), CollectionsSingletonSetSerializer.class);
			kryo.addDefaultSerializer(TreeSet.class, TreeSetSerializer.class);
			kryo.addDefaultSerializer(Collection.class, V4CollectionSerializer.class);
			kryo.addDefaultSerializer(TreeMap.class, TreeMapSerializer.class);
			kryo.addDefaultSerializer(Map.class, MapSerializer.class);
			kryo.addDefaultSerializer(TimeZone.class, TimeZoneSerializer.class);
			kryo.addDefaultSerializer(Calendar.class, CalendarSerializer.class);
			kryo.addDefaultSerializer(Locale.class, LocaleSerializer.class);
			kryo.addDefaultSerializer(Charset.class, CharsetSerializer.class);
			kryo.addDefaultSerializer(URL.class, URLSerializer.class);
			OptionalSerializers.addDefaultSerializers(kryo);
			TimeSerializers.addDefaultSerializers(kryo);
		}
		
	};

	public abstract void addDefaultSerializers(Kryo kryo);
}
