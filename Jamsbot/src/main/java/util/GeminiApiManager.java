package util;

import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import constants.BotConnectionPropertyKey;

public class GeminiApiManager {

	private static final String API_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash-lite:generateContent?key=";

	public static String generateTheme() {
		String apiKey = null;
		try {
			apiKey = PropertyManager.getProperties(BotConnectionPropertyKey.GEMINI_API_KEY.getKey());
		} catch (Exception e) {
			// キーが存在しない場合など
		}
		
		if (apiKey == null || apiKey.trim().isEmpty()) {
			return "お題（APIキーが未設定のため生成できませんでした）";
		}

		try {
			URL url = new URL(API_URL + apiKey.trim());
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("POST");
			conn.setRequestProperty("Content-Type", "application/json");
			conn.setDoOutput(true);

			// JSON Request Body
			String prompt = "ボードゲーム『ito』で使える、盛り上がる面白いお題を3つ提案してください。それぞれ「・〇〇」のような箇条書き形式で、テキストのみを出力してください。";
			
			JsonObject part = new JsonObject();
			part.addProperty("text", prompt);
			
			JsonArray parts = new JsonArray();
			parts.add(part);
			
			JsonObject content = new JsonObject();
			content.add("parts", parts);
			
			JsonArray contents = new JsonArray();
			contents.add(content);
			
			JsonObject requestBody = new JsonObject();
			requestBody.add("contents", contents);

			Gson gson = new Gson();
			String jsonInputString = gson.toJson(requestBody);

			try (OutputStream os = conn.getOutputStream()) {
				byte[] input = jsonInputString.getBytes("utf-8");
				os.write(input, 0, input.length);
			}

			int responseCode = conn.getResponseCode();
			if (responseCode == HttpURLConnection.HTTP_OK) {
				try (InputStreamReader reader = new InputStreamReader(conn.getInputStream(), "utf-8")) {
					JsonObject jsonResponse = gson.fromJson(reader, JsonObject.class);
					
					// Parse: candidates[0].content.parts[0].text
					JsonArray candidates = jsonResponse.getAsJsonArray("candidates");
					if (candidates != null && candidates.size() > 0) {
						JsonObject firstCandidate = candidates.get(0).getAsJsonObject();
						JsonObject resContent = firstCandidate.getAsJsonObject("content");
						if (resContent != null) {
							JsonArray resParts = resContent.getAsJsonArray("parts");
							if (resParts != null && resParts.size() > 0) {
								return resParts.get(0).getAsJsonObject().get("text").getAsString().trim();
							}
						}
					}
				}
			} else {
				System.err.println("Gemini API Error: Response Code " + responseCode);
				try (java.io.InputStream errorStream = conn.getErrorStream()) {
					if (errorStream != null) {
						try (java.io.BufferedReader br = new java.io.BufferedReader(new InputStreamReader(errorStream, "utf-8"))) {
							String responseLine = null;
							while ((responseLine = br.readLine()) != null) {
								System.err.println(responseLine);
							}
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return getFallbackTheme();
	}

	private static String getFallbackTheme() {
		String[] fallbacks = {
			"無人島に持っていきたいもの",
			"人気な食べ物・料理",
			"子供の頃に好きだった遊び",
			"最強の動物",
			"ドラえもんのひみつ道具の便利さ",
			"貰って嬉しいプレゼント",
			"言われて嬉しい言葉",
			"学校の先生の口癖",
			"一生に一度は行きたい場所",
			"一番怖いもの"
		};
		java.util.Random rand = new java.util.Random();
		java.util.Set<Integer> indices = new java.util.HashSet<>();
		while (indices.size() < 3) {
			indices.add(rand.nextInt(fallbacks.length));
		}
		
		StringBuilder sb = new StringBuilder();
		sb.append("【AI混雑中のため自動生成】\n");
		for (int idx : indices) {
			sb.append("・").append(fallbacks[idx]).append("\n");
		}
		return sb.toString().trim();
	}
}
