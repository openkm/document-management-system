package com.openkm.servlet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

public class HibernateFilter implements Filter {
	private static Logger log = LoggerFactory.getLogger(HibernateFilter.class);

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		log.info("Init filter");
	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		//boolean action = false;

		if (request instanceof HttpServletRequest) {
			HttpServletRequest httpRequest = (HttpServletRequest) request;
			String req = httpRequest.getRequestURL().toString();
			String params = httpRequest.getQueryString();
			if (!req.endsWith(".png") && !req.endsWith(".gif") && !req.endsWith(".ico")
					&& !req.endsWith(".css") && !req.endsWith(".js")) {
				log.info("ACT: {}", req + (params == null ? "" : "?" + params));
				//action = true;
			} else {
				//log.info("RES: {}", req + (params == null ? "": "?"+params));
			}
		} else {
			log.info("NOK: {}", request.getClass());
		}

		try {
			// Continue request processing)
			chain.doFilter(request, response);
		} finally {
			//if (action) {
			//HibernateUtil.closeSession();
			//}
		}
	}

	@Override
	public void destroy() {
		log.info("Destroy filter");
	}
}
