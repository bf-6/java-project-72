package hexlet.code.controller;


import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import hexlet.code.model.UrlCheck;
import hexlet.code.repository.ChecksRepository;
import hexlet.code.repository.UrlsRepository;
import hexlet.code.util.NamedRoutes;
import io.javalin.http.Context;
import io.javalin.http.NotFoundResponse;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.sql.SQLException;

public class CheckController {

    public static void create(Context ctx) throws SQLException, UnirestException {
        var id = ctx.pathParamAsClass("id", Long.class).get();

        try {
            var url = UrlsRepository.find(id)
                    .orElseThrow(() -> new NotFoundResponse("Entity with id = " + id + " not found"));

            var response = Unirest.get(url.getName()).asString();
            Document document = Jsoup.parse(response.getBody());
            var code = response.getStatus();

            Element titleTag = document.selectFirst("title");
            String title = titleTag != null ? titleTag.text() : "";

            Element h1Tag = document.selectFirst("h1");
            String h1 = h1Tag != null ? h1Tag.text() : "";

            Element metaDescription = document.selectFirst("meta[name=description]");
            String description = metaDescription != null ? metaDescription.attr("content") : "";

            var urlCheck = new UrlCheck(code, title, h1, description, id);
            ChecksRepository.save(urlCheck);
            ctx.sessionAttribute("flash", "Страница успешно проверена");
            ctx.sessionAttribute("flash-type", "success");
            ctx.redirect(NamedRoutes.urlPath(url.getId()));
        } catch (UnirestException e) {
            ctx.sessionAttribute("flash", "Некорректный адрес");
            ctx.sessionAttribute("flash-type", "danger");
        } catch (Exception e) {
            ctx.sessionAttribute("flash", e.getMessage());
            ctx.sessionAttribute("flash-type", "danger");
        }

    }
}
