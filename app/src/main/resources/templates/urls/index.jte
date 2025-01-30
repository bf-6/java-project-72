@import hexlet.code.dto.urls.UrlsPage
@import hexlet.code.util.NamedRoutes
@param UrlsPage page

@template.layout.page(
page = page,
content = @`
    <p>
        URLS:
    </p>
    @for(var url : page.getUrls())
        <div> ${url.getId()} <a href="${NamedRoutes.urlPath(url.getId())}">${url.getName()}</a> ${String.valueOf(url.getCreatedAt())}</div>
    @endfor
`
)