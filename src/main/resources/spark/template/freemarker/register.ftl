<!DOCTYPE html>
<html>
<head>
	<#include "header.ftl">
</head>

<body>

 	<div id="page">
 		<#include "header2.ftl">
        <#include "nav.ftl">
			  
        <section class="card register">
  <h2 style="display: none;">register</h2>
  <form name="myform">

    <div class="textintro">
      <h1>Register</h1>
    </div>

    <fieldset>
      <p>
        <label class="control-lable" for="email">Email:</label>
        <input id="email" type="email" name="email" placeholder="Email">
      </p>
      <p>
        <label class="control-lable" for="psw">Passwords:</label>
        <input id="psw" type="password" name="password" placeholder="Password">
      </p>  
      <p>
        <label class="control-lable" for="first">First Name:</label>
        <input id="first" type="text" name="firstname" placeholder="First Name">
      </p>
      <p>
        <label class="control-lable" for="last">Last Name:</label>
        <input id="last" type="text" name="lastname" placeholder="Last Name">
      </p>

    </fieldset>

    <button type="submit" onclick=registerCheck()>Register</button>
    <p>If you already have an account, please <a href="login.ftl">login</a></p>
  </form>
</section>



		</div>


</body>
</html>