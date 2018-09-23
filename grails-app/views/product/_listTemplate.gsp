<script>
  function clearSearchForm() {
    document.getElementById('name').value ='';
    document.getElementById('brand').value ='';
  }
</script>
<div class="well well-sm"></div>

<div id="products" class="row list-group">
  <div class="col-lg-8">
    <g:if test="${searchResult}">
      <g:each in="${searchResult}" var="product">
        <div class="item col-xs-4 col-md-4 list-group-item">
          <div class="thumbnail">
            <img class="group list-group-image" src="${product.imageUrl}" alt="">

            <div class="caption">
              <h4 class="group inner list-group-item-heading">
                ${product.name}</h4>

              <p class="group inner list-group-item-text">
                ${product.brand}</p>

              <div class="row">
                <div class="col-xs-12 col-md-6">
                  <p class="lead">
                    ${product.price}${product.currencyId}</p>

                  <p class="lead">
                    ${product.quantity} In Stock</p>
                </div>

                <div class="col-xs-12 col-md-6">
                  <g:link class="btn btn-edit" controller="product" action="show"
                          params="${[id: product.productId] << searchParams ?: [:]}">Show</g:link>
                  <sec:ifAnyGranted roles="ROLE_ADMIN">
                    <g:link class="btn btn-edit" controller="product" action="edit"
                            params="${[id: product.productId] << searchParams ?: [:]}">Edit</g:link>
                    <g:link class="btn btn-delete" controller="product" action="delete"
                            params="${[id: product.productId]}">Delete</g:link>
                  </sec:ifAnyGranted>
                </div>
              </div>
            </div>
          </div>
        </div>
      </g:each>
    </g:if>
    <g:else>
      <div class="col-lg-8">
        <label class="label label-default">No results</label>
      </div>
    </g:else>

  </div>

  <div class="col-lg-4">
    <div class="menu menu-right" role="menu">
      <form class="form-horizontal" role="form">
        <div class="form-group">
          <label for="brand">Brand</label>
          <input class="form-control" id="brand" name="brand" type="text" maxlength="255" value="${searchParams.brand}">
        </div>

        <div class="form-group">
          <label for="name">Name</label>
          <input class="form-control" id="name" name="name" type="text" maxlength="255" value="${searchParams.name}">
        </div>

        <div class="btn-group pull-right">
          <button type="submit" class="btn btn-primary">Search</button>
          <button type="button" onclick="clearSearchForm();" class="btn btn-default">Reset</button>
        </div>
      </form>
    </div>
  </div>
</div>

<div class="row pagination-control pgn">
  <div class="col-lg-8">
    <div class="btn-group pull-right pgn">
      <g:paginate controller="product" action="index" total="${total}" max="${max}" params="${searchParams}"
                  offset="${offset}"/>
    </div>
  </div>
</div>