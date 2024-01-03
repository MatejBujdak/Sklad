package application;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;


@WebServlet("/ServletMain?")
public class ServletMain extends HttpServlet {
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
    
    //// DOGET ////
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
            vypisTovarDodavatelov(stmt, id, operacia);
            zobrazTlacidla(out, request.getParameter("add_form"));
      
        } catch (Exception e) {
            out.println(e);
        }
    }
    
    //metoda na vypis zoznamu udajov z tabulky 1 a 2 spojenim cez tabulku 3
    protected void vypisTovarDodavatelov(Statement stmt, int id, String operacia) {
        try {
            String query = "SELECT distribucie.id AS id, tovar.nazov, tovar.cena, dodavatelia.nazov AS dodavatel_nazov, dodavatelia.ico, dodavatelia.adresa " +
                           "FROM tovar " +
                           "INNER JOIN distribucie ON tovar.id = distribucie.tovar_id " +
                           "INNER JOIN dodavatelia ON distribucie.dodavatel_id = dodavatelia.id";
            
            ResultSet resultSet = stmt.executeQuery(query);

            out.println("<html><body>");
            out.println("<h1>Zoznam udajov:</h1>");
            out.println("<table border='1'>");
            out.println("<tr><th>id</th><th>nazov</th><th>cena</th><th>dodavatel_nazov</th><th>ico</th><th>adresa</th></tr>");

            while (resultSet.next()) {
                out.println("<tr>");
                
                // v pripade stlacenia tlacidla "editovat"
                if(resultSet.getInt("id") == id && operacia.equals("editovat")) {
                	out.println("<form action='/Sklad/ServletMain' method='post'>");
	                out.println("<td>" + resultSet.getInt("id") + "</td>");               
	                out.println("<td><select name='nazov_produktu'>");
	                vypisMoznostiTovaru();
	                out.println("</select></td>");
	                out.println("<td>" + resultSet.getDouble("cena") + "</td>");
	                out.println("<td><select name='nazov_dodavatelov'>");
	                vypisMoznostiDodavatelov();
	                out.println("</select></td>");
	                out.println("<td>" + resultSet.getString("ico") + "</td>");
	                out.println("<td>" + resultSet.getString("adresa") + "</td>");
	                out.println("<input type=hidden name ='id' value='"+ resultSet.getString("id")+"'>");
	                out.println("<input type='hidden' name='operacia' value='potvrdit'>");
	                out.println("<td><input type='submit' value='potvrdit'></td>");
	                out.println("</form>");
                }else {
                	out.println("<td>" + resultSet.getInt("id") + "</td>");
 	                out.println("<td>" + resultSet.getString("nazov") + "</td>");
 	                out.println("<td>" + resultSet.getDouble("cena") + "</td>");
 	                out.println("<td>" + resultSet.getString("dodavatel_nazov") + "</td>");
 	                out.println("<td>" + resultSet.getString("ico") + "</td>");
 	                out.println("<td>" + resultSet.getString("adresa") + "</td>");
                	out.println("<td><form action='/Sklad/ServletMain' method='post'>");
                	out.println("<input type=hidden name ='id' value='"+ resultSet.getString("id")+"'>");
	                out.println("<input type='hidden' name='operacia' value='editovat'>");
	                out.println("<input type='submit' value='editovat'>");
	                out.println("</form></td>");
                }
                out.println("<td><form action='/Sklad/ServletMain' method='post'>");
                out.println("<input type=hidden name ='id' value='"+ resultSet.getString("id")+"'>");
                out.println("<input type='hidden' name='operacia' value='odstranit'>");
                out.println("<input type='submit' value='odstranit'>");
                out.println("</form></td>");
                out.println("</tr>");
            }

            out.println("</table>");
            out.println("</body></html>");

        } catch (Exception e) {
            out.println(e);
        }
    }
    
    /// metoda na vypisanie tlacidiel a formularu v pripade stlacenia "Pridaj formular"
    private void zobrazTlacidla(PrintWriter out, String operacia) {
    	out.println("<br>");
    	if(operacia != null && operacia.equals("add_form")) {
    		zobrazFormularPrePridanie(out);
    	}else {
    		out.println("<form action='/Sklad/ServletMain' method='get'>");
        	out.println("<input type=hidden name ='add_form' value='add_form'>");
            out.println("<button type='submit'>Pridaj formular</button>");
            out.println("</form>");
    	}
    	out.println("<form action='/Sklad/Tovar' method='get'>");
        out.println("<button type='submit'>Tovar</button>");
        out.println("</form>");
        out.println("<form action='/Sklad/Dodavatelia' method='get'>");
        out.println("<button type='submit'>Dodavatelia</button>");
        out.println("</form>");
    }


  	// metoda na vypisanie <options> pre combobox (dodavatelia)
    private void vypisMoznostiDodavatelov() {
        try {
            Statement stmt = con.createStatement();
            ResultSet resultSet = stmt.executeQuery("SELECT nazov FROM dodavatelia");

            while (resultSet.next()) {
                String nazovDodavatela = resultSet.getString("nazov");
                out.println("<option value='" + nazovDodavatela + "'>" + nazovDodavatela + "</option>");
            }
        } catch (Exception e) {
            out.println(e.getMessage());
        }
    }
    
    // metoda na vypisanie <options> pre combobox (tovar)
    private void vypisMoznostiTovaru() {
        try {
            Statement stmt = con.createStatement();
            ResultSet resultSet = stmt.executeQuery("SELECT nazov FROM tovar");

            while (resultSet.next()) {
                String nazovDodavatela = resultSet.getString("nazov");
                out.println("<option value='" + nazovDodavatela + "'>" + nazovDodavatela + "</option>");
            }
        } catch (Exception e) {
            out.println(e.getMessage());
        }
    }

    // metoda na zobrazenie formularu, ktory sa zobrazi po stlaceni na "Pridaj formular"
    private void zobrazFormularPrePridanie(PrintWriter out) {
    	//COMBOBOX PRE PRIDAVANIE UDAJOV
        out.println("<form action='/Sklad/ServletMain' method='post'>"); 
        out.println("<select name='nazov_produktu'>");
        vypisMoznostiTovaru();
        out.println("</select>");
        out.println("<select name='nazov_dodavatela'>");
        vypisMoznostiDodavatelov();
        out.println("</select>");
        out.println("<input type='hidden' name='operacia' value='pridanie'>");
        out.println("<input type='submit' value='pridať položku'>");
        out.println("</form>");
    }
    
    /////// DOPOST //////
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
		  }else if (operacia.equals("pridanie")) {
		       pridajPolozku(out, request.getParameter("nazov_produktu"), request.getParameter("nazov_dodavatela"));
		  }else if(operacia.equals("potvrdit")){
			   upravPolozku(out, request.getParameter("nazov_produktu"), request.getParameter("nazov_dodavatelov"), request.getParameter("id"));
		  }

		  doGet(request, response);
		
	}
	
	// UPDATE 
	private void upravPolozku(PrintWriter out, String nazov_produktu, String nazov_dodavatela, String id) {
		 try {
		        Statement stmt = con.createStatement();

		        int tovar_id = ziskajIdProduktu(out, nazov_produktu);
		        int dodavatel_id = ziskajIdDodavatela(out, nazov_dodavatela);
		        String sql = "UPDATE distribucie SET tovar_id = " + tovar_id + ", dodavatel_id = " + dodavatel_id + " WHERE id = " + id;
		        
		        stmt.executeUpdate(sql);
		        
		    } catch (Exception e) {
		        out.println(e);
		    }
	 }
	
	// DELETE
	private void vymazPolozku(PrintWriter out, String id) {
		 try {
		        Statement stmt = con.createStatement();
	            String sql = "DELETE FROM distribucie WHERE id = " + id;
	            stmt.executeUpdate(sql);


		    } catch (Exception e) {
		        out.println(e);
		    }
	 }
	
	// INSERT
	private void pridajPolozku(PrintWriter out, String nazov_produktu, String nazov_dodavatela) {
	    try {
	    	Statement stmt = con.createStatement();
	        String sql = "INSERT INTO distribucie (tovar_id, dodavatel_id) VALUES (";
	        sql += ziskajIdProduktu(out, nazov_produktu) + ", ";
	        sql += ziskajIdDodavatela(out, nazov_dodavatela) + ") ";
	        stmt.executeUpdate(sql);
	        
	    }  catch (Exception e) {
	        out.println(e.getMessage());
	    }
	}
	
	//metoda na ziskanie idcka tovaru podla nazvu
	private int ziskajIdProduktu(PrintWriter out, String nazov_produktu) {
	    try (Statement stmt = con.createStatement()) {
	        String sql = "SELECT id FROM tovar WHERE nazov = '" + nazov_produktu + "'";
	        try (ResultSet rs = stmt.executeQuery(sql)) {
	            if (rs.next()) {
	                return rs.getInt("id");
	            } else {
	                return -1;
	            }
	        }
	    } catch (Exception e) {
	        out.println(e.getMessage());
	        return -1;
	    }
	}

	//metoda na ziskanie idcka dodavatela podla nazvu
	private int ziskajIdDodavatela(PrintWriter out, String nazov_dodavatela) {
	    try (Statement stmt = con.createStatement()) {
	        String sql = "SELECT id FROM dodavatelia WHERE nazov = '" + nazov_dodavatela + "'";
	        try (ResultSet rs = stmt.executeQuery(sql)) {
	            if (rs.next()) {
	                return rs.getInt("id");
	            } else {
	                return -1;
	            }
	        }
	    } catch (Exception e) {
	        out.println(e.getMessage());
	        return -1;
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

