package com.company;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class Main {
    private static final boolean WARNING = true;
    private static final boolean INFO = false;

    public static void main(String[] args) throws IOException, ParseException, SQLException {
        // write your code here
        Log log = new Log();
        DBWriter dbw = new DBWriter();
        DBReader dr = new DBReader();
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
//        dbw.writeTrain();
        int id = 0;
        do {

            System.out.println("Welcome to Railway Reservation\nEnter ID:");
            id = Integer.parseInt(br.readLine());
            if (id == 0) {
                int ch1;
                do {
                    System.out.println("Hello Admin\n1.User\n2.Trains\n3.Logout");
                    log.log(WARNING, "Admin Logged In");
                    ch1 = Integer.parseInt(br.readLine());
                    if (ch1 == 1) {
                        int ch2;
                        do {
                            System.out.println("1.Add\n2.View\n3.Count\n4.Exit");
                            ch2 = Integer.parseInt(br.readLine());
                            switch (ch2) {
                                case 1:
                                    System.out.print("Enter Name: ");
                                    String name = br.readLine();
                                    System.out.print("Enter Password: ");
                                    String pwd = br.readLine();
                                    System.out.print("Enter Phone number: ");
                                    String ph = br.readLine();
                                    System.out.print("Entetr age: ");
                                    int age = Integer.parseInt(br.readLine());
                                    dbw.writeUser(name, pwd, ph, age);
                                    log.log(INFO, "User Added");
                                    break;
                                case 2:
                                    dr.viewUser();
                                    System.out.print("Enter UID: ");
                                    int uid = Integer.parseInt(br.readLine());
                                    dr.viewUser(uid);
                                    log.log(INFO, "Users Viewed "+uid);
                                    break;
                                case 3:
                                    System.out.println("Total number of users: " + dr.countUser());
                            }
                        } while (ch2 < 4);
                    }
                    else if(ch1==2)
                    {
                        int ch2;
                        do {
                            System.out.println("1.Add\n2.View\n3.Count\n4.Exit");
                            ch2 = Integer.parseInt(br.readLine());
                            switch (ch2) {
                                case 1:
                                    dbw.writeTrain();
                                    log.log(INFO,"Train created");
                                    break;
                                case 2:
                                    dr.viewTrains();
                                    break;
                                case 3:
                                    System.out.println("Total number of Trains: " + dr.countTrain());
                            }
                        } while (ch2 < 4);
                    }

                } while (ch1 < 3);
                log.log(WARNING, "Admin Logged off");
            }
            else if(id>0)
            {
                System.out.print("Enter Password: ");
                String pwd = br.readLine();
                if(!dr.checkPass(id,pwd)) {
                    System.out.println("User / Password not found");
                    continue;
                }
                else
                {
                    System.out.println("Welcome back ");
                    log.log(INFO,"User "+id+" Logged in");
                    int ch1;
                    do {
                        System.out.println("1.Book Train\n2.View Booked PNR\n3.Cancel Tickets\n4.View My Details\n5. Exit");
                        ch1 = Integer.parseInt(br.readLine());
                        switch (ch1) {
                            case 1:
                                dbw.bookTrain(id);
                                break;
                            case 2:
                                dr.viewPNR(id);
                                break;
                            case 3:
                                int pnr = dbw.cancel(id);
                                log.log(INFO,"PNR: "+pnr+" is cancelled");
                                break;

                            case 4:
                                dr.viewUser(id);
                                break;
                        }
                    } while (ch1 < 5);
                    log.log(INFO,"User: "+id+" Logged off");
                }

            }
        } while (id >= 0);
    }
}
