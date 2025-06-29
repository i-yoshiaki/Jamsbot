package com.discord.Jamsbot.commandListener;

import constants.BotConnectionPropertyKey;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import util.PropertyManager;

public class server extends commandListenerAbstract {
	public server(SlashCommandInteractionEvent e) {
		super(e);
	}

	@Override
	public void execute() {
		String subGroup = event.getSubcommandGroup();
		switch (subGroup) {
		case "start" -> serverStart(event);
		case "stop" -> serverStop(event);
		}
	}

	private void serverStart(SlashCommandInteractionEvent event) {
		event.reply("サーバー起動用バッチ起動").setEphemeral(false).queue();
		String sub = event.getSubcommandName();

		String batchFilePath = PropertyManager.getProperties(BotConnectionPropertyKey.SERVER_BATCHFILE_PATH.getKey()); // サーバー起動バッチファイルのパスを指定
		String startBatchFile = batchFilePath + "ServerStarter.bat";
		if (sub.equals("minecraft")) {
			try {
				// ProcessBuilderを作成します。
				// Windowsの場合: "cmd.exe", "/c", "バッチファイルパス"
				// Linux/macOSの場合: "sh", "バッチファイルパス" (またはbashなど)
				ProcessBuilder pb;
				if (System.getProperty("os.name").toLowerCase().contains("win")) {
					// Windowsの場合
					pb = new ProcessBuilder("cmd.exe", "/c", startBatchFile);
				} else {
					// Linux/macOSの場合 (例: shスクリプト)
					pb = new ProcessBuilder("sh", startBatchFile);
				}

				// 標準出力と標準エラー出力を現在のJavaプロセスにマージします。（オプション）
				// これにより、バッチファイルの出力がJavaアプリケーションのコンソールに表示されます。
				pb.inheritIO();

				// プロセスを開始します。
				pb.start();

			} catch (Exception e) {
				System.err.println("バッチファイルの起動中にエラーが発生しました: " + e.getMessage());
				e.printStackTrace();
			}
		}
	}

	private void serverStop(SlashCommandInteractionEvent event) {
		event.reply("サーバー停止用バッチ起動").setEphemeral(false).queue();

		String sub = event.getSubcommandName();

		String batchFilePath = PropertyManager.getProperties(BotConnectionPropertyKey.SERVER_BATCHFILE_PATH.getKey()); // サーバー起動バッチファイルのパスを指定
		String startBatchFile = batchFilePath + "ServerStoper.bat";
		if (sub.equals("minecraft")) {
			try {
				// ProcessBuilderを作成します。
				// Windowsの場合: "cmd.exe", "/c", "バッチファイルパス"
				// Linux/macOSの場合: "sh", "バッチファイルパス" (またはbashなど)
				ProcessBuilder pb;
				if (System.getProperty("os.name").toLowerCase().contains("win")) {
					// Windowsの場合
					pb = new ProcessBuilder("cmd.exe", "/c", startBatchFile);
				} else {
					// Linux/macOSの場合 (例: shスクリプト)
					pb = new ProcessBuilder("sh", startBatchFile);
				}

				// 標準出力と標準エラー出力を現在のJavaプロセスにマージします。（オプション）
				// これにより、バッチファイルの出力がJavaアプリケーションのコンソールに表示されます。
				pb.inheritIO();

				// プロセスを開始します。
				pb.start();

			} catch (Exception e) {
				System.err.println("バッチファイルの起動中にエラーが発生しました: " + e.getMessage());
				e.printStackTrace();
			}
		}

	}

}
