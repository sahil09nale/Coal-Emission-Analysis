package com.coalemission.beans;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Getconnection {
	static String url = "jdbc:mysql://localhost:3306/coal_mines";
    static String user = "root";
    static String password = "Madhura@300"; // Change according to your MySQL setup

	
	public static Connection getConnection() throws SQLException
	{
		
		  Connection conn = DriverManager.getConnection(url, user, password);
		return conn;
		
	}

}
