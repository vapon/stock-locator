<!DOCTYPE html>
<html>
<head>
  <meta name="layout" content="main"/>
</head>

<body>
<div class="nav" role="navigation">
  <ul>
    <li><g:link action="index" params="${searchParams}" class="btn btn-default" controller="product">Back</g:link></li>
  </ul>
</div>

<div class="well well-sm"></div>

<div id="create-product" class="row list-group" role="main">
  <g:render template="editTemplate"/>
</div>
</body>
</html>