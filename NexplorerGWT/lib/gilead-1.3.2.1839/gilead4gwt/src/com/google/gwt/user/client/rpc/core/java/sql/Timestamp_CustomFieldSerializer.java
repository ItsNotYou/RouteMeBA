package com.google.gwt.user.client.rpc.core.java.sql;

import java.sql.Timestamp;

import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.client.rpc.SerializationStreamReader;
import com.google.gwt.user.client.rpc.SerializationStreamWriter;

public final class Timestamp_CustomFieldSerializer {

	public static void deserialize(SerializationStreamReader streamReader,
			Timestamp instance) throws SerializationException
	{
		// no fields
	}

	public static Timestamp instantiate(SerializationStreamReader streamReader)
										throws SerializationException {
		Timestamp instance =  new Timestamp(streamReader.readLong());
		instance.setNanos(streamReader.readInt());
		
		return instance;
	}

	public static void serialize(SerializationStreamWriter streamWriter,
								 Timestamp instance) throws SerializationException
    {
		streamWriter.writeLong(instance.getTime());
		streamWriter.writeInt(instance.getNanos());
	}

}
