package robots;

import api.*;
import map.Map;
//import bc.*;

/**
 * Represents a healer robot in the game
 * @author virsain
 */
public class Healer extends AbstractRobot {
	public Healer(int i, GameController g, Map map, MapLocation location) {
		super(i, g, map, location);
		occupantType = UnitType.Healer;
	}
}