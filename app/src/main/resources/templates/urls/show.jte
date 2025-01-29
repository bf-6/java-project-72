@import hexlet.code.dto.urls.UrlPage
@param UrlPage page

@template.layout.page(
content = @`
    <div>${page.getUrl().getName()} ---> ${page.getUrl().getCreatedAt().toString()}</div>
`
)
