package se.liu.imt.DistMatrix;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.List;

public class MeshReader implements AltReader {

	private class Mesh {
		String MH = null;
		String UI = null;
		List<String> MN = new LinkedList<String>();
	}

	@Override
	public void Read(BufferedReader r, Connection con) throws IOException, SQLException {
		int state = 0;
		String strLine;

		List<Mesh> meshList = new LinkedList<Mesh>();
		
		Mesh m = null;
		
		while ((strLine = r.readLine()) != null) {
			
			if (strLine.equals("*NEWRECORD")) {
				m = new Mesh();
				continue;
			}

			if (strLine.startsWith("MH ")) {
				m.MH = strLine.substring(5);
			}

			if (strLine.startsWith("MN")) {
				m.MN.add(strLine.substring(5));
			}

			if (strLine.startsWith("UI")) {
				m.UI = strLine.substring(5);
				meshList.add(m);
			}

		}
		
		System.out.println(meshList.size());
		for(Mesh m1 : meshList) {
			Statement stmt = con.createStatement();
			String MH = URLEncoder.encode(m1.MH, "UTF-8");
			MH = MH.length() > 144 ? MH.substring(0, 144) : MH;
			for(String MN : m1.MN) { 
				String sql = "INSERT INTO mesh (UI, MN, MH) VALUES ('" + m1.UI + "','" + MN + "','" + MH + "');";
				stmt.executeUpdate(sql);
			}
		}
		
	}

}
