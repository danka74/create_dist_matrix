package se.liu.imt.DistMatrix;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
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

public class ReadAlt {

	public static void main(String[] args) throws IOException,
			ClassNotFoundException, SQLException {

		// create Options object
		Options options = new Options();

		// add OWL output format option
		options.addOption("f", "alt-format", true,
				"ALT input format [mesh|atc|icd]");

		CommandLineParser parser = new PosixParser();
		CommandLine cmd = null;
		try {
			cmd = parser.parse(options, args);
		} catch (ParseException e) {
			e.printStackTrace();
			System.exit(1);
		}

		String format = cmd.getOptionValue("alt-format");

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
		BufferedReader altReader = new BufferedReader(new FileReader(fileName));

		if (format.equalsIgnoreCase("mesh")) {
			MeshReader m = new MeshReader();
			m.Read(altReader, con);
		} else if (format.equalsIgnoreCase("atc")) {
			AtcReader a = new AtcReader();
			a.Read(altReader, con);
		}
		else if (format.equalsIgnoreCase("icd")) {
			IcdReader i = new IcdReader();
			i.Read(altReader, con);
		}

	}
}
