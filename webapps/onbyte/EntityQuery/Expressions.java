package com.intelinet.seam;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.el.ELContext;
import javax.el.ExpressionFactory;

import org.hibernate.mapping.Component;
import org.jboss.weld.resources.spi.ResourceLoader;

// Referenced classes of package org.jboss.seam.core:
//            ResourceLoader

public class Expressions
implements Serializable
{
	public static interface MethodExpression
	extends Serializable
	{

		public abstract Object invoke(Object aobj[]);

		public abstract String getExpressionString();

		public abstract javax.el.MethodExpression toUnifiedMethodExpression();
	}

	public static interface ValueExpression
	extends Serializable
	{

		public abstract Object getValue();

		public abstract void setValue(Object obj);

		public abstract String getExpressionString();

		public abstract Class getType();

		public abstract javax.el.ValueExpression toUnifiedValueExpression();
	}


	private static final LogProvider log;
	private static List blacklist;

	public Expressions()
	{
	}

	public ExpressionFactory getExpressionFactory()
	{
		return SeamExpressionFactory.INSTANCE;
	}

	public ELContext getELContext()
	{
		return EL.createELContext();
	}

	public ValueExpression createValueExpression(String expression)
	{
		return createValueExpression(expression, java.lang.Object.class);
	}

	public MethodExpression createMethodExpression(String expression)
	{
		return createMethodExpression(expression, java.lang.Object.class, new Class[0]);
	}

	public ValueExpression createValueExpression(final String expression, final Class type)
	{
		checkELExpression(expression);
		return new ValueExpression() {

			private javax.el.ValueExpression facesValueExpression;
			private javax.el.ValueExpression seamValueExpression;
			final String val$expression;
			final Class val$type;
			final Expressions this$0;

			@Override
			public javax.el.ValueExpression toUnifiedValueExpression()
			{
				if(isFacesContextActive())
				{
					if(facesValueExpression == null)
					{
						facesValueExpression = createExpression();
					}
					return facesValueExpression;
				}
				if(seamValueExpression == null)
				{
					seamValueExpression = createExpression();
				}
				return seamValueExpression;
			}

			private javax.el.ValueExpression createExpression()
			{
				return getExpressionFactory().createValueExpression(getELContext(), expression, type);
			}

			@Override
			public Object getValue()
			{
				return toUnifiedValueExpression().getValue(getELContext());
			}

			@Override
			public void setValue(Object value)
			{
				toUnifiedValueExpression().setValue(getELContext(), value);
			}

			@Override
			public String getExpressionString()
			{
				return expression;
			}

			@Override
			public Class getType()
			{
				return toUnifiedValueExpression().getType(getELContext());
			}


			{
				this$0 = Expressions.this;
				expression = s;
				type = class1;
				super();
			}
		};
	}

	public transient MethodExpression createMethodExpression(final String expression, final Class type, final Class argTypes[])
	{
		checkELExpression(expression);
		return new MethodExpression() {

			private javax.el.MethodExpression facesMethodExpression;
			private javax.el.MethodExpression seamMethodExpression;
			final String val$expression;
			final Class val$type;
			final Class val$argTypes[];
			final Expressions this$0;

			@Override
			public javax.el.MethodExpression toUnifiedMethodExpression()
			{
				if(isFacesContextActive())
				{
					if(facesMethodExpression == null)
					{
						facesMethodExpression = createExpression();
					}
					return facesMethodExpression;
				}
				if(seamMethodExpression == null)
				{
					seamMethodExpression = createExpression();
				}
				return seamMethodExpression;
			}

			private javax.el.MethodExpression createExpression()
			{
				return getExpressionFactory().createMethodExpression(getELContext(), expression, type, argTypes);
			}

			@Override
			public transient Object invoke(Object args[])
			{
				return toUnifiedMethodExpression().invoke(getELContext(), args);
			}

			@Override
			public String getExpressionString()
			{
				return expression;
			}


			{
				this$0 = Expressions.this;
				expression = s;
				type = class1;
				argTypes = aclass;
				super();
			}
		};
	}

	protected boolean isFacesContextActive()
	{
		return false;
	}

	public static Expressions instance()
	{
		if(!Contexts.isApplicationContextActive())
		{
			return new Expressions();
		} else
		{
			return (Expressions)Component.getInstance(com.intelinet.seam.Expressions.class, ScopeType.APPLICATION);
		}
	}

	private static void checkELExpression(String expression)
	{
		for(int index = 0; blacklist.size() > index; index++)
		{
			if(expression.contains((CharSequence)blacklist.get(index)))
			{
				throw new IllegalArgumentException("This EL expression is not allowed!");
			}
		}

		if(expression.contains(".getClass()"))
		{
			throw new IllegalArgumentException("This EL expression is not allowed!");
		} else
		{
			return;
		}
	}

	static
	{
		BufferedReader reader;
		log = Logging.getLogProvider(com.intelinet.seam.Expressions.class);
		blacklist = new ArrayList();
		reader = null;
		java.io.InputStream blacklistIS = ResourceLoader.instance().getResourceAsStream("blacklist.properties");
		reader = new BufferedReader(new InputStreamReader(blacklistIS));
		String line;
		while((line = reader.readLine()) != null)
		{
			blacklist.add(line);
		}
		IOException e;
		if(reader != null)
		{
			try
			{
				reader.close();
			}
			// Misplaced declaration of an exception variable
			catch(IOException e) { }
		}
		break MISSING_BLOCK_LABEL_125;
		e;
		log.warn("Black list of non-valid EL expressions was not found!");
		if(reader != null)
		{
			try
			{
				reader.close();
			}
			// Misplaced declaration of an exception variable
			catch(IOException e) { }
		}
		break MISSING_BLOCK_LABEL_125;
		Exception exception;
		exception;
		if(reader != null)
		{
			try
			{
				reader.close();
			}
			catch(IOException e) { }
		}
		throw exception;
	}
}
