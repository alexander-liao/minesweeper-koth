package programmed_bots;

import bot.MineBot;

public class TestBot extends MineBot {
	public int changeMines(int[][] field) {
		return 2;
	}

	public int[] proceed(int[][] field) {
		return new int[] { 2, (int) (Math.random() * 8) };
	}
}