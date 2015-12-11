package se.liu.imt.DistMatrix;

import java.io.BufferedReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.net.URLEncoder;


public class AtcReader implements AltReader {


	@Override
	public void Read(BufferedReader r, Connection con) throws IOException,
			SQLException {
		String strLine;

		Statement stmt = con.createStatement();
		
		while ((strLine = r.readLine()) != null) {

			String[] tokens = strLine.split("\t");
			
			String name = URLEncoder.encode(tokens[1], "UTF-8");
			name = name.length() > 144 ? name.substring(0, 144) : name;
			
			String sql = "INSERT INTO atc (code, name) VALUES ('" + tokens[0] + "','" + name + "');";
			stmt.executeUpdate(sql);
		}

	}

}
