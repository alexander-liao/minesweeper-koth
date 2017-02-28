package bot;

public abstract class MineBot {
	private int x, y;

	public abstract int changeMines(int[][] field);

	public abstract int[] proceed(int[][] field);

	public final int getX() {
		return x;
	}

	public final void setX(int x) {
		this.x = x;
	}

	public final int getY() {
		return y;
	}

	public final void setY(int y) {
		this.y = y;
	}
}