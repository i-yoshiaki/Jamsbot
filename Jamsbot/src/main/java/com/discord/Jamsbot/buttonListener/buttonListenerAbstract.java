package com.discord.Jamsbot.buttonListener;

import org.jetbrains.annotations.NotNull;

import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;

public abstract class buttonListenerAbstract {
	protected final ButtonInteractionEvent event;

	public buttonListenerAbstract(@NotNull ButtonInteractionEvent event) {
		this.event = event;
	}

	public abstract void execute();
}