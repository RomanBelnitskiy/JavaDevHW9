package org.example;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.ZoneId;
import java.time.ZoneOffset;

@WebFilter(value = "/time")
public class TimezoneValidateFilter extends HttpFilter {
    @Override
    protected void doFilter(HttpServletRequest req, HttpServletResponse res, FilterChain chain) throws IOException, ServletException {
        String timezone = req.getParameter("timezone");
        if (timezone == null) {
            chain.doFilter(req, res);
        } else {
            try {
                timezone = timezone.replace(" ", "+");
                if (timezone.length() == 3) {
                    ZoneId.of(timezone);
                } else {
                    ZoneId.ofOffset(timezone.substring(0, 3),
                            ZoneOffset.of(timezone.substring(3)));
                }

                chain.doFilter(req, res);
            } catch (Exception ex) {
                res.setStatus(400);
                res.setContentType("text/html; charset=utf-8");
                res.getWriter().write("Invalid timezone");
                res.getWriter().close();
            }
        }
    }
}
