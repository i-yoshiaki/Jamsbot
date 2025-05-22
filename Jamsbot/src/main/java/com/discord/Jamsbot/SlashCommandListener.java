package com.discord.Jamsbot;

import org.jetbrains.annotations.NotNull;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import util.PropertyManager;

public class SlashCommandListener extends ListenerAdapter {
	@Override
	public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
		final String PACKAGE_NAME = "com.discord.Jamsbot.commandListener";
		try {
			String fullClassName = PACKAGE_NAME + "." + event.getName();
			Class<?> clazz = Class.forName(fullClassName);
			clazz.getConstructor(SlashCommandInteractionEvent.class).newInstance(event);
		}catch (Exception e){
			event.reply(PropertyManager.getProperties("コマンドオブジェクトが生成できませんでした。")).setEphemeral(true).queue();
		}
	}
}