package com.company;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DBWriter {
    private static String dbURL = "jdbc:mysql://localhost:3306/railway";
    private static String dbUser = "root";
    private static String dbPwd = "";
    private static String writeTrain = "INSERT INTO train(trainno,source,dest,genprice,acprice,gen,ac,gtat,atat,class,workdays) VALUES(?,?,?,?,?,?,?,?,?,?,?)";
    private static String fetch = "SELECT * FROM train_status WHERE trainno = ? AND date = ?";
    private static String newstatus = "INSERT INTO train_status(date,gen,ac,gentat,actat,trainno) VALUES (?,?,?,?,?,?)";
    private static String updatestatus = "UPDATE train_status SET ? = ? WHERE tid = ?";
    private static String insertUser = "INSERT INTO user(name,pwd,phno,age) VALUES (?,?,?,?)";
    private static String available = "SELECT * FROM train WHERE source = ? AND dest = ?";
    private static String clean = "DELETE FROM train WHERE date <= ?";
    private static String selectAll = "Select * FROM train";
    private static int ac = 900, sl = 350;
    Connection con;
    BufferedReader br;

    public DBWriter() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            this.con = DriverManager.getConnection(dbURL, dbUser, dbPwd);
            this.br = new BufferedReader(new InputStreamReader(System.in));
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        this.dbClean();
    }

    void writeTrain() throws IOException {

        System.out.println("Enter the following Details:");
        System.out.println("Train No.\tSource\tDestination\tSL\tAC\tSL Tatkal\tAC Tatkal");
        String[] s = br.readLine().split("\t");
        System.out.println("Special Class?\n1.General\n2.Shatapdi\n3.Duronto");
        int cls = Integer.parseInt(br.readLine());
        System.out.println("Enter Days of the week: [1-> Sunday]");
        String days = br.readLine();
        float acp = ac, slp = sl;
        if (cls == 2) {
            acp *= 1.5;
            slp *= 1.5;
        } else if (cls == 3) {
            acp *= 2.2;
            slp *= 2.2;
        }
        try {
            PreparedStatement pr = con.prepareStatement(writeTrain, Statement.RETURN_GENERATED_KEYS);
            pr.setInt(1, Integer.parseInt(s[0]));
            pr.setString(2, s[1]);
            pr.setString(3, s[2]);
            pr.setFloat(4, slp);
            pr.setFloat(5, acp);
            pr.setInt(6, Integer.parseInt(s[3]));
            pr.setInt(7, Integer.parseInt(s[4]));
            pr.setInt(8, Integer.parseInt(s[5]));
            pr.setInt(9, Integer.parseInt(s[6]));
            pr.setInt(10, cls);
            pr.setString(11, days);
            int x = pr.executeUpdate();
//            System.out.println(x);
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    int updateTrain(String date, int tno, int count, String type) {
        int statid = 0;
        try {
            PreparedStatement ps = con.prepareStatement(fetch);
            ps.setString(2, date);
            ps.setInt(1, tno);
            ResultSet rs = ps.executeQuery();
            int ac = 0, gen = 0, actat = 0, gentat = 0;
            if (type.equalsIgnoreCase("AC"))
                ac = count;
            else if (type.equalsIgnoreCase("SL"))
                gen = count;
            else if (type.equalsIgnoreCase("SLTAT"))
                gentat = count;
            else if (type.equalsIgnoreCase("ACTAT"))
                actat = count;
            if (!rs.next()) {
                ps = con.prepareStatement(newstatus, Statement.RETURN_GENERATED_KEYS);
                ps.setString(1, date);
                ps.setInt(2, gen);
                ps.setInt(3, ac);
                ps.setInt(4, gentat);
                ps.setInt(5, actat);
                ps.setInt(6, tno);
                ps.executeUpdate();
                ResultSet rs1 = ps.getGeneratedKeys();
                if (rs1.next())
                    statid = rs1.getInt(1);
            } else {
                gen = rs.getInt(3);
                ac = rs.getInt(4);
                gentat = rs.getInt(5);
                actat = rs.getInt(6);
                ResultSetMetaData rsmd = rs.getMetaData();
                int x = 0, y = 0;
                if (type.equalsIgnoreCase("AC")) {
                    y = count + ac;
                    x = 4;
                } else if (type.equalsIgnoreCase("SL")) {
                    y = gen + count;
                    x = 3;
                } else if (type.equalsIgnoreCase("SLTAT")) {
                    y = gentat + count;
                    x = 5;
                } else if (type.equalsIgnoreCase("ACTAT")) {
                    y = actat + count;
                    x = 6;
                }

                String name = rsmd.getColumnName(x);
//                System.out.println(name);
                ps = con.prepareStatement("UPDATE train_status SET " + name + " = ? WHERE statno = ?", Statement.RETURN_GENERATED_KEYS);
//                ps.setString(1,name);
                ps.setInt(1, y);
                statid = rs.getInt(1);
                ps.setInt(2, rs.getInt(1));
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return statid;
    }


    public void writeUser(String name, String pwd, String ph, int age) {

        try {
            PreparedStatement ps = con.prepareStatement(insertUser);
            ps.setString(1, name);
            ps.setString(2, pwd);
            ps.setString(3, ph);
            ps.setInt(4, age);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public void bookTrain(int uid) throws IOException, ParseException, SQLException {
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        int count = 0;
        Calendar c = Calendar.getInstance();
        Date date;
        Date today = new Date();
        String dayofweek = "";
        System.out.println("Tatkal? [Y/N]");
        boolean tat = br.readLine().equalsIgnoreCase("y");
        System.out.print("Enter From: ");
        String from = br.readLine();
        System.out.print("Enter To: ");
        String to = br.readLine();
        if (!tat) {
            System.out.print("Enter date: ");
            date = sdf.parse(br.readLine());
            c.setTime(date);
            if (date.compareTo(today) < 0) {
                System.out.println("Invalid date");
                return;
            }

        } else {
            c.add(Calendar.DATE, 1);
            date = c.getTime();
        }
        dayofweek += c.get(Calendar.DAY_OF_WEEK);
        PreparedStatement ps = con.prepareStatement(available);
        ps.setString(1, from);
        ps.setString(2, to);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            if (rs.getString(11).contains(dayofweek)) {
                System.out.println(rs.getInt(1));
                count++;
            }
        }
        if (count == 0) {
            System.out.println("No Trains :(");
            return;
        }
        System.out.print("Enter Train Number: ");
        int train_no = Integer.parseInt(br.readLine());
        ps = con.prepareStatement("SELECT * FROM train WHERE trainno = ?");
        ps.setInt(1, train_no);
        rs = ps.executeQuery();
        PreparedStatement ps1 = con.prepareStatement("SELECT * FROM train_status WHERE trainno = ? AND date = ?");
        String datestr = sdf.format(date);
        ps1.setString(2, datestr);
        ps1.setInt(1, train_no);
        ResultSet rs1 = ps1.executeQuery();
        int sl = 0, ac = 0, act = 0, slt = 0, slp = 0, acp = 0;
        if (rs.next()) {
            sl = rs.getInt(6);
            ac = rs.getInt(7);
            act = rs.getInt(9);
            slt = rs.getInt(8);
            slp = rs.getInt(4);
            acp = rs.getInt(5);
            if (rs1.next()) {
                sl -= rs1.getInt(3);
                ac -= rs1.getInt(4);
                act -= rs1.getInt(6);
                slt -= rs1.getInt(5);
            }
        }
        if (tat) {
            System.out.println("Tatkal Tickets Available");
            System.out.println("SL - " + slt + "\nAC - " + act);
        } else {
            System.out.println("Tickets Available");
            System.out.println("SL - " + sl + "\nAC - " + ac);
        }
        System.out.print("Enter Type: ");
        String type = br.readLine();
        System.out.print("Enter Count: ");
        int co = Integer.parseInt(br.readLine());
        int cost;
        if(type.equalsIgnoreCase("SL"))
            cost = slp*co;
        else
            cost = acp*co;
        if (tat)
            type += "TAT";
        int statno = this.updateTrain(datestr, train_no, co, type);
        ps1 = con.prepareStatement("INSERT INTO ticket(uid,statno,status,count,cost) VALUES(?,?,?,?,?)", Statement.RETURN_GENERATED_KEYS);
        ps1.setInt(1, uid);
        ps1.setInt(2, statno);
        ps1.setString(3, "CNF");
        ps1.setInt(4,co);
        ps1.setString(5,Integer.toString(cost));
        ps1.executeUpdate();
        ResultSet rsx = ps1.getGeneratedKeys();
        if (rsx.next())
            System.out.println("Ticket Booked:\nPNR Number = " + rsx.getLong(1));

    }

    void dbClean() {
        String clean = "DELETE FROM train_status WHERE statno=?";
        String sql = "SELECT * FROM train_status";
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        Date today = new Date();
        try {
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Date x = sdf.parse(rs.getString(2));
                if (x.compareTo(today) < 0) {
                    ps = con.prepareStatement(clean);
                    ps.setInt(1, rs.getInt(1));
                    ps.executeUpdate();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }


    }
    int cancel(int id)
    {
        int pnr=-1;
        try {
            PreparedStatement ps = con.prepareStatement("SELECT * FROM ticket WHERE uid = ?");
            ps.setInt(1,id);
            ResultSet rs = ps.executeQuery();
            System.out.println("Your Booked Tickets:");
            while (rs.next())
            {
                System.out.println(rs.getInt(1));
            }
            System.out.print("Enter PNR to cancel: ");
            pnr = Integer.parseInt(br.readLine());
            ps = con.prepareStatement("DELETE FROM ticket WHERE pnrno = ?");
            ps.setInt(1,pnr);
            int numrows = ps.executeUpdate();
            if(numrows==0)
                System.out.println("Invalid PNR");
            else
                System.out.println("Cancelled!!!");
            ps = con.prepareStatement("SELECT * FROM ticket WHERE status = ? ORDER BY stamp DESC LIMIT 1");
            ps.setString(1,"WL");
            ResultSet rsf =  ps.executeQuery();
            if(rsf.next()) {
                ps = con.prepareStatement("UPDATE ticket SET status = ? WHERE pnrno = ?");
                ps.setString(1, "CNF");
                ps.setInt(2, rsf.getInt(1));
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
     return pnr;
    }
}
