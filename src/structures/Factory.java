import bc.*;

/**
 * Represents a Factory
 * @author virsain
 */
public class Factory extends AbstractStructure {
	/**
	 * Creates a new Factory
	 */
	public Factory(int id, GameController g, Map map, MapLocation location) {
		super(id, g, map, location, UnitType.Factory);
		
		// factories can only be made by worker's in their blueprint stage
		state = State.Blueprint;
		previousState = State.Blueprint;
	}
	
	/**
	 * Unloads a unit in a particular direction
	 * @param dir
	 * 	The direction to unload the unit
	 * @return
	 * 	1 if unloaded successfully
	 * 	2 if unloading was not possible
	 */
	public int unload(Direction dir) {
		previousState = state;
		
		// check if factory can unload
		if (!gc.canUnload(id, dir)) {
			state = State.Unload;
			return 2;
		}
		
		System.out.println("unloading");
		gc.unload(id, dir);
		state = State.Idle;
		return 1;
	}
	
	/**
	 * Produces a unit of a particular type
	 * @param unitType
	 * 	The unit type to be produced
	 * @return
	 *   -1 if still a blueprint
	 *    0 if done producing
	 * 	1 if started produced successfully
	 * 	2 if producing was not possible (not enough karbonite)
	 * 	3 if already producing something
	 */
	public int produce(UnitType unitType) {
		System.out.println("Amount of Karbonite " + gc.karbonite());
		previousState = state;
		
		// check to see if the factory is built
		if (gc.unit(id).structureIsBuilt() == 0) {
			state = State.Blueprint;
			System.out.println("Factory Not Built");
			return -1;
		}
		
		// if the factory is still producing
		System.out.println("yo yo");
		if (gc.unit(id).isFactoryProducing() == 1) {
			System.out.println("Not done producing");
			state = State.Produce;
			return 3;
		/* if the factory is not producing and just finished producing
		 * return in case the unit needs to be unloaded
		 */ 
		} else if (previousState == State.Produce) {
			System.out.println("Finished producing");
			state = State.Idle;
			return 0;
		}
		
		// if the factory can't produce (not enough karbonite)
		if (!gc.canProduceRobot(id, unitType)) {
			System.out.println("Can't produce, no Karbonite");
			state = State.Bankrupt;
			return 2;
		}
		
		System.out.println("producing");
		gc.produceRobot(id, unitType);
		state = State.Produce;
		return 1;
	}	
}
