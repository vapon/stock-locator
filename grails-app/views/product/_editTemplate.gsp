<script>
  document.addEventListener("DOMContentLoaded", function () {
    if ("${isViewOnly}") {
      var inputs = document.getElementsByTagName('input');
      Array.prototype.forEach.call(inputs, function (el) {
        el.readOnly = true;
        el.disabled = true;
      });
    }
  });
</script>

<div class="col-lg-12">
  <g:form resource="${product}" method="POST">
    <div class="col-md-5">
      <fieldset class="form row">
        <f:field property="productId" bean="product"></f:field>
        <f:field property="name" bean="product"></f:field>
        <f:field property="brand" bean="product"></f:field>
        <f:field property="description" bean="product"></f:field>
        <f:field property="imageUrl" bean="product"></f:field>
      </fieldset>
    </div>
    <div class="col-md-5">
      <fieldset class="form row">
        <f:field property="price" bean="product"></f:field>
        <f:field property="currencyId" bean="product"></f:field>
        <f:field property="salesSize" bean="product"></f:field>
        <f:field property="salesUnitId" bean="product"></f:field>
        <f:field property="quantity" bean="product"></f:field>
      </fieldset>
    </div>

    <div class="col-md-2">
      <fieldset class="btn-group pull-right">
        <g:submitButton name="save" class="btn btn-primary" value="Save"/>
      </fieldset>
    </div>
  </g:form>
</div>