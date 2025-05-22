package task;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import constants.BlackDesertBossTimerDbPropertyKey;
import constants.BotConnectionPropertyKey;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import util.JDBCConnector;
import util.PropertyManager;

public class BlackDesertBossTimer implements Runnable {
	private Guild guild = null;
	private String CHANNEL_ID = "";

	public BlackDesertBossTimer(Guild guild, String channelId) {
		this.guild = guild;
		this.CHANNEL_ID = channelId;
	}

	@Override
	public void run() {
		//現在日付取得
		//10分前にジョブが起動するため+10分する
		LocalDateTime nowDatePlasTenMinutes = LocalDateTime.now().plusMinutes(10);
		//LIstの場合-1なので-1する。
		int dayOfWeek = nowDatePlasTenMinutes.getDayOfWeek().getValue() - 1;
		int hour = nowDatePlasTenMinutes.getHour();
		//曜日文字列に変換用リスト
		List<String> dayOfWeekList = new ArrayList<>(Arrays.asList("月", "火", "水", "木", "金", "土", "日"));
		System.out.println("dayOfWeek:" + dayOfWeekList.get(dayOfWeek) + "\nhour:" + hour);

		// Embed作成
		EmbedBuilder eb = new EmbedBuilder();
		eb.setAuthor("Jam'sBot", "https://x.com/OFFICIAL_BDJP",
				"https://pbs.twimg.com/profile_images/1469250199189032962/9xwX5PES_400x400.jpg");
		eb.setTitle("ボスの時間10分前です。");
		eb.setColor(0x120033);
		eb.setThumbnail("https://stickershop.line-scdn.net/stickershop/v1/product/22602169/LINEStorePC/main.png");
		eb.setFooter("Made by Jam");

		//db用変数初期化
		Connection con = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;

		// SQL実行
		try {
			JDBCConnector connector = new JDBCConnector();
			con = connector.connect(PropertyManager.getProperties(BlackDesertBossTimerDbPropertyKey.BLACKDESERT_DB_NAME.getKey()));
			pstmt = con.prepareStatement(" select timetable.id,boss_time.day_of_week_char,boss_time.time,boss.name\n"
					+ " from timetable\n"
					+ " inner join boss on timetable.boss_id = boss.id\n"
					+ " inner join boss_time on timetable.boss_time_id = boss_time.id\n"
					+ " where boss_time.day_of_week_char = ? and boss_time.time = ?\n"
					+ " order by timetable.id;");
			pstmt.setString(1, dayOfWeekList.get(dayOfWeek));
			pstmt.setString(2, String.format("%02d", hour));

			//SQL結果をResultSetに格納
			rs = pstmt.executeQuery();

			//結果格納用List
			List<String> rsList = new ArrayList<>();
			//結果をEmbedに追加してListにも追加しとく
			while (rs.next()) {
				System.out.println(rs.getString("boss.name"));
				eb.addField(rs.getString("boss.name"), "", true);
				rsList.add(rs.getString("boss.name"));
			}

			System.out.println(rsList.size());
			//リストが空のときはボスがない
			if (rsList.isEmpty()) {
				System.out.println("ボスがない時間です。(" + nowDatePlasTenMinutes.toString() + ")");
				return;
			}
			//メインチャンネルに送信
			guild.getTextChannelById(CHANNEL_ID).sendMessageEmbeds(eb.build()).queue();
		} catch (Exception e) {
			guild.getTextChannelById(PropertyManager.getProperties(BotConnectionPropertyKey.ADMIN_CHANNEL_ID.getKey())).sendMessage("【定期実行】黒い砂漠:SQL実行できませんでした。")
					.queue();
			e.printStackTrace();
		}
		//close処理 returnよりも前に実行されるよ
		finally {
			if (rs != null) {
				try {
					rs.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (pstmt != null) {
				try {
					pstmt.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (con != null) {
				try {
					con.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
	}
}