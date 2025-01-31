package hexlet.code;

import hexlet.code.model.Url;
import hexlet.code.repository.UrlRepository;
import hexlet.code.util.NamedRoutes;
import io.javalin.Javalin;
import io.javalin.testtools.JavalinTest;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThat;

public class AppTest {

    public static Javalin app;
    public static MockWebServer mockWebServer;

    @BeforeEach
    public final void setUp() throws IOException, SQLException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();
        app = App.getApp();
    }

    @AfterAll
    public static void tearDown() throws IOException {
        mockWebServer.shutdown();
    }

    @Test
    public void testMainPage() {
        JavalinTest.test(app, (server, client) -> {
            var response = client.get(NamedRoutes.mainPage());
            assertThat(response.code()).isEqualTo(200);
            assertThat(response.body().string()).contains("Анализатор страниц");
        });
    }

    @Test
    public void testUrlsPage() {
        JavalinTest.test(app, (server, client) -> {
            var response = client.get(NamedRoutes.urlsPath());
            assertThat(response.code()).isEqualTo(200);
        });
    }

    @Test
    public void testBuildUserPage() {
        JavalinTest.test(app, (server, client) -> {
            var response = client.get(NamedRoutes.buildPath());
            assertThat(response.code()).isEqualTo(200);
        });
    }

    @Test
    public void testCreateUrl() {
        JavalinTest.test(app, (server, client) -> {
            var requestBody = "name=https://ru.hexlet.io/projects/72/members/41454?step=6";
            var response = client.post(NamedRoutes.urlsPath(), requestBody);
            assertThat(response.code()).isEqualTo(200);
            assertThat(response.body().string()).contains("https://ru.hexlet.io");
        });
    }

    @Test
    public void testSave() throws URISyntaxException, MalformedURLException, SQLException {
        var url = new Url("https://ru.hexlet.io");
        UrlRepository.save(url);

        JavalinTest.test(app, (server, client) -> {
            var response = client.get(NamedRoutes.urlPath(UrlRepository.search(url.getName()).get().getId()));
            assertThat(response.code()).isEqualTo(200);
        });
    }

    @Test
    public void testCheck() {
        JavalinTest.test(app, (server, client) -> {
            String responseBody = "Hello, MockWebServer!";

            mockWebServer.enqueue(new MockResponse().setBody(responseBody).setResponseCode(200));

            String baseUrl = mockWebServer.url("/").toString();

            client.post(NamedRoutes.urlsPath(), "name=" + baseUrl);

            var urlsResponse = client.get(NamedRoutes.urlsPath());
            assertThat(urlsResponse.code()).isEqualTo(200);
            });
    }

}
