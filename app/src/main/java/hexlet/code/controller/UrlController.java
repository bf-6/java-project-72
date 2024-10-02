package hexlet.code.controller;

import hexlet.code.dto.urls.UrlsPage;
import hexlet.code.repository.UrlRepository;
import io.javalin.http.Context;

import java.sql.SQLException;

import static io.javalin.rendering.template.TemplateUtil.model;

public class UrlController {

    public static void index(Context ctx) throws SQLException {
        var urls = UrlRepository.getEntities();
        var page = new UrlsPage(urls);
        ctx.render("urls/index.jte", model("page", page));
    }

}
