package edu.uoc.practica.bd.uocdb.exercise1;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import edu.uoc.practica.bd.util.DBAccessor;
import edu.uoc.practica.bd.util.FileUtilities;

public class Exercise1UpdateOrDeleteToyDataFromFile {
	private FileUtilities fileUtilities;

	public Exercise1UpdateOrDeleteToyDataFromFile() {
		fileUtilities = new FileUtilities();
	}

	public static void main(String[] args) {

		Exercise1UpdateOrDeleteToyDataFromFile app = new Exercise1UpdateOrDeleteToyDataFromFile();
		app.run();
	}

	private void run() {

		List<List<String>> fileContents = null;

		try {
			fileContents = fileUtilities.readFileFromClasspath("exercise1.data");
		} catch (FileNotFoundException e) {
			System.err.println("ERROR: File not found");
			e.printStackTrace();
		} catch (IOException e) {
			System.err.println("ERROR: I/O error");
			e.printStackTrace();
		}
		if (fileContents == null) {
			return;
		}

		DBAccessor dbaccessor = new DBAccessor();
		dbaccessor.init();
		Connection conn = dbaccessor.getConnection();
		// TODO Prepare everything before updating or delete
	
		// TODO Update or delete Toy for every row in file
		for (List<String> row : fileContents) {	
	
		}
		// TODO Validate transaction

		// TODO Close resources and check exceptions
	}

	private void setUpdatePreparedStatement(PreparedStatement updateStatement, List<String> row) throws SQLException {
		setValueOrNull(updateStatement, 1, getToyManufacturer(row));
		setValueOrNull(updateStatement, 2, getToyPrice(row));
		setValueOrNull(updateStatement, 3, getToyType(row));
		setValueOrNull(updateStatement, 4, getToyName(row));
		setValueOrNull(updateStatement, 5, getToyId(row));
	}

	private void setDeletePreparedStatement(PreparedStatement deleteStatement, List<String> row) throws SQLException {
		setValueOrNull(deleteStatement, 1, getToyId(row));
	}

	private void setSelectPreparedStatement(PreparedStatement selectStatement, List<String> row) throws SQLException {
		setValueOrNull(selectStatement, 1, getToyId(row));
	}

	private Integer getToyId(List<String> row) {
		return getInt(row, 0);
	}

	private Double getToyPrice(List<String> row) {
		return getDouble(row, 2);
	}

	private String getToyName(List<String> row) {
		return getString(row, 1);
	}

	private String getToyType(List<String> row) {
		return getString(row, 3);
	}

	private String getToyManufacturer(List<String> row) {
		return getString(row, 4);
	}

	private Integer getInt(List<String> row, int index) {
		return getIntegerFromStringOrNull(row.get(index));
	}

	private Double getDouble(List<String> row, int index) {
		return getDoubleFromStringOrNull(row.get(index));
	}

	private String getString(List<String> row, int index) {
		return row.get(index);
	}

	private Integer getIntegerFromStringOrNull(String integer) {
		return (null != integer) ? Integer.valueOf(integer) : null;
	}

	private Double getDoubleFromStringOrNull(String dbl) {
		return (null != dbl) ? Double.valueOf(dbl) : null;
	}

	private void setValueOrNull(PreparedStatement preparedStatement, int parameterIndex, Object obj)
			throws SQLException {
		// TODO: look
		preparedStatement.setObject(parameterIndex, obj);
	}

}
