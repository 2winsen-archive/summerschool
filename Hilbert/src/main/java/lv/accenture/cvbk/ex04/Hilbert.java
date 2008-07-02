package lv.accenture.cvbk.ex04;

public class Hilbert {
	
	private String strA = "v1,h1,v-1,";
	private String strC = "h-1,v-1,h1,";
	private String strD = "h1,v1,h-1,";
	private String strU = "v-1,h1,v1,";
	
  /*  public String sampleSVG() {
        return "<?xml version='1.0'?>\n"
            + "<!DOCTYPE svg PUBLIC '-//W3C//DTD SVG 1.0//EN'\n"
            + "'http://www.w3.org/TR/2001/REC-SVG-20010904/DTD/svg10.dtd'>\n"
            + "<svg width='10cm' height='10cm' viewBox='0 0 2 2'>\n"
            + "<title>Hilbert curve</title>\n"
            + "<desc>This is a sample SVG file for Hilbert curve</desc>\n"
            + "<rect x='0' y='0' width='2' height='2' fill='#cccccc'/>\n"
            + "<g transform='translate(0,2) scale(1 -1)'  \n"
            + "   style='stroke:blue;stroke-width:0.01;fill:none;'>\n"
            + "<path d='M,0.5,0.5,v1,h1,v-1'/>\n"
            + "</g>\n" + "</svg>\n";
	}*/

	public String hilbertSVG(int order) {
		StringBuffer sb = new StringBuffer();
		sb.append("<?xml version='1.0'?>\n<!DOCTYPE svg PUBLIC '-//W3C//DTD SVG 1.0//EN'\n");
		sb.append("'http://www.w3.org/TR/2001/REC-SVG-20010904/DTD/svg10.dtd'>\n");
		sb.append("<svg width='10cm' height='10cm' viewBox='0 0 ");
		sb.append(Math.pow(2, order) + " ");
		sb.append(Math.pow(2, order));
		sb.append("'>\n");
		sb.append("<title>Hilbert curve</title>\n");
		sb.append("<desc>This is a SVG file for Hilbert curve or order ");
		sb.append(order);
		sb.append("</desc>\n");
		sb.append("<rect x='0' y='0' width='" + Math.pow(2, order) + "' height='" +
				 + Math.pow(2, order) +"' fill='#cccccc'/>\n");
		sb.append("<g transform='translate(0," + Math.pow(2, order) + ") scale(1 -1)'  \n");
		sb.append("   style='stroke:blue;stroke-width:0.01;fill:none;'>\n");
		
		
		sb.append("<path d='M,0.5,0.5,");	
		changeSVG(order);
		sb.append(strA);
		
		
		sb.append("'/>\n");
		
		sb.append("</g>\n" + "</svg>\n");
		return sb.toString();
	}
	public void changeSVG(int order)
	{
		StringBuffer buf = new StringBuffer();
		buf.append(strD + ",");
		
		for(int i=1;i<order;i++)
		{
			if(i!=1)
				buf.append(strD + ",");
			
			buf.append("v1,");
			buf.append(strA + ",");
			buf.append("h1,");
			buf.append(strA + ",");
			buf.append("v-1,");
			buf.append(strC + ",");
			
			//change variables
			strA = buf.toString();
			strC = rotateCSVG(strA);
			strD = rotateDSVG(strA);
			
			buf.delete(0, buf.capacity());			
		}
	}
	
	//v1 -> a
	//v-1 -> b
	//h1 -> c
	//h-1 -> d
	
	public String rotateDSVG(String s)
	{
		s = s.replaceAll("v1", "c");
		s = s.replaceAll("v-1", "d");
		s = s.replaceAll("h1", "a");
		s = s.replaceAll("h-1", "b");
		
		//return string right
		s = s.replaceAll("a", "v1");
		s = s.replaceAll("b", "v-1");
		s = s.replaceAll("c", "h1");
		s = s.replaceAll("d", "h-1");
		
		return s;
	}
	public String rotateCSVG(String s)
	{
		s = s.replaceAll("v1", "d");
		s = s.replaceAll("v-1", "c");
		s = s.replaceAll("h1", "b");
		s = s.replaceAll("h-1", "a");
		
		//return string right
		s = s.replaceAll("a", "v1");
		s = s.replaceAll("b", "v-1");
		s = s.replaceAll("c", "h1");
		s = s.replaceAll("d", "h-1");
		
		return s;
	}
}