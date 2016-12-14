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
          return new ModelAndView(attributes, "db_users.ftl");
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
          
          
          //SELECT table_to_xml('stocks', true, false, '');
          //ResultSet rss = stmts.executeQuery("SELECT * FROM stocks");
          ResultSet rss = stmts.executeQuery("SELECT table_to_xml('stocks', true, false, '')");
          ArrayList<String> outputs = new ArrayList<String>();
          while (rss.next()) {
            outputs.add( "Name: " + rss.getString("name"));
            outputs.add( "Price: " + rss.getInt("price"));
            outputs.add( "Gainer/Loser: " + rss.getString("gorl"));
            outputs.add( "Volumn: " + rss.getInt("volumn"));
            outputs.add( "% Change:" + rss.getInt("change") + "%");
          }
          attributes.put("results", outputs);
          return new ModelAndView(attributes, "db_stocks.ftl");
        } 
        catch (Exception e) {
          attributes.put("message", "There was an error: " + e);
          return new ModelAndView(attributes, "error.ftl");
        }  
        finally {
          if (connection != null) try{connection.close();} catch(SQLException e){}
        }
    }, new FreeMarkerEngine());
    


    get("/db_news", (req, res) -> {
      Connection connection = null;
      Map<String, Object> attributes = new HashMap<>();
        try {
          connection = DatabaseUrl.extract().getConnection();
          Statement stmts = connection.createStatement();
          //heroku pg:psql
          //CREATE TABLE news(title VARCHAR(255),type VARCHAR(20),content VARCHAR(2000),time timestamp);
          //INSERT INTO news(title,type,content,time) VALUES ('‘The Russia/CIA card’: Trump doubles down on dismissal of Kremlin influencing election','energy','President-elect Donald Trump doubled down on his dismissal of the Central Intelligence Agency’s reported assessment that Russia interfered in the U.S. presidential election, calling it a “card” being played by the Democrats to undercut his stunning victory over Hillary Clinton. “Can you imagine if the election results were the opposite and WE tried to play the Russia/CIA card,” Trump tweeted on Monday. “It would be called conspiracy theory!”The Washington Post reported on Friday that the CIA believes Russia’s hacking of Democratic National Committee emails was done to help Trump win the White House. In an interview with “Fox News Sunday,” Trump called the assessment “ridiculous.” “Hacking is very interesting,” Trump said. “Once they hack, if you don’t catch them in the act you’re not going to catch them. They have no idea if it’s Russia or China or somebody. It could be somebody sitting in a bed someplace. I mean, they have no idea.” The president-elect reiterated that point on Twitter while tossing in a falsehood.',now());
          //INSERT INTO news(title,type,content,time) VALUES ('Oakland warehouse fire leads to crackdown on illegal artist spaces around the country','industrials','The massive fire that tore through a converted warehouse in Oakland, killing 36 people in the deadliest blaze in the United States in more than a decade, has officials examining similar illegal spaces — and the artists who create, perform and, in many cases, live in them bracing for a crackdown. Authorities are still trying to determine what caused the Dec. 2 fire, which broke out during a concert inside what was known as the Ghost Ship, a 4,000-square-foot building that former tenants described as “a death trap with few exits, a rickety makeshift staircase, piles of driftwood and a labyrinth of electrical cords.” In Baltimore, dozens of artists living in a building known as the Bell Foundry were evicted last week after the city said it received a complaint “about individuals living there in deplorable conditions.” “The main electrical source had illegal, dangerous connections; there were extension cords used to feed multiple fixtures,” said Katy Byrne, a spokeswoman for Baltimore’s Department of Housing and Community Development. “None of the electrical systems was grounded.” In Denver, fire officials shut down Rhinoceropolis, a landmark “DIY” performance space, and evicted five people who had been living in illegal lofts on Thursday after it was deemed “unsafe.”',now());
          //INSERT INTO news(title,type,content,time) VALUES ('Pay 0% interest until 2018','financial','Banks are now offering incredibly long periods of 0% Intro APR interest. If you’re carrying a balance on a high interest credit card or are looking to make a big purchase in the coming months, it’s time to switch cards and save money.  Our credit card experts have hand selected the below cards because of their long 0% intro APR interest offers. These offers range from 12 months 0% Intro APR all the way up to 21 months 0% Intro APR!  Compare these cards and discover which card is best for you.',now());
          //INSERT INTO news(title,type,content,time) VALUES (‘’，‘’，‘’,now());
          ResultSet rss = stmts.executeQuery("SELECT * FROM news");
          ArrayList<String> outputs = new ArrayList<String>();
          while (rss.next()) { 
            //title,type,content,time
            outputs.add( "Title: " + rss.getString("title"));
            outputs.add( "Type: " + rss.getString("type"));
            outputs.add( "Content: " + rss.getString("content"));
            outputs.add( "time: " + rss.getTimestamp("time"));
            
          }
          attributes.put("results", outputs);
          return new ModelAndView(attributes, "db_news.ftl");
        } 
        catch (Exception e) {
          attributes.put("message", "There was an error: " + e);
          return new ModelAndView(attributes, "error.ftl");
        }  
        finally {
          if (connection != null) try{connection.close();} catch(SQLException e){}
        }
    }, new FreeMarkerEngine());

    get("/db_news_energy", (req, res) -> {
      Connection connection = null;
      Map<String, Object> attributes = new HashMap<>();
        try {
          connection = DatabaseUrl.extract().getConnection();
          Statement stmts = connection.createStatement();
          
          ResultSet rss = stmts.executeQuery("SELECT * FROM news WHERE type='energy'");
          ArrayList<String> outputs = new ArrayList<String>();
          while (rss.next()) { 
            //title,type,content,time
            outputs.add( "Title: " + rss.getString("title"));
            outputs.add( "Type: " + rss.getString("type"));
            outputs.add( "Content: " + rss.getString("content"));
            outputs.add( "time: " + rss.getTimestamp("time"));
            
          }
          attributes.put("results", outputs);
          return new ModelAndView(attributes, "db_news.ftl");
        } 
        catch (Exception e) {
          attributes.put("message", "There was an error: " + e);
          return new ModelAndView(attributes, "error.ftl");
        }  
        finally {
          if (connection != null) try{connection.close();} catch(SQLException e){}
        }
    }, new FreeMarkerEngine());

    get("/db_news_financial", (req, res) -> {
      Connection connection = null;
      Map<String, Object> attributes = new HashMap<>();
        try {
          connection = DatabaseUrl.extract().getConnection();
          Statement stmts = connection.createStatement();
          
          ResultSet rss = stmts.executeQuery("SELECT * FROM news WHERE type='financial'");
          ArrayList<String> outputs = new ArrayList<String>();
          while (rss.next()) { 
            //title,type,content,time
            outputs.add( "Title: " + rss.getString("title"));
            outputs.add( "Type: " + rss.getString("type"));
            outputs.add( "Content: " + rss.getString("content"));
            outputs.add( "time: " + rss.getTimestamp("time"));
            
          }
          attributes.put("results", outputs);
          return new ModelAndView(attributes, "db_news.ftl");
        } 
        catch (Exception e) {
          attributes.put("message", "There was an error: " + e);
          return new ModelAndView(attributes, "error.ftl");
        }  
        finally {
          if (connection != null) try{connection.close();} catch(SQLException e){}
        }
    }, new FreeMarkerEngine());

    get("/db_news_healthcare", (req, res) -> {
      Connection connection = null;
      Map<String, Object> attributes = new HashMap<>();
        try {
          connection = DatabaseUrl.extract().getConnection();
          Statement stmts = connection.createStatement();
          
          ResultSet rss = stmts.executeQuery("SELECT * FROM news WHERE type='healthcare'");
          ArrayList<String> outputs = new ArrayList<String>();
          while (rss.next()) { 
            //title,type,content,time
            outputs.add( "Title: " + rss.getString("title"));
            outputs.add( "Type: " + rss.getString("type"));
            outputs.add( "Content: " + rss.getString("content"));
            outputs.add( "time: " + rss.getTimestamp("time"));
            
          }
          attributes.put("results", outputs);
          return new ModelAndView(attributes, "db_news.ftl");
        } 
        catch (Exception e) {
          attributes.put("message", "There was an error: " + e);
          return new ModelAndView(attributes, "error.ftl");
        }  
        finally {
          if (connection != null) try{connection.close();} catch(SQLException e){}
        }
    }, new FreeMarkerEngine());

    get("/db_news_business", (req, res) -> {
      Connection connection = null;
      Map<String, Object> attributes = new HashMap<>();
        try {
          connection = DatabaseUrl.extract().getConnection();
          Statement stmts = connection.createStatement();
          
          ResultSet rss = stmts.executeQuery("SELECT * FROM news WHERE type='business'");
          ArrayList<String> outputs = new ArrayList<String>();
          while (rss.next()) { 
            //title,type,content,time
            outputs.add( "Title: " + rss.getString("title"));
            outputs.add( "Type: " + rss.getString("type"));
            outputs.add( "Content: " + rss.getString("content"));
            outputs.add( "Time: " + rss.getTimestamp("time"));
            
          }
          attributes.put("results", outputs);
          return new ModelAndView(attributes, "db_news.ftl");
        } 
        catch (Exception e) {
          attributes.put("message", "There was an error: " + e);
          return new ModelAndView(attributes, "error.ftl");
        }  
        finally {
          if (connection != null) try{connection.close();} catch(SQLException e){}
        }
    }, new FreeMarkerEngine());

    get("/db_news_telecom", (req, res) -> {
      Connection connection = null;
      Map<String, Object> attributes = new HashMap<>();
        try {
          connection = DatabaseUrl.extract().getConnection();
          Statement stmts = connection.createStatement();
          
          ResultSet rss = stmts.executeQuery("SELECT * FROM news WHERE type='telecom'");
          ArrayList<String> outputs = new ArrayList<String>();
          while (rss.next()) { 
            //title,type,content,time
            outputs.add( "Title: " + rss.getString("title"));
            outputs.add( "Type: " + rss.getString("type"));
            outputs.add( "Content: " + rss.getString("content"));
            outputs.add( "time: " + rss.getTimestamp("time"));
            
          }
          attributes.put("results", outputs);
          return new ModelAndView(attributes, "db_news.ftl");
        } 
        catch (Exception e) {
          attributes.put("message", "There was an error: " + e);
          return new ModelAndView(attributes, "error.ftl");
        }  
        finally {
          if (connection != null) try{connection.close();} catch(SQLException e){}
        }
    }, new FreeMarkerEngine());

    get("/db_news_ch", (req, res) -> {
      Connection connection = null;
      Map<String, Object> attributes = new HashMap<>();
        try {
          connection = DatabaseUrl.extract().getConnection();
          Statement stmts = connection.createStatement();
          
          ResultSet rss = stmts.executeQuery("SELECT * FROM news WHERE type='ch'");
          ArrayList<String> outputs = new ArrayList<String>();
          while (rss.next()) { 
            //title,type,content,time
            outputs.add( "Title: " + rss.getString("title"));
            outputs.add( "Type: " + rss.getString("type"));
            outputs.add( "Content: " + rss.getString("content"));
            outputs.add( "time: " + rss.getTimestamp("time"));
            
          }
          attributes.put("results", outputs);
          return new ModelAndView(attributes, "db_news.ftl");
        } 
        catch (Exception e) {
          attributes.put("message", "There was an error: " + e);
          return new ModelAndView(attributes, "error.ftl");
        }  
        finally {
          if (connection != null) try{connection.close();} catch(SQLException e){}
        }
    }, new FreeMarkerEngine());

    get("/db_news_cs", (req, res) -> {
      Connection connection = null;
      Map<String, Object> attributes = new HashMap<>();
        try {
          connection = DatabaseUrl.extract().getConnection();
          Statement stmts = connection.createStatement();
          
          ResultSet rss = stmts.executeQuery("SELECT * FROM news WHERE type='cs'");
          ArrayList<String> outputs = new ArrayList<String>();
          while (rss.next()) { 
            //title,type,content,time
            outputs.add( "Title: " + rss.getString("title"));
            outputs.add( "Type: " + rss.getString("type"));
            outputs.add( "Content: " + rss.getString("content"));
            outputs.add( "time: " + rss.getTimestamp("time"));
            
          }
          attributes.put("results", outputs);
          return new ModelAndView(attributes, "db_news.ftl");
        } 
        catch (Exception e) {
          attributes.put("message", "There was an error: " + e);
          return new ModelAndView(attributes, "error.ftl");
        }  
        finally {
          if (connection != null) try{connection.close();} catch(SQLException e){}
        }
    }, new FreeMarkerEngine());

    get("/db_news_industrials", (req, res) -> {
      Connection connection = null;
      Map<String, Object> attributes = new HashMap<>();
        try {
          connection = DatabaseUrl.extract().getConnection();
          Statement stmts = connection.createStatement();
          
          ResultSet rss = stmts.executeQuery("SELECT * FROM news WHERE type='industrials'");
          ArrayList<String> outputs = new ArrayList<String>();
          while (rss.next()) { 
            //title,type,content,time
            outputs.add( "Title: " + rss.getString("title"));
            outputs.add( "Type: " + rss.getString("type"));
            outputs.add( "Content: " + rss.getString("content"));
            outputs.add( "time: " + rss.getTimestamp("time"));
            
          }
          attributes.put("results", outputs);
          return new ModelAndView(attributes, "db_news.ftl");
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
