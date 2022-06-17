package util;

import javax.sql.rowset.CachedRowSet;
import javax.sql.rowset.RowSetProvider;
import java.sql.*;

public class MySqlHandler extends DbConfigs {

    static Connection dbcon;

    public static Connection getConnection() {

        String connectionString = "jdbc:mysql://" + DbConfigs.dbhost + ":" + DbConfigs.dbport + "/" + DbConfigs.dbname;

        try {
            dbcon = DriverManager.getConnection(connectionString, DbConfigs.dbuser, DbConfigs.dbpass);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dbcon;
    }

    public static void closeConnection() throws SQLException{
        try{
            if(dbcon != null && !dbcon.isClosed()){
                dbcon.close();
            }
        }catch(Exception e){throw e;}
    }

    public static ResultSet dbQuery(String query) throws SQLException{
        Statement statement = null;
        ResultSet resultSet = null;
        CachedRowSet cachedRowSet = null;
        try{
            getConnection();
            statement = dbcon.createStatement();
            resultSet = statement.executeQuery(query);
            cachedRowSet = RowSetProvider.newFactory().createCachedRowSet();
            cachedRowSet.populate(resultSet);
        }catch (SQLException e){
            throw e;
        } finally{
            if(resultSet != null){
                resultSet.close();
            }
            if(statement != null){
                statement.close();
            }
            closeConnection();
        }
        return cachedRowSet;
    }

    public static void dbUpdate(String sql)throws SQLException{
        PreparedStatement statement = null;
        try{
            getConnection();
            statement = dbcon.prepareStatement(sql);
            statement.executeUpdate(sql);
        } catch(SQLException e){
            throw e;
        } finally {
            if(statement !=null){
                statement.close();
            }
            closeConnection();
        }
    }
}

