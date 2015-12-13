package se.liu.imt.DistMatrix;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class CreateDistMatrixSCT {

	/**
	 * @param args
	 * @throws
	 */
	public static void main(String[] args) {
		try {
			Class.forName("com.mysql.jdbc.Driver");

			Connection con = DriverManager.getConnection(
					"jdbc:mysql://localhost:3306/assess_ct", "snomed",
					"Snomed2015");

			Statement stmt = con.createStatement();

			ResultSet allConceptsRS = stmt
					.executeQuery("SELECT code, id FROM codes WHERE scenario = 'SCT' ORDER BY id");

			ArrayList<Integer> codes = new ArrayList<Integer>();
			ArrayList<Integer> ids = new ArrayList<Integer>();

			while (allConceptsRS.next()) {
				int code = allConceptsRS.getInt(1);
				int id = allConceptsRS.getInt(2);
				try {
					codes.add(code);
					ids.add(id);
				} catch (NumberFormatException e) {
					;
				}
			}

			allConceptsRS.close();
			stmt.close();
			con.close();

			con = DriverManager.getConnection(
					"jdbc:mysql://localhost:3306/snomed_20150731", "snomed",
					"Snomed2015");

			PreparedStatement distStmt = con
					.prepareStatement("SELECT t1.SupertypeId, c.descendants, t1.PathLength, t2.PathLength "
							+ "FROM transitiveclosure t1 join "
							+ "transitiveclosure t2 on t1.SupertypeId = t2.SupertypeId "
							+ "JOIN concepts c ON t1.SupertypeId = c.Id "
							+ "WHERE t1.SubtypeId = ?  AND t2.SubtypeId = ? "
							+ "ORDER BY (t1.PathLength + t2.PathLength) ASC LIMIT 1");

			PreparedStatement countStmt = con
					.prepareStatement("SELECT descendants FROM concepts WHERE Id = ?");

			int i, j;

//			for (i = 0; i < codes.size(); i++) {
//				if (i != 0)
//					System.out.print("\t");
//				System.out.print(codes.get(i));
//			}
//			System.out.print("\n");

			for (i = 0; i < codes.size(); i++) {
//				System.out.print(codes.get(i));
				for (j = 0; j < codes.size(); j++) {
					if(j != 0)
						System.out.print(",");
					int code1 = codes.get(i);
					int code2 = codes.get(j);
					if(code1 == 0 || code2 == 0) {
						System.out.print("0"); // missing value
						continue;
					}
					
					countStmt.setInt(1, code1);
					ResultSet countRS1 = countStmt.executeQuery();
					int count1 = countRS1.next() ? countRS1.getInt(1) : 0;
					countRS1.close();
					countStmt.setInt(1, code2);
					ResultSet countRS2 = countStmt.executeQuery();
					int count2= countRS2.next() ? countRS2.getInt(1) : 0;
					countRS2.close();
					
					distStmt.setInt(1, codes.get(i));
					distStmt.setInt(2, codes.get(j));
					ResultSet distRS = distStmt.executeQuery();
					if (distRS.next()) {
						// Resnik
						int freq_c = distRS.getInt(2);
						double freq_root = 317057.0;
						double q = freq_c / freq_root;
						double ic_c = -Math.log(q);
						double ic1 = -Math.log(count1/freq_root);
						double ic2 = -Math.log(count2/freq_root);
						double lin = 2*ic_c/(ic1+ic2);
						System.out.print(lin);
					} else 
						throw (new Exception("Missing: " + codes.get(i) + ", "
								+ codes.get(j)));

				}
				System.out.print("\n");
			}

			con.close();

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
