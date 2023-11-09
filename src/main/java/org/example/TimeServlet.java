package org.example;

import org.thymeleaf.ITemplateEngine;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.WebApplicationTemplateResolver;
import org.thymeleaf.web.IWebApplication;
import org.thymeleaf.web.IWebExchange;
import org.thymeleaf.web.servlet.JavaxServletWebApplication;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Optional;

@WebServlet(value = "/time")
public class TimeServlet extends HttpServlet {
    private DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss zzz");
    private JavaxServletWebApplication application;
    private ITemplateEngine templateEngine;


    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);

        this.application = JavaxServletWebApplication.buildApplication(config.getServletContext());
        this.templateEngine = buildTemplateEngine(application);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Cache-Control", "no-cache");
        response.setDateHeader("Expires", 0);


        ZoneId zoneId = getZoneId(request);
        response.addCookie(new Cookie("lastTimezone", zoneId.getId()));
        ZonedDateTime utcDateTime = getZonedDateTime(zoneId);

        final IWebExchange webExchange = this.application.buildExchange(request, response);
        WebContext context = new WebContext(webExchange, webExchange.getLocale());
        context.setVariable("today", utcDateTime);
        context.setVariable("df", dateTimeFormatter);

        templateEngine.process("time", context, response.getWriter());
        response.getWriter().close();
    }

    private ZoneId getZoneId(HttpServletRequest request) {
        Object zoneIdObject = request.getAttribute("zoneId");
        if (zoneIdObject != null) {
            return (ZoneId) zoneIdObject;
        }

        Optional<Cookie> cookieOptional = getLastTimezoneCookie(request);
        if (cookieOptional.isPresent()) {
            Cookie timezoneCookie = cookieOptional.get();
            String timezone = timezoneCookie.getValue();
            return ZoneId.of(timezone);
        }

        return ZoneId.of("Etc/UTC");
    }

    private Optional<Cookie> getLastTimezoneCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null || cookies.length == 0) {
            return Optional.empty();
        }
        return Arrays.stream(cookies)
                .filter(cookie -> "lastTimezone".equals(cookie.getName()))
                .findFirst();
    }

    private ZonedDateTime getZonedDateTime(ZoneId zoneId) {
        return LocalDateTime.now()
                .atZone(ZoneId.systemDefault())
                .withZoneSameInstant(zoneId);
    }

    private static ITemplateEngine buildTemplateEngine(final IWebApplication application) {

        final WebApplicationTemplateResolver templateResolver = new WebApplicationTemplateResolver(application);

        templateResolver.setTemplateMode(TemplateMode.HTML);
        templateResolver.setPrefix("/WEB-INF/templates/");
        templateResolver.setSuffix(".html");
        templateResolver.setCacheTTLMs(Long.valueOf(3600000L));
        templateResolver.setCacheable(false);

        final TemplateEngine templateEngine = new TemplateEngine();
        templateEngine.setTemplateResolver(templateResolver);

        return templateEngine;
    }
}