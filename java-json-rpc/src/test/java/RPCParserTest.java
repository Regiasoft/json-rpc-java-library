import java.lang.reflect.Method;

import org.junit.Assert;
import org.junit.Test;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.regiasoft.libs.jsonrpc.RPCParser;
import com.regiasoft.libs.jsonrpc.RPCRequest;
import com.regiasoft.libs.jsonrpc.RPCResponse;


public class RPCParserTest {

	@Test
	public void testCallProcedure() {
		
		RPCParser<TestClass> parser 
			= new RPCParser<TestClass>(TestClass.class, new TestClass());
		
		RPCRequest req = new RPCRequest();
		req.setMethod("add");
		
		JsonElement je = new JsonParser().parse("[1,2]");
		req.setParams(je.getAsJsonArray());
		
		RPCResponse resp = parser.callProcedure(req);
		Assert.assertNotNull(resp);
		if (resp.getResult() == null) {
			System.out.println(resp.getError());
		}
		Assert.assertNotNull(resp.getResult());
		Assert.assertEquals((int) resp.getResult(), 3);
		
		req = new RPCRequest();
		req.setMethod("printText");
		req.setParams(new JsonParser().parse("[\"TextTextText\"]").getAsJsonArray());
		resp = parser.callProcedure(req);
		
		Assert.assertTrue((boolean) resp.getResult()); 
	}

	@Test
	public void testGson() {
//		
//		TestBean b = new TestBean();
//		
//		b.setTestStringProp("teststring");
//		b.setTestIntProp(2);
//		b.setTestIntegerProp(1);
//		
//		String s = g.toJson(b);
//		System.out.println(s);
		
		String testjson = "[12, 5, {\"testStringProp\":\"teststring\","
				+ "\"testIntProp\":2,\"testIntegerProp\":1}]";
//		String testjson = "[12, \"testStringProp\", {\"testStringProp\":\"teststring\","
//				+ "\"testIntProp\":2,\"testIntegerProp\":1}]";
//		
//		String testjson = "[12, \"testStringProp\"]";
		
		JsonParser jp = new JsonParser();
		JsonArray ja = jp.parse(testjson).getAsJsonArray();
		
		RPCParser<TestClass> parser 
			= new RPCParser<TestClass>(TestClass.class, new TestClass());
		
		Method m = parser.predictMethod("predictableMethod", ja);
		
		if (m != null) {
			for (Class<?> c : m.getParameterTypes()) {
				System.out.print(c.getName()+" ");
			}
		} else {
			System.out.println("no method found");
		}
	}
	
	public static class TestBean {
		private String testStringProp;
		private int testIntProp;
		private Integer testIntegerProp;
		
		public String getTestStringProp() {
			return testStringProp;
		}
		public void setTestStringProp(String testStringProp) {
			this.testStringProp = testStringProp;
		}
		public int getTestIntProp() {
			return testIntProp;
		}
		public void setTestIntProp(int testIntProp) {
			this.testIntProp = testIntProp;
		}
		public Integer getTestIntegerProp() {
			return testIntegerProp;
		}
		public void setTestIntegerProp(Integer testIntegerProp) {
			this.testIntegerProp = testIntegerProp;
		}		
	}
	
	public static class TestClass {
		
		public void predictableMethod(int i, String s, TestBean bean) {
			System.out.println("method1");
		}
		
		public void predictableMethod(int i, String s) {
			System.out.println("method2");
		}
		
		public void predictableMethod(int i, int s, TestBean bean) {
			System.out.println("method3");
		}
		public int add(int a, int b) {
			return a+b;
		}
		
		public boolean printText(String text) {
			return true;
		}
	}
}
