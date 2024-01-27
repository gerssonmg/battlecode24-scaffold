package Lecture2;

import battlecode.common.*;

import java.util.Random;

/**
 * RobotPlayer is the class that describes your main robot strategy.
 * The run() method inside this class is like your main function: this is what we'll call once your robot
 * is created!
 */
public strictfp class RobotPlayer {

    public static Random random =null;

    public static void run(RobotController rc) throws GameActionException {
        while (true) {
            try {
                if(random == null) random = new Random(rc.getID());
                trySpawn(rc);
                if(rc.isSpawned()){
                    // check round num and call setup / main phase logic
                    int round = rc.getRoundNum();
                    if(round <= GameConstants.SETUP_ROUNDS) Setup.runSetup(rc);
                    else MainPhase.runMainPhase(rc);
                }
            } catch (GameActionException e) {
                // Oh no! It looks like we did something illegal in the Battlecode world. You should
                // handle GameActionExceptions judiciously, in case unexpected events occur in the game
                // world. Remember, uncaught exceptions cause your robot to explode!
                e.printStackTrace();

            } catch (Exception e) {
                // Oh no! It looks like our code tried to do something bad. This isn't a
                // GameActionException, so it's more likely to be a bug in our code.
                e.printStackTrace();

            } finally {
                // Signify we've done everything we want to do, thereby ending our turn.
                // This will make our code wait until the next turn, and then perform this loop again.
                Clock.yield();
            }
            // End of loop: go back to the top. Clock.yield() has ended, so it's time for another turn!
        }
        // Your code should never reach here (unless it's intentional)! Self-destruction imminent...
    }

    private static void trySpawn(RobotController rc) throws GameActionException {
        MapLocation[] locations = rc.getAllySpawnLocations();
        for(MapLocation loc : locations){
            if(rc.canSpawn(loc)) {
                rc.spawn(loc);
                break;
            }
        }
    }

}
