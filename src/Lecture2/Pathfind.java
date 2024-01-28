package Lecture2;

import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.RobotController;

public class Pathfind {

    static Direction direction;

    public static void moveTowards(RobotController rc, MapLocation loc) throws GameActionException {

        Direction dir = rc.getLocation().directionTo(loc);
        if (rc.canMove(dir)) rc.move(dir);
        else if (rc.canFill(rc.getLocation().add(dir))) rc.fill(rc.getLocation().add(dir));
        else {
            Direction randomDir = Direction.allDirections()[RobotPlayer.random.nextInt(8)];
            if (rc.canMove(randomDir)) rc.move(randomDir);
        }
    }

    public static void explore(RobotController rc) throws GameActionException {
        //
        //



        if (rc.isMovementReady()) {
            if (direction != null && rc.canMove(direction)) rc.move(direction);
            else {
                direction = Direction.allDirections()[RobotPlayer.random.nextInt(8)];
                if (rc.canMove(direction)) rc.move(direction);
                else forceMoveFillWater(rc);
            }
        }
        /**
         * Se não se moveu, e pq deve estar empedido por agua.
         * Então pegue uma direção aleatoria e tente ir para la.
         * **/
        else {
            forceMoveFillWater(rc);
        }

        MapLocation[] crumbLocs = rc.senseNearbyCrumbs(-1);
        if (crumbLocs.length > 0) {
            moveTowards(rc, crumbLocs[0]);
        }
    }

    public static void forceMoveFillWater(RobotController rc) throws GameActionException {
        MapLocation positionActual = rc.getLocation();

        Direction randomDir = Direction.allDirections()[RobotPlayer.random.nextInt(8)];
        MapLocation positionAuxNearby = positionActual.add(randomDir);
        moveTowards(rc, positionAuxNearby);
    }
}
