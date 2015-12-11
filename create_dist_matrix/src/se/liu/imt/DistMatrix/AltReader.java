package se.liu.imt.DistMatrix;

import java.io.BufferedReader;
import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

public interface AltReader {
	void Read(BufferedReader r, Connection con) throws IOException, SQLException;
}
