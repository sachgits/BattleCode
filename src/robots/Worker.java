import bc.*;

/**
 * Represents a worker robot in the game
 * @author virsain
 */
public class Worker extends AbstractRobot {
	// the blueprint which the worker is currently working on
	public AbstractStructure currentBlueprint; 
	
	/**
	 * Creates a new Worker
	 * @param i
	 * 	The worker's id
	 * @param g
	 * 	The GameController for the game
	 * @param map
	 * 	The BattleMap for the game
	 * @param location
	 * 	The initial location of the worker
	 */
	public Worker(int i, GameController g, Map map, MapLocation location) {
		super(i, g, map, location, UnitType.Worker);
		state = State.Idle;
		previousState = State.Idle;
	}
	
	/**
	 * Creates a new blueprint at the specified direction
	 * @param dir
	 * 	The direction from the worker
	 * @return
	 * 	A Blueprint class representing the blueprint
	 */
	public AbstractStructure setBlueprint(Direction dir, UnitType structureType) {
		previousState = state;
		
		// Check if the worker (occupantType) can lay the blueprint
		if (!gc.canBlueprint(id, structureType, dir)) {
			state = State.Idle;
			return null;
		}
		
		// lay the blueprint
		gc.blueprint(id, structureType, dir);

		// get the fields required to make the blueprint class
		MapLocation blueprintLocation = currentLocation.add(dir);
		int blueprintId = gc.senseUnitAtLocation(blueprintLocation).id();
		
		// update the map about the blueprint location
		battleMap.updateOccupant(blueprintLocation, structureType);

		// update the state
		state = State.Build;
		
		if (structureType == UnitType.Factory)
			currentBlueprint = new Factory(blueprintId, gc, battleMap, blueprintLocation);
		
		// return the newly created blueprint class
		return currentBlueprint;
	}
	
	/**
	 * Builds on a given blueprint
	 * @param blueprint
	 * 	Blueprint on which to build on
	 * @return
	 * 	0 if building, 1 if structure is finished building, 
	 * 	2 if failed to build
	 */
	public int build(AbstractStructure blueprint) {
		currentBlueprint = blueprint;
		return build();
	}
	

	/**
	 * Builds on an existing blueprint
	 * @return
	 * 	0 if building, 1 if structure is finished building, 
	 * 	2 if failed to build
	 */
	public int build() {
		previousState = state;
		// check to see if the worker can build
		if (!gc.canBuild(id, currentBlueprint.id)) return 2;
		
		gc.build(id, currentBlueprint.id);
		
		// if the structure is completely built
		if (gc.unit(currentBlueprint.id).structureIsBuilt() == 1) {
			state = State.Idle;
			currentBlueprint.state = AbstractStructure.State.Idle;
			System.out.println("Structure built");
			currentBlueprint = null;
			return 1;
		}
		
		// if the structure is not done building
		state = State.Build;
		return 0;
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
		previousState = state;
		int returnValue = super.move();

		// if no path is set, or worker finished moving, worker's state is null
		if (returnValue == -1 && returnValue == 1)
			state = State.Idle;
	
		// if worker is still moving along path, worker's state is MOVE
		else 
			state = State.Move;
		
		return returnValue;
		
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
		previousState = state;
		int returnValue = super.move(dir);

		// worker finished moving, worker's state is null
		if (returnValue == 1) {
			state = State.Idle;
		}
		// if worker is still must move along direction, worker's state is MOVE
		else 
			state = State.Move;
		
		return returnValue;
	}
	
	public int ability() {
		return 0;
	}
}
