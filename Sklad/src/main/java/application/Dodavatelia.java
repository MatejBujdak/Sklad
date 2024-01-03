package application;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;


public class Dodavatelia extends HttpServlet {     
	private static final long serialVersionUID = 1L;
    String URL = "jdbc:mysql://localhost:3307/";
    String databaza = "sklad";
    String userName = "root";
    String pass = "";
    Connection con;
    PrintWriter out;
    
    @Override
    public void init() throws ServletException {
     super.init();
     try {
    	 Class.forName("com.mysql.cj.jdbc.Driver");
         con = DriverManager.getConnection(URL + databaza, userName, pass);
     } catch (Exception ex) {}
    }
    
    /////DO GET/////
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    	out = response.getWriter();
        if (con == null) {
            out.println("nieje mozne sa pripojit na databazu");
            return;
      }
        
        String operacia = request.getParameter("operacia");
        
        try {
        	response.setContentType("text/html;charset=UTF-8");
            Statement stmt = con.createStatement();
            int id = 0;
            if(request.getParameter("id") != null) {
            	id = Integer.parseInt(request.getParameter("id"));
            }
            
            vypisDodavatelov(stmt, id, operacia);
            
            out.println("<form action='/Sklad/ServletMain' method='get'>");
            out.println("<button type='submit'>Hlavna stranka</button>");
            out.println("</form>");

      
        } catch (Exception e) {
            out.println(e);
        }
    }
	
    //metoda na vypis dodavatelov z tabulky dodavatelia
	 protected void vypisDodavatelov(Statement stmt, int id, String operacia) {
	    	try {
	    		ResultSet resultSet = stmt.executeQuery("SELECT * FROM dodavatelia");
	            out.println("<html><body>");
	            out.println("<h1>Zoznam dodavatelov:</h1>");
	            out.println("<table border='1'>");
	            out.println("<tr><th>id</th><th>nazov</th><th>ico</th><th>adresa</th></tr>");

	            while (resultSet.next()) {
	                out.println("<tr>");

	                // v pripade stlacenia tlacidla "editovat"
	                if(resultSet.getInt("id") == id && operacia.equals("editovat")) {
	                	out.println("<form action='/Sklad/Dodavatelia' method='post'>");
	                	out.println("<td>" + resultSet.getInt("id") + "</td>");
	                	out.println("<input type='hidden' name='id' value='" + resultSet.getInt("id") + "'>");
	                	out.println("<td><input type='text' name='nazov' value='"+ resultSet.getString("nazov") +"'></td>");
	                	out.println("<td><input type='text' name='ico' value='"+ resultSet.getString("ico") +"'></td>");
	                	out.println("<td><input type='text' name='adresa' value='"+ resultSet.getString("adresa") +"'></td>"); 
		                out.println("<input type='hidden' name='operacia' value='potvrdit'>");
		                out.println("<td><input type='submit' value='potvrdit'></td>");
		                out.println("</form>");
	                }else {
	                	out.println("<td>" + resultSet.getInt("id") + "</td>");
		                out.println("<td>" + resultSet.getString("nazov") + "</td>");
		                out.println("<td>" + resultSet.getString("ico") + "</td>"); 
		                out.println("<td>" + resultSet.getString("adresa") + "</td>"); 
		                out.println("<td><form action='/Sklad/Dodavatelia' method='post'>");
		                out.println("<input type=hidden name ='id' value='"+ resultSet.getString("id")+"'>");
		                out.println("<input type='hidden' name='operacia' value='editovat'>");
		                out.println("<input type='submit' value='editovat'>");
		                out.println("</form></td>");
		                
	                }  
	                out.println("<td><form action='/Sklad/Dodavatelia' method='post'>");
	                out.println("<input type=hidden name ='id' value='"+ resultSet.getString("id")+"'>");
	                out.println("<input type='hidden' name='operacia' value='odstranit'>");
	                out.println("<input type='submit' value='odstranit'>");
	                out.println("</form></td>");
	                out.println("</tr>");
	                
	                
	            }

	            out.println("</table>");
	            zobrazFormularPrePridanie(out);
	            out.println("</body></html>");
	            

	        } catch (Exception e) {
	            out.println(e);
	        }
	    }

	 ////DO POST////
	 protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		  response.setContentType("text/html;charset=UTF-8");
		  PrintWriter out = response.getWriter();
		  if (con == null) {
		      out.println("nieje mozne spojenie s databázu");
		       return;
		  }
		  
		  String operacia = request.getParameter("operacia");

		  if (operacia.equals("odstranit")) {
		       vymazPolozku(out, request.getParameter("id"));
		  }else if(operacia.equals("pridanie")){
			  pridajPolozku(request.getParameter("nazov"), request.getParameter("ico"), request.getParameter("adresa"), out);
		  }else if(operacia.equals("potvrdit")){
			  upravPolozku(out, request.getParameter("id"), request.getParameter("nazov"), request.getParameter("ico"), request.getParameter("adresa"));
		  }

		  doGet(request, response);
		}
	 
	 // UPDATE
	 private void upravPolozku(PrintWriter out, String id, String nazov, String ico, String adresa) {
		 try {
		        Statement stmt = con.createStatement();
		        String sql = "UPDATE dodavatelia SET nazov = '" + nazov + "', ico = '" + ico + "', adresa = '" + adresa + "' WHERE id = " + id;
		        
		        stmt.executeUpdate(sql);
		        
		    } catch (Exception e) {
		        out.println(e);
		    }
	 }
	 
	 // DELETE
	 private void vymazPolozku(PrintWriter out, String id) {
		 try {
		        Statement checkStmt = con.createStatement();
		        ResultSet checkResult = checkStmt.executeQuery("SELECT COUNT(*) AS count FROM distribucie WHERE dodavatel_id = " + id);

		        if (checkResult.next() && checkResult.getInt("count") > 0) {
		            out.println("Nie je možné vymazať dodavatela, pretože existujú odkazy na tento záznam v spojovacej tabuľke.");
		        } else {
		            Statement stmt = con.createStatement();
		            String sql = "DELETE FROM dodavatelia WHERE id = " + id;
		            stmt.executeUpdate(sql);
		            String sql2 = "DELETE FROM distribucie WHERE dodavatel_id = " + id;
		            stmt.executeUpdate(sql2);
		        }
		    } catch (Exception e) {
		        out.println(e);
		    }
	 }
	 	
	 // metoda na zobrazenie formularu
	 private void zobrazFormularPrePridanie(PrintWriter out) {
		 	out.println("<form action='/Sklad/Dodavatelia' method='post'>");
	        out.println("<input type='text' name='nazov'>");
	        out.println("<input type='text' name='ico'>");
	        out.println("<input type='text' name='adresa'>");
	        out.println("<input type='hidden' name='operacia' value='pridanie'>");
	        out.println("<input type='submit' value='pridat polozku'>");
	        out.println("</form>");
	        
	    }
	 
	 // INSERT
	 private void pridajPolozku(String nazov, String ico, String adresa, PrintWriter out) {
		    try {
		    	Statement stmt = con.createStatement();
		        if (nazov.isEmpty() || ico.isEmpty() || adresa.isEmpty()) {
		            out.println("Nesmu byt prazdne udaje.");
		            return;
		        }


		        String sql = "INSERT INTO dodavatelia (nazov, ico, adresa) VALUES (";
		        sql += "'" + nazov + "', ";
		        sql += "'" + ico + "', ";
		        sql += "'" + adresa + "') ";
		        stmt.executeUpdate(sql);
		        
		    }  catch (Exception e) {
		        out.println(e.getMessage());
		    }
		}

		
		@Override
		public void destroy() {
		  try {
			  out.flush();
	          out.close();
	          con.close();
		  } catch (Exception ex) {}
		  super.destroy();
		}

}
