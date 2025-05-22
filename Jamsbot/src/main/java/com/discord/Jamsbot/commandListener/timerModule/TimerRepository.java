package com.discord.Jamsbot.commandListener.timerModule;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import constants.TimerDbPropertyKey;
import util.JDBCConnector;
import util.PropertyManager;

public class TimerRepository {

	public long save(String userId, String channelId, LocalDateTime triggerTime) {
		String sql = "INSERT INTO timers (user_id, channel_id, trigger_time) VALUES (?, ?, ?)";
		JDBCConnector connector = new JDBCConnector();
		try (Connection conn = connector.connect(PropertyManager.getProperties(TimerDbPropertyKey.TIMER_DB_NAME.getKey()));
				PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
			stmt.setString(1, userId);
			stmt.setString(2, channelId);
			stmt.setTimestamp(3, Timestamp.valueOf(triggerTime));
			stmt.executeUpdate();

			try (ResultSet rs = stmt.getGeneratedKeys()) {
				if (rs.next())
					System.out.println(rs.getLong(1));
					return rs.getLong(1);
			}
		} catch (SQLException | ClassNotFoundException e) {
			e.printStackTrace();
		}
		return -1;
	}

	public List<TimerData> findAllUpcoming() {
		List<TimerData> list = new ArrayList<>();
		String sql = "SELECT * FROM timers WHERE trigger_time > NOW()";
		JDBCConnector connector = new JDBCConnector();
		try (Connection conn = connector.connect(PropertyManager.getProperties(TimerDbPropertyKey.TIMER_DB_NAME.getKey()));
				PreparedStatement stmt = conn.prepareStatement(sql);
				ResultSet rs = stmt.executeQuery()) {
			while (rs.next()) {
				TimerData data = new TimerData(
						rs.getLong("id"),
						rs.getString("user_id"),
						rs.getString("channel_id"),
						rs.getTimestamp("trigger_time").toLocalDateTime());
				list.add(data);
			}
		} catch (SQLException | ClassNotFoundException e) {
			e.printStackTrace();
		}
		return list;
	}

	public void delete(long id) {
		String sql = "DELETE FROM timers WHERE id = ?";
		JDBCConnector connector = new JDBCConnector();
		try (Connection conn = connector.connect(PropertyManager.getProperties(TimerDbPropertyKey.TIMER_DB_NAME.getKey()));
				PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setLong(1, id);
			stmt.executeUpdate();
		} catch (SQLException | ClassNotFoundException e) {
			e.printStackTrace();
		}
	}
}