package edu.uoc.practica.bd.uocdb.exercise2;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import edu.uoc.practica.bd.util.Column;
import edu.uoc.practica.bd.util.DBAccessor;
import edu.uoc.practica.bd.util.Report;

public class Exercise2PrintReportOverQuery {
	public static void main(String[] args) {
		Exercise2PrintReportOverQuery app = new Exercise2PrintReportOverQuery();
		app.run();
	}

	private void run() {
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
			
			
			int childId;
			String childName = "";
			String childCity = "";
			int totalNumToys;
			int totalNumLetters;
			int maxNumToysInLetter;
			int mostAskedToy;
			// TODO Loop over results and get the main values

			// TODO End loop

			report.printReport(list);
			// TODO Close All resources
			
		}
	}
}