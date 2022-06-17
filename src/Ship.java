import java.awt.Point;

public class Ship {

	private int size;
	private int lives;
	private boolean isSunk;
	private Point[] positions;

	public Ship(int size) {
		this.size = size;
		lives = size;
		isSunk = false;
		positions = new Point[size];
	}

	public int getSize() {
		return size;
	}

	public int getLives() {
		return lives;
	}

	public void decreaseLives() {
		lives--;
	}

	public boolean status() {
		return isSunk;
	}

	public void sunk() {
		isSunk = true;
	}

	public Point[] getPosition() {
		return positions;
	}

	public void setPosition(int index, Point x) {
		positions[index] = x;
	}

}
