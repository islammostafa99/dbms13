package eg.edu.alexu.csd.oop.db;

import java.sql.SQLException;

public class Parser {

    private String[] command;
    private  DBMS_N database;
    private Table table;
    public void whatCommand(String query){
        /*
         *if integer or string
         * validate " "
         * check other tests
         * rage3 el regex hate3raf eh elly na2es
         * 2abl el kaws wel3alama wel 7agat dy space or not..check
         */
        query = query.toLowerCase();
        String commandTemp;
        if(query.matches("(?i)^([ ]*create[ ]+database[ ]+[a-z_0-9]+[ ]*)$")){
             commandTemp = query.substring(query.lastIndexOf("database"));
            this.command =commandTemp.split("[ ]+");
            database = new DBMS_N(query , this.command[1]);
            try {
                database.createDatabase(this.command[1] , database.dropIfExists(this.command[1]));
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }else if(query.matches("(?i)^([ ]*create[ ]+table[ ]+[a-z_0-9]+" +
                "[ ]*[(][ ]*([a-z_0-9]+[ ]+[a-z_0-9]+[ ]*,[ ]*)*([a-z_0-9]+[ ]+[a-z_0-9]+)+[ ]*[)][ ]*)$")){
                database.setQuery(query);
                commandTemp = query.substring(query.lastIndexOf("table"));
                this.command =commandTemp.split("[ ]+");
                String columns = query.substring(query.indexOf("("), query.indexOf(")"));//kda el kawas ela5eer me4 ma3aya
                table = new Table(database.getDatabaseName(), this.command[1], columns);
                try {
                    table.createTable(table.dropIfExists(database.getDatabaseName()+"\\"+this.command[1]+".xml"));
                } catch (SQLException e) {
                    e.printStackTrace();
                }
        }else if(query.matches("(?i)^([ ]*drop[ ]+database[ ]+[a-z_0-9]+[ ]*)$")){
            database.setQuery(query);
            commandTemp = query.substring(query.lastIndexOf("database"));
            this.command =commandTemp.split("[ ]+");
            try {
                database.dropDatabase(this.command[1] , database.dropIfExists(this.command[1]));
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }else if(query.matches("(?i)^([ ]*drop[ ]+table[ ]+[a-z_0-9]+[ ]*)$")){
            database.setQuery(query);
            commandTemp = query.substring(query.lastIndexOf("table"));
            this.command =commandTemp.split("[ ]+");
            try {
                table.dropTable(table.dropIfExists(database.getDatabaseName()+"\\"+this.command[1]+".xml"));
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }else if(query.matches("(?i)^([ ]*insert[ ]+into[ ]+[a-z_0-9]+[ ]*[(].*[)][ ]*" +
                "values[ ]*[(].*[)][ ]*)$")){
            database.setQuery(query);
        }
        else if(query.matches("(?i)^([ ]*select[ ]+(([a-z_0-9]+[ ]*,[ ]*)*([a-z_0-9]+)+|\\*)[ ]+from[ ]+[a-z_0-9]+" +
                "([ ]+where[ ]+([a-z_0-9]+[ ]*=[ ]*[a-z_0-9]+[ ]*))?[ ]*)$")){
            database.setQuery(query);
        }else if(query.matches("(?i)^([ ]*delete[ ]+from[ ]+[a-z_0-9]+" +
                "([ ]+where[ ]+([a-z_0-9]+[ ]*=[ ]*[a-z_0-9]+[ ]*))?[ ]*)$")){
            database.setQuery(query);
        }else if(query.matches("(?i)^([ ]*update[ ]+[a-z_0-9]+[ ]+" +
                "set[ ]+(([a-z_0-9]+[ ]*=[ ]*[a-z_0-9]+[ ]*,[ ]*)*([a-z_0-9]+[ ]*=[ ]*[a-z_0-9])+|\\*[ ]*=[ ]*[a-z_0-9]+)" +
                "([ ]+where[ ]+([a-z_0-9]+[ ]*=[ ]*[a-z_0-9]+[ ]*))?[ ]*)$")){
            database.setQuery(query);
        }else{
            System.out.println("syntax error");
        }
    }

    public boolean testWrongQuery(String query){
        /*
         *if integer or string
         * validate " "
         * check other tests
         * rage3 el regex hate3raf eh elly na2es
         * 2abl el kaws wel3alama wel 7agat dy space or not..check
         */
        if(query.matches("(?i)^([ ]*create[ ]+database[ ]+[a-z_0-9]+[ ]*)$")){
            return true;
        }else if(query.matches("(?i)^([ ]*create[ ]+table[ ]+[a-z_0-9]+" +
                "[ ]*[(][ ]*([a-z_0-9]+[ ]+[a-z_0-9]+[ ]*,[ ]*)*([a-z_0-9]+[ ]+[a-z_0-9]+)+[ ]*[)][ ]*)$")){
        }else if(query.matches("(?i)^([ ]*drop[ ]+database[ ]+[a-z_0-9]+[ ]*)$")){
            return true;

        }else if(query.matches("(?i)^([ ]*drop[ ]+table[ ]+[a-z_0-9]+[ ]*)$")){
            return true;

        }else if(query.matches("(?i)^([ ]*insert[ ]+into[ ]+[a-z_0-9]+[ ]*[(].*[)][ ]*" +
                "values[ ]*[(].*[)][ ]*)$")){
            return true;

        }
        else if(query.matches("(?i)^([ ]*select[ ]+(([a-z_0-9]+[ ]*,[ ]*)*([a-z_0-9]+)+|\\*)[ ]+from[ ]+[a-z_0-9]+" +
                "([ ]+where[ ]+([a-z_0-9]+[ ]*=[ ]*[a-z_0-9]+[ ]*))?[ ]*)$")){
            return true;

        }else if(query.matches("(?i)^([ ]*delete[ ]+from[ ]+[a-z_0-9]+" +
                "([ ]+where[ ]+([a-z_0-9]+[ ]*=[ ]*[a-z_0-9]+[ ]*))?[ ]*)$")){
            return true;

        }else if(query.matches("(?i)^([ ]*update[ ]+[a-z_0-9]+[ ]+" +
                "set[ ]+(([a-z_0-9]+[ ]*=[ ]*[a-z_0-9]+[ ]*,[ ]*)*([a-z_0-9]+[ ]*=[ ]*[a-z_0-9])+|\\*[ ]*=[ ]*[a-z_0-9]+)" +
                "([ ]+where[ ]+([a-z_0-9]+[ ]*=[ ]*[a-z_0-9]+[ ]*))?[ ]*)$")){
            return true;

        }
        return false;
    }

}
