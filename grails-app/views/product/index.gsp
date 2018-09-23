<!DOCTYPE html>
<html>
<head>
  <meta name="layout" content="main"/>
</head>
%{--TODO: Move labels to i18n.properties !!--}%
<body>
<div class="nav" role="navigation">
  <ul>
    <sec:ifAnyGranted roles="ROLE_ADMIN">
      <li><g:link class="btn btn-default" controller="product" action="create">Create</g:link></li>
      <li><g:link class="btn btn-default" controller="product" params="${searchParams}"
                  action="importProducts">Import</g:link></li>
    </sec:ifAnyGranted>
    <li><g:link class="btn btn-default" controller="product" action="exportProducts">Export</g:link></li>
    <li><g:link class="btn btn-primary" controller="product" action="showLimited"
                params="[quantity: 5]">Limited Quantity Available</g:link></li>
  </ul>
</div>

<div class="container-fluid">
  <g:render template="listTemplate"/>
</div>

</body>
</html>