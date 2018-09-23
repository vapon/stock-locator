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

<div class="well well-sm"></div>

<div class="container-fluid">
  <div class="row list-group">
    <div class="col-md-6">
      <g:uploadForm class="form-group" action="upload">
        <div class="col-sm-6">
          <input class="form-control" type="file" name="upload"/>
        </div>

        <div class="col-sm-2">
          <input class="btn btn-primary pull-left" type="submit" value="Import"/>
        </div>
      </g:uploadForm>
    </div>
  </div>
  <div class="row">
    <div class="col-lmd-6">
      <p>
        <g:link action="getImportSample" params="[format: 'csv']">CSV Import Template</g:link>
      </p>
      <p>
        <g:link action="getImportSample" params="[format: 'xls']">Excel Import Template</g:link>
      </p>
    </div>
  </div>
  <div class="row">
    <div class="col-md-6">
      <g:if test="${status == 'COMPLETED'}">
        <div class="alert alert-success" role="alert">
          Import successfully finished.
        </div>
      </g:if>
      <g:if test="${status == 'FAILED'}">
        <div class="alert alert-warning" role="alert">
          %{--TODO: add proper error report--}%
          Import completed with errors: products already exist.
        </div>
      </g:if>
    </div>
  </div>
</div>
</body>
</html>