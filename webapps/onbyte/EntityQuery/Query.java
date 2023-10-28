package com.intelinet.seam;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.faces.model.DataModel;

import org.jboss.seam.persistence.QueryParser;

// Referenced classes of package org.jboss.seam.framework:
//            PersistenceController

public abstract class Query extends PersistenceContextProvider
{

	private static final Pattern SUBJECT_PATTERN = Pattern.compile("^select\\s+(\\w+(?:\\s*\\.\\s*\\w+)*?)(?:\\s*,\\s*(\\w+(?:\\s*\\.\\s*\\w+)*?))*?" +
			"\\s+from"
			, 2);
	private static final Pattern FROM_PATTERN = Pattern.compile("(^|\\s)(from)\\s", 2);
	private static final Pattern WHERE_PATTERN = Pattern.compile("\\s(where)\\s", 2);
	private static final Pattern ORDER_PATTERN = Pattern.compile("\\s(order)(\\s)+by\\s", 2);
	private static final Pattern GROUP_PATTERN = Pattern.compile("\\s(group)(\\s)+by\\s", 2);
	private static final Pattern ORDER_COLUMN_PATTERN = Pattern.compile("^\\w+(\\.\\w+)*$");
	private static final String DIR_ASC = "asc";
	private static final String DIR_DESC = "desc";
	private static final String LOGIC_OPERATOR_AND = "and";
	private static final String LOGIC_OPERATOR_OR = "or";
	private String ejbql;
	private Integer firstResult;
	private Integer maxResults;
	private List restrictions;
	private String order;
	private String orderColumn;
	private String orderDirection;
	private String restrictionLogicOperator;
	private String groupBy;
	private boolean useWildcardAsCountQuerySubject;
	private DataModel dataModel;
	private String parsedEjbql;
	private List queryParameters;
	private List parsedRestrictions;
	private List restrictionParameters;
	private List queryParameterValues;
	private List restrictionParameterValues;

	public Query()
	{
		restrictions = new ArrayList(0);
		useWildcardAsCountQuerySubject = true;
	}

	public abstract List getResultList();

	public abstract Object getSingleResult();

	public abstract Long getResultCount();

	public void validate()
	{
		if(getEjbql() == null)
		{
			throw new IllegalStateException("ejbql is null");
		} else
		{
			return;
		}
	}

	public DataModel getDataModel()
	{
		if(dataModel == null)
		{
			dataModel = DataModels.instance().getDataModel(this);
		}
		return dataModel;
	}

	public Object getDataModelSelection()
	{
		return getDataModel().getRowData();
	}

	public int getDataModelSelectionIndex()
	{
		return getDataModel().getRowIndex();
	}

	public void refresh()
	{
		clearDataModel();
	}

	public void last()
	{
		setFirstResult(Integer.valueOf(getLastFirstResult().intValue()));
	}

	public void next()
	{
		setFirstResult(Integer.valueOf(getNextFirstResult()));
	}

	public void previous()
	{
		setFirstResult(Integer.valueOf(getPreviousFirstResult()));
	}

	public void first()
	{
		setFirstResult(Integer.valueOf(0));
	}

	protected void clearDataModel()
	{
		dataModel = null;
	}

	public Long getLastFirstResult()
	{
		Integer pc = getPageCount();
		return pc != null ? Long.valueOf((pc.longValue() - 1L) * getMaxResults().intValue()) : null;
	}

	public int getNextFirstResult()
	{
		Integer fr = getFirstResult();
		return (fr != null ? fr.intValue() : 0) + getMaxResults().intValue();
	}

	public int getPreviousFirstResult()
	{
		Integer fr = getFirstResult();
		Integer mr = getMaxResults();
		return mr.intValue() < (fr != null ? fr.intValue() : 0) ? fr.intValue() - mr.intValue() : 0;
	}

	public Integer getPageCount()
	{
		if(getMaxResults() == null)
		{
			return null;
		} else
		{
			int rc = getResultCount().intValue();
			int mr = getMaxResults().intValue();
			int pages = rc / mr;
			return Integer.valueOf(rc % mr != 0 ? pages + 1 : pages);
		}
	}

	protected void parseEjbql()
	{
		if(parsedEjbql == null || parsedRestrictions == null)
		{
			QueryParser qp = new QueryParser(null, getEjbql());
			queryParameters = qp.getParameterValues();
			parsedEjbql = qp.getEjbql();
			List restrictionFragments = getRestrictions();
			parsedRestrictions = new ArrayList(restrictionFragments.size());
			restrictionParameters = new ArrayList(restrictionFragments.size());
			QueryParser rqp;
			for(Iterator i$ = restrictionFragments.iterator(); i$.hasNext(); restrictionParameters.addAll(rqp.getParameterValues()))
			{
				com.intelinet.seam.Expressions.ValueExpression restriction = (com.intelinet.seam.Expressions.ValueExpression)i$.next();
				rqp = new QueryParser(null, restriction.getExpressionString(), queryParameters.size() + restrictionParameters.size());
				if(rqp.getParameterValues().size() != 1)
				{
					throw new IllegalArgumentException((new StringBuilder()).append("there should be exactly one value binding in a restriction: ").append(restriction).toString());
				}
				parsedRestrictions.add(rqp.getEjbql());
			}

		}
	}

	protected String getRenderedEjbql()
	{
		StringBuilder builder = (new StringBuilder()).append(parsedEjbql);
		for(int i = 0; i < getRestrictions().size(); i++)
		{
			Object parameterValue = ((com.intelinet.seam.Expressions.ValueExpression)restrictionParameters.get(i)).getValue();
			if(!isRestrictionParameterSet(parameterValue))
			{
				continue;
			}
			if(WHERE_PATTERN.matcher(builder).find())
			{
				builder.append(" ").append(getRestrictionLogicOperator()).append(" ");
			} else
			{
				builder.append(" where ");
			}
			builder.append((String)parsedRestrictions.get(i));
		}

		if(getGroupBy() != null)
		{
			builder.append(" group by ").append(getGroupBy());
		}
		if(getOrder() != null)
		{
			builder.append(" order by ").append(getOrder());
		}
		return builder.toString();
	}

	protected boolean isRestrictionParameterSet(Object parameterValue)
	{
		return parameterValue != null && !"".equals(parameterValue) && (!(parameterValue instanceof Collection) || !((Collection)parameterValue).isEmpty());
	}

	protected String getCountEjbql()
	{
		String ejbql = getRenderedEjbql();
		Matcher fromMatcher = FROM_PATTERN.matcher(ejbql);
		if(!fromMatcher.find())
		{
			throw new IllegalArgumentException("no from clause found in query");
		}
		int fromLoc = fromMatcher.start(2);
		Matcher orderMatcher = ORDER_PATTERN.matcher(ejbql);
		int orderLoc = orderMatcher.find() ? orderMatcher.start(1) : ejbql.length();
		Matcher groupMatcher = GROUP_PATTERN.matcher(ejbql);
		int groupLoc = groupMatcher.find() ? groupMatcher.start(1) : orderLoc;
		Matcher whereMatcher = WHERE_PATTERN.matcher(ejbql);
		int whereLoc = whereMatcher.find() ? whereMatcher.start(1) : groupLoc;
		String subject;
		if(getGroupBy() != null)
		{
			subject = (new StringBuilder()).append("distinct ").append(getGroupBy()).toString();
		} else
			if(useWildcardAsCountQuerySubject)
			{
				subject = "*";
			} else
			{
				Matcher subjectMatcher = SUBJECT_PATTERN.matcher(ejbql);
				if(subjectMatcher.find())
				{
					subject = subjectMatcher.group(1);
				} else
				{
					throw new IllegalStateException("invalid select clause for query");
				}
			}
		return (new StringBuilder(ejbql.length() + 15)).append("select count(").append(subject).append(") ").append(ejbql.substring(fromLoc, whereLoc).replace("join fetch", "join")).append(ejbql.substring(whereLoc, groupLoc)).toString().trim();
	}

	public String getEjbql()
	{
		return ejbql;
	}

	public void setEjbql(String ejbql)
	{
		this.ejbql = ejbql;
		parsedEjbql = null;
		refresh();
	}

	public Integer getFirstResult()
	{
		return firstResult;
	}

	public boolean isPreviousExists()
	{
		return getFirstResult() != null && getFirstResult().intValue() != 0;
	}

	public abstract boolean isNextExists();

	public boolean isPaginated()
	{
		return isNextExists() || isPreviousExists();
	}

	public void setFirstResult(Integer firstResult)
	{
		this.firstResult = firstResult;
		refresh();
	}

	public Integer getMaxResults()
	{
		return maxResults;
	}

	public void setMaxResults(Integer maxResults)
	{
		this.maxResults = maxResults;
		refresh();
	}

	public List getRestrictions()
	{
		return restrictions;
	}

	public void setRestrictions(List restrictions)
	{
		this.restrictions = restrictions;
		parsedRestrictions = null;
		refresh();
	}

	public void setRestrictionExpressionStrings(List expressionStrings)
	{
		com.intelinet.seam.Expressions expressions = new com.intelinet.seam.Expressions();
		List restrictionVEs = new ArrayList(expressionStrings.size());
		String expressionString;
		for(Iterator i$ = expressionStrings.iterator(); i$.hasNext(); restrictionVEs.add(expressions.createValueExpression(expressionString)))
		{
			expressionString = (String)i$.next();
		}

		setRestrictions(restrictionVEs);
	}

	public List getRestrictionExpressionStrings()
	{
		List expressionStrings = new ArrayList();
		com.intelinet.seam.Expressions.ValueExpression restriction;
		for(Iterator i$ = getRestrictions().iterator(); i$.hasNext(); expressionStrings.add(restriction.getExpressionString()))
		{
			restriction = (com.intelinet.seam.Expressions.ValueExpression)i$.next();
		}

		return expressionStrings;
	}

	public String getGroupBy()
	{
		return groupBy;
	}

	public void setGroupBy(String groupBy)
	{
		this.groupBy = groupBy;
	}

	public String getOrder()
	{
		String column = getOrderColumn();
		if(column == null)
		{
			return order;
		}
		String direction = getOrderDirection();
		if(direction == null)
		{
			return column;
		} else
		{
			return (new StringBuilder()).append(column).append(' ').append(direction).toString();
		}
	}

	public void setOrder(String order)
	{
		this.order = order;
		refresh();
	}

	public String getOrderDirection()
	{
		return orderDirection;
	}

	public void setOrderDirection(String orderDirection)
	{
		this.orderDirection = sanitizeOrderDirection(orderDirection);
	}

	private String sanitizeOrderDirection(String direction)
	{
		if(direction == null || direction.length() == 0)
		{
			return null;
		}
		if(direction.equalsIgnoreCase("asc"))
		{
			return "asc";
		}
		if(direction.equalsIgnoreCase("desc"))
		{
			return "desc";
		} else
		{
			throw new IllegalArgumentException("invalid order direction");
		}
	}

	public String getOrderColumn()
	{
		return orderColumn;
	}

	public void setOrderColumn(String orderColumn)
	{
		this.orderColumn = sanitizeOrderColumn(orderColumn);
	}

	private String sanitizeOrderColumn(String columnName)
	{
		if(columnName == null || columnName.trim().length() == 0)
		{
			return null;
		}
		if(ORDER_COLUMN_PATTERN.matcher(columnName).find())
		{
			return columnName;
		} else
		{
			throw new IllegalArgumentException((new StringBuilder()).append("invalid order column (\"").append(columnName).append("\" must match the regular expression \"").append(ORDER_COLUMN_PATTERN).append("\")").toString());
		}
	}

	public String getRestrictionLogicOperator()
	{
		return restrictionLogicOperator == null ? "and" : restrictionLogicOperator;
	}

	public void setRestrictionLogicOperator(String operator)
	{
		restrictionLogicOperator = sanitizeRestrictionLogicOperator(operator);
	}

	private String sanitizeRestrictionLogicOperator(String operator)
	{
		if(operator == null || operator.trim().length() == 0)
		{
			return "and";
		}
		if(!"and".equals(operator) && !"or".equals(operator))
		{
			throw new IllegalArgumentException((new StringBuilder()).append("Invalid restriction logic operator: ").append(operator).toString());
		} else
		{
			return operator;
		}
	}

	protected List getQueryParameters()
	{
		return queryParameters;
	}

	protected List getRestrictionParameters()
	{
		return restrictionParameters;
	}

	private static boolean isAnyParameterDirty(List valueBindings, List lastParameterValues)
	{
		if(lastParameterValues == null)
		{
			return true;
		}
		for(int i = 0; i < valueBindings.size(); i++)
		{
			Object parameterValue = ((com.intelinet.seam.Expressions.ValueExpression)valueBindings.get(i)).getValue();
			Object lastParameterValue = lastParameterValues.get(i);
			if("".equals(parameterValue))
			{
				parameterValue = null;
			}
			if("".equals(lastParameterValue))
			{
				lastParameterValue = null;
			}
			if(parameterValue != lastParameterValue && (parameterValue == null || !parameterValue.equals(lastParameterValue)))
			{
				return true;
			}
		}

		return false;
	}

	private static List getParameterValues(List valueBindings)
	{
		List values = new ArrayList(valueBindings.size());
		for(int i = 0; i < valueBindings.size(); i++)
		{
			values.add(((com.intelinet.seam.Expressions.ValueExpression)valueBindings.get(i)).getValue());
		}

		return values;
	}

	protected void evaluateAllParameters()
	{
		setQueryParameterValues(getParameterValues(getQueryParameters()));
		setRestrictionParameterValues(getParameterValues(getRestrictionParameters()));
	}

	protected boolean isAnyParameterDirty()
	{
		return isAnyParameterDirty(getQueryParameters(), getQueryParameterValues()) || isAnyParameterDirty(getRestrictionParameters(), getRestrictionParameterValues());
	}

	protected List getQueryParameterValues()
	{
		return queryParameterValues;
	}

	protected void setQueryParameterValues(List queryParameterValues)
	{
		this.queryParameterValues = queryParameterValues;
	}

	protected List getRestrictionParameterValues()
	{
		return restrictionParameterValues;
	}

	protected void setRestrictionParameterValues(List restrictionParameterValues)
	{
		this.restrictionParameterValues = restrictionParameterValues;
	}

	protected List truncResultList(List results)
	{
		Integer mr = getMaxResults();
		if(mr != null && results.size() > mr.intValue())
		{
			return results.subList(0, mr.intValue());
		} else
		{
			return results;
		}
	}

	protected boolean isUseWildcardAsCountQuerySubject()
	{
		return useWildcardAsCountQuerySubject;
	}

	protected void setUseWildcardAsCountQuerySubject(boolean useCompliantCountQuerySubject)
	{
		useWildcardAsCountQuerySubject = useCompliantCountQuerySubject;
	}

}
