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
          
          ResultSet rss = stmts.executeQuery("SELECT * FROM users WHERE email=email");
          //ArrayList<String> outputs = new ArrayList<String>();
          String db_password= new String();
          while (rss.next()){
            db_password=rss.getString("password");
          }
          // while (rss.next()) {
          //   outputs.add( "Email: " + rss.getString("email"));
          //   outputs.add( "Password: " + rss.getString("password"));
          //   outputs.add( "FirstName: " + rss.getString("firstname"));
          //   outputs.add( "LastName: " + rss.getString("lastname"));
          // }
          
          if(password.equals(db_password)){
            attributes.put("message", "Login Success!!");
            return new ModelAndView(attributes, "user_info.ftl");
          }
          else {
            attributes.put("message", "Email and Password not match");
            return new ModelAndView(attributes, "login.ftl");
            }


          //attributes.put("results", outputs);
          // return new ModelAndView(attributes, "db.ftl");
        } 
        catch (Exception e) {
          attributes.put("message", "There was an error: " + e);
          return new ModelAndView(attributes, "error.ftl");
        }  
        finally {
          if (connection != null) try{connection.close();} catch(SQLException e){}
        }
    }, new FreeMarkerEngine());




    // get("/db", (req, res) -> {
    //   Connection connection = null;
    //   Map<String, Object> attributes = new HashMap<>();
    //   try {
    //     connection = DatabaseUrl.extract().getConnection();

    //     Statement stmt = connection.createStatement();
    //     stmt.executeUpdate("CREATE TABLE IF NOT EXISTS ticks (tick timestamp)");
    //     stmt.executeUpdate("INSERT INTO ticks VALUES (now())");
    //     ResultSet rs = stmt.executeQuery("SELECT tick FROM ticks");

    //     ArrayList<String> output = new ArrayList<String>();
    //     while (rs.next()) {
    //       output.add( "Read from DB: " + rs.getTimestamp("tick"));
    //     }

    //     attributes.put("results", output);
    //     return new ModelAndView(attributes, "db.ftl");
    //   } catch (Exception e) {
    //     attributes.put("message", "There was an error: " + e);
    //     return new ModelAndView(attributes, "error.ftl");
    //   } finally {
    //     if (connection != null) try{connection.close();} catch(SQLException e){}
    //   }
    // }, new FreeMarkerEngine());
    get("/db", (req, res) -> {
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


  }

}
