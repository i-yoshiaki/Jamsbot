package com.discord.Jamsbot.commandListener;

import org.jetbrains.annotations.NotNull;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public abstract class commandListenerAbstract {
	public commandListenerAbstract(SlashCommandInteractionEvent e) {
		commandInteracton(e);
	}
	
	protected  abstract void commandInteracton(@NotNull SlashCommandInteractionEvent event);
}
