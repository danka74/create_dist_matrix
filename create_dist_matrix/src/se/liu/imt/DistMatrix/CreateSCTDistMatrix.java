package se.liu.imt.DistMatrix;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class CreateSCTDistMatrix {

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
					.executeQuery("SELECT DISTINCT target FROM stats WHERE scenario = 'SCT' AND target NOT LIKE '%;%'");

			ArrayList<Integer> codes = new ArrayList<Integer>();

			while (allConceptsRS.next()) {
				String target = allConceptsRS.getString(1);
				int bar = target.indexOf('|');
				target = bar > 0 ? target.substring(0, bar) : target;
				try {
					codes.add(Integer.parseInt(target));
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

			int i, j;

			for (i = 0; i < codes.size(); i++) {
				if (i != 0)
					System.out.print("\t");
				System.out.print(codes.get(i));
			}
			System.out.print("\n");

			for (i = 0; i < codes.size(); i++) {
				System.out.print(codes.get(i));
				for (j = 0; j < codes.size(); j++) {
					distStmt.setInt(1, codes.get(i));
					distStmt.setInt(2, codes.get(j));
					ResultSet distRS = distStmt.executeQuery();
					System.out.print("\t");
					if (distRS.next()) {
						// Resnik
						int freq_c = distRS.getInt(2);
						double freq_root = 317057.0;
						double q = freq_c/freq_root;
						double ic = - Math.log(q);
						System.out.print(ic);
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
