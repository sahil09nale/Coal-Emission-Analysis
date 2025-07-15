package com.coalemission.beans;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

// Main class
public class CoalMineEmissionJDBCUpdted{
    private Scanner sc;
    private Connection con;

    public CoalMineEmissionJDBCUpdted() {
        sc = new Scanner(System.in);
        try {
            con = Getconnection.getConnection();
        } catch (SQLException e) {
            System.err.println("ERROR: Failed to establish database connection.");
            e.printStackTrace();
        }
    }

    public void insertData() {
        printHeader("Insert New Emission Record");
        System.out.print("Enter Mine ID (e.g., MINEXX): ");
        String mid = sc.next();
        System.out.print("Enter Activity Type (Excavation, Transportation, Drilling, Hauling): ");
        String at = sc.next();
        System.out.print("Enter Fuel Consumed (in liters): ");
        double fuel = sc.nextDouble();
        System.out.print("Enter Emission Factor (EF): ");
        double ef = sc.nextDouble();
        sc.nextLine();
        System.out.print("Enter Equipment Type: ");
        String equip = sc.nextLine();

        String sql = "INSERT INTO emissions(mineID, activityType, equipment, fuelConsumed, emissionFactor) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, mid);
            ps.setString(2, at);
            ps.setString(3, equip);
            ps.setDouble(4, fuel);
            ps.setDouble(5, ef);
            int i = ps.executeUpdate();
            if (i == 1) {
                System.out.println();
                System.out.println("[SUCCESS] Record successfully added to the emissions database.");
                System.out.println();
            }
        } catch (SQLException e) {
            System.err.println("ERROR: Could not insert data into database.");
            e.printStackTrace();
        }
    }

    public void updateData() {
        printHeader("Update Emission Factor");
        System.out.print("Enter the ID of the record to update: ");
        int m_id = sc.nextInt();
        System.out.print("Enter the new Emission Factor (EF): ");
        double e_f = sc.nextDouble();

        String sql = "UPDATE emissions SET emissionFactor = ? WHERE id = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setDouble(1, e_f);
            ps.setInt(2, m_id);
            int rows = ps.executeUpdate();
            if (rows > 0) {
                System.out.println();
                System.out.println("[SUCCESS] Emission factor updated for record ID: " + m_id);
                System.out.println();
            } else {
                System.out.println();
                System.out.println("[NOTICE] No record found with ID: " + m_id);
                System.out.println();
            }
        } catch (SQLException e) {
            System.err.println("ERROR: Could not update emission factor.");
            e.printStackTrace();
        }
    }

    public void delData() {
        printHeader("Delete Emission Record");
        System.out.print("Enter the ID of the record to delete: ");
        int m_id = sc.nextInt();

        String sql = "DELETE FROM emissions WHERE id = ?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, m_id);
            int rows = ps.executeUpdate();
            if (rows > 0) {
                System.out.println();
                System.out.println("[SUCCESS] Record with ID " + m_id + " deleted.");
                System.out.println();
            } else {
                System.out.println();
                System.out.println("[NOTICE] No record found with ID: " + m_id);
                System.out.println();
            }
        } catch (SQLException e) {
            System.err.println("ERROR: Could not delete record.");
            e.printStackTrace();
        }
    }

    public void showData() {
        printHeader("All Emission Records");
        System.out.println("----------------------------------------------------------------------------");
        System.out.printf("%-10s %-15s %-15s %-10s %-10s %-15s%n", "Mine ID", "Activity", "Equipment", "Fuel(L)", "EF", "Emission(kg)");
        System.out.println("----------------------------------------------------------------------------");

        double totalEmission = 0;
        String query = "SELECT * FROM emissions";
        try (Statement stmt = con.createStatement();
             ResultSet rs = stmt.executeQuery(query)) {
            while (rs.next()) {
                String mineID = rs.getString("mineID");
                String activityType = rs.getString("activityType");
                String equipment = rs.getString("equipment");
                double fuel = rs.getDouble("fuelConsumed");
                double ef = rs.getDouble("emissionFactor");
                double emission = fuel * ef;

                System.out.printf("%-10s %-15s %-15s %-10.2f %-10.2f %-15.2f%n", mineID, activityType, equipment, fuel, ef, emission);
                totalEmission += emission;
            }
            System.out.println();
            System.out.println("Total Emissions from All Activities: " + totalEmission + " kg CO2");
            System.out.println();
        } catch (SQLException e) {
            System.err.println("ERROR: Could not retrieve emission data.");
            e.printStackTrace();
        }
    }

    public void showEmissionReport() {
        printHeader("Emission Report with Analysis");
        System.out.println("-----------------------------------------------------------");
        System.out.printf("%-10s %-15s %-10s %-10s %-15s%n", "Mine ID", "Equipment", "Fuel(L)", "EF", "Emission(kg)");
        System.out.println("-----------------------------------------------------------");

        String query = "SELECT * FROM emissions";
        try (Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(query)) {
            while (rs.next()) {
                String mineID = rs.getString("mineID");
                String equipment = rs.getString("equipment");
                double fuel = rs.getDouble("fuelConsumed");
                double ef = rs.getDouble("emissionFactor");
                double emission = fuel * ef;

                System.out.printf("%-10s %-15s %-10.2f %-10.2f %-15.2f%n", mineID, equipment, fuel, ef, emission);

                if (emission <= 100) {
                    System.out.println("Status: Safe");
                    System.out.println("-> No remedial action required. Emissions are within acceptable limits.");
                    System.out.println("----------------------------------------------------------------------------");
                } else if (emission <= 300) {           
                    System.out.println("Status: Moderate");
                    System.out.println("-> Recommended: Basic carbon sinks (e.g., tree planting).");
                    System.out.println("--------------------------------------------------------------");
            
                } else if (emission <= 600) {
                	System.out.println("Status: High");
                    System.out.println("-> Recommended: Medium remedial actions (e.g., afforestation, soil carbon techniques).");
                    System.out.println("------------------------------------------------------------------------------------------");
                } else {
                	System.out.println("Status: Critical");
                    System.out.println("-> Recommended: Aggressive measures (e.g., wetland restoration, large-scale reforestation).");
                    System.out.println("-----------------------------------------------------------------------------------------------");
                }
            }
        } catch (SQLException e) {
            System.err.println("ERROR: Could not generate emission report.");
            e.printStackTrace();
        }
    }

    private void printHeader(String title) {
        System.out.println("===========================================================");
        System.out.println("  " + title);
        System.out.println("===========================================================");
        System.out.println();
    }

    public static void main(String[] args) {
    	CoalMineEmissionJDBCUpdted obj = new CoalMineEmissionJDBCUpdted();
        int ch;
        do {
            System.out.println("===========================================================");
            System.out.println("             Carbon Emission Analysis System");
            System.out.println("===========================================================");
            System.out.println("1. Insert Coal Data");
            System.out.println("2. Show All Emission Data");
            System.out.println("3. Update Emission Factor");
            System.out.println("4. Delete Record");
            System.out.println("5. Show Emission Report with Analysis");
            System.out.println("6. Exit");
            System.out.print("Enter your choice: ");
            ch = obj.sc.nextInt();

            switch (ch) {
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
                    System.out.println();
                    System.out.println("Exiting the system. Goodbye!");
                    System.out.println();
                    break;
                default:
                    System.out.println();
                    System.out.println("Invalid choice. Please select a valid option (1-6).");
                    System.out.println();
            }
        } while (ch != 6);
    }
}
