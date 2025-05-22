package com.discord.Jamsbot.commandListener;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public class ping extends commandListenerAbstract {

	public ping(SlashCommandInteractionEvent e) {
		super(e);
	}

	@Override
	protected void commandInteracton(SlashCommandInteractionEvent event) {
		long timeBefore = System.currentTimeMillis();
		event.reply("🏓 Pong!").queue(response -> {
			long latency = System.currentTimeMillis() - timeBefore;
			response.editOriginalFormat("🏓 Pong! 応答時間: %dms", latency).queue();
		});
	}

}
