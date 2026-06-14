package com.discord.Jamsbot.commandListener;

import java.awt.Color;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;

public class splarandomweapon extends commandListenerAbstract {

	public splarandomweapon(SlashCommandInteractionEvent e) {
		super(e);
	}

	@Override
	public void execute() {
		OptionMapping typeOption = event.getOption("type");
		OptionMapping subOption = event.getOption("sub");
		OptionMapping specialOption = event.getOption("special");

		// 前後の余分な空白を消して取得
		String selectedType = typeOption != null ? typeOption.getAsString().trim() : null;
		String selectedSub = subOption != null ? subOption.getAsString().trim() : null;
		String selectedSpecial = specialOption != null ? specialOption.getAsString().trim() : null;

		event.deferReply().queue();

		HttpClient client = HttpClient.newHttpClient();
		HttpRequest request = HttpRequest.newBuilder()
				.uri(URI.create("https://stat.ink/api/v3/weapon"))
				.header("User-Agent", "YourDiscordBotName/1.0")
				.timeout(Duration.ofSeconds(10))
				.GET()
				.build();

		client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
				.thenAccept(response -> {
					if (response.statusCode() != 200) {
						event.getHook().sendMessage("ブキデータの取得に失敗しました。(ステータスコード: " + response.statusCode() + ")").queue();
						return;
					}

					try {
						JsonArray allWeapons = JsonParser.parseString(response.body()).getAsJsonArray();
						List<JsonObject> filteredWeapons = new ArrayList<>();

						// 原因究明用のデバッグカウンター
						int totalWeapons = allWeapons.size();
						int skipCountByNull = 0;
						int skipCountByCondition = 0;
						JsonObject sampleWeapon = null; // 構造確認用

						for (JsonElement element : allWeapons) {
							try {
								JsonObject weapon = element.getAsJsonObject();
								if (sampleWeapon == null)
									sampleWeapon = weapon; // 1件目を保持

								// 各項目の「name」オブジェクトを取得
								JsonObject wNameObj = weapon.has("name") ? weapon.getAsJsonObject("name") : null;
								JsonObject typeNameObj = weapon.has("type") && !weapon.get("type").isJsonNull()
										? weapon.getAsJsonObject("type").getAsJsonObject("name")
										: null;
								JsonObject subNameObj = weapon.has("sub") && !weapon.get("sub").isJsonNull()
										? weapon.getAsJsonObject("sub").getAsJsonObject("name")
										: null;
								JsonObject specialNameObj = weapon.has("special") && !weapon.get("special").isJsonNull()
										? weapon.getAsJsonObject("special").getAsJsonObject("name")
										: null;

								// あらゆるパターンを想定して日本語名を取得
								String weaponName = getJaName(wNameObj);
								String typeName = getJaName(typeNameObj);
								String subName = getJaName(subNameObj);
								String specialName = getJaName(specialNameObj);

								// 日本語名がどれか1つでも欠けていたら（クマサンブキや不完全データ）スキップ
								if (weaponName == null || typeName == null || subName == null || specialName == null) {
									skipCountByNull++;
									continue;
								}

								// 比較用にトリム
								typeName = typeName.trim();
								subName = subName.trim();
								specialName = specialName.trim();

								// ユーザーが指定した検索条件と一致するかチェック（未指定ならスルー）
								if (selectedType != null && !selectedType.equals(typeName)) {
									skipCountByCondition++;
									continue;
								}
								if (selectedSub != null && !selectedSub.equals(subName)) {
									skipCountByCondition++;
									continue;
								}
								if (selectedSpecial != null && !selectedSpecial.equals(specialName)) {
									skipCountByCondition++;
									continue;
								}

								// すべての条件をクリアしたブキを候補に追加
								filteredWeapons.add(weapon);
							} catch (Exception ex) {
								skipCountByNull++;
							}
						}

						// 🛠️ 【重要】もし候補が0件だった場合、コンソールに原因を詳しく出力する
						if (filteredWeapons.isEmpty()) {
							System.out.println("\n====== [splarandomweapon] ブキが1件もみつかりませんでした ======");
							System.out.println("APIから取得した総件数: " + totalWeapons);
							System.out.println("日本語名が取得できずにスキップされた数: " + skipCountByNull);
							System.out.println("検索条件（カテゴリー等）が合わずにスキップされた数: " + skipCountByCondition);
							if (sampleWeapon != null && sampleWeapon.has("name")) {
								System.out.println("実際のデータのキー一覧: " + sampleWeapon.getAsJsonObject("name").keySet());
							}
							System.out.println("===============================================================\n");

							event.getHook().sendMessage("指定された条件に完全一致するブキは見つかりませんでした🦑💦\n条件を変えてみてください。").queue();
							return;
						}

						// 候補の中からランダムに1つ選ぶ
						Random random = new Random();
						JsonObject pickedWeapon = filteredWeapons.get(random.nextInt(filteredWeapons.size()));

						String finalWeaponName = getJaName(pickedWeapon.getAsJsonObject("name"));
						String finalTypeName = getJaName(pickedWeapon.getAsJsonObject("type").getAsJsonObject("name"));
						String finalSubName = getJaName(pickedWeapon.getAsJsonObject("sub").getAsJsonObject("name"));
						String finalSpecialName = getJaName(pickedWeapon.getAsJsonObject("special").getAsJsonObject("name"));

						// Embedの作成
						EmbedBuilder embed = new EmbedBuilder();
						embed.setTitle("🎲 ランダムブキ決定！");
						embed.setColor(Color.YELLOW);

						StringBuilder filterInfo = new StringBuilder("【検索条件】 ");
						if (selectedType == null && selectedSub == null && selectedSpecial == null) {
							filterInfo.append("全ブキからランダム");
						} else {
							if (selectedType != null)
								filterInfo.append("[").append(selectedType).append("] ");
							if (selectedSub != null)
								filterInfo.append("[").append(selectedSub).append("] ");
							if (selectedSpecial != null)
								filterInfo.append("[").append(selectedSpecial).append("]");
						}
						embed.setDescription(filterInfo.toString());

						embed.addField("名前", "**" + finalWeaponName + "**", false);
						embed.addField("カテゴリー", finalTypeName, true);
						embed.addField("サブ", finalSubName, true);
						embed.addField("スペシャル", finalSpecialName, true);

						event.getHook().sendMessageEmbeds(embed.build()).queue();

					} catch (Exception ex) {
						ex.printStackTrace();
						event.getHook().sendMessage("データの解析中にエラーが発生しました。").queue();
					}
				})
				.exceptionally(ex -> {
					ex.printStackTrace();
					event.getHook().sendMessage("APIとの通信中にエラーが発生しました。").queue();
					return null;
				});
	}

	// ====== あらゆる日本語キーのパターン（ハイフン、大文字小文字、短縮）を網羅するメソッド ======
	private String getJaName(JsonObject nameObj) {
		if (nameObj == null)
			return null;

		// 最有力候補の「ja-JP」から順にチェック
		String[] keys = { "ja-JP", "ja_JP", "ja_jp", "ja" };
		for (String key : keys) {
			if (nameObj.has(key) && !nameObj.get(key).isJsonNull()) {
				return nameObj.get(key).getAsString();
			}
		}
		return null;
	}
}