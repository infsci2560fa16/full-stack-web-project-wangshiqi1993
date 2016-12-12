<!DOCTYPE html>
<html>
<head>
	<#include "header.ftl">
</head>

<body>

 	<div id="page">
 		<#include "header2.ftl">
        <#include "nav.ftl">
			  
        <section class="card login">
				<h2 style="display: none;">login</h2>
                <form name="myform"novalidate>

                <div class="textintro">
                    <h1>Login to check your account</h1>
                    <p id="login"></p>
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

                </fieldset>

            <button type="submit" onclick="loginCheck()">Login</button>
            <p>If you don't have an account, please <a href="register.html">register</a></p>

            </form>

			</section>



		</div>


</body>
</html>