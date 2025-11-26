package com.discord.Jamsbot.buttonListener;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import constants.BlackDesertBossTimerDbPropertyKey;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import util.JDBCConnector;
import util.PropertyManager;

public class worldbosswed extends buttonListenerAbstract {

	public worldbosswed(ButtonInteractionEvent event) {
		super(event);
	}

	@Override
	public void execute() {
		System.out.println("ボス時間割曜日呼び出し");
		//SQL定義
		String sql = " select timetable.id,boss_time.day_of_week_char,boss_time.time,boss.name\n"
				+ " from timetable\n"
				+ " inner join boss on timetable.boss_id = boss.id\n"
				+ " inner join boss_time on timetable.boss_time_id = boss_time.id\n"
				+ " where boss.is_deleted = false \n"
				+ " and boss_time.day_of_week_char = ? \n"
				+ " order by boss_time.time;";
		//接続
		JDBCConnector connector = new JDBCConnector();
		//結果用List
		List<String> rsNameList = new ArrayList<>();
		List<String> rsTimeList = new ArrayList<>();
		try (Connection conn = connector.connect(PropertyManager.getProperties(BlackDesertBossTimerDbPropertyKey.BLACKDESERT_DB_NAME.getKey()));
				PreparedStatement stmt = conn.prepareStatement(sql)) {
			stmt.setString(1, "水");
			ResultSet rs = stmt.executeQuery();

			while (rs.next()) {
				System.out.println(rs.getString("boss.name"));
				rsNameList.add(rs.getString("boss.name"));
				rsTimeList.add(rs.getString("boss_time.time"));
			}

			//リストが空のときはボスがない
			if (rsNameList.isEmpty()) {
				System.out.println("ボスがみつかりません。");
				event.reply("ボスがみつかりませんでした。").setEphemeral(true).queue();
				;
				return;
			}

		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		}

		// Embed作成
		EmbedBuilder eb = new EmbedBuilder();
		eb.setAuthor("Jam'sBot", "https://x.com/OFFICIAL_BDJP",
				"https://pbs.twimg.com/profile_images/1469250199189032962/9xwX5PES_400x400.jpg");
		eb.setTitle("水曜日のボススケジュールです。");
		eb.setColor(0x120033);
		eb.setThumbnail("https://stickershop.line-scdn.net/stickershop/v1/product/22602169/LINEStorePC/main.png");
		eb.setFooter("Made by Jam");

		int i = 0; // 現在のリストのインデックス

		while (i < rsTimeList.size()) {
			// 現在の塊の基準値
			int currentBlockValue = i;
			String currentS = rsTimeList.get(i);

			String s = "";//フィールド用文字列
			// currentBlockValue と同じ値が続く限りカウント
			int j = i; // 内部ループ用のインデックス
			while (j < rsTimeList.size() && rsTimeList.get(j).equals(currentS)) {
				s += rsNameList.get(j) + "      ";
				j++;
			}

			//フィールドを追加
			eb.addField(rsTimeList.get(currentBlockValue), s, false);
			// 次の塊の開始位置にインデックスを進める
			i = j;
		}

		MessageEmbed extraEmbed = eb.build();

		// ボタンのインタラクションに対して新しいメッセージとしてリプライします
		// これにより、ボタンを押した結果として新しいメッセージが送信されます。
		event.replyEmbeds(extraEmbed)
				.setEphemeral(true) // trueにするとボタンを押した本人にだけ見えるメッセージになります
				.queue();
	}

}
