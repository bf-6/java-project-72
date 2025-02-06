package hexlet.code.controller;

import hexlet.code.dto.BasePage;
import hexlet.code.dto.urls.UrlPage;
import hexlet.code.dto.urls.UrlsPage;
import hexlet.code.model.Url;
import hexlet.code.repository.ChecksRepository;
import hexlet.code.repository.UrlsRepository;
import hexlet.code.util.NamedRoutes;
import io.javalin.http.Context;
import io.javalin.http.NotFoundResponse;
import org.apache.hc.core5.net.URIBuilder;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.sql.SQLException;

import static io.javalin.rendering.template.TemplateUtil.model;

public class UrlController {

    public static void index(Context ctx) throws SQLException {
        var listUrls = UrlsRepository.getEntities();
        var listChecks = ChecksRepository.getUrlCheckMap();
        var page = new UrlsPage(listUrls, listChecks);
        page.setFlash(ctx.consumeSessionAttribute("flash")); // ОТРАБОТКА ФЛЕШ СООБЩЕНИЙ
        page.setFlashType(ctx.consumeSessionAttribute("flash-type"));
        ctx.render("urls/index.jte", model("page", page));
    }

    public static void show(Context ctx) {
        var id = ctx.pathParamAsClass("id", Long.class).get();
        Url url;

        try {
            url = UrlsRepository.find(id)
                    .orElseThrow(() -> new NotFoundResponse("Страница не найдена"));
        } catch (Exception e) {
            ctx.sessionAttribute("flash", e.getMessage());
            ctx.sessionAttribute("flash-type", "danger");
            ctx.redirect(NamedRoutes.urlsPath());
            return;
        }

        var checks = ChecksRepository.getUrlChecks(id);
        var page = new UrlPage(url, checks);
        page.setFlash(ctx.consumeSessionAttribute("flash")); // ОТРАБОТКА ФЛЕШ СООБЩЕНИЙ
        page.setFlashType(ctx.consumeSessionAttribute("flash-type"));
        ctx.render("urls/show.jte", model("page", page));
    }

    public static void build(Context ctx) {
        var page = new BasePage();
        page.setFlash(ctx.consumeSessionAttribute("flash")); // ОТРАБОТКА ФЛЕШ СООБЩЕНИЙ
        page.setFlashType(ctx.consumeSessionAttribute("flash-type"));
        ctx.render("urls/build.jte", model("page", page));
    }

    public static void create(Context ctx) throws IOException, SQLException, URISyntaxException {
        var name = ctx.formParamAsClass("url", String.class).get();

        URL uri = null;
        try {
            uri = new URI(name).toURL();
        } catch (Exception e) {
            ctx.sessionAttribute("flash", "Некорректный URL");
            ctx.sessionAttribute("flash-type", "danger");
            ctx.redirect(NamedRoutes.buildPath());
            return;
        }

        var protocol = uri.getProtocol();
        var port = uri.getPort();
        var host = uri.getHost();

        URL url = new URIBuilder().setScheme(protocol).setHost(host).setPort(port).build().toURL();

        if (UrlsRepository.findByName(String.valueOf(url)).isEmpty()) {
            var currentUrl = new Url(String.valueOf(url));
            UrlsRepository.save(currentUrl);
        } else {
            ctx.sessionAttribute("flash", "Страница уже существует");
            ctx.sessionAttribute("flash-type", "info");
            ctx.redirect(NamedRoutes.urlsPath());
            return;
        }

        // ДОБАВЛЕНИЕ ФЛЕШ СООБЩЕНИЯ О ДОБАВЛЕННИ НОВОГО САЙТА
        ctx.sessionAttribute("flash", "Страница успешно добавлена");
        ctx.sessionAttribute("flash-type", "success");
        ctx.redirect(NamedRoutes.urlsPath());
    }

}
