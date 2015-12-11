package se.liu.imt.DistMatrix;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class IcdReader implements AltReader {

	@Override
	public void Read(BufferedReader r, Connection con) throws IOException,
			SQLException {
		String strLine;

		Statement stmt = con.createStatement();
		
		while ((strLine = r.readLine()) != null) {

			String[] tokens = strLine.split("\t");
			
			String code = tokens[0].substring(0, 3) + "." + tokens[0].substring(3);
			String name = URLEncoder.encode(tokens[1], "UTF-8");
			name = name.length() > 144 ? name.substring(0, 144) : name;
			
			String sql = "INSERT INTO icd (code, name) VALUES ('" + code + "','" + name + "');";
			stmt.executeUpdate(sql);
		}


	}

}
