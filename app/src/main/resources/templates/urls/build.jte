@import hexlet.code.dto.BasePage
@param BasePage page

@template.layout.page(
page = page,
content = @`
<form action="/urls" method="post">
    <div>
        <label>
            Name
            <input type="text" name="name" />
        </label>
    </div>
    <input type="submit" value="Проверить" />
</form>
`
)
