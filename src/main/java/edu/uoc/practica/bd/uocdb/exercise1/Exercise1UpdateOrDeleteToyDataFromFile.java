package edu.uoc.practica.bd.uocdb.exercise1;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.*;
import java.util.List;

import edu.uoc.practica.bd.util.DBAccessor;
import edu.uoc.practica.bd.util.FileUtilities;

public class Exercise1UpdateOrDeleteToyDataFromFile {
	private FileUtilities fileUtilities;
	private static Integer MAX_PRICE_DEVIATION_ALLOWED = 20;

	public Exercise1UpdateOrDeleteToyDataFromFile() {
		fileUtilities = new FileUtilities();
	}

	public static void main(String[] args) {

		Exercise1UpdateOrDeleteToyDataFromFile app = new Exercise1UpdateOrDeleteToyDataFromFile();
		app.run();
	}

	private void run() {

		List<List<String>> fileContents = this.getFileContent();

		DBAccessor dbaccessor = new DBAccessor();
		dbaccessor.init();
		Connection conn = dbaccessor.getConnection();
		// TODO Prepare everything before updating or delete

		try {
			conn.setAutoCommit(false);
            PreparedStatement pst_check_toy = conn.prepareStatement("SELECT toy_id FROM wished_toy WHERE toy_id=?");
            PreparedStatement pst_delete_toy = conn.prepareStatement("DELETE FROM toy WHERE toy_id=?");
            PreparedStatement pst_get_toy = conn.prepareStatement("SELECT * FROM toy WHERE toy_id=?");
            PreparedStatement pst_update_toy = conn.prepareStatement(
                    "UPDATE toy " +
                         "SET manufacturer = ?, price = ?, toy_type = ?, toy_name = ? " +
                         "WHERE toy_id = ?"
            );

            // TODO Update or delete Toy for every row in file
            for (List<String> row : fileContents) {
                this.setSelectPreparedStatement(pst_check_toy, row);
                ResultSet rs = pst_check_toy.executeQuery();

                if (!rs.next())
                    this.removeToy(pst_delete_toy, row);
                else
                    this.updateToy(pst_get_toy, pst_update_toy, row);

            }

            // TODO Validate transaction
            conn.commit();

		} catch (SQLException e) {
			e.printStackTrace();
            this.closeConnection(conn, false);
		}


		// TODO Close resources and check exceptions
        this.closeConnection(conn, true);
	}

	private List<List<String>> getFileContent() {
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
			return null;
		}

		return fileContents;
	}

	private void closeConnection(Connection conn, Boolean succesfull) {
        try {

			if (succesfull) {
				conn.close();
			} else {
				conn.rollback();
			}

		} catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void updateToy(PreparedStatement pst_get_toy, PreparedStatement pst_update_toy, List<String> row) {

	    try {

	        this.setSelectPreparedStatement(pst_get_toy, row);
            ResultSet rs = pst_get_toy.executeQuery();

	        if (rs.next()) {
                Double old_price = rs.getDouble("price");
                Double new_price = this.getToyPrice(row);

                Double price_deviation =  ((new_price / old_price) - 1) * 100;

                price_deviation = Math.abs(price_deviation);

                if ( price_deviation > MAX_PRICE_DEVIATION_ALLOWED ) {
                    throw new Exception(String.format("Price update failed for toy_id: %d " +
                            ", current deviation: %f.  Max allowed: %d",
                            this.getToyId(row), price_deviation, MAX_PRICE_DEVIATION_ALLOWED));
                }

                this.setUpdatePreparedStatement(pst_update_toy, row);
                Integer affected_rows = pst_update_toy.executeUpdate();

				if (affected_rows == 0)
					throw new SQLException(String.format("toy_id: %s was not correctly modified", this.getToyId(row)));
            }

        } catch (SQLException e) {
	        e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void removeToy(PreparedStatement pst_delete_toy, List<String> row) {
        // Toy is not in WISHED_TOY table, so we remove it from TOY table.
        try {
            this.setDeletePreparedStatement(pst_delete_toy, row);
            Integer modified_rows = pst_delete_toy.executeUpdate();

            if (modified_rows == 0)
                throw new SQLException(String.format("toy_id: %s was not present in DB", this.getToyId(row)));

        } catch (Exception e) {
            e.printStackTrace();
        }
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
        return this.getStringOrNull(row, index);
    }

	private String getStringOrNull(List<String> row, int index){
	    String str = (index < row.size()) ? row.get(index) : null;

	    if (str != null && str.isEmpty())
	        return null;

		return str;
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
		if (obj == null)
			preparedStatement.setNull(parameterIndex, Types.NULL);
		else
			preparedStatement.setObject(parameterIndex, obj);

	}

}
