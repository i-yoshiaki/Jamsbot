package util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

public class ItoSessionManager {

	private static final Map<String, GameSession> sessions = new ConcurrentHashMap<>();

	public static void createSession(String messageId, int count) {
		sessions.put(messageId, new GameSession(count));
	}

	public static void removeSession(String messageId) {
		sessions.remove(messageId);
	}

	public static boolean hasSession(String messageId) {
		return sessions.containsKey(messageId);
	}

	public static List<Integer> drawCards(String messageId, String userId) {
		GameSession session = sessions.get(messageId);
		if (session == null) {
			return null;
		}
		return session.drawCards(userId);
	}

	public static int getCardCount(String messageId) {
		GameSession session = sessions.get(messageId);
		return session != null ? session.cardCount : 0;
	}

	public static Map<String, List<Integer>> getAllUserCards(String messageId) {
		GameSession session = sessions.get(messageId);
		return session != null ? session.userCards : null;
	}

	private static class GameSession {
		private final int cardCount;
		private final Map<String, List<Integer>> userCards = new ConcurrentHashMap<>();
		private final List<Integer> deck = new ArrayList<>();
		private final Random random = new Random();

		public GameSession(int count) {
			this.cardCount = count;
			for (int i = 1; i <= 100; i++) {
				deck.add(i);
			}
			Collections.shuffle(deck, random);
		}

		public synchronized List<Integer> drawCards(String userId) {
			if (userCards.containsKey(userId)) {
				return userCards.get(userId);
			}

			List<Integer> drawnCards = new ArrayList<>();
			for (int i = 0; i < cardCount; i++) {
				if (deck.isEmpty()) {
					break;
				}
				drawnCards.add(deck.remove(deck.size() - 1));
			}

			Collections.sort(drawnCards);
			userCards.put(userId, drawnCards);
			return drawnCards;
		}
	}
}
