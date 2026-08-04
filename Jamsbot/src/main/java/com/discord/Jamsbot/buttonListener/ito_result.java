package com.discord.Jamsbot.buttonListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import org.jetbrains.annotations.NotNull;

import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import util.ItoSessionManager;

public class ito_result extends buttonListenerAbstract {

	public ito_result(@NotNull ButtonInteractionEvent event) {
		super(event);
	}

	@Override
	public void execute() {
		String messageId = event.getMessageId();

		if (!ItoSessionManager.hasSession(messageId)) {
			event.reply("このゲームはすでに終了しているか、無効なセッションです。").setEphemeral(true).queue();
			return;
		}

		Map<String, List<Integer>> allUserCards = ItoSessionManager.getAllUserCards(messageId);
		
		if (allUserCards == null || allUserCards.isEmpty()) {
			event.reply("まだ誰もカードを引いていません！").setEphemeral(true).queue();
			return;
		}

		// Flatten to a list of pairs (cardNumber, userId)
		List<CardEntry> entries = new ArrayList<>();
		for (Map.Entry<String, List<Integer>> entry : allUserCards.entrySet()) {
			String userId = entry.getKey();
			for (Integer card : entry.getValue()) {
				entries.add(new CardEntry(card, userId));
			}
		}

		// Sort by card number ascending
		Collections.sort(entries, Comparator.comparingInt(a -> a.cardNumber));

		StringBuilder sb = new StringBuilder();
		sb.append("🎉 **結果発表** 🎉\n\n");
		for (CardEntry entry : entries) {
			sb.append("**").append(entry.cardNumber).append("** : <@").append(entry.userId).append(">\n");
		}

		// Send publicly in the channel (not ephemeral)
		event.reply(sb.toString()).queue();
	}

	private static class CardEntry {
		int cardNumber;
		String userId;

		CardEntry(int cardNumber, String userId) {
			this.cardNumber = cardNumber;
			this.userId = userId;
		}
	}
}
