package util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import constants.DatabasePropertyKey;

public class JDBCConnector {
	
	public JDBCConnector() {
		
	}
	
	public Connection connect(String databasesName) throws ClassNotFoundException, SQLException {
		// DB接続用変数
		String DATABASE_NAME = databasesName;
		String PROPATIES = "?characterEncoding=UTF-8&serverTimezone=JST";
		String URL = "jdbc:mySQL://localhost/" + DATABASE_NAME + PROPATIES;

		//DB接続用・ユーザ定数
		String USER = PropertyManager.getProperties(DatabasePropertyKey.USER.getKey());
		String PASS = PropertyManager.getProperties(DatabasePropertyKey.PASS.getKey());

			//MySQL に接続する
			Class.forName("com.mysql.cj.jdbc.Driver");
			//データベースに接続
			Connection con = DriverManager.getConnection(URL, USER, PASS);

			// データベースに対する処理
			System.out.println("データベースに接続に成功");
			
			return con;

	}
}