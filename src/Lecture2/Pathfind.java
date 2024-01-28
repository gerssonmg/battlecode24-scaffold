package Lecture2;

import battlecode.common.*;

import java.util.HashSet;

public class Pathfind {

    static Direction direction;
    private static MapLocation prevDest = null;
    private static HashSet<MapLocation> line = null;
    private static int obstacleStartDist = 0;

    private static MapLocation positionActual = new MapLocation(-1, -1);
    private static int roundActual = 0;

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

        MapLocation[] crumbLocs = rc.senseNearbyCrumbs(-1);

        int round = rc.getRoundNum();

        if (crumbLocs.length > 0 && round <= GameConstants.SETUP_ROUNDS ) {
            moveTowards(rc, crumbLocs[0]);
        }

        if (rc.isMovementReady()) {
            if (direction != null && rc.canMove(direction)) rc.move(direction);
            else {

                if(positionActual.equals(rc.getLocation())){
                    roundActual+=1;
                } else {
                    positionActual = rc.getLocation();
                    roundActual=0;
                }

                if(roundActual<5){
                    whyBugNav(2, rc);
                } else {

                /**
                 Se passar 3 round, se se movimentar, forçar aleatorio.
                 Pode estar preso por agua
                 **/
                direction = Direction.allDirections()[RobotPlayer.random.nextInt(8)];
                if (rc.canMove(direction)) rc.move(direction);
                else forceMoveFillWater(rc);
                }

            }
        }
        /**
         * Se não se moveu, e pq deve estar empedido por agua.
         * Então pegue uma direção aleatoria e tente ir para la.
         * **/
        else {
            forceMoveFillWater(rc);
        }

    }

    private static int bugState = 0;
    private static MapLocation closestObstable = null;
    private static int closestObstableDist = 10000;
    private static Direction bugDir = null;

    public static void resetBug() {
        bugState = 0;
        closestObstable = null;
        closestObstableDist = 10000;
        bugDir = null;
    }

    public static void bugNavOne(RobotController rc, MapLocation destination) throws GameActionException {
        if (bugState == 0) {
            bugDir = rc.getLocation().directionTo(destination);
            if (rc.canMove(bugDir)) {
                rc.move(bugDir);
            } else {
                bugState = 1;
                closestObstable = null;
                closestObstableDist = 10000;
            }
        } else {
            if (rc.getLocation().equals(closestObstable)) {
                bugState = 0;
            }

            if (rc.getLocation().distanceSquaredTo(destination) < closestObstableDist) {
                closestObstableDist = rc.getLocation().distanceSquaredTo(destination);
                closestObstable = rc.getLocation();
            }

            for (int i = 0; i < 8; i++) {
                if (rc.canMove(bugDir)) {
                    rc.move(bugDir);
                    bugDir = bugDir.rotateRight();
                    bugDir = bugDir.rotateRight();
                    break;
                } else {
                    bugDir = bugDir.rotateLeft();
                }
            }
        }
    }

    public static void bugNav2(RobotController rc, MapLocation destination) throws GameActionException {

        if(!destination.equals(prevDest)){
            prevDest = destination;
            line = createLine(rc.getLocation(), destination);
        }

        for(MapLocation loc : line){
            rc.setIndicatorDot(loc, 255, 0,  0);;
        }

        if (bugState == 0) {
            bugDir = rc.getLocation().directionTo(destination);
            if (rc.canMove(bugDir)) {
                rc.move(bugDir);
            } else {
                bugState = 1;
                obstacleStartDist = rc.getLocation().distanceSquaredTo(destination);
                bugDir = rc.getLocation().directionTo(destination);
            }
        } else {
            if(line.contains(rc.getLocation()) && rc.getLocation().distanceSquaredTo(destination) < obstacleStartDist){
                bugState = 0;
            }

            for (int i = 0; i < 8; i++) {
                if (rc.canMove(bugDir)) {
                    rc.move(bugDir);
                    bugDir = bugDir.rotateRight();
                    bugDir = bugDir.rotateRight();
                    break;
                } else {
                    bugDir = bugDir.rotateLeft();
                }
            }
        }
    }

    public static void bugNavZero(RobotController rc, MapLocation destination) throws GameActionException {
        Direction bugDir = rc.getLocation().directionTo(destination);
        if (rc.canMove(bugDir)) {
            rc.move(bugDir);
        } else {
            for (int i = 0; i < 8; i++) {
                if (rc.canMove(bugDir)) {
                    rc.move(bugDir);
                    break;
                } else {
                    bugDir = bugDir.rotateLeft();
                }
            }
        }
    }

    public static void whyBugNav(int bugNav, RobotController rc) throws GameActionException {
        MapLocation destination = new MapLocation(4,8);

        if(bugNav == 0) {
            Pathfind.bugNavZero(rc, destination);
        }
        else if(bugNav == 1) {
            Pathfind.bugNavOne(rc, destination);
        }
        else {
            Pathfind.bugNav2(rc, destination);
        }
    }

    public static void forceMoveFillWater(RobotController rc) throws GameActionException {
        MapLocation positionActual = rc.getLocation();

        Direction randomDir = Direction.allDirections()[RobotPlayer.random.nextInt(8)];
        MapLocation positionAuxNearby = positionActual.add(randomDir);
        moveTowards(rc, positionAuxNearby);
    }


    private static HashSet<MapLocation> createLine(MapLocation a, MapLocation b) {
        HashSet<MapLocation> locs = new HashSet<>();
        int x = a.x, y = a.y;
        int dx = b.x - a.x;
        int dy = b.y - a.y;
        int sx = (int) Math.signum(dx);
        int sy = (int) Math.signum(dy);
        dx = Math.abs(dx);
        dy = Math.abs(dy);
        int d = Math.max(dx, dy);
        int r = d / 2;
        if (dx > dy) {
            for (int i = 0; i < d; i++) {
                locs.add(new MapLocation(x, y));
                x += sx;
                r += dy;
                if (r >= dx) {
                    locs.add(new MapLocation(x, y));
                    y += sy;
                    r -= dx;
                }
            }
        } else {
            for (int i = 0; i < d; i++) {
                locs.add(new MapLocation(x, y));
                y += sy;
                r += dx;
                if (r >= dy) {
                    locs.add(new MapLocation(x, y));
                    x += sx;
                    r -= dy;
                }
            }
        }
        locs.add(new MapLocation(x, y));
        return locs;
    }
}
