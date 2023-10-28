package com.intelinet.seam;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.spi.PersistenceProvider;
import javax.transaction.SystemException;

import org.hibernate.Transaction;
import org.jboss.seam.persistence.QueryParser;

// Referenced classes of package org.jboss.seam.framework:
//            Query

public class EntityQuery extends com.intelinet.seam.Query
{

	private List resultList;
	private Object singleResult;
	private Long resultCount;
	private Map hints;

	public EntityQuery()
	{
	}

	@Override
	public void validate()
	{
		super.validate();
		if(getEntityManager() == null)
		{
			throw new IllegalStateException("entityManager is null");
		}
		if(!PersistenceProvider.instance().supportsFeature(org.jboss.seam.persistence.PersistenceProvider.Feature.WILDCARD_AS_COUNT_QUERY_SUBJECT))
		{
			setUseWildcardAsCountQuerySubject(false);
		}
	}

	@Override
	public boolean isNextExists()
	{
		return resultList != null && getMaxResults() != null && resultList.size() > getMaxResults().intValue();
	}

	@Override
	public List getResultList()
	{
		if(isAnyParameterDirty())
		{
			refresh();
		}
		initResultList();
		return truncResultList(resultList);
	}

	private void initResultList()
	{
		if(resultList == null)
		{
			Query query = createQuery();
			resultList = query != null ? query.getResultList() : null;
		}
	}

	@Override
	public Object getSingleResult()
	{
		if(isAnyParameterDirty())
		{
			refresh();
		}
		initSingleResult();
		return singleResult;
	}

	private void initSingleResult()
	{
		if(singleResult == null)
		{
			Query query = createQuery();
			singleResult = query != null ? query.getSingleResult() : null;
		}
	}

	@Override
	public Long getResultCount()
	{
		if(isAnyParameterDirty())
		{
			refresh();
		}
		initResultCount();
		return resultCount;
	}

	private void initResultCount()
	{
		if(resultCount == null)
		{
			Query query = createCountQuery();
			resultCount = query != null ? (Long)query.getSingleResult() : null;
		}
	}

	@Override
	public void refresh()
	{
		super.refresh();
		resultCount = null;
		resultList = null;
		singleResult = null;
	}

	public EntityManager getEntityManager()
	{
		return (EntityManager)getPersistenceContext();
	}

	public void setEntityManager(EntityManager entityManager)
	{
		setPersistenceContext(entityManager);
	}

	protected String getPersistenceContextName()
	{
		return "entityManager";
	}

	protected Query createQuery()
	{
		parseEjbql();
		evaluateAllParameters();
		joinTransaction();
		Query query = getEntityManager().createQuery(getRenderedEjbql());
		setParameters(query, getQueryParameterValues(), 0);
		setParameters(query, getRestrictionParameterValues(), getQueryParameterValues().size());
		if(getFirstResult() != null)
		{
			query.setFirstResult(getFirstResult().intValue());
		}
		if(getMaxResults() != null)
		{
			query.setMaxResults(getMaxResults().intValue() + 1);
		}
		if(getHints() != null)
		{
			java.util.Map.Entry me;
			for(Iterator i$ = getHints().entrySet().iterator(); i$.hasNext(); query.setHint((String)me.getKey(), me.getValue()))
			{
				me = (java.util.Map.Entry)i$.next();
			}

		}
		
		
		
		return query;
	}

	protected Query createCountQuery()
	{
		parseEjbql();
		evaluateAllParameters();
		joinTransaction();
		Query query = getEntityManager().createQuery(getCountEjbql());
		setParameters(query, getQueryParameterValues(), 0);
		setParameters(query, getRestrictionParameterValues(), getQueryParameterValues().size());
		return query;
	}

	private void setParameters(Query query, List parameters, int start)
	{
		for(int i = 0; i < parameters.size(); i++)
		{
			Object parameterValue = parameters.get(i);
			if(isRestrictionParameterSet(parameterValue))
			{
				query.setParameter(QueryParser.getParameterName(start + i), parameterValue);
			}
		}

	}

	public Map getHints()
	{
		return hints;
	}

	public void setHints(Map hints)
	{
		this.hints = hints;
	}

	protected void joinTransaction()
	{
		try
		{
			Transaction.instance().enlist(getEntityManager());
		}
		catch(SystemException se)
		{
			throw new RuntimeException("could not join transaction", se);
		}
	}
}
