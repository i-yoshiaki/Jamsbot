package com.discord.Jamsbot.commandListener;

import java.awt.Color;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;

public class splaschedule extends commandListenerAbstract {

    public splaschedule(SlashCommandInteractionEvent e) {
        super(e);
    }

    @Override
    public void execute() {
        String ruleParam = event.getOption("rule").getAsString();
        event.deferReply().queue();

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://spla3.yuu26.com/api/" + ruleParam + "/schedule"))
                .header("User-Agent", "YourDiscordBotName/1.0") // 適宜変更してください
                .timeout(Duration.ofSeconds(10))
                .GET()
                .build();

        client.sendAsync(request, HttpResponse.BodyHandlers.ofString())
            .thenAccept(response -> {
                if (response.statusCode() != 200) {
                    event.getHook().sendMessage("スケジュールの取得に失敗しました。(ステータスコード: " + response.statusCode() + ")").queue();
                    return;
                }

                try {
                    JsonObject jsonObject = JsonParser.parseString(response.body()).getAsJsonObject();
                    JsonArray results = jsonObject.getAsJsonArray("results");

                    if (results.size() < 2) {
                        event.getHook().sendMessage("現在取得できるスケジュールデータがありません。").queue();
                        return;
                    }

                    JsonObject now = results.get(0).getAsJsonObject();
                    JsonObject next = results.get(1).getAsJsonObject();
                    
                    // ★ 選択されたルールに応じたテーマカラーを取得
                    Color themeColor = getRuleColor(ruleParam);

                    if (ruleParam.equals("coop-grouping")) {
                        // サーモンラン用
                        EmbedBuilder nowEmbed = createSalmonEmbed("🐟 現在のシフト", now, themeColor);
                        EmbedBuilder nextEmbed = createSalmonEmbed("⏭️ 次のシフト", next, themeColor);
                        event.getHook().sendMessageEmbeds(nowEmbed.build(), nextEmbed.build()).queue();
                    } else {
                        // バトル用
                        EmbedBuilder nowEmbed = createBattleEmbed("🟢 現在のスケジュール", now, themeColor);
                        EmbedBuilder nextEmbed = createBattleEmbed("⏭️ 次のスケジュール", next, themeColor);
                        event.getHook().sendMessageEmbeds(nowEmbed.build(), nextEmbed.build()).queue();
                    }

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

    // ====== ルールに応じて色を返すメソッド ======
    private Color getRuleColor(String ruleParam) {
        switch (ruleParam) {
            case "regular":
                return new Color(25, 215, 25); // 緑
            case "bankara-open":
            case "bankara-challenge":
                return new Color(245, 73, 16); // 濃い目のオレンジ（朱色寄り）
            case "x":
                return new Color(15, 206, 179); // 水色（エメラルドグリーン寄り）
            case "fest":
            case "fest-challenge":
                return new Color(138, 43, 226); // 紫
            case "event":
                return new Color(255, 51, 153); // ショッキングピンク
            case "coop-grouping":
                return new Color(255, 140, 0); // オレンジ（バンカラより黄色寄り）
            default:
                return Color.GRAY; // 万が一のデフォルト色
        }
    }

    // ====== バトル系(レギュラーやバンカラ等)のEmbed作成メソッド ======
    // 引数にColorを追加して色を動的に設定できるように変更
    private EmbedBuilder createBattleEmbed(String titlePrefix, JsonObject schedule, Color color) {
        EmbedBuilder embed = new EmbedBuilder();
        
        String startTime = formatTime(schedule.get("start_time").getAsString());
        String endTime = formatTime(schedule.get("end_time").getAsString());
        String timeStr = startTime + " 〜 " + endTime;

        String ruleName = schedule.getAsJsonObject("rule").get("name").getAsString();
        JsonArray stages = schedule.getAsJsonArray("stages");
        
        String stage1Name = stages.get(0).getAsJsonObject().get("name").getAsString();
        String stage1Image = stages.get(0).getAsJsonObject().get("image").getAsString();
        String stage2Name = stages.get(1).getAsJsonObject().get("name").getAsString();
        String stage2Image = stages.get(1).getAsJsonObject().get("image").getAsString();

        embed.setTitle(titlePrefix + " (" + timeStr + ")");
        embed.setColor(color); // ★ ここで取得した色をセット
        embed.addField("ルール", ruleName, false);
        embed.addField("ステージ", stage1Name + "\n" + stage2Name, false);
        
        embed.setImage(stage1Image);
        embed.setThumbnail(stage2Image);

        return embed;
    }

    // ====== サーモンラン専用のEmbed作成メソッド ======
    // 引数にColorを追加して色を動的に設定できるように変更
    private EmbedBuilder createSalmonEmbed(String titlePrefix, JsonObject schedule, Color color) {
        EmbedBuilder embed = new EmbedBuilder();
        
        String startTime = formatTime(schedule.get("start_time").getAsString());
        String endTime = formatTime(schedule.get("end_time").getAsString());
        String timeStr = startTime + " 〜 " + endTime;

        JsonObject stage = schedule.getAsJsonObject("stage");
        String stageName = stage.get("name").getAsString();
        String stageImage = stage.get("image").getAsString();

        JsonArray weapons = schedule.getAsJsonArray("weapons");
        StringBuilder weaponNames = new StringBuilder();
        for (JsonElement w : weapons) {
            weaponNames.append("・").append(w.getAsJsonObject().get("name").getAsString()).append("\n");
        }

        embed.setTitle(titlePrefix + " (" + timeStr + ")");
        embed.setColor(color); // ★ ここで取得した色をセット
        embed.addField("ステージ", stageName, false);
        embed.addField("支給ブキ", weaponNames.toString(), false);
        embed.setImage(stageImage);

        return embed;
    }

    // ====== 日付の見た目を「MM/dd HH:mm」にするメソッド ======
    private String formatTime(String isoTime) {
        OffsetDateTime odt = OffsetDateTime.parse(isoTime);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd HH:mm");
        return odt.format(formatter);
    }
}
