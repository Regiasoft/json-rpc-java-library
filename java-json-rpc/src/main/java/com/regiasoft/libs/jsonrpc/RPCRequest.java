package com.regiasoft.libs.jsonrpc;

import com.google.gson.JsonArray;


public class RPCRequest {
	
	private String version;
	private String method;
	private JsonArray params;
	private int id;
	
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	public String getMethod() {
		return method;
	}
	public void setMethod(String method) {
		this.method = method;
	}
	public JsonArray getParams() {
		return params;
	}
	public void setParams(JsonArray params) {
		this.params = params;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
}
