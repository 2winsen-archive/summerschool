package lv.accenture.ex06;

import static org.junit.Assert.*;

import org.junit.Test;

public class FSResourceListTest {
	
	@Test
	public void testGetAll() {
		FSResourceList dra = new FSResourceList("/home/student/workspace/ex06/src/main/resources/retiman");
		String[] res = dra.getAllResources();
		assertEquals(288, res.length);
		
	}

}
