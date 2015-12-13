package se.liu.imt.DistMatrix;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.URLEncoder;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;

public class CreateDistMatrixALT {

	public CreateDistMatrixALT() {
		// TODO Auto-generated constructor stub
	}
	
	private static int getIndex(int id, List<Integer> ids) {
		for(int i = 0; i < ids.size(); i++)
			if(ids.get(i) == id)
				return i;
		return -1;
	}

	public static void main(String[] args) throws IOException,
			ClassNotFoundException, SQLException {

		// create Options object
		Options options = new Options();

//		// add OWL output format option
//		options.addOption("f", "alt-format", true,
//				"ALT input format [mesh|atc|icd]");

		CommandLineParser parser = new PosixParser();
		CommandLine cmd = null;
		try {
			cmd = parser.parse(options, args);
		} catch (ParseException e) {
			e.printStackTrace();
			System.exit(1);
		}

//		String format = cmd.getOptionValue("alt-format");

		List<?> argList = (List<?>) cmd.getArgList();
		if (argList.size() < 1) {
			HelpFormatter f = new HelpFormatter();
			f.printHelp("ReadAlt", options);
			System.exit(2);
		}

		Class.forName("com.mysql.jdbc.Driver");
		String url = "jdbc:mysql://localhost:3306/assess_ct";
		Connection con = DriverManager.getConnection(url, "snomed",
				"Snomed2015");
		
		String fileName = (String) argList.get(0);
		BufferedReader r = new BufferedReader(new FileReader(fileName));
		
		ResultSet allALTRS = con.createStatement().executeQuery("SELECT id FROM codes WHERE scenario='ALT' ORDER BY id");
		LinkedList<Integer> ids = new LinkedList<Integer>();
		while(allALTRS.next()) {
			ids.add(allALTRS.getInt(1));
		}
		
		int noCodes = ids.size();
		
		double distMatrix[][] = new double[noCodes][noCodes];
		for(int i = 0; i < noCodes; i++)
			for(int j = 0; j < noCodes; j++)
				distMatrix[i][j] = 0.0;
		
		String strLine;
		
		PreparedStatement idStmt = con
				.prepareStatement("SELECT id FROM codes WHERE code = ?");
		
		while ((strLine = r.readLine()) != null) {

			String[] tokens = strLine.split(",");
			
			idStmt.setString(1, tokens[0]);
			ResultSet idRS = idStmt.executeQuery();
			int id1 = idRS.next() ? idRS.getInt(1) : -1;
			idRS.close();
			
			idStmt.setString(1, tokens[1]);
			idRS = idStmt.executeQuery();
			int id2 = idRS.next() ? idRS.getInt(1) : -1;
			
			if(id1 != -1 && id2 != -1) {
				double lin = Double.parseDouble(tokens[2]);
				int index1 = getIndex(id1, ids);
				int index2 = getIndex(id2, ids);
				distMatrix[index1][index2] = lin;
				distMatrix[index2][index1] = lin;
			}
			
			
		}

		for(int i = 0; i < noCodes; i++) {
			for(int j = 0; j < noCodes; j++) {
				if(j != 0)
					System.out.print(",");
				if(i == j)
					System.out.print("1");
				else
					System.out.print(distMatrix[i][j]);
			}
			System.out.println();
		}



	}

}
