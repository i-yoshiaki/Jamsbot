package com.discord.Jamsbot;

import java.util.List;

import constants.BotConnectionPropertyKey;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.interactions.commands.build.OptionData;
import net.dv8tion.jda.api.interactions.commands.build.SlashCommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandData;
import net.dv8tion.jda.api.interactions.commands.build.SubcommandGroupData;
import util.PropertyManager;

public class CommandAutoRegister {

	public static void registerCommands(JDA jda) {
		List<SlashCommandData> globalCommandList = List.of(
				// ping
				Commands.slash("ping", "応答速度を確認")
						.setGuildOnly(true),
				// luckymonsters
				Commands.slash("luckymonsters", "ラキモンコマンド")
						.setGuildOnly(false),
				// timer
				Commands.slash("timer", "タイマーコマンド")
						.addSubcommands(
								new SubcommandData("timer", "指定時間後に通知(DMに通知します。)")
										.addOptions(
												new OptionData(OptionType.INTEGER, "hours", "時間", false).setMinValue(0).setMaxValue(23),
												new OptionData(OptionType.INTEGER, "minutes", "分", false).setMinValue(0).setMaxValue(59),
												new OptionData(OptionType.INTEGER, "seconds", "秒", false).setMinValue(0).setMaxValue(59)

										),
								new SubcommandData("alarm", "指定日時に通知(DMに通知します。)")
										.addOptions(
												new OptionData(OptionType.STRING, "date", "日付（yyyy-MM-dd）", true),
												new OptionData(OptionType.INTEGER, "hours", "時間 (0-23)", true).setMinValue(0).setMaxValue(23),
												new OptionData(OptionType.INTEGER, "minutes", "分 (0-59)", true).setMinValue(0).setMaxValue(59),
												new OptionData(OptionType.INTEGER, "seconds", "秒 (0-59)", false).setMinValue(0).setMaxValue(59)

										))
						.setGuildOnly(false),
				//worldbosstimetable
				Commands.slash("worldbosstimetable", "ワールドボス時間割")
						.setGuildOnly(false),
				// gemini
				//				Commands.slash("gemini", "geminiに質問できます。").addOption(OptionType.STRING, "text", "質問内容", true),

				// admin
				Commands.slash("admin", "管理者向けコマンド")
						.addSubcommands(
								new SubcommandData("ban", "ユーザーをBAN")
										.addOption(OptionType.USER, "target", "対象のユーザー", true),
								new SubcommandData("kick", "ユーザーをキック")
										.addOption(OptionType.USER, "target", "対象のユーザー", true))
						.setGuildOnly(true),

				//server
				Commands.slash("server", "サーバーコマンド")
						.addSubcommandGroups(
								new SubcommandGroupData("start", "サーバーを起動します。")
										.addSubcommands(
												new SubcommandData("minecraft", "Minecraftサーバー")),
								new SubcommandGroupData("stop", "サーバーを停止します。")
										.addSubcommands(
												new SubcommandData("minecraft", "Minecraftサーバー")))
						.setGuildOnly(true),

				//blackdeserttask
				Commands.slash("blackdeserttask", "黒い砂漠の定期通知on/off")
						.addSubcommands(
								new SubcommandData("on", "定期通知をonにする")
									.addOption(OptionType.BOOLEAN, "is_event","イベントボスのみ",false),
								new SubcommandData("off", "定期通知をoffにする"))
						.setGuildOnly(true));

		List<SlashCommandData> privateCommandList = List.of(

		);

		//グローバルコマンド初期化
		//		jda.updateCommands().queue(
		//				success -> System.out.println("グローバルコマンドの削除が完了しました。"),
		//				error -> System.err.println("グローバルコマンドの削除中にエラーが発生しました: " + error.getMessage()));
		//
		//ギルドコマンド初期化
		Guild guild = jda.getGuildById(PropertyManager.getProperties(BotConnectionPropertyKey.GUILD_ID.getKey()));
		//		guild.updateCommands().queue(
		//				success -> System.out.println(guild.getName() + " のギルドコマンドの削除が完了しました。"),
		//				error -> System.err.println(guild.getName() + " のギルドコマンドの削除中にエラーが発生しました: " + error.getMessage()));

		//		 グローバルコマンドとして登録（反映に最大1時間かかる）
		jda.updateCommands().addCommands(globalCommandList).queue();

		// ギルド限定登録（即時反映）
		if (guild != null) {
			guild.updateCommands().addCommands(privateCommandList).queue();
		}
	}
}
