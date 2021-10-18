<html lang="cn">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title>FreeMarker Web</title>
</head>
<body>
<h1>Hello ${name} !</h1>
<h1>${comments.createTime?string("dd.MM.yyyy HH:mm:ss")}</h1>
<h1>${comments.getContent()}</h1>
<h1>${comments.getArticleId()}</h1>

<#list list as str>
    <h1>${str}</h1>
</#list>

<#list map ? keys as key>
    <h1>key:${key} </h1>
    <h1>value: ${map[key]}</h1>
</#list>

<#if comments.getArticleId() != "123">
    <div>test</div>
</#if>
</body>
</html>