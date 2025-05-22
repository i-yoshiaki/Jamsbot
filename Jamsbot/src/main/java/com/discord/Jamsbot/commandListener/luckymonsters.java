package com.discord.Jamsbot.commandListener;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

import org.jetbrains.annotations.NotNull;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public class luckymonsters extends commandListenerAbstract {

	public luckymonsters(SlashCommandInteractionEvent e) {
		super(e);
	}
	
	@Override
	public void commandInteracton(@NotNull SlashCommandInteractionEvent event) {
		event.reply("処理中...").setEphemeral(true).queue(
				response ->{
					List<String> urlList = new ArrayList<String>();
					try {
						Document doc = Jsoup.connect("https://xn--eckwa2aa3a9c8j8bve9d.gamewith.jp/article/show/148271").get();
						// Get all img tags
						Elements img = doc.getElementsByTag("img");
						List<String> elList = new ArrayList<String>();
						// Loop through img tags
						for (Element el : img) {
							if (el.attr("alt").equals("ラキモン") || el.attr("alt").contains("からのラキモン")) {
								elList.add(el.attr("src"));
							}
						}
						// 重複削除
						List<String> hashSetList = new ArrayList<String>(new LinkedHashSet<>(elList));
						// 1px画像を削除
						for (int i = 0; i < hashSetList.size(); i++) {
							if (hashSetList.get(i).contains("transparent1px")) {
								hashSetList.remove(i);
							}
						}
						// ラキモン画像２枚出力
						for (String s : hashSetList) {
							System.out.println(s);
							urlList.add(s);
						}
						// 処理中を編集して出力
						event.getHook().editOriginal(urlList.get(0) + "\n" + urlList.get(1)).queue();
					} catch (Exception e) {
						event.reply("URLが取得できませんでした。").setEphemeral(true).queue();
						e.printStackTrace();
					}
				});
	}
}	