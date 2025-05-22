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
					.addEventListeners(new SlashCommandListener())
					.setActivity(Activity.customStatus("コマンドは[/]を入力")) // "～をプレイ中" の ～の部分
					.build();

			// ログインが完了するまで待つ
			jda.awaitReady();

			// ログイン完了!
			System.out.println("Logged in.");

			// タイマー再起動
			TimerRepository repo = new TimerRepository();
			TimerScheduler scheduler = new TimerScheduler(jda);
			for (TimerData timer : repo.findAllUpcoming()) {
				System.out.println("タイマー再起動ID:" + timer.id());
				scheduler.reSchedule(timer, repo);
			}
			// タイマー再起動後にいらないタイマーのデリート処理を入れたい

			// 参加しているサーバーを ID から取得
			Guild guild = jda.getGuildById(PropertyManager.getProperties(BotConnectionPropertyKey.GUILD_ID.getKey()));
			guild.getTextChannelById(PropertyManager.getProperties(BotConnectionPropertyKey.ADMIN_CHANNEL_ID.getKey())).sendMessage("Logged in")
					.queue();

			// 登録するコマンドを作成
			// コマンドを指定したサーバーに登録
			CommandAutoRegister.registerCommands(guild);

			// 定期実行
			DiscordBot app = new DiscordBot();
			app.scheduler(guild);

		} catch (InvalidTokenException | InterruptedException e) {
			e.printStackTrace();
		}
	}

	private void scheduler(Guild guild) {
		Scheduler scheduler = new Scheduler();
		// schedule.
		scheduler.schedule("0 0 10 * *|0 0 20 * *|0 0 30 * *|0 0 L 2 *",
				new task.MonsterStrikeDays(guild, PropertyManager.getProperties(BotConnectionPropertyKey.MONSTERSTRIKE_CHANNEL_ID.getKey())));
		scheduler.schedule("20 1 * * *|50 10 * * *|50 13 * * *|50 15 * * *|50 18 * * *|20 22 * * *|50 22 * * *",
				new task.BlackDesertBossTimer(guild, PropertyManager.getProperties(BotConnectionPropertyKey.BLACKDESERT_CHANNEL_ID.getKey())));
		// start cron4j scheduler.
		scheduler.start();
	}
}
