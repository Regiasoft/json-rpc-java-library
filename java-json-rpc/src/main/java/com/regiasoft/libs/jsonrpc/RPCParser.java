package com.regiasoft.libs.jsonrpc;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.util.ClassUtils;

public class RPCParser<T> {
	
	protected Class<T> interfaceClass;
	protected T implementationObject;
	
	protected Map<String, List<Method>> methods;
	
	public RPCParser(Class<T> interfaceClass, T implementation) {
		this.interfaceClass = interfaceClass;
		this.implementationObject = implementation;
		
		readMethods();
	}
	
	/**
	 * This methods reads the invokable methods from the interface
	 */
	protected void readMethods() {

		this.methods = new HashMap<String, List<Method>>();
		
		for(Method m : interfaceClass.getMethods()) {
			
			if (!methods.containsKey(m.getName())) {
				methods.put(m.getName(), new ArrayList<Method>());
			}
			
			methods.get(m.getName()).add(m);			
		}
		
	}
	
	public RPCResponse callProcedure(RPCRequest request) {
		
		RPCResponse response = new RPCResponse();
		response.setId(request.getId());
		response.setVersion(request.getVersion());
		
		List<Method> possibleMethods = this.methods.get(request.getMethod());
		
		if (possibleMethods == null) {
			response.setError(new MethodNotFoundError().toString());
			
			return response;
		}
		
		Method matchingMethod = null;
		for (Method m : possibleMethods) {
			
			Class<?>[] paramTypes = m.getParameterTypes();
			
			boolean isMatching = true;
			
			if (paramTypes.length == request.getParams().length) {
				
				for(int i = 0; i < paramTypes.length; i++) {
					
					Class<?> type = paramTypes[i];
					// if the type is a primitive => resolve this primitive
					if (type.isPrimitive()) {
						type = ClassUtils.resolvePrimitiveIfNecessary(type);
					}
					
					if (request.getParams()[i] != null) {						
						if (!type.isInstance(request.getParams()[i])) {
							isMatching = false;
						}
					}
				}
			} else {
				isMatching = false;
			}
			
			if (isMatching) {
				matchingMethod = m;
				break;
			}
		}
		
		if (matchingMethod == null) {
			response.setError(new MethodNotFoundError().toString());
			
			return response;
		}
		
		// invoke the found method
		try {
			Object result = matchingMethod.invoke(implementationObject, request.getParams());
			response.setResult(result);
		} catch (IllegalArgumentException | IllegalAccessException e) {
			response.setError(e.toString());
		} catch (InvocationTargetException e) {
			response.setError(e.getTargetException().toString());
		}
		
		return response;
	}
}
