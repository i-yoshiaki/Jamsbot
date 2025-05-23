package com.discord.Jamsbot.commandListener;

import org.jetbrains.annotations.NotNull;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public abstract class commandListenerAbstract {
	protected final SlashCommandInteractionEvent event;

	public commandListenerAbstract(@NotNull SlashCommandInteractionEvent event) {
		this.event = event;
	}

	public abstract void execute();
}