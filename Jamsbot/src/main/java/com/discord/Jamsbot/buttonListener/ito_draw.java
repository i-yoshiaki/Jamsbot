package com.discord.Jamsbot.buttonListener;

import java.util.List;

import org.jetbrains.annotations.NotNull;

import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import util.ItoSessionManager;

public class ito_draw extends buttonListenerAbstract {

	public ito_draw(@NotNull ButtonInteractionEvent event) {
		super(event);
	}

	@Override
	public void execute() {
		String messageId = event.getMessageId();
		String userId = event.getUser().getId();

		if (!ItoSessionManager.hasSession(messageId)) {
			event.reply("このゲームはすでに終了しているか、無効なセッションです。").setEphemeral(true).queue();
			return;
		}

		List<Integer> drawnCards = ItoSessionManager.drawCards(messageId, userId);
		
		if (drawnCards == null || drawnCards.isEmpty()) {
			event.reply("カードを引けませんでした（デッキが空かもしれません）。").setEphemeral(true).queue();
			return;
		}

		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < drawnCards.size(); i++) {
			sb.append(drawnCards.get(i));
			if (i < drawnCards.size() - 1) {
				sb.append(", ");
			}
		}

		event.reply("あなたの数字は **" + sb.toString() + "** です！\n（※他の人には見えないように注意してください）")
				.setEphemeral(true)
				.queue();
	}
}
