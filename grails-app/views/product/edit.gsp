<!DOCTYPE html>
<html>
<head>
  <meta name="layout" content="main"/>
</head>

<body>
<div class="nav" role="navigation">
  <ul>
    <li><g:link class="btn btn-default" action="index" params="${searchParams}" controller="product">Back</g:link></li>
  </ul>
</div>

<div class="container container-fluid">
  <div class="well well-sm"></div>

  <div id="edit-product" class="row list-group" role="main">
    <g:render template="editTemplate"/>
  </div>
</div>
</body>
</html>