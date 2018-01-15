import bc.*;

/**
 * Super class for all Robots
 * @author virsain
 *
 */
public abstract class AbstractRobot extends AbstractUnit {
	public enum State { Build, Move, Mine, Idle; };

	GameController gc;  // the game controller for the game
	MapLocation[] movePath;  // the path along which the robot must move
	int moveIndex;  // the index of movePath, the number of steps the robot
						 // has already taken along the path
	MapLocation currentLocation; // the robot's current location
	Map battleMap;  // A grid of TileNodes representing the full map
	UnitType type; // The robot Type
	public State state; // The robot's present state
	public State previousState;  // The robot's previous state

	public AbstractRobot(int i, GameController g, Map map, MapLocation location,
			UnitType t) {
		id = i;
		gc = g;
		battleMap = map;
		moveIndex = 0;
		currentLocation = location;
		type = t;

	}

	/**
	 * Tries to move the robot along a path.
	 * @return
	 * 	Returns -1 if no path is set
	 * 	Returns  0 if successfully moved
	 * 	Returns  1 if successfully moved and reached destination
	 * 	Returns  2 if not ready to move (still on cooldown)
	 * 	Returns  3 if path is blocked
	 */
	public int move() {
		// no path is set
		if (movePath == null) return -1;

		// movement cooldown still up
		if (!gc.isMoveReady(id)) return 2;

		// get the next step's direction
		Direction dir = currentLocation.directionTo(movePath[moveIndex]);

		// check if the robot can move in that direction
		if (!gc.canMove(id, dir)) return 3;

		gc.moveRobot(id, dir);
		// set previous location's occupant to 0
		battleMap.updateOccupant(currentLocation, null);
		// update the current location
		currentLocation = movePath[moveIndex];
		// update the map to the new location of the robot
		battleMap.updateOccupant(currentLocation, type);

		moveIndex++;

		// if reached destination then reset path
		if (moveIndex == movePath.length) {
			movePath = null;
			moveIndex = 0;
			return 1;
		}

		return 0;
	}

	/**
	 * Tries to move the robot in a particular direction
	 *
	 * @param dir
	 * 	The direction in which the robot must move
	 * @return
	 * 	Returns 1 if successfully moved
	 * 	Returns 2 if not ready to move (still on cooldown)
	 * 	Returns 3 if path is blocked
	 */
	public int move(Direction dir) {
		// movement cooldown still up
		if (!gc.isMoveReady(id)) return 2;

		// check if the robot can move in that direction
		if (!gc.canMove(id, dir)) return 3;

		gc.moveRobot(id, dir);
		// set previous location's occupant to 0
		battleMap.updateOccupant(currentLocation, null);
		// update the current location
		currentLocation = currentLocation.add(dir);
		// update the map to the new location of the robot
		battleMap.updateOccupant(currentLocation, occupantType);
		return 1;
	}

	/**
	 * Tries to move the robot in a particular direction
	 *
	 * @param dir
	 * 	The direction in which the robot must move
	 * @return
	 * 	Returns 1 if successfully moved
	 * 	Returns 2 if not ready to move (still on cooldown)
	 * 	Returns 3 if path is blocked
	 */
	public int move(Direction dir) {
		// movement cooldown still up
		if (!gc.isMoveReady(id)) return 2;

		// check if the robot can move in that direction
		if (!gc.canMove(id, dir)) return 3;

		gc.moveRobot(id, dir);
		// set previous location's occupant to 0
		battleMap.updateOccupant(currentLocation, null);
		// update the current location
		currentLocation = currentLocation.add(dir);
		// update the map to the new location of the robot
		battleMap.updateOccupant(currentLocation, type);
		return 1;
	}

	/**
	 * Finds the shortestPath between the start and end locations and sets
	 * the robot's movePath to that path
	 *
	 * @param start
	 * 	The start location
	 * @param end
	 * 	The end location
	 *
	 * @return
	 * 	True if there is a path between the start and end locations, false
	 * 	if no path exists.
	 */
	public boolean setPath(MapLocation start, MapLocation end) {
		MapLocation[] path = battleMap.shortestPath(start, end);

		// if path is empty, then no path exists
		if (path.length == 0) return false;

		movePath = path;
		return true;
	}

	public MapLocation getLocation() {
		return currentLocation;
	}

	public abstract int ability();
}
