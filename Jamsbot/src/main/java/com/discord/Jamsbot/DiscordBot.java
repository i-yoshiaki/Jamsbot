package com.discord.Jamsbot;

import com.discord.Jamsbot.commandListener.timerModule.TimerData;
import com.discord.Jamsbot.commandListener.timerModule.TimerRepository;
import com.discord.Jamsbot.commandListener.timerModule.TimerScheduler;

import constants.BotConnectionPropertyKey;
import it.sauronsoftware.cron4j.Scheduler;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.exceptions.InvalidTokenException;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.requests.GatewayIntent;
import util.PropertyManager;

public class DiscordBot extends ListenerAdapter {

	public static void main(String[] args) {
		try {
			// Login 処理
			JDA jda = JDABuilder
					.createLight(PropertyManager.getProperties(BotConnectionPropertyKey.TOKEN.getKey()), GatewayIntent.GUILD_MESSAGES,
							GatewayIntent.DIRECT_MESSAGES)
					.addEventListeners(new SlashCommandListener(),new ButtonListener())
					.setActivity(Activity.customStatus("コマンドは[/]を入力")) // "～をプレイ中" の ～の部分
					.build();

			// ログインが完了するまで待つ
			jda.awaitReady();

			// ログイン完了!
			System.out.println("Logged in.");

			// 参加しているサーバーを ID から取得
			Guild guild = jda.getGuildById(PropertyManager.getProperties(BotConnectionPropertyKey.GUILD_ID.getKey()));
			guild.getTextChannelById(PropertyManager.getProperties(BotConnectionPropertyKey.ADMIN_CHANNEL_ID.getKey())).sendMessage("Logged in")
					.queue();

			// 登録するコマンドを作成
			// コマンドを指定したサーバーに登録
			CommandAutoRegister.registerCommands(jda);

			// 定期実行
			DiscordBot app = new DiscordBot();
			app.firstExecute(guild, jda);

		} catch (InvalidTokenException | InterruptedException e) {
			e.printStackTrace();
		}
	}

	private void firstExecute(Guild guild, JDA jda) {
		Scheduler scheduler = new Scheduler();
		// 定期実行スケジュール
		scheduler.schedule("0 0 10 * *|0 0 20 * *|0 0 30 * *|0 0 L 2 *",
				new task.MonsterStrikeDays(guild, PropertyManager.getProperties(BotConnectionPropertyKey.MONSTERSTRIKE_CHANNEL_ID.getKey())));
		scheduler.schedule("20 1 * * *|50 10 * * *|50 13 * * *|50 15 * * *|50 18 * * *|20 22 * * *|50 22 * * *",
				new task.BlackDesertBossTimer(guild, PropertyManager.getProperties(BotConnectionPropertyKey.BLACKDESERT_CHANNEL_ID.getKey())));
		// start cron4j scheduler.
		scheduler.start();

		// タイマー再起動
		TimerRepository repo = new TimerRepository();
		TimerScheduler timerScheduler = new TimerScheduler(jda);
		for (TimerData timer : repo.findAllUpcoming()) {
			System.out.println("タイマー再起動ID:" + timer.id());
			timerScheduler.reSchedule(timer, repo);
		}

		// 不要なタイマー削除
		int cnt = 0;
		for (long id : repo.findExpiredItems()) {
			repo.delete(id);
			cnt++;
		}
		System.out.println("削除対象タイマー:" + cnt + "件削除済み");
		if (cnt != 0) {
			guild.getTextChannelById(PropertyManager.getProperties(BotConnectionPropertyKey.MAIN_CHANNEL_ID.getKey()))
					.sendMessage("再起動等と被ってしまい実行されず削除されたタイマーが" + cnt + "件あります。").queue();
		}
	}
}
