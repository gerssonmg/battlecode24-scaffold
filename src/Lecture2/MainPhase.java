package Lecture2;

import battlecode.common.*;
import java.nio.file.Path;
import java.util.*;

public class MainPhase {

    public static void runMainPhase(RobotController rc) throws GameActionException {

        //try to buy action and capturing upgrades
        // Tenta comprar uma atualizaçao
        if(rc.canBuyGlobal(GlobalUpgrade.ACTION)){
            rc.buyGlobal(GlobalUpgrade.ACTION);
        } if (rc.canBuyGlobal(GlobalUpgrade.CAPTURING)) {
            rc.buyGlobal(GlobalUpgrade.CAPTURING);
        }

        //attack enemies, prioritizing enemies that have your flag
        // Pega posição dos inimigos e se tiver com a bandeira aliada
        // ataca
        RobotInfo[] nearbyEnemies = rc.senseNearbyRobots(-1, rc.getTeam().opponent());
        for(RobotInfo robot : nearbyEnemies){
            if(robot.hasFlag()){
                Pathfind.moveTowards(rc, robot.getLocation());
                if(rc.canAttack(robot.getLocation())) rc.attack(robot.getLocation());
            }
        }

        // ataca
        for(RobotInfo robot : nearbyEnemies){
            if(rc.canAttack(robot.getLocation())) {
                rc.attack(robot.getLocation());
            }
        }
        //try to heal frindly robots
        for(RobotInfo robot: rc.senseNearbyRobots(-1, rc.getTeam())){
            if(rc.canHeal(robot.getLocation())) rc.heal(robot.getLocation());
        }

        /**
            Se não tiver com a flag, pega todas as flags

            Pega a flag mais proxima, e se move em direção dela
            Se poder pegar a flag, pega

            Se movimento para algum lugar
         **/
        if (!rc.hasFlag()) {
            ArrayList<MapLocation> flagLocs = new ArrayList<>();
            // cost 50
            FlagInfo[] enemyFlags = rc.senseNearbyFlags(-1, rc.getTeam().opponent());
            for (FlagInfo flag : enemyFlags) flagLocs.add(flag.getLocation());
            if (flagLocs.size() == 0) {
                // cost 100
                MapLocation[] broadcastLocs = rc.senseBroadcastFlagLocations();
                for (MapLocation flagLoc : broadcastLocs) flagLocs.add(flagLoc);
            }

            MapLocation closestFlag = findClosestLocation(rc.getLocation(), flagLocs);
            if (closestFlag != null) {
                Pathfind.moveTowards(rc, closestFlag);
                if(rc.canPickupFlag(closestFlag)) rc.pickupFlag(closestFlag);
            }

            Pathfind.explore(rc);

            /**
             * Se esta com uma flag inimiga, va para uma base aliada
             * **/
        } else {
            MapLocation[] spawnLocs = rc.getAllySpawnLocations();
            MapLocation closestSpawn = findClosestLocation(rc.getLocation(), Arrays.asList(spawnLocs));
            Pathfind.moveTowards(rc, closestSpawn);
        }
    }

    public static MapLocation findClosestLocation(MapLocation me, List<MapLocation> otherLocs) {
        MapLocation closest = null;

        int minDist = Integer.MAX_VALUE;
        for (MapLocation loc : otherLocs) {
            int dist = me.distanceSquaredTo(loc);
            if (dist < minDist) {
                minDist = dist;
                closest = loc;
            }
        }
        return closest;
    }


}
