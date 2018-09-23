<!DOCTYPE html>
<html>
<head>
  <meta name="layout" content="main"/>
</head>

<body>
<div class="nav" role="navigation">
  <ul>
    <li><g:link class="btn btn-default" controller="product" action="index">Back</g:link></li>
  </ul>
</div>

<div class="container container-fluid">
  <g:render template="listTemplate"/>
</div>

</body>
</html>