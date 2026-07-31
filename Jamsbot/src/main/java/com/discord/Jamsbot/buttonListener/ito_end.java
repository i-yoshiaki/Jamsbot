package com.discord.Jamsbot.buttonListener;

import org.jetbrains.annotations.NotNull;

import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import util.ItoSessionManager;

public class ito_end extends buttonListenerAbstract {

	public ito_end(@NotNull ButtonInteractionEvent event) {
		super(event);
	}

	@Override
	public void execute() {
		String messageId = event.getMessageId();

		if (!ItoSessionManager.hasSession(messageId)) {
			event.reply("このゲームはすでに終了しています。").setEphemeral(true).queue();
			return;
		}

		ItoSessionManager.removeSession(messageId);

		event.editMessage("ボードゲーム「ito」\n\nこのゲームは終了しました。")
				.setActionRow(
						Button.primary("ito_draw", "カードを引く").asDisabled(),
						Button.danger("ito_end", "ゲーム終了").asDisabled()
				)
				.queue();
	}
}
