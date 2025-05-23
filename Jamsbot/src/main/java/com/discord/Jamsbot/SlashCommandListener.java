package com.discord.Jamsbot;

import java.lang.reflect.Constructor;

import org.jetbrains.annotations.NotNull;

import com.discord.Jamsbot.commandListener.commandListenerAbstract;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import util.PropertyManager;

public class SlashCommandListener extends ListenerAdapter {
	@Override
	public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
		final String PACKAGE_NAME = "com.discord.Jamsbot.commandListener";
		try {
			String fullClassName = PACKAGE_NAME + "." + event.getName();
			Class<?> clazz = Class.forName(fullClassName);

			if (!commandListenerAbstract.class.isAssignableFrom(clazz)) {
				throw new IllegalArgumentException("Class " + fullClassName + " does not extend commandListenerAbstract.");
			}

			// コンストラクタを取得
			Constructor<?> constructor = clazz.getConstructor(SlashCommandInteractionEvent.class);

			// インスタンスを生成し、commandListenerAbstract型にキャスト
			commandListenerAbstract commandInstance = (commandListenerAbstract) constructor.newInstance(event);

			// 生成されたインスタンスの execute() メソッドを呼び出す
			commandInstance.execute();

		} catch (ClassNotFoundException e) {
			// コマンドクラスが見つからない場合
			event.reply("コマンドが見つかりませんでした。: " + event.getName()).setEphemeral(true).queue();
			System.err.println("Command class not found: " + PACKAGE_NAME + "." + event.getName() + " - " + e.getMessage());
		} catch (NoSuchMethodException e) {
			// 適切なコンストラクタが見つからない場合
			event.reply("コマンドクラスの初期化に失敗しました。").setEphemeral(true).queue();
			System.err.println("Constructor not found for " + PACKAGE_NAME + "." + event.getName() + " - " + e.getMessage());
		} catch (InstantiationException | IllegalAccessException | java.lang.reflect.InvocationTargetException e) {
			// インスタンス化またはメソッド呼び出しに失敗した場合
			event.reply("コマンドの実行中にエラーが発生しました。").setEphemeral(true).queue();
			System.err.println("Error during command execution for " + PACKAGE_NAME + "." + event.getName() + " - " + e.getMessage());
			e.printStackTrace(); // 詳細なスタックトレースを出力
		} catch (Exception e) {
			// その他の予期せぬエラー
			// PropertyManager.getProperties() を使っている部分はこのcatchで処理されます
			event.reply(PropertyManager.getProperties("コマンドオブジェクトが生成できませんでした。")).setEphemeral(true).queue();
			System.err.println("Unhandled exception in SlashCommandListener: " + e.getMessage());
			e.printStackTrace();
		}
	}
}