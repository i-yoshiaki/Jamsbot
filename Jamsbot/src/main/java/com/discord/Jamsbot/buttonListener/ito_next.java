package com.discord.Jamsbot.buttonListener;

import java.util.Collections;

import org.jetbrains.annotations.NotNull;

import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import util.GeminiApiManager;
import util.ItoSessionManager;

public class ito_next extends buttonListenerAbstract {

	public ito_next(@NotNull ButtonInteractionEvent event) {
		super(event);
	}

	@Override
	public void execute() {
		String messageId = event.getMessageId();

		if (!ItoSessionManager.hasSession(messageId)) {
			event.reply("このゲームはすでに終了しているか、無効なセッションです。").setEphemeral(true).queue();
			return;
		}

		// Retrieve current card count
		int count = ItoSessionManager.getCardCount(messageId);

		// Disable buttons on the old message to prevent further interaction
		event.getMessage().editMessageComponents(Collections.emptyList()).queue();

		// Start a new game
		event.deferReply().queue(hook -> {
			String theme = GeminiApiManager.generateTheme();
			
			hook.editOriginal("ボードゲーム「ito」を開始しました！\n\n"
					+ "**【今回のお題候補】**\n"
					+ theme + "\n\n"
					+ "1人あたりが引くカードの枚数: **" + count + "** 枚\n\n"
					+ "参加するプレイヤーは下の「カードを引く」ボタンを押してください。")
					.setActionRow(
							Button.primary("ito_draw", "カードを引く"),
							Button.success("ito_result", "結果発表"),
							Button.secondary("ito_next", "ネクストゲーム"),
							Button.danger("ito_end", "ゲーム終了")
					)
					.queue(message -> {
						// Remove old session and create a new one
						ItoSessionManager.removeSession(messageId);
						ItoSessionManager.createSession(message.getId(), count);
					});
		});
	}
}
