package hexlet.code;

import hexlet.code.model.Url;
import hexlet.code.repository.UrlsRepository;
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
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThat;

public class AppTest {

    public static Javalin app;
    public static MockWebServer mockWebServer;

    public static String readFixture(String fileName) throws IOException {
        Path filePath = Paths.get("src/test/resources", fileName);
        return new String(Files.readAllBytes(filePath));
    }

    @BeforeEach
    public final void setUp() throws IOException, SQLException {
        mockWebServer = new MockWebServer();
        MockResponse mockedResponse = new MockResponse()
                .setBody(readFixture("index.html"));
        mockWebServer.enqueue(mockedResponse);
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
            assert response.body() != null;
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
            var requestBody = "name=https://example.edu/";
            var response = client.post(NamedRoutes.urlsPath(), requestBody);
            assertThat(response.code()).isEqualTo(200);
            assertThat(response.body().string()).contains("https://example.edu");

            response = client.get(NamedRoutes.urlsPath());
            assertThat(response.code()).isEqualTo(200);
            assertThat(response.body().string()).contains("https://example.edu");

            response = client.get(NamedRoutes.urlPath(1L));
            assertThat(response.code()).isEqualTo(200);
            assertThat(response.body().string()).contains("https://example.edu");
        });
    }

    @Test
    public void testSave() throws URISyntaxException, MalformedURLException, SQLException {
        var url = new Url("https://ru.hexlet.io");
        UrlsRepository.save(url);

        JavalinTest.test(app, (server, client) -> {
            var response = client.get(NamedRoutes.urlPath(UrlsRepository.findByName(url.getName()).get().getId()));
            assertThat(response.code()).isEqualTo(200);
        });
    }

    @Test
    public void testCheck() throws SQLException {

        String mockUrl = mockWebServer.url("/").toString();
        Url url = new Url(mockUrl);
        UrlsRepository.save(url);

        JavalinTest.test(app, (server, client) -> {
            Url savedUrl = UrlsRepository.findByName(mockWebServer.url("/").toString()).orElseThrow();
            var response = client.post(NamedRoutes.checksPath(savedUrl.getId()));
            assertThat(response.code()).isEqualTo(200);

            response = client.get(NamedRoutes.urlPath(1L));
            assertThat(response.code()).isEqualTo(200);
            assertThat(response.body().string()).contains("Example Domain");

            response = client.get(NamedRoutes.urlsPath());
            assertThat(response.code()).isEqualTo(200);

        });
    }

}
