package com.regiasoft.libs.jsonrpc;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonSyntaxException;

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
	
	public Method predictMethod(String name, JsonArray args) {
		
		List<Method> m = this.methods.get(name);
				
		//first remove the ones with wrong param length
		m.removeIf((method) -> method.getParameterCount() != args.size());
		
		// then test for correct param types
		Gson gson = new Gson();
		m.removeIf((method) -> {
			Class<?>[] classes = method.getParameterTypes();
			
			for (int i = 0; i < args.size(); i++) {
			
				JsonElement e = args.get(i);
				
				try {
					gson.fromJson(e, classes[i]);					
				} catch (JsonSyntaxException ex) {
					return true;					
				}
			}
			return false;
		});
				
		// only the matching should be left
		if (m.size() > 0) {
			return m.get(0);
		} else {
			return null;
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
		
		Method matchingMethod = predictMethod(request.getMethod(), request.getParams());
		
		if (matchingMethod == null) {
			response.setError(new MethodNotFoundError().toString());
			
			return response;
		}
	
		// invoke the found method
		try {
			//parse the parameters
			Object[] params = new Object[request.getParams().size()];
			
			Gson gson = new Gson();
			for (int i = 0; i < request.getParams().size(); i++) {
				
				params[i] = gson.fromJson(request.getParams().get(i), 
						matchingMethod.getParameterTypes()[i]);				
			}
			
			Object result = matchingMethod.invoke(implementationObject, params);
			response.setResult(result);
			
		} catch (IllegalArgumentException | IllegalAccessException e) {
			response.setError(e.toString());
		} catch (InvocationTargetException e) {
			response.setError(e.getTargetException().toString());
		}
		
		return response;
	}
}
