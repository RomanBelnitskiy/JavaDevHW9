package org.example;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

@WebServlet(value = "/time")
public class TimeServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/html; charset=utf-8");
        ZoneId zoneId = ZoneId.of("Etc/UTC");



        String timezone = req.getParameter("timezone");
        if (timezone != null && !timezone.isEmpty()) {
            timezone = timezone.replace(" ", "+");
            zoneId = ZoneId.ofOffset(timezone.substring(0, 3),
                    ZoneOffset.of(timezone.substring(3)));
        }

        DateTimeFormatter targetFormatter = DateTimeFormatter.ofPattern(
                "yyyy-MM-dd HH:mm:ss zzz");

        ZonedDateTime utcDateTime = LocalDateTime.now()
                .atZone(ZoneId.systemDefault())
                .withZoneSameInstant(zoneId);
        String formatterUtcDateTime = utcDateTime.format(targetFormatter);

        resp.getWriter().write(formatterUtcDateTime);
        resp.getWriter().close();
    }
}