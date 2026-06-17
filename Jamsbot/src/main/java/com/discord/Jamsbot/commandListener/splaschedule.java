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
                        event.getHook().sendMessage("スケジュールの取得に失敗しました。(ステータスコード: " + response.statusCode() + ")")
                                .queue();
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
                        String ruleDisplayName = getRuleDisplayName(ruleParam);

                        if (ruleParam.equals("coop-grouping")) {
                            // サーモンラン用
                            EmbedBuilder nowEmbed = createSalmonEmbed("現在", now, themeColor, ruleDisplayName);
                            EmbedBuilder nextEmbed = createSalmonEmbed("次", next, themeColor, ruleDisplayName);
                            event.getHook().sendMessageEmbeds(nowEmbed.build(), nextEmbed.build()).queue();
                        } else {
                            // バトル用
                            EmbedBuilder nowEmbed = createBattleEmbed("現在", now, themeColor, ruleDisplayName);
                            EmbedBuilder nextEmbed = createBattleEmbed("次", next, themeColor, ruleDisplayName);
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

    // ====== ルールに応じて表示名を返すメソッド ======
    private String getRuleDisplayName(String ruleParam) {
        switch (ruleParam) {
            case "regular": return "レギュラーマッチ";
            case "bankara-open": return "バンカラマッチ (オープン)";
            case "bankara-challenge": return "バンカラマッチ (チャレンジ)";
            case "x": return "Xマッチ";
            case "fest": return "フェスマッチ";
            case "fest-challenge": return "フェスマッチ (チャレンジ)";
            case "event": return "イベントマッチ";
            case "coop-grouping": return "サーモンラン";
            default: return ruleParam;
        }
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
    // 引数にColorとruleDisplayNameを追加して動的に設定できるように変更
    private EmbedBuilder createBattleEmbed(String timePrefix, JsonObject schedule, Color color, String ruleDisplayName) {
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

        embed.setTitle("【" + timePrefix + "】" + ruleDisplayName);
        embed.setColor(color); // ★ ここで取得した色をセット

        StringBuilder desc = new StringBuilder();
        desc.append("**時間**: ").append(timeStr).append("\n");
        desc.append("**ルール**: ").append(ruleName).append("\n");
        desc.append("**ステージ**: ").append(stage1Name).append(" / ").append(stage2Name);
        embed.setDescription(desc.toString());

        embed.setImage(stage1Image);
        embed.setThumbnail(stage2Image);

        return embed;
    }

    // ====== サーモンラン専用のEmbed作成メソッド ======
    // 引数にColorとruleDisplayNameを追加して動的に設定できるように変更
    private EmbedBuilder createSalmonEmbed(String timePrefix, JsonObject schedule, Color color, String ruleDisplayName) {
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

        embed.setTitle("【" + timePrefix + "】" + ruleDisplayName);
        embed.setColor(color); // ★ ここで取得した色をセット

        StringBuilder desc = new StringBuilder();
        desc.append("**時間**: ").append(timeStr).append("\n");
        desc.append("**ステージ**: ").append(stageName).append("\n");
        desc.append("**支給ブキ**:\n").append(weaponNames.toString());
        embed.setDescription(desc.toString());

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
