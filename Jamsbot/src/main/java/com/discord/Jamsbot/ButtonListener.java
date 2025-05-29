package com.discord.Jamsbot;

import java.lang.reflect.Constructor;

import org.jetbrains.annotations.NotNull;

import com.discord.Jamsbot.buttonListener.buttonListenerAbstract;

import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import util.PropertyManager;

public class ButtonListener extends ListenerAdapter {
	@Override
	public void onButtonInteraction(@NotNull ButtonInteractionEvent event) {
		final String PACKAGE_NAME = "com.discord.Jamsbot.buttonListener";
		try {
			String fullClassName = PACKAGE_NAME + "." + event.getComponentId();
			Class<?> clazz = Class.forName(fullClassName);

			if (!buttonListenerAbstract.class.isAssignableFrom(clazz)) {
				throw new IllegalArgumentException("Class " + fullClassName + " does not extend buttonListenerAbstract.");
			}

			// コンストラクタを取得
			Constructor<?> constructor = clazz.getConstructor(ButtonInteractionEvent.class);

			// インスタンスを生成し、buttonListenerAbstract型にキャスト
			buttonListenerAbstract buttonInstance = (buttonListenerAbstract) constructor.newInstance(event);

			// 生成されたインスタンスの execute() メソッドを呼び出す
			buttonInstance.execute();

		} catch (ClassNotFoundException e) {
			// コマンドクラスが見つからない場合
			event.reply("ボタン処理が見つかりませんでした。: " + event.getComponentId()).setEphemeral(true).queue();
			System.err.println("Command class not found: " + PACKAGE_NAME + "." + event.getComponentId() + " - " + e.getMessage());
		} catch (NoSuchMethodException e) {
			// 適切なコンストラクタが見つからない場合
			event.reply("ボタンクラスの初期化に失敗しました。").setEphemeral(true).queue();
			System.err.println("Constructor not found for " + PACKAGE_NAME + "." + event.getComponentId() + " - " + e.getMessage());
		} catch (InstantiationException | IllegalAccessException | java.lang.reflect.InvocationTargetException e) {
			// インスタンス化またはメソッド呼び出しに失敗した場合
			event.reply("ボタンの実行中にエラーが発生しました。").setEphemeral(true).queue();
			System.err.println("Error during command execution for " + PACKAGE_NAME + "." + event.getComponentId() + " - " + e.getMessage());
			e.printStackTrace(); // 詳細なスタックトレースを出力
		} catch (Exception e) {
			// その他の予期せぬエラー
			// PropertyManager.getProperties() を使っている部分はこのcatchで処理されます
			event.reply(PropertyManager.getProperties("ボタンオブジェクトが生成できませんでした。")).setEphemeral(true).queue();
			System.err.println("Unhandled exception in SlashCommandListener: " + e.getMessage());
			e.printStackTrace();
		}
	}
}