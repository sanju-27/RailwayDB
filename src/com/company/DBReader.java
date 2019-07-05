package com.company;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.sql.*;

public class DBReader {
    private static String dbURL = "jdbc:mysql://localhost:3306/railway";
    private static String dbUser = "root";
    private static String dbPwd = "";
    private static String countUser = "SELECT * FROM user";
    private static String viewUser = "SELECT * FROM user WHERE uid =?";
    private static String viewTrain = "SELECT * FROM train";
    private static String countTrain = "SELECT trainno FROM train";
    private Connection con;
    private BufferedReader br;

    DBReader() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            this.con = DriverManager.getConnection(dbURL, dbUser, dbPwd);
            this.br = new BufferedReader(new InputStreamReader(System.in));
        } catch (ClassNotFoundException | SQLException e) {
            e.printStackTrace();
        }
    }

    int countUser() {
        int count = 0;
        try {
            PreparedStatement ps = con.prepareStatement(countUser);
            ResultSet rs = ps.executeQuery();
//            count = rs.getRow();
            while (rs.next())
                count++;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return count;
    }

    void viewUser(int id) {
        try {
            PreparedStatement ps = con.prepareStatement(viewUser);
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                System.out.println("UID: " + rs.getInt(1) + " Name: " + rs.getString(2) + "\nPhone: " + rs.getString(4) + " Age: " + rs.getInt(5));
            } else
                System.out.println("User not found");
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    void viewUser() {
        try {
            PreparedStatement ps = con.prepareStatement(countUser);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                System.out.println(rs.getInt(1) + ". " + rs.getString(2));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    void viewTrains() {
        try {
            PreparedStatement ps = con.prepareStatement(viewTrain);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                System.out.println("Train No.: " + rs.getInt(1));
                System.out.println("Route:");
                System.out.println("From: " + rs.getString(2) + " To: " + rs.getString(3));
                System.out.println("Working Days:\n" + rs.getString(11));
                System.out.println("------------------------------------------------------------");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    int countTrain() {
        int count = 0;
        try {
            PreparedStatement ps = con.prepareStatement(countTrain);
            ResultSet rs = ps.executeQuery();
            while (rs.next())
                count++;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return count;
    }

    boolean checkPass(int id, String pwd) {

        try {
            PreparedStatement ps = con.prepareStatement(viewUser);
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return pwd.equals(rs.getString(3));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    void viewPNR(int id) {
        try {
            PreparedStatement ps = con.prepareStatement("SELECT * FROM ticket WHERE uid = ?");
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            ps = con.prepareStatement("SELECT * FROM user WHERE uid = ?");
            ps.setInt(1, id);
            ResultSet rs2 = ps.executeQuery();
            rs2.next();
            while (rs.next()) {

                System.out.println("PNR Number: " + rs.getInt(1));
//                if(!rs2.next())
                System.out.println("User Name: " + rs2.getString(2));
                System.out.println("Number of Passengers: " + rs.getInt(5) + "\tBill: " + rs.getString(6));
                ps = con.prepareStatement("SELECT * FROM train_status WHERE statno = ?");
                ps.setInt(1, rs.getInt("statno"));
                ResultSet rs3 = ps.executeQuery();
                if (rs3.next()) {
                    System.out.println("Date: " + rs3.getString(2));
                    ps = con.prepareStatement("SELECT * FROM train WHERE trainno = ?");
                    ps.setInt(1, rs3.getInt("trainno"));
                    ResultSet rs4 = ps.executeQuery();
                    if (rs4.next())
                        System.out.println("From: " + rs4.getString(2) + "\tTo: " + rs4.getString(3));
                }
                System.out.println("-----------------------------------");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    String viewName(int id) {
        String s = "";
        try {
            PreparedStatement ps = con.prepareStatement("SELECT name FROM user WHERE uid = "+id);
            ResultSet rs = ps.executeQuery();
            if(rs.next())
                s = rs.getString(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return s;
    }
}
