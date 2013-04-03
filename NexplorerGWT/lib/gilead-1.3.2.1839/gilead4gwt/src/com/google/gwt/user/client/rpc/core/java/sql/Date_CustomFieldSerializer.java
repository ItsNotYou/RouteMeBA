package com.google.gwt.user.client.rpc.core.java.sql;

import java.sql.Date;

import com.google.gwt.user.client.rpc.SerializationException;
import com.google.gwt.user.client.rpc.SerializationStreamReader;
import com.google.gwt.user.client.rpc.SerializationStreamWriter;

public final class Date_CustomFieldSerializer {

	public static void deserialize(SerializationStreamReader streamReader,
			Date instance) throws SerializationException
	{
		// no fields
	}

	public static Date instantiate(SerializationStreamReader streamReader)
										throws SerializationException {
		Date instance =  new Date(streamReader.readLong());
		
		return instance;
	}

	public static void serialize(SerializationStreamWriter streamWriter,
								 Date instance) throws SerializationException
    {
		streamWriter.writeLong(instance.getTime());
	}

}
