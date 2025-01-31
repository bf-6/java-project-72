@import hexlet.code.dto.urls.UrlsPage
@import hexlet.code.util.NamedRoutes
@param UrlsPage page

@template.layout.page(
page = page,
content = @`
    <main class="flex-grow-1">
        <section>
            <div class="container-lg mt-5">
                <h1>Сайты</h1>
                <table class="table table-bordered table-hover mt-3">
                    <thead>
                    <tr>
                        <th class="col-1">ID</th>
                        <th>Имя</th>
                        <th class="col-2">Последняя проверка</th>
                        <th class="col-1">Код ответа</th>
                    </tr>
                    </thead>
                    <tbody>
                    @if(!page.getUrls().isEmpty())
                        @for(var url : page.getUrls())
                            <tr>
                                <td>${url.getId()}</td>
                                <td><a href="${NamedRoutes.urlPath(url.getId())}">${url.getName()}</a></td>
                                <td>
                                    <%--@if(page.getLatestChecks().containsKey(url.getId()))
                                        ${page.getLatestChecks().get(url.getId()).getCreatedAt().toString()}
                                    @endif--%>
                                </td>
                                <td>
                                    <%--@if(page.getLatestChecks().containsKey(url.getId()))
                                        ${page.getLatestChecks().get(url.getId()).getStatusCode()}
                                    @endif--%>
                                </td>
                            </tr>
                        @endfor
                    @endif
                    </tbody>
                </table>
            </div>
        </section>
    </main>
`
)