package com.discord.Jamsbot.commandListener.timerModule;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import net.dv8tion.jda.api.JDA;

public class TimerScheduler {

	private final ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
	private final JDA jda;

	public TimerScheduler(JDA jda) {
		this.jda = jda;
	}

	public void reSchedule(TimerData timer, TimerRepository repo) {
		long delay = Duration.between(LocalDateTime.now(), timer.triggerTime()).toMillis();
		System.out.println("reSchedule:起動");
		executor.schedule(() -> {
			jda.retrieveUserById(timer.userId()).queue(user -> {
				user.openPrivateChannel()
						.flatMap(channel -> channel.sendMessage("<@" + timer.userId() + "> ⌛️タイマーの時間になりました！"))
						.queue(
								success -> System.out.println("DM送信成功"),
								failure -> System.err.println("DM送信失敗: " + failure.getMessage()));

				repo.delete(timer.id());
			}, failure -> {
				System.err.println("ユーザー取得失敗: " + failure.getMessage());
			});
		}, delay, TimeUnit.MILLISECONDS);
	}
}