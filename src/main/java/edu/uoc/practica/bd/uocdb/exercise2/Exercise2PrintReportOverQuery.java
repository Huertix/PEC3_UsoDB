package edu.uoc.practica.bd.uocdb.exercise2;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import edu.uoc.practica.bd.util.Column;
import edu.uoc.practica.bd.util.DBAccessor;
import edu.uoc.practica.bd.util.FileUtilities;
import edu.uoc.practica.bd.util.Report;
import sun.font.ScriptRun;

public class Exercise2PrintReportOverQuery {

    private FileUtilities fileUtilities;
    private static final String NO_SQL_FUNCTION = "42883";
    private static final String SQL_TOP_10_PROCEDURE = "list_top_toys_children";

    public static void main(String[] args) {
        Exercise2PrintReportOverQuery app = new Exercise2PrintReportOverQuery();
        app.run();
    }

    private void run() {
        this.fileUtilities = new FileUtilities();
        DBAccessor dbaccessor = new DBAccessor();
        dbaccessor.init();
        Connection conn = dbaccessor.getConnection();
        if (conn != null) {

            List<Column> columns = Arrays.asList(
                    new Column("Child Id", 4, "childId"),
                    new Column("Child Name", 15, "childName"),
                    new Column("Child City", 15, "childCity"),
                    new Column("Total Num Toys", 15, "totalNumToys"),
                    new Column("Total Num Letters", 18, "totalNumLetters"),
                    new Column("Max Toys in Letter", 15, "maxNumToysInLetter"),
                    new Column("Most Asked Toy", 12, "mostAskedToy"));

            Report report = new Report();
            report.setColumns(columns);
            List<Object> list = new ArrayList<Object>();

            // TODO Execute stored procedure
            try {
                /*
                    I could not find a way of getting a Cursor with CallableStatement

                        CallableStatement cst = conn.prepareCall(String.format("{?=call %s()}", SQL_TOP_10_PROCEDURE));
                        cst.registerOutParameter(1, TYPE.REF_CURSOR);
                        cst.execute();
                        ResultSet rs = (ResultSet) cst.getObject(1);

                    So, I am using the solution proposed in postgresql offical doc.
                    https://jdbc.postgresql.org/documentation/91/callproc.html
                */
                Statement stmt = conn.createStatement();
                ResultSet rs = stmt.executeQuery(String.format("SELECT * FROM %s()",  SQL_TOP_10_PROCEDURE));

                // TODO Loop over results and get the main values
                while (rs.next()) {
                    int childId = rs.getInt(1);
                    String childName = rs.getString(2);
                    String childCity = rs.getString(3);
                    int totalNumToys = rs.getInt(4);
                    int totalNumLetters = rs.getInt(5);
                    int maxNumToysInLetter = rs.getInt(6);
                    int mostAskedToy = rs.getInt(7);

                    Exercise2Row row = new Exercise2Row( childId, childName, childCity,
                                                            totalNumToys, totalNumLetters,
                                                             maxNumToysInLetter, mostAskedToy);

                    list.add(row);
                }

                // TODO End loop
                report.printReport(list);
                // TODO Close All resources
                rs.close();
                stmt.close();

            } catch (SQLException e) {
                e.printStackTrace();

                if (e.getSQLState().equalsIgnoreCase(NO_SQL_FUNCTION)) {
                    try {
                        this.register_procedure_in_db(conn);
                    } catch (Exception e1) {
                        e1.printStackTrace();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    /**
     *  Register the procedure requested by file path parameter in DB.
     *  File is hardcopied for this version.
     *
     * @param conn Connection object
     * @throws Exception
     */
    private void register_procedure_in_db(Connection conn) throws Exception {
        Boolean success = this.executeDBScripts("procedure.sql", conn.createStatement());

        if (success)
            this.run();
        else
            throw new Exception("It was an error trying to get data from DB");
    }

    /**
     * Execute the script in DB.
     *
     * @param scriptFilePath
     * @param stmt
     * @return Boolean True if script was executed.
     * @throws IOException
     * @throws SQLException
     */
    private boolean executeDBScripts(String scriptFilePath, Statement stmt) throws IOException, SQLException {
        boolean isScriptExecuted = false;
        try {
            BufferedReader in = fileUtilities.readSQLFileFromFileClassPath(scriptFilePath);
            String str;
            StringBuilder sb = new StringBuilder();
            while ((str = in.readLine()) != null) {
                sb.append(str).append("\n ");
            }
            in.close();
            stmt.executeUpdate(sb.toString());
            isScriptExecuted = true;
        } catch (Exception e) {
            System.err.println("Failed to Execute" + scriptFilePath +". The error is"+ e.getMessage());
        }
        return isScriptExecuted;
    }

    /**
     * Not in used.....
     * @param cst
     * @throws SQLException
     */
    private void register_procedure_ouput(CallableStatement cst) throws SQLException {
        cst.registerOutParameter(1, Types.SMALLINT);
        cst.registerOutParameter(2, Types.VARCHAR);
        cst.registerOutParameter(3, Types.VARCHAR);
        cst.registerOutParameter(4, Types.INTEGER);
        cst.registerOutParameter(5, Types.INTEGER);
        cst.registerOutParameter(6, Types.INTEGER);
        cst.registerOutParameter(7, Types.SMALLINT);
    }
}