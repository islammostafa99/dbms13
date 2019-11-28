package eg.edu.alexu.csd.oop.db;

public class Main {
    public static void main(String[] args){
//        String s= "  Ahmed Adel salama";
//        String s1 = s.substring(s.lastIndexOf("adel"));
//        String[] arr = s1.split("[ ]+");
//        for(String x : arr){
//            System.out.println(x);
//        }
        ControllerFactory a = new ControllerFactory();
//        a.Command("Create database AAAV");
//        a.Command("Create database AAAV;");
//        a.Command("create table ieee;");
//        a.Command("insert into ieee(name,weight)values('eslam' ,'50');");
//        a.Command("SELECT * FROM ieee;");
//        a.Command("SELECT name, age FROM ieee;");
//        a.Command("select name from ieee;");
//        a.Command("drop table ieee;");
//        a.Command("drop database abdo;");
//        a.Command("drop database;");
//        a.Command("drop data base;;;");
//        a.Command("drop database;");
         a.Command("create database abdo;");
         a.Command("create table ieee(  name string ,weight int , age int);");
//        a.addCommand("create database ahmed;");
//        a.addCommand("create table ieee (name string ,weight int , age int,tall int);");
//        String command="insert into ieee (name  ,weight)"
//
//                + "values ('eslam' ,'50');";
//        String command1="insert into ieee"
//
//                + "values ( 'moh11','30','20','10');";
//        a.addCommand(command);
//        //a.addCommand(command1);
//        // a.addCommand("SELECT name, weight" +
//        // "from ieee;");
////        a.addCommand("SELECT * FROM ieee;");
//        //a.addCommand("SELECT name, age FROM ieee;");
////        a.addCommand("select name from ieee;");
////        a.addCommand("select name, weight, age from ieee;");
////        //  a.addCommand("UPDATE Customers\n SET ContactName = 'Alfred Schmidt', City= 'Frankfurt'\n WHERE CustomerID = 1;");
//        // a.addCommand("DELETE FROM ieee WHERE weight='50';");
//        a.addCommand("update ieee set weight = 30;");
//        a.addCommand("select weight from ieee;");
////        a.addCommand("drop table ieee;");
////        a.addCommand("drop database abdo;");
    }
}