package com.example.moneyconverteronline.services;

import com.example.moneyconverteronline.models.ValutaModel;
import org.apache.tomcat.jni.File;
import org.thymeleaf.util.Validate;

import java.io.*;
import java.net.URL;
import java.sql.*;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Scanner;

public class MoneyService {
    static Scanner scanner = new Scanner(System.in);
    static Statement stmt;
    static Statement stmt1;
    static ResultSet rs;
    static int rs1;
    static String sqlString;
    static String sqlString2;
    static Connection con;
    static boolean stop = false;
    static String insertSTRengSKUL;
    static String insertSTRengSKULCreateTable;
    static String insertSTRengSKULDropTable;
    ArrayList<ValutaModel> valutaModels = new ArrayList<ValutaModel>();
    static String calculatedValue;


    public String getCalculatedValue() {
        return calculatedValue;
    }

    public ArrayList<ValutaModel> getValutaModels() {
        return valutaModels;
    }

    URL urlOne = new URL("https://www.nationalbanken.dk/_vti_bin/DN/DataService.svc/CurrencyRatesXML?lang=da");
    Scanner sc = new Scanner(urlOne.openStream());
    String sqlQuwery = "";

    public MoneyService() throws IOException {
    }


    public void csvValutaInsert() throws SQLException {
        try
        {
            stmt = con.createStatement();
            stmt.executeUpdate("DROP TABLE values_money");
        }
        catch (Exception e)
        {
            System.out.println(e);
            System.out.println("No existing table to delete");
        }


        //Create a table in the database
        sqlString = "CREATE TABLE `values_money` ( " +
                "`ccode` VARCHAR(3) NULL," +
                "`desc` VARCHAR(25) NULL," +
                "`rate` DECIMAL(10,2) NULL)";
        stmt.executeUpdate(sqlString);


        sc.nextLine();
        sc.nextLine();
        sc.nextLine();
        int counter = 0;
        while (sc.hasNext() && counter < 33) {
            String[] valutaKnowledge = sc.nextLine().split("=");
            String[] codeOne = valutaKnowledge[1].replace("'", "").split(" ");
            String code = codeOne[0].replace("\"", "");
            String desc = valutaKnowledge[2].replace("'", "").replace("\"","").replace("rate","");
            String[] rateOne = valutaKnowledge[3].replace("'", "").split(" ");
            String rate = rateOne[0].replace("\"", "").replace(",",".");
            sqlQuwery = "INSERT INTO values_money (`ccode`,`desc`,`rate`)\n" +
                    "VALUES ('" + code + "','" + desc + "','" + rate + "');\n";
            if (!rate.equals("-")){
                insertData(sqlQuwery);}
            counter++;
        }
    }

    public void csvValutaWriterToFile() throws SQLException, IOException {
        FileWriter fileWriter = new FileWriter("src/main/resources/currencies.csv");
        fileWriter.flush();
        sc.nextLine();
        sc.nextLine();
        sc.nextLine();
        int counter = 0;
        while (sc.hasNext() && counter < 33) {
            String[] valutaKnowledge = sc.nextLine().split("=");
            String[] codeOne = valutaKnowledge[1].replace("'", "").split(" ");
            String code = codeOne[0].replace("\"", "");
            String desc = valutaKnowledge[2].replace("'", "").replace("\"","").replace("rate","");
            String[] rateOne = valutaKnowledge[3].replace("'", "").split(" ");
            String rate = rateOne[0].replace("\"", "").replace(",",".");
            sqlQuwery = code + ";" + desc + ";" + rate+"\n";
            if (!rate.equals("-")){
          fileWriter.write(sqlQuwery);
            }
            counter++;
        }
        fileWriter.close();
    }




    /*
Date: 13/03/2022

Simple demo using JDBC to connect to a Database and getting some data.

*/




    public void connect2Database() {

        try {
            //Define URL of database server for database named test_hotel
            //on the localhost with the default port number 3306.
            String url = "jdbc:mysql://localhost:3306/valuta_convertx";

            con = DriverManager.getConnection(url, "", "");

            //Display the URL and connection information
            System.out.println("URL: " + url);
            System.out.println("Det gik bra!");

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

    }

    public void insertData(String insertKSUQL) throws SQLException {

        //Get another statement object initialized as shown.
        stmt1 = con.createStatement();
        //Query the database, storing the result in an object of type ResultSet
        sqlString2 = insertKSUQL;
        rs1 = stmt1.executeUpdate(sqlString2);
    }

    public void showCurrencies() throws SQLException {
        //Get another statement object initialized as shown.
        stmt = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);

        //Query the database, storing the result in an object of type ResultSet
        sqlString = "SELECT * from values_money"  /*+ "'" + currencyName + "'"*/;
        rs = stmt.executeQuery(sqlString);

        //Use the methods of class ResultSet in a loop
        // to display all of the data in the result.
        while (rs.next()) {
            String code = rs.getString("ccode");
            String desc = rs.getString("desc");
            String rate = rs.getString("rate");
            valutaModels.add(new ValutaModel(code,desc,Double.parseDouble(rate)));
        }//end while loop


    }
    public void convertMoney(String currencyToConvertFrom, String amount) throws SQLException {
        //Get another statement object initialized as shown.
        stmt = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
        //Query the database, storing the result in an object of type ResultSet
        sqlString = "SELECT rate,ccode FROM values_money WHERE ccode = " + "'" + currencyToConvertFrom + "';";
        rs = stmt.executeQuery(sqlString);
        //Use the methods of class ResultSet in a loop
        // to display all of the data in the result.
        while (rs.next()) {
            String col1 = rs.getString("ccode");
            String col2 = rs.getString("rate");
            double calcValue = (Double.parseDouble(col2)/100)*Double.parseDouble(amount);
            calculatedValue = "\t" + calcValue + " DKK";
        }//end while loop


    }

    public void convertMoneyViaCsv(String currencyToConvertFrom, String amount) throws SQLException, FileNotFoundException {
        //Get another statement object initialized as shown.
        //Use the methods of class ResultSet in a loop
        // to display all of the data in the result.
        FileReader fileReader = new FileReader("src/main/resources/currencies.csv");
        Scanner scanner1 = new Scanner(fileReader);
        while (scanner1.hasNext()) {
            String[] countrySplit = scanner1.nextLine().split(";");
            if (countrySplit[0].equals(currencyToConvertFrom.toUpperCase(Locale.ROOT))) {
                String rate = countrySplit[2];
                double calcValue = (Double.parseDouble(rate) / 100) * Double.parseDouble(amount);
                calculatedValue = "\t" + calcValue + " DKK";
            }
        }//end while loop


    }
    public void convertMoneyFromSelectCURR() throws SQLException {
        //Get another statement object initialized as shown.
        stmt = con.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
        Scanner scanner1 = new Scanner(System.in);
        System.out.println("[FROM] (TYPE 3 CAPITAL LETTERS):");
        String currencyToConvertFrom = scanner1.nextLine();
        System.out.println("[TO] (TYPE 3 CAPITAL LETTERS):");
        String currencyToConvertTo = scanner1.nextLine();
        System.out.println("THE DESIRED AMOUNT");
        double amount = scanner.nextDouble();
        //Query the database, storing the result in an object of type ResultSet
        sqlString = "SELECT "+ currencyToConvertFrom+".rate / "+ currencyToConvertTo+".rate * "+amount+" AS CalcRate  FROM values_money WHERE code = " + "'" + currencyToConvertFrom
                + "' AND code = " + "'" + currencyToConvertTo + "';";
        rs = stmt.executeQuery(sqlString);

        //Use the methods of class ResultSet in a loop
        // to display all of the data in the result.
        System.out.println("DET SVARER TIL:");
        while (rs.next()) {
            String col1 = rs.getString("CalcRate");
            double col2 = rs.getDouble("rate");
            System.out.println("\t" + col1 + " DKK");
        }//end while loop


    }


}
