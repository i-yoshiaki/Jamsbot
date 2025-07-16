package com.discord.Jamsbot.commandListener;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import constants.BlackDesertBossTimerDbPropertyKey;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import util.JDBCConnector;
import util.PropertyManager;

public class blackdeserttask extends commandListenerAbstract {
	public blackdeserttask(SlashCommandInteractionEvent e) {
		super(e);
	}

	@Override
	public void execute() {
		String subName = event.getSubcommandName();
		switch (subName) {
		case "on" -> taskOn(event);
		case "off" -> taskOff(event);
		}
	}

	private void taskOn(SlashCommandInteractionEvent event) {
		event.reply("黒い砂漠の定期通知をオンにします").setEphemeral(true).queue();
		String sql = "INSERT INTO discord_user (user_id, flag) VALUES (?, ?) ON DUPLICATE KEY UPDATE flag = VALUES(flag);";
		JDBCConnector connector = new JDBCConnector();
		try (Connection conn = connector.connect(PropertyManager.getProperties(BlackDesertBossTimerDbPropertyKey.BLACKDESERT_DB_NAME.getKey()));
				PreparedStatement stmt = conn.prepareStatement(sql);) {
			stmt.setString(1, event.getUser().getId());
			stmt.setBoolean(2, true);
			stmt.executeUpdate();

		} catch (SQLException e) {
			System.err.println("データベース操作中にエラーが発生しました: " + e.getMessage());
			e.printStackTrace();
		} catch (Exception e) {
			// 例えば、JDBCConnector.connect() などで発生する可能性のある予期せぬ例外
			System.err.println("プログラム実行中に予期せぬエラーが発生しました: " + e.getMessage());
			e.printStackTrace();
		}
	}

	private void taskOff(SlashCommandInteractionEvent event) {
		event.reply("黒い砂漠の定期通知をオフにします").setEphemeral(true).queue();
		String sql = "INSERT INTO discord_user (user_id, flag) VALUES (?, ?) ON DUPLICATE KEY UPDATE flag = VALUES(flag);";
		JDBCConnector connector = new JDBCConnector();
		try (Connection conn = connector.connect(PropertyManager.getProperties(BlackDesertBossTimerDbPropertyKey.BLACKDESERT_DB_NAME.getKey()));
				PreparedStatement stmt = conn.prepareStatement(sql);) {
			stmt.setString(1, event.getUser().getId());
			stmt.setBoolean(2, false);
			stmt.executeUpdate();

		} catch (SQLException e) {
			System.err.println("データベース操作中にエラーが発生しました: " + e.getMessage());
			e.printStackTrace();
		} catch (Exception e) {
			// 例えば、JDBCConnector.connect() などで発生する可能性のある予期せぬ例外
			System.err.println("プログラム実行中に予期せぬエラーが発生しました: " + e.getMessage());
			e.printStackTrace();
		}
	}

}
