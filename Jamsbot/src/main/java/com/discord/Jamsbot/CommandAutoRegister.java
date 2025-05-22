package com.discord.Jamsbot;

import java.util.List;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;

public class CommandAutoRegister {

	public static void registerCommands(Guild guild) {
		List<SlashCommandData> commandList = List.of(
				// ping
				Commands.slash("ping", "応答速度を確認"),
				// luckymonsters
				Commands.slash("luckymonsters", "ラキモンコマンド"),
				// timer
				Commands.slash("timer", "タイマーコマンド")
						.addSubcommands(
								new SubcommandData("timer", "指定時間後に通知(サーバー再起動を超えるタイマーの場合DMに送られます。)")
										.addOptions(
												new OptionData(OptionType.INTEGER, "hours", "時間", false).setMinValue(0).setMaxValue(23),
												new OptionData(OptionType.INTEGER, "minutes", "分", false).setMinValue(0).setMaxValue(59),
												new OptionData(OptionType.INTEGER, "seconds", "秒", false).setMinValue(0).setMaxValue(59)

										),
								new SubcommandData("alarm", "指定日時に通知(サーバー再起動を超えるアラームの場合DMに送られます。)")
										.addOptions(
												new OptionData(OptionType.STRING, "date", "日付（yyyy-MM-dd）", true),
												new OptionData(OptionType.INTEGER, "hours", "時間 (0-23)", true).setMinValue(0).setMaxValue(23),
												new OptionData(OptionType.INTEGER, "minutes", "分 (0-59)", true).setMinValue(0).setMaxValue(59),
												new OptionData(OptionType.INTEGER, "seconds", "秒 (0-59)", false).setMinValue(0).setMaxValue(59)

										)),

				// admin
				Commands.slash("admin", "管理者向けコマンド")
						.addSubcommands(
								new SubcommandData("ban", "ユーザーをBAN")
										.addOption(OptionType.USER, "target", "対象のユーザー", true),
								new SubcommandData("kick", "ユーザーをキック")
										.addOption(OptionType.USER, "target", "対象のユーザー", true)));

		// グローバルコマンドとして登録（反映に最大1時間かかる）
		//        jda.updateCommands().addCommands(commandList).queue();

		// ギルド限定登録（即時反映）
		if (guild != null) {
			guild.updateCommands().addCommands(commandList).queue();
		}
	}
}
