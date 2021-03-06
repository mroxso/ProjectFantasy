package info.gridworld.actor;

import GeneticAlgorithm.Config;
import info.gridworld.grid.Grid;
import info.gridworld.grid.Location;

import java.awt.*;
import java.util.ArrayList;

/**
 * An <code>Actor</code> is an entity with a color and direction that can act.
 * <br />
 * The API of this class is testable on the AP CS A and AB exams.
 */
public class Actor extends ActorBase {
    private Grid<Actor> grid;
    private Location location;
    private int direction;
    private Color color;


    /**
     * Gets the color of this actor.
     *
     * @return the color of this actor
     */
    public Color getColor() {
        return color;
    }

    /**
     * Sets the color of this actor.
     *
     * @param newColor the new color
     */
    public void setColor(Color newColor) {
        color = newColor;
    }

    /**
     * Gets the current direction of this actor.
     *
     * @return the direction of this actor, an angle between 0 and 359 degrees
     */
    public int getDirection() {
        return direction;
    }

    /**
     * Sets the current direction of this actor.
     *
     * @param newDirection the new direction. The direction of this actor is set
     *                     to the angle between 0 and 359 degrees that is equivalent to
     *                     <code>newDirection</code>.
     */
    public void setDirection(int newDirection) {
        direction = newDirection % Location.FULL_CIRCLE;
        if (direction < 0)
            direction += Location.FULL_CIRCLE;
    }

    /**
     * Gets the grid in which this actor is located.
     *
     * @return the grid of this actor, or <code>null</code> if this actor is
     * not contained in a grid
     */
    public Grid<Actor> getGrid() {
        return grid;
    }

    /**
     * Gets the location of this actor.
     *
     * @return the location of this actor, or <code>null</code> if this actor is
     * not contained in a grid
     */
    public Location getLocation() {
        return location;
    }

    /**
     * Puts this actor into a grid. If there is another actor at the given
     * location, it is removed. <br />
     * Precondition: (1) This actor is not contained in a grid (2)
     * <code>loc</code> is valid in <code>gr</code>
     *
     * @param gr  the grid into which this actor should be placed
     * @param loc the location into which the actor should be placed
     */
    public void putSelfInGrid(Grid<Actor> gr, Location loc) {
        if (grid != null)
            throw new IllegalStateException(
                    "This actor is already contained in a grid.");

        Actor actor = gr.get(loc);
        if (actor != null)
            actor.removeSelfFromGrid();
        gr.put(loc, this);
        grid = gr;
        location = loc;
    }

    /**
     * Removes this actor from its grid. <br />
     * Precondition: This actor is contained in a grid
     */
    public void removeSelfFromGrid() {
        if (grid == null)
            throw new IllegalStateException(
                    "This actor is not contained in a grid.");
        if (grid.get(location) != this)
            throw new IllegalStateException(
                    "The grid contains a different actor at location "
                            + location + ".");

        grid.remove(location);
        grid = null;
        location = null;
    }

    /**
     * Moves this actor to a new location. If there is another actor at the
     * given location, it is removed. <br />
     * Precondition: (1) This actor is contained in a grid (2)
     * <code>newLocation</code> is valid in the grid of this actor
     *
     * @param newLocation the new location
     */
    public void moveTo(Location newLocation) {
        boolean isKilled = spendEnergyAndCheck(Config.moveCostEnergy);
        if (isKilled) {
            return;
        }

        if (grid == null)
            throw new IllegalStateException("This actor is not in a grid.");
        if (grid.get(location) != this)
            throw new IllegalStateException(
                    "The grid contains a different actor at location "
                            + location + ".");
        if (!grid.isValid(newLocation))
            throw new IllegalArgumentException("Location " + newLocation
                    + " is not valid.");

        if (newLocation.equals(location))
            return;
        grid.remove(location);
        Actor other = grid.get(newLocation);
        if (other != null)
            other.removeSelfFromGrid();
        location = newLocation;
        grid.put(location, this);
    }

    protected boolean spendEnergyAndCheck(int type) {
        boolean isKilled = false;
        int energy = getEnergyLevel();
        if (type == Config.encode.eat) {
            energy -= Config.eatCostEnergy;
        } else {
            energy -= Config.moveCostEnergy;
        }
        setEnergyLevel(energy);

        if (energy <= 0) {
            Location loc = getLocation();
            if (isHoldInHand()){
                justPutThingDown(loc);
            }
            isKilled = true;
            removeSelfFromGrid();
        }
        return isKilled;
    }

    protected void justPutThingDown(Location oldLoc) {
/*        if (!isHoldInHand()){
            return;
        }
        if (isStrawberry()) {
            getFormerStrawberry().putSelfInGrid(getGrid(), oldLoc);
            removeSelfFromGrid();
            cleanHands();
        } else if (isMushroom()) {
            getFormerMushroom().putSelfInGrid(getGrid(), oldLoc);
            removeSelfFromGrid();
            cleanHands();
        } else if (isCreature()) {
            getFormerCreature().putSelfInGrid(getGrid(), oldLoc);
            removeSelfFromGrid();
            cleanHands();
        }*/
    }

    /**
     * Gets the actors for processing. Implemented to return the actors that
     * occupy neighboring grid locations. Override this method in subclasses to
     * look elsewhere for actors to process.<br />
     * Postcondition: The state of all actors is unchanged.
     *
     * @return a list of actors that this critter wishes to process.
     */
    public ArrayList<Actor> getActors() {
        return getGrid().getNeighbors(getLocation());
    }

    /**
     * Reverses the direction of this actor. Override this method in subclasses
     * of <code>Actor</code> to define types of actors with different behavior
     */
    public void act() {
        //setDirection(getDirection() + Location.HALF_CIRCLE);
    }

    /**
     * Creates a string that describes this actor.
     *
     * @return a string with the location, direction, and color of this actor
     */
    public String toString() {
        return getClass().getName() + "[location=" + location + ",direction="
                + direction + ",color=" + color + "]";
    }

    /**
     * Check if certain types have shown nearby.
     *
     * @param actorsList List of nearby actors.
     * @return 4 digits int[]; [0]Mushroom; [1]Strawberry; [2]Creature; [3]Monster
     */
    public int[] findNearbyActors(ArrayList<Actor> actorsList) {
        int[] typeList = {0, 0, 0, 0};
        for (Actor a : actorsList) {
            if (a instanceof Mushroom) {
                typeList[0] = 1;
            }
            if (a instanceof Strawberry) {
                typeList[1] = 1;
            }
            if (a instanceof Creature) {
                typeList[2] = 1;
            }
            if (a instanceof Monster) {
                typeList[3] = 1;
            }
        }
        return typeList;
    }


    /**
     * Gets a list of possible locations for the next move. These locations must
     * be valid in the grid of this critter. Implemented to return the empty
     * neighboring locations. Override this method in subclasses to look
     * elsewhere for move locations.<br />
     * Postcondition: The state of all actors is unchanged.
     *
     * @return a list of possible locations for the next move
     */
    public ArrayList<Location> getMoveLocations() {
        return getGrid().getEmptyAdjacentLocations(getLocation());
    }

    /**
     * Selects the location for the next move. Implemented to randomly pick one
     * of the possible locations, or to return the current location if
     * <code>locs</code> has size 0. Override this method in subclasses that
     * have another mechanism for selecting the next move location. <br />
     * Postcondition: (1) The returned location is an element of
     * <code>locs</code>, this critter's current location, or
     * <code>null</code>. (2) The state of all actors is unchanged.
     *
     * @param locs the possible locations for the next move
     * @return the location that was selected for the next move.
     */
    public Location selectMoveLocation(ArrayList<Location> locs) {
        int n = locs.size();
        if (n == 0)
            return getLocation();
        int r = (int) (Math.random() * n);
        return locs.get(r);
    }

    /**
     * Moves this critter to the given location <code>loc</code>, or removes
     * this critter from its grid if <code>loc</code> is <code>null</code>.
     * An actor may be added to the old location. If there is a different actor
     * at location <code>loc</code>, that actor is removed from the grid.
     * Override this method in subclasses that want to carry out other actions
     * (for example, turning this critter or adding an occupant in its previous
     * location). <br />
     * Postcondition: (1) <code>getLocation() == loc</code>. (2) The state of
     * all actors other than those at the old and new locations is unchanged.
     *
     * @param loc the location to move to
     */
    public void makeMove(Location loc) {
        if (loc == null)
            removeSelfFromGrid();
        else
            moveTo(loc);
    }

    public void randomAct() {

        ArrayList<Location> locs = getMoveLocations();
        Location newLoc = selectMoveLocation(locs);
        makeMove(newLoc);
    }

    /**
     * deal with the situation that one wants to cross another
     * @param newLocation target location
     */
    public void handleCrossLocation(Location newLocation){
        grid.remove(location);
        Actor other = grid.get(newLocation);
        if (other != null)
            other.removeSelfFromGrid();
        location = newLocation;
        grid.put(location, this);
    }

}