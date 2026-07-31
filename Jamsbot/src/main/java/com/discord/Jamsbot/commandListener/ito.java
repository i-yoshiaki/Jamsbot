package com.discord.Jamsbot.commandListener;

import org.jetbrains.annotations.NotNull;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import util.GeminiApiManager;
import util.ItoSessionManager;

public class ito extends commandListenerAbstract {

	public ito(@NotNull SlashCommandInteractionEvent event) {
		super(event);
	}

	@Override
	public void execute() {
		OptionMapping countOption = event.getOption("count");
		int count = countOption != null ? countOption.getAsInt() : 1;

		// API通信に時間がかかる可能性があるため、deferReplyを使用
		event.deferReply().queue(hook -> {
			// Gemini APIからお題を取得
			String theme = GeminiApiManager.generateTheme();
			
			hook.editOriginal("ボードゲーム「ito」を開始しました！\n\n"
					+ "**【今回のお題候補】**\n"
					+ theme + "\n\n"
					+ "1人あたりが引くカードの枚数: **" + count + "** 枚\n\n"
					+ "参加するプレイヤーは下の「カードを引く」ボタンを押してください。")
					.setActionRow(
							Button.primary("ito_draw", "カードを引く"),
							Button.danger("ito_end", "ゲーム終了")
					)
					.queue(message -> {
						ItoSessionManager.createSession(message.getId(), count);
					});
		});
	}
}
