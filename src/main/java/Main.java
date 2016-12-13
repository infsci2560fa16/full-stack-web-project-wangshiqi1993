import java.sql.*;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Map;

import java.net.URI;
import java.net.URISyntaxException;

import static spark.Spark.*;
import spark.template.freemarker.FreeMarkerEngine;
import spark.ModelAndView;
import static spark.Spark.get;
import com.google.gson.Gson;

import com.heroku.sdk.jdbc.DatabaseUrl;

public class Main {

  public static void main(String[] args) {

    Gson gson = new Gson();

    port(Integer.valueOf(System.getenv("PORT")));
    staticFileLocation("/spark/template/freemarker");
    externalStaticFileLocation("/public/index.html");

    get("/", (request, response) -> {
            Map<String, Object> attributes = new HashMap<>();
            // attributes.put("message", "Hello World!");

            return new ModelAndView(attributes, "index.ftl");
        }, new FreeMarkerEngine());


    get("/category", (request, response) -> {
            Map<String, Object> attributes = new HashMap<>();
            return new ModelAndView(attributes, "category.ftl");
    }, new FreeMarkerEngine());
    get("/contact", (request, response) -> {
            Map<String, Object> attributes = new HashMap<>();
            return new ModelAndView(attributes, "contact.ftl");
    }, new FreeMarkerEngine());
    get("/login", (request, response) -> {
            Map<String, Object> attributes = new HashMap<>();
            return new ModelAndView(attributes, "login.ftl");
    }, new FreeMarkerEngine());
    get("/register", (request, response) -> {
            Map<String, Object> attributes = new HashMap<>();
            return new ModelAndView(attributes, "register.ftl");
    }, new FreeMarkerEngine());
    

    post("/insert_users", (request, response) -> {

      String email = request.queryParams("email");
      String password = request.queryParams("password");
      String firstname = request.queryParams("firstname");
      String lastname = request.queryParams("lastname");
      

      Connection connection = null;
      PreparedStatement pst = null;

      Map<String, Object> attributes = new HashMap<>();
        try {
          connection = DatabaseUrl.extract().getConnection();
          // Statement stmts = connection.createStatement();
          // stmts.executeUpdate("CREATE TABLE IF NOT EXISTS users (email VARCHAR(255),password VARCHAR(255),firstname VARCHAR(255), lastname VARCHAR(255))");
          // stmts.executeUpdate("INSERT INTO users (email,password,firstname,lastname) VALUES (Email,Password,Firstname,Lastname)");
          // return new ModelAndView(attributes, "db.ftl");
          String sql = "INSERT INTO users(email, password, firstname, lastname) VALUES(?, ?, ?, ?)";
          pst = connection.prepareStatement(sql);

          pst.setString(1, email);
          pst.setString(2, password);
          pst.setString(3, firstname);
          pst.setString(4, lastname);

          pst.executeUpdate();

          attributes.put("message", "Success!! Thank you for registering");
          return new ModelAndView(attributes, "error.ftl");

        } 
        catch (Exception e) {
          attributes.put("message", "There was an error: " + e);
          return new ModelAndView(attributes, "error.ftl");
        }  
        finally {
          if (connection != null) try{connection.close();} catch(SQLException e){}
        }
    }, new FreeMarkerEngine());


get("/check_login", (request, response) -> {
      String email = request.queryParams("email");
      String password = request.queryParams("password");
      Connection connection = null;
      
      Map<String, Object> attributes = new HashMap<>();
        try {
          connection = DatabaseUrl.extract().getConnection();
          Statement stmts = connection.createStatement();
          
          ResultSet rss = stmts.executeQuery("SELECT * FROM users");
          //ArrayList<String> outputs = new ArrayList<String>();
          String db_email;
          String db_password;
          while (rss.next()){
            db_email=rss.getString("email");
            db_password=rss.getString("password");

            if(db_email.equals(email) && db_password.equals(password)){
            attributes.put("message", "Login Success!!");
            return new ModelAndView(attributes, "user_info.ftl");
            //response.sendRedirect("AddToShoppingCart.jsp");	
            }
          }

          attributes.put("message", "Wrong Email or Password!!");
          return new ModelAndView(attributes, "error.ftl");

        } 
        catch (Exception e) {
          attributes.put("message", "There was an error: " + e);
          return new ModelAndView(attributes, "error.ftl");
        }  
        finally {
          if (connection != null) try{connection.close();} catch(SQLException e){}
        }
    }, new FreeMarkerEngine());


    get("/db_users", (req, res) -> {
      Connection connection = null;
      Map<String, Object> attributes = new HashMap<>();
        try {
          connection = DatabaseUrl.extract().getConnection();
          Statement stmts = connection.createStatement();
          
          ResultSet rss = stmts.executeQuery("SELECT * FROM users ");
          ArrayList<String> outputs = new ArrayList<String>();
          while (rss.next()) {
            outputs.add( "Email: " + rss.getString("email"));
            outputs.add( "Password: " + rss.getString("password"));
            outputs.add( "FirstName: " + rss.getString("firstname"));
            outputs.add( "LastName: " + rss.getString("lastname"));
          }
          attributes.put("results", outputs);
          return new ModelAndView(attributes, "db.ftl");
        } 
        catch (Exception e) {
          attributes.put("message", "There was an error: " + e);
          return new ModelAndView(attributes, "error.ftl");
        }  
        finally {
          if (connection != null) try{connection.close();} catch(SQLException e){}
        }
    }, new FreeMarkerEngine());

    get("/db_stocks", (req, res) -> {
      Connection connection = null;
      Map<String, Object> attributes = new HashMap<>();
        try {
          connection = DatabaseUrl.extract().getConnection();
          Statement stmts = connection.createStatement();
          //heroku pg:psql
          //CREATE TABLE stocks(name VARCHAR(255),price FLOAT,gorl VARCHAR(10), volumn FLOAT,change FLOAT);
          //INSERT INTO stocks(name,price,gorl,volumn,change) VALUES ('USEG',2.52,'G',24637,78.85);
          //INSERT INTO stocks(name,price,gorl,volumn,change) VALUES ('GLF',2.10,'G',443998,23.24);
          //INSERT INTO stocks(name,price,gorl,volumn,change) VALUES ('OPGN',1.25,'G',260282,14.68);
          //INSERT INTO stocks(name,price,gorl,volumn,change) VALUES ('PRTO',2.62,'L',37-51,73.48);
          ResultSet rss = stmts.executeQuery("SELECT * FROM stocks");
          ArrayList<String> outputs = new ArrayList<String>();
          while (rss.next()) {
            outputs.add( "Name: " + rss.getString("name"));
            outputs.add( "Price: " + rss.getInt("price"));
            outputs.add( "Gainer/Loser: " + rss.getString("gorl"));
            outputs.add( "Volumn: " + rss.getInt("volumn"));
            outputs.add( "% Change:" + rss.getInt("change") + "%");
          }
          attributes.put("results", outputs);
          return new ModelAndView(attributes, "db.ftl");
        } 
        catch (Exception e) {
          attributes.put("message", "There was an error: " + e);
          return new ModelAndView(attributes, "error.ftl");
        }  
        finally {
          if (connection != null) try{connection.close();} catch(SQLException e){}
        }
    }, new FreeMarkerEngine());

  }

}
