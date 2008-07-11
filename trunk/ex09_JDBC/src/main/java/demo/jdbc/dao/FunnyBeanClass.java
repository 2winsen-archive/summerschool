package demo.jdbc.dao;

import java.util.List;

public class FunnyBeanClass {
	private int aaa;
	private String bbb;
	private List<String> ccc;

	public void setAaa(int aaa) {
		this.aaa = aaa;
	}

	public void setBbb(String bbb) {
		this.bbb = bbb;
	}

	public void setCcc(List<String> ccc) {
		this.ccc = ccc;
	}

	public String toString() {
		return "[aaa=" + aaa + ",bbb='" + bbb + "',ccc=" + ccc + "]";
	}

}
