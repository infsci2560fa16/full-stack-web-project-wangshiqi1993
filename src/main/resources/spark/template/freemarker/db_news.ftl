<!DOCTYPE html>
<html>
<head>
  <#include "header.ftl">
</head>

<body>
  <div id="page">
    <#include "header2.ftl">
      <#include "nav.ftl">

    <div class="container">
      <h1>Database Output</h1>
       <ul>

        <#list results as results>

          <div class="results">
            <h2> ${result.title} </h2>
            <p> ${result.content}</p>
            <i>Published on the ${results.time}</i>
          </div>
          

        </#list>

        </ul>
    </div>

  </div>

</body>
</html>
