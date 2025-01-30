package hexlet.code.controller;

import hexlet.code.dto.BasePage;
import hexlet.code.dto.urls.UrlPage;
import hexlet.code.dto.urls.UrlsPage;
import hexlet.code.model.Url;
import hexlet.code.repository.UrlRepository;
import hexlet.code.util.NamedRoutes;
import io.javalin.http.Context;
import io.javalin.http.NotFoundResponse;
import org.apache.hc.core5.net.URIBuilder;

import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.sql.SQLException;

import static io.javalin.rendering.template.TemplateUtil.model;

public class UrlController {

    public static void index(Context ctx) throws SQLException {
        var listUrls = UrlRepository.getEntities();
        var page = new UrlsPage(listUrls);
        page.setFlash(ctx.consumeSessionAttribute("flash")); // ОТРАБОТКА ФЛЕШ СООБЩЕНИЙ
        ctx.render("urls/index.jte", model("page", page));
    }

    public static void show(Context ctx) throws SQLException {
        var id = ctx.pathParamAsClass("id", Long.class).get();
        var url = UrlRepository.find(id)
                .orElseThrow(() -> new NotFoundResponse("Entity with id = " + id + " not found"));
        var page = new UrlPage(url);
        ctx.render("urls/show.jte", model("page", page));
    }

    public static void build(Context ctx) {
        var page = new BasePage();
        page.setFlash(ctx.consumeSessionAttribute("flash")); // ОТРАБОТКА ФЛЕШ СООБЩЕНИЙ
        ctx.render("urls/build.jte", model("page", page));
    }

    public static void create(Context ctx) {
        var name = ctx.formParamAsClass("name", String.class).get();

        try {
            URL uri = new URI(name).toURL();
            var protocol = uri.getProtocol();
            var port = uri.getPort();
            var host = uri.getHost();

            URL url = new URIBuilder().setScheme(protocol).setHost(host).setPort(port).build().toURL();

            if (UrlRepository.search(String.valueOf(url)).isEmpty()) {
                var currentUrl = new Url(String.valueOf(url));
                UrlRepository.save(currentUrl);
            } else {
                throw new IOException("Страница уже существует");
            }
            // ДОБАВЛЕНИЕ ФЛЕШ СООБЩЕНИЯ О ДОБАВЛЕННИ НОВОГО САЙТА
            ctx.sessionAttribute("flash", "Страница успешно добавлена");
            ctx.redirect(NamedRoutes.urlsPath());
        } catch (IOException e) {
            ctx.sessionAttribute("flash", e.getMessage());
            ctx.redirect(NamedRoutes.urlsPath());
        } catch (Exception e) {
            ctx.sessionAttribute("flash", "Некорректный URL");
            ctx.redirect(NamedRoutes.buildPath());
        }
    }

}
