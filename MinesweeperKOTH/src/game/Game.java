package game;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import bot.MineBot;

public class Game {
	private static int[][] board;

	public static void main(String[] args)
			throws InstantiationException, IllegalAccessException, ClassNotFoundException, IOException {
		String[] botnames = { "TestBot" };
		int botcount = botnames.length;
		List<MineBot> defusers = new ArrayList<>();
		List<MineBot> bombers = new ArrayList<>();
		List<MineBot> movers = new ArrayList<>();
		List<MineBot> bots = new ArrayList<>();
		for (String name : botnames) {
			bots.add((MineBot) Class.forName("programmed_bots." + name).newInstance());
		}
		board = new int[botcount + 10][botcount + 10];
		int pos = 5;
		for (MineBot bot : bots) {
			bot.setX(pos);
			bot.setY(pos);
			pos += 10;
		}
		for (int iteration = 0; !bots.isEmpty(); iteration++) {
			int[][] field = neighbors(board, bots);
			for (int i = 0; i < botcount; i++) {
				try {
					int type = bots.get(i).changeMines(clone(field));
					(type == 0 ? movers : type == 1 ? defusers : bombers).add(bots.get(i));
				} catch (Throwable t) {
					System.out.println(bots.get(i).getClass().getSimpleName() + " killed itself...");
					bots.remove(i);
					botcount--;
				}
			}
			for (MineBot bot : defusers) {
				try {
					int[] move = bot.proceed(clone(field));
					if (move[0] == 4) {
						int dx = xFromMove(move[1]);
						int dy = yFromMove(move[1]);
						if (get(board, bot.getX() + dx, bot.getY() + dy) == 1) {
							set(board, bot.getX() + dx, bot.getY() + dy, -1);
						}
					}
				} catch (Throwable t) {
					System.out.println(bot.getClass().getSimpleName() + " killed itself...");
					bots.remove(bot);
					botcount--;
				}
			}
			field = neighbors(board, bots);
			for (MineBot bot : bombers) {
				try {
					int[] move = bot.proceed(clone(field));
					if (move[0] == 2 || move[0] == 3) {
						int dx = xFromMove(move[1]);
						int dy = yFromMove(move[1]);
						if (move[0] == 2) {
							if (get(board, bot.getX(), bot.getY()) == 0) {
								set(board, bot.getX(), bot.getY(), 1);
							}
							move(bot, bot.getX() + dx, bot.getY() + dy);
						} else {
							if (get(board, bot.getX() + dx, bot.getY() + dy) == 0) {
								set(board, bot.getX() + dx, bot.getY() + dy, 1);
							}
						}
					}
				} catch (Throwable t) {
					System.out.println(bot.getClass().getSimpleName() + " killed itself...");
					bots.remove(bot);
					botcount--;
				}
			}
			field = neighbors(board, bots);
			for (MineBot bot : movers) {
				try {
					int[] move = bot.proceed(clone(field));
					if (move[0] == 1) {
						int dx = xFromMove(move[1]);
						int dy = yFromMove(move[1]);
						move(bot, bot.getX() + dx, bot.getY() + dy);
					}
				} catch (Throwable t) {
					System.out.println(bot.getClass().getSimpleName() + " killed itself...");
					bots.remove(bot);
					botcount--;
				}
			}
			List<MineBot> dead = new ArrayList<>();
			for (MineBot bot : bots) {
				if (board[bot.getX()][bot.getY()] == 1) {
					System.out.println(bot.getClass().getSimpleName() + " died, with score " + iteration);
					dead.add(bot);
					botcount--;
				}
			}
			for (MineBot bot : dead) {
				bots.remove(bot);
				board[bot.getX()][bot.getY()] = 0;
			}
			defusers.clear();
			bombers.clear();
			movers.clear();
			for (int i = 0; i < board.length; i++) {
				for (int j = 0; j < board[i].length; j++) {
					if (board[i][j] == -1) {
						board[i][j] = 0;
					}
				}
			}
		}
	}

	public static void move(MineBot bot, int x, int y) {
		if (x >= 0 && x < board.length && y >= 0 && y < board[x].length) {
			bot.setX(x);
			bot.setY(y);
		}
	}

	public static int[][] neighbors(int[][] board, List<MineBot> bots) {
		int[][] neighbors = new int[board.length][board[0].length];
		for (int x = 0; x < board.length; x++) {
			for (int y = 0; y < board[x].length; y++) {
				if (board[x][y] != 0) {
					for (int i = -1; i <= 1; i++) {
						for (int j = -1; j <= 1; j++) {
							if (i != 0 || j != 0) {
								set(neighbors, x + i, y + j, get(board, x + i, y + j) + 1);
							}
						}
					}
				}
			}
		}
		for (MineBot bot : bots) {
			neighbors[bot.getX()][bot.getY()] += 8;
		}
		return neighbors;
	}

	public static int get(int[][] board, int x, int y) {
		return x >= 0 && x < board.length && y >= 0 && y < board[x].length ? board[x][y] : 0;
	}

	public static void set(int[][] board, int x, int y, int n) {
		if (x >= 0 && x < board.length && y >= 0 && y < board[x].length) {
			board[x][y] = n;
		}
	}

	public static int xFromMove(int move) {
		return new int[] { -1, 0, 1, 1, 1, 0, -1, -1 }[move];
	}

	public static int yFromMove(int move) {
		return new int[] { -1, -1, -1, 0, 1, 1, 1, 0 }[move];
	}

	public static int[][] clone(int[][] array) {
		int[][] clone = new int[array.length][];
		for (int row = 0; row < array.length; row++) {
			clone[row] = new int[array[row].length];
			System.arraycopy(array[row], 0, clone[row], 0, array[row].length);
		}
		return clone;
	}
}