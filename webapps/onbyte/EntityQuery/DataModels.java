package com.intelinet.seam;

import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.faces.model.ArrayDataModel;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;

import org.hibernate.mapping.Component;

public class DataModels
{

	public DataModels()
	{
	}

	public DataModel getDataModel(Object value)
	{
		if(value instanceof List)
		{
			return new ListDataModel((List)value);
		}
		if(value instanceof Object[])
		{
			return new ArrayDataModel((Object[])value);
		}
		if(value instanceof Map)
		{
			return new MapDataModel((Map)value);
		}
		if(value instanceof Set)
		{
			return new SetDataModel((Set)value);
		} else
		{
			throw new IllegalArgumentException((new StringBuilder()).append("unknown collection type: ").append(value.getClass()).toString());
		}
	}

	public DataModel getDataModel(Query query)
	{
		return getDataModel(query.getResultList());
	}

	public static DataModels instance()
	{
		return (DataModels)Component.getInstance(com.intelinet.seam.DataModels.class, ScopeType.STATELESS);
	}
}
