/*
 * %W% %E%
 * 
 * Copyright (c) 2006, Oracle and/or its affiliates. All rights reserved.
 * ORACLE PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package com.sun.jmx.mbeanserver;

import static com.sun.jmx.mbeanserver.Util.*;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Map;
import java.util.HashMap;
import javax.management.Attribute;
import javax.management.MBeanServerConnection;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectName;

/**
   <p>Helper class for an {@link InvocationHandler} that forwards methods from an
   MXBean interface to a named
   MXBean in an MBean Server and handles translation between the
   arbitrary Java types in the interface and the Open Types used
   by the MXBean.</p>

   @since 1.6
*/
public class MXBeanProxy {
    public MXBeanProxy(Class<?> mxbeanInterface)
	    throws IllegalArgumentException {

	if (mxbeanInterface == null)
	    throw new IllegalArgumentException("Null parameter");

	final MBeanAnalyzer<ConvertingMethod> analyzer;
        try {
            analyzer =
                MXBeanIntrospector.getInstance().getAnalyzer(mxbeanInterface);
        } catch (NotCompliantMBeanException e) {
            throw new IllegalArgumentException(e);
        }
	analyzer.visit(new Visitor());
    }

    private class Visitor implements MBeanAnalyzer.MBeanVisitor<ConvertingMethod> {
	public void visitAttribute(String attributeName,
				   ConvertingMethod getter,
				   ConvertingMethod setter) {
	    if (getter != null) {
		getter.checkCallToOpen();
		Method getterMethod = getter.getMethod();
		handlerMap.put(getterMethod,
                               new GetHandler(attributeName, getter));
	    }
	    if (setter != null) {
		// return type is void, no need for checkCallToOpen
		Method setterMethod = setter.getMethod();
		handlerMap.put(setterMethod,
                               new SetHandler(attributeName, setter));
	    }
	}

	public void visitOperation(String operationName,
				   ConvertingMethod operation) {
	    operation.checkCallToOpen();
	    Method operationMethod = operation.getMethod();
	    String[] sig = operation.getOpenSignature();
	    handlerMap.put(operationMethod,
			   new InvokeHandler(operationName, sig, operation));
	}
    }

    private static abstract class Handler {
	Handler(String name, ConvertingMethod cm) {
	    this.name = name;
            this.convertingMethod = cm;
	}

	String getName() {
	    return name;
	}
        
        ConvertingMethod getConvertingMethod() {
            return convertingMethod;
        }

	abstract Object invoke(MBeanServerConnection mbsc,
                               ObjectName name, Object[] args) throws Exception;

	private final String name;
        private final ConvertingMethod convertingMethod;
    }

    private static class GetHandler extends Handler {
	GetHandler(String attributeName, ConvertingMethod cm) {
	    super(attributeName, cm);
	}

	@Override
        Object invoke(MBeanServerConnection mbsc, ObjectName name, Object[] args)
                throws Exception {
	    assert(args == null || args.length == 0);
	    return mbsc.getAttribute(name, getName());
	}
    }

    private static class SetHandler extends Handler {
	SetHandler(String attributeName, ConvertingMethod cm) {
	    super(attributeName, cm);
	}

	@Override
        Object invoke(MBeanServerConnection mbsc, ObjectName name, Object[] args)
                throws Exception {
	    assert(args.length == 1);
	    Attribute attr = new Attribute(getName(), args[0]);
	    mbsc.setAttribute(name, attr);
	    return null;
	}
    }

    private static class InvokeHandler extends Handler {
	InvokeHandler(String operationName, String[] signature,
                      ConvertingMethod cm) {
	    super(operationName, cm);
	    this.signature = signature;
	}

        Object invoke(MBeanServerConnection mbsc, ObjectName name, Object[] args)
                throws Exception {
	    return mbsc.invoke(name, getName(), args, signature);
	}

	private final String[] signature;
    }

    public Object invoke(MBeanServerConnection mbsc, ObjectName name,
                         Method method, Object[] args)
	    throws Throwable {

	Handler handler = handlerMap.get(method);
	ConvertingMethod cm = handler.getConvertingMethod();
        MXBeanLookup lookup = MXBeanLookup.lookupFor(mbsc);
        Object[] openArgs = cm.toOpenParameters(lookup, args);
        Object result = handler.invoke(mbsc, name, openArgs);
        return cm.fromOpenReturnValue(lookup, result);
    }

    private final Map<Method, Handler> handlerMap = newMap();
}
