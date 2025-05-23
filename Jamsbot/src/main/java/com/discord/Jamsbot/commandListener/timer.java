package com.discord.Jamsbot.commandListener;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.Timer;
import java.util.TimerTask;

import com.discord.Jamsbot.commandListener.timerModule.TimerRepository;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;

public class timer extends commandListenerAbstract {
	public timer(SlashCommandInteractionEvent e) {
		super(e);
	}

	@Override
	public void execute() {
		String sub = event.getSubcommandName();
		switch (sub) {
		case "timer" -> handleTimer(event);
		case "alarm" -> handleAlarm(event);
		}
	}

	private void handleTimer(SlashCommandInteractionEvent event) {
		int hours = getOptionInt(event, "hours", 0);
		int minutes = getOptionInt(event, "minutes", 0);
		int seconds = getOptionInt(event, "seconds", 0);

		long delayMillis = Duration.ofHours(hours)
				.plusMinutes(minutes)
				.plusSeconds(seconds)
				.toMillis();

		//DBにセーブしとく
		LocalDateTime triggerAt = LocalDateTime.now().plus(Duration.ofMillis(delayMillis));
		TimerRepository repo = new TimerRepository();
		long id = repo.save(event.getUser().getId(), event.getChannel().getId(), triggerAt);

		event.reply("⏱ タイマーをセットしました！").setEphemeral(true).queue();

		Timer timer = new Timer();
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				event.getHook().sendMessage("⏰ タイマーが終了しました！").setEphemeral(true).queue();
				repo.delete(id);
			}
		}, delayMillis);
	}

	private void handleAlarm(SlashCommandInteractionEvent event) {
		String dateStr = event.getOption("date").getAsString();
		int hour = getOptionInt(event, "hours", 0);
		int minute = getOptionInt(event, "minutes", 0);
		int second = getOptionInt(event, "seconds", 0);

		try {
			LocalDate date = LocalDate.parse(dateStr);
			LocalDateTime dateTime = date.atTime(hour, minute, second);
			long delayMillis = Duration.between(LocalDateTime.now(), dateTime).toMillis();

			if (delayMillis <= 0) {
				event.reply("⚠️ 過去の時間は指定できません！").setEphemeral(true).queue();
				return;
			}

			TimerRepository repo = new TimerRepository();
			long id = repo.save(event.getUser().getId(), event.getChannel().getId(), dateTime);
			event.reply("⏰ アラームをセットしました！").setEphemeral(true).queue();

			Timer timer = new Timer();
			timer.schedule(new TimerTask() {
				@Override
				public void run() {
					event.getHook().sendMessage("🔔 アラームの時間になりました！").setEphemeral(true).queue();
					repo.delete(id);
				}
			}, delayMillis);

		} catch (DateTimeParseException e) {
			event.reply("📅 日付の形式が正しくありません（例: 2001-03-14）").setEphemeral(true).queue();
		}
	}

	private int getOptionInt(SlashCommandInteractionEvent event, String name, int defaultValue) {
		OptionMapping option = event.getOption(name);

		if (option != null) {
			defaultValue = option.getAsInt();
		}

		return defaultValue;
	}

}
