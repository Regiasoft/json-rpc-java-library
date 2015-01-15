import org.junit.Assert;
import org.junit.Test;

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
		req.setParams(new Object[]{1, 2});
		
		RPCResponse resp = parser.callProcedure(req);
		Assert.assertNotNull(resp);
		if (resp.getResult() == null) {
			System.out.println(resp.getError());
		}
		Assert.assertNotNull(resp.getResult());
		Assert.assertEquals((int) resp.getResult(), 3);
		
		req = new RPCRequest();
		req.setMethod("printText");
		req.setParams(new Object[]{"dies ist ein text"});
		resp = parser.callProcedure(req);
		
		Assert.assertTrue((boolean) resp.getResult()); 
	}

	public class TestClass {
		
		public int add(int a, int b) {
			return a+b;
		}
		
		public boolean printText(String text) {
			System.out.println(text);
			return true;
		}
	}
}
