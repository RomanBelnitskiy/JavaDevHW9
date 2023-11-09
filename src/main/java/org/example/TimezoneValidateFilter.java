package org.example;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebFilter(value = "/time")
public class TimezoneValidateFilter extends HttpFilter {
    @Override
    protected void doFilter(HttpServletRequest req, HttpServletResponse res, FilterChain chain) throws IOException, ServletException {
        String timeZoneParam = req.getParameter("timezone");
        if (timeZoneParam == null) {
            chain.doFilter(req, res);
        } else if (ZoneIdValidator.validate(timeZoneParam)) {
            req.setAttribute("zoneId", ZoneIdValidator.getZoneId());
            chain.doFilter(req, res);
        }

        res.setStatus(400);
        res.setContentType("text/html; charset=utf-8");
        res.getWriter().write("Invalid timezone");
        res.getWriter().close();
    }
}
