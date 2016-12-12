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

      String firstName = request.queryParams("firstname");
      String lastName = request.queryParams("lastname");
      String email = request.queryParams("email");
      String password = request.queryParams("password");

      Connection connection = null;
      PreparedStatement pst = null;

      Map<String, Object> attributes = new HashMap<>();

      try{

        connection = DatabaseUrl.extract().getConnection();
        String sql = "INSERT INTO users(firstName, lastName, email, password) VALUES(?, ?, ?, ?)";
        pst = connection.prepareStatement(sql);

        pst.setString(1, firstName);
        pst.setString(2, lastName);
        pst.setString(3, email);
        pst.setString(4, password);

        pst.executeUpdate();

        attributes.put("message", "Thank you for registering");
        
        return new ModelAndView(attributes, "error.ftl");
      } catch (Exception e){
        attributes.put("message", "There was an error" + e);
        return new ModelAndView(attributes, "error.ftl");
      } finally {
        if (connection != null) try{connection.close();} catch(SQLException e){}
      }
    }, new FreeMarkerEngine());







    get("/db", (req, res) -> {
      Connection connection = null;
      Map<String, Object> attributes = new HashMap<>();
      try {
        connection = DatabaseUrl.extract().getConnection();

        Statement stmt = connection.createStatement();
        stmt.executeUpdate("CREATE TABLE IF NOT EXISTS ticks (tick timestamp)");
        stmt.executeUpdate("INSERT INTO ticks VALUES (now())");
        ResultSet rs = stmt.executeQuery("SELECT tick FROM ticks");

        ArrayList<String> output = new ArrayList<String>();
        while (rs.next()) {
          output.add( "Read from DB: " + rs.getTimestamp("tick"));
        }

        attributes.put("results", output);
        return new ModelAndView(attributes, "db.ftl");
      } catch (Exception e) {
        attributes.put("message", "There was an error: " + e);
        return new ModelAndView(attributes, "error.ftl");
      } finally {
        if (connection != null) try{connection.close();} catch(SQLException e){}
      }
    }, new FreeMarkerEngine());

  }

}
