package com.coalemission.beans;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

// Main class
public class CoalMineEmissionJDBC
{
	Scanner sc;
	Connection con;
	public CoalMineEmissionJDBC() {
		// TODO Auto-generated constructor stub
		sc = new Scanner(System.in);
		try {
			con= Getconnection.getConnection();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void updateData()
	{
		System.out.println("Enter the ID to update data");
		int m_id=sc.nextInt();
		
		System.out.println("Enter the emmission factor to update");
		double e_f=sc.nextDouble();
		
		String sql="update emissions set emissionFactor=? where id=?";
		try
		{
			PreparedStatement ps=con.prepareStatement(sql);
			ps.setDouble(1,e_f);
			ps.setInt(2,m_id);
			int rows_updated = ps.executeUpdate(); 
			System.out.println(m_id+"record is updated");
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
	}
	
	public void delData()
	{
		System.out.println("Enter the ID to delete data");
		int m_id=sc.nextInt();
		
		String sql="delete from emissions where id=?";
		try
		{
			PreparedStatement ps=con.prepareStatement(sql);
			ps.setInt(1,m_id);
			int rows_deleted = ps.executeUpdate(); 
			System.out.println(m_id+"record is deleted");
		}
		catch(SQLException e)
		{
			e.printStackTrace();
		}
	}
	public void insertData()
	{
		//System.out.println("Welcome to Report");
		System.out.println("Enter MINEID example:MINEXX");
		String mid= sc.next();
		
		System.out.println("Enter Activity Type (Excavation,Transportation,Drilling,Hauling");
		String at= sc.next();

		System.out.println("Enter Fuel");
		double fuel= sc.nextDouble();

		System.out.println("Enter Emission Factor");
		double ef= sc.nextDouble();
		sc.nextLine();
		System.out.println("Enter Equipment type");
		String equip= sc.nextLine();
        
	
		try {
			//Connection con= Getconnection.getConnection();
			String sql = ("insert into emissions(mineID,activityType,equipment,fuelConsumed,emissionFactor) values(?,?,?,?,?)");
			PreparedStatement ps=con.prepareStatement(sql);
			ps.setString(1,mid);
			ps.setString(2,at);
			ps.setString(3,equip);
			ps.setDouble(4,fuel);
			ps.setDouble(5,ef);
			int i=ps.executeUpdate();
			if(i==1)
			{
				System.out.println("Record is Successfully added");
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
	}
	public void showEmissionReport()
	{
		
			try {
				Statement st=con.createStatement();
				ResultSet rs=st.executeQuery("select * from emissions");
				System.out.println("Carbon Emission Report:");
		       // System.out.printf("%-10s %-15s %-10s %-10s %-10s\n", "MineID", "Equipment", "Fuel", "EF", "Emission");
				while(rs.next())
				{
					String mineID = rs.getString("mineID");
	                String activityType = rs.getString("activityType");
	                String equipment = rs.getString("equipment");
	                double fuel = rs.getDouble("fuelConsumed");
	                double ef = rs.getDouble("emissionFactor");
	                double calemission=fuel*ef;
	                System.out.printf("%-10s %-15s %-10.2f %-10.2f %-10.2f\n",
	                        mineID, equipment, fuel, ef,calemission);  
	                if(calemission<=100)
	                {
	                	System.out.println("No action is triggered\nThe emissions are within a tolerable range, and hence, no remedial action is deemed necessa"
	                			+ "ry.");
	                }
	                else if(calemission>100 && calemission<=300)
	                {
	                	System.out.println("Carbon sink calculation\nModerate emissions detected; basic carbon sinks like tree planting can effectively neutralize this impact.");
	                }
	                else if(calemission>300 && calemission<=600)
	                {
	                	System.out.println("Sink + Medium Remedial Action\nHigh carbon emissions identified; appropriate remedial steps such as afforestation and soil carbon sequestration are advised");
	                }
	                else
	                {
	                	System.out.println("Sink + Aggressive Remedial\nCritical emission level recorded; large-scale carbon offset mechanisms like wetland restoration or reforestation must be initiated");
	                }
				}
				
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
	}
	
	public void showData()
	{
		double totalEmission = 0;

        System.out.println("Carbon Emission Report:");
        System.out.printf("%-10s %-15s %-10s %-10s %-10s\n", "MineID", "Equipment", "Fuel", "EF", "Emission");

        try {
            Connection conn =Getconnection.getConnection();
            String query = "SELECT * FROM emissions";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);

            while (rs.next()) {
                String mineID = rs.getString("mineID");
                String activityType = rs.getString("activityType");
                String equipment = rs.getString("equipment");
                double fuel = rs.getDouble("fuelConsumed");
                double ef = rs.getDouble("emissionFactor");

                MiningActivity activity;

                switch (activityType.toLowerCase()) {
                    case "excavation":
                        activity = new Excavation(mineID, equipment, fuel, ef);
                        break;
                    case "transportation":
                        activity = new Transportation(mineID, equipment, fuel, ef);
                        break;
                    case "drilling":
                        activity = new Drilling(mineID, equipment, fuel, ef);
                        break;
                    case "hauling":
                        activity = new Hauling(mineID, equipment, fuel, ef);
                        break;
                    default:
                        activity = new MiningActivity(mineID, equipment, fuel, ef);
                }

                activity.displayInfo();
                totalEmission += activity.calculateEmission();
            }

            System.out.println("\nTotal Emissions from All Activities: " + totalEmission + " kg CO2");

            rs.close();
            stmt.close();
            conn.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
		
	}
    public static void main(String[] args) {
        CoalMineEmissionJDBC obj=new CoalMineEmissionJDBC();
        System.out.println("---Welcome to Carbon Emission Analysis in Coal Mining---\n");
        System.out.println("Please enter the necessary details:\t");
        int ch;
        do
        {
        	System.out.println("1.Insert Coal Data\n2.Show Coal Data\n3.Update Emission Factor\n4.Delete Data\n5.Show Emission Report\n6.Exit\nEnter your choice");
        	ch= obj.sc.nextInt();
        	switch(ch)
        	{
        		case 1:
        			obj.insertData();
        			break;
        		case 2:
        			obj.showData();
        			break;
        		case 3:
        			obj.updateData();
        			break;
        		case 4:
        			obj.delData();
        			break;
        		case 5:
        			obj.showEmissionReport();
        			break;
        		case 6:
        			System.out.println("Exiting....");
        			break;
        	}
        }
        while(ch!=6);
    }
}