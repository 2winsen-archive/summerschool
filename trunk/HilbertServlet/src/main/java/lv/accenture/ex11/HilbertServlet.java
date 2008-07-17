package lv.accenture.ex11;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@SuppressWarnings("serial")
public class HilbertServlet extends HttpServlet {

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws IOException {
		
		response.setContentType("image/svg+xml; charset=UTF-8");
		PrintWriter out = response.getWriter();
		//out.println("<p>" + request.getParameter("a") + "</p>");
		
		Hilbert hil = new Hilbert();
		out.println(hil.hilbertSVG(Integer.parseInt(request.getParameter("order")), 
				request.getParameter("colour"), request.getParameter("title")));
	}

}