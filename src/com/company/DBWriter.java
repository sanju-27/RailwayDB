package com.company;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.*;

public class DBWriter {
    private static String dbURL = "jdbc:mysql://localhost:3306/railway";
    private static String dbUser = "root";
    private static String dbPwd = "";
    private static String writeTrain = "INSERT INTO train(trainno,source,dest,genprice,acprice,gen,ac,gtat,atat,class,workdays) VALUES(?,?,?,?,?,?,?,?,?,?,?)";
    private static String fetch = "SELECT * FROM train_status WHERE trainno = ? AND date = ?";
    private static String insert = "INSERT INTO train(train,date) VALUES (?,?)";
    private static String update = "UPDATE train SET train = ? WHERE tid = ?";
    private static String clean = "DELETE FROM train WHERE date <= ?";
    private static String selectAll = "Select * FROM train";
    private static int ac = 900, sl = 350;
    Connection con;
    BufferedReader br;
    public DBWriter()
    {
        try{
            Class.forName("com.mysql.cj.jdbc.Driver");
            this.con = DriverManager.getConnection(dbURL, dbUser, dbPwd);
            this.br = new BufferedReader(new InputStreamReader(System.in));
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    void writeTrain()throws IOException
    {
        System.out.println("Enter the following Details:");
        System.out.println("Train No.\tSource\tDestination\tSL\tAC\tSL Tatkal\tAC Tatkal");
        String[] s = br.readLine().split("\t");
        System.out.println("Special Class?\n1.General\n2.Shatapdi\n3.Duronto");
        int cls = Integer.parseInt(br.readLine());
        System.out.println("Enter Days of the week: [1-> Sunday]");
        String days = br.readLine();
        float acp = ac, slp = sl;
        if(cls==2)
        {
            acp *= 1.5;
            slp *= 1.5;
        }
        else if(cls==3)
        {
            acp *= 2.2;
            slp *= 2.2;
        }
        try {
            PreparedStatement pr = con.prepareStatement(writeTrain, Statement.RETURN_GENERATED_KEYS);
            pr.setInt(1,Integer.parseInt(s[0]));
            pr.setString(2,s[1]);
            pr.setString(3,s[2]);
            pr.setFloat(4,slp);
            pr.setFloat(5,acp);
            pr.setInt(6,Integer.parseInt(s[3]));
            pr.setInt(7,Integer.parseInt(s[4]));
            pr.setInt(8,Integer.parseInt(s[5]));
            pr.setInt(9,Integer.parseInt(s[6]));
            pr.setInt(10,cls);
            pr.setString(11,days);
            int x = pr.executeUpdate();
            System.out.println(x);
        }
        catch (SQLException e) {
            e.printStackTrace();
        }

    }
    void updateTrain(String date, int tno, int count, String type)
    {
        try {
            PreparedStatement ps = con.prepareStatement(fetch);
            ps.setString(2,date);
            ps.setInt(5,tno);
            ResultSet rs = ps.executeQuery();
            if(!rs.next())
            {
                ps = con.prepareStatement(newstat);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }



}
