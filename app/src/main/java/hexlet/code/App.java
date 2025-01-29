package hexlet.code;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import gg.jte.ContentType;
import gg.jte.TemplateEngine;
import gg.jte.resolve.ResourceCodeResolver;
import hexlet.code.dto.BasePage;
import hexlet.code.dto.urls.UrlPage;
import hexlet.code.dto.urls.UrlsPage;
import hexlet.code.model.Url;
import hexlet.code.repository.BaseRepository;
import hexlet.code.repository.UrlRepository;
import io.javalin.Javalin;
import io.javalin.http.NotFoundResponse;
import io.javalin.rendering.template.JavalinJte;
import lombok.extern.slf4j.Slf4j;
import org.apache.hc.core5.net.URIBuilder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.stream.Collectors;

import static io.javalin.rendering.template.TemplateUtil.model;

@Slf4j
public class App {

    private static int getPort() {
        String port = System.getenv().getOrDefault("DB_PORT", "7070");
        return Integer.valueOf(port);
    }

    private static String readResourceFile(String fileName) throws IOException {
        var inputStream = App.class.getClassLoader().getResourceAsStream(fileName);
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            return reader.lines().collect(Collectors.joining("\n"));
        }
    }

    //-------------------------------------------------------------------------------

    public static Javalin getApp() throws SQLException, IOException {

        var hikariConfig = new HikariConfig();
        var dataBase = System.getenv()
                .getOrDefault("JDBC_DATABASE_URL", "jdbc:h2:mem:project;DB_CLOSE_DELAY=-1;");
        if (dataBase.contains("postgresql")) {
            hikariConfig.setDriverClassName("org.postgresql.Driver");
        }

        hikariConfig.setJdbcUrl(dataBase);
        log.info("jdbcUrl: " + dataBase);

        var dataSource = new HikariDataSource(hikariConfig);
        var sql = readResourceFile("urls.sql");

        log.info(sql);
        try (var connection = dataSource.getConnection();
             var statement = connection.createStatement()) {
            statement.execute(sql);
        }
        BaseRepository.dataSource = dataSource;

        var app = Javalin.create(config -> {
            config.bundledPlugins.enableDevLogging();
            config.fileRenderer(new JavalinJte(createTemplateEngine()));
        });

        //-------------------------------------------------------------------------------

        app.get("/", ctx -> ctx.render("urls/build.jte"));

        app.get("/urls", ctx -> {
            var listUrls = UrlRepository.getEntities();
            var page = new UrlsPage(listUrls);
            page.setFlash(ctx.consumeSessionAttribute("flash")); // ОТРАБОТКА ФЛЕШ СООБЩЕНИЙ
            ctx.render("urls/index.jte", model("page", page));
        });

        app.get("/urls/build", ctx -> {
            var page = new BasePage();
            page.setFlash(ctx.consumeSessionAttribute("flash")); // ОТРАБОТКА ФЛЕШ СООБЩЕНИЙ
            ctx.render("urls/build.jte", model("page", page));
        });

        app.get("/urls/{id}", ctx -> {
            var id = ctx.pathParamAsClass("id", Long.class).get();

            var url = UrlRepository.find(id)
                    .orElseThrow(() -> new NotFoundResponse("Entity with id = " + id + " not found"));
            var page = new UrlPage(url);

            ctx.render("urls/show.jte", model("page", page));

        });

        app.post("/urls", ctx -> {
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
                ctx.sessionAttribute("flash", "Страница успешно добавлена"); // ДОБАВЛЕНИЕ ФЛЕШ СООБЩЕНИЯ О ДОБАВЛЕННИ НОВОГО САЙТА
                ctx.redirect("/urls");
            } catch (IOException e) {
                ctx.sessionAttribute("flash", e.getMessage());
                ctx.redirect("/urls");
            } catch (Exception e) {
                ctx.sessionAttribute("flash", "Некорректный URL");
                ctx.redirect("/urls/build");
            }
        });


        //-------------------------------------------------------------------------------

        return app;

    }

    //-------------------------------------------------------------------------------

    private static TemplateEngine createTemplateEngine() {
        ClassLoader classLoader = App.class.getClassLoader();
        ResourceCodeResolver codeResolver = new ResourceCodeResolver("templates", classLoader);
        TemplateEngine templateEngine = TemplateEngine.create(codeResolver, ContentType.Html);
        return templateEngine;
    }

    public static void main(String[] args) throws SQLException, IOException {
        var app = getApp();
        app.start(getPort());
    }

}
