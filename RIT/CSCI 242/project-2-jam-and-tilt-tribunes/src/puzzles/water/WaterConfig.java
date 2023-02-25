package puzzles.water;

import puzzles.common.solver.Configuration;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

/**
 * Configuration for solving the water puzzle.
 *
 * @author Ethan Hartman (theeman05)
 */
public class WaterConfig implements Configuration {
    private final List<Integer> bucketSizes, bucketFills; // Sizes of the buckets, and how much fluid is in the buckets.
    private final int desiredFill; // The fill we are trying to reach

    /**
     * Constructor for creating a WaterConfig object with the given fills and sizes.
     *
     * @param desiredFill Desired fill to reach
     * @param bucketSizes Sizes of the buckets
     * @param bucketFills How much fluid is in the buckets
     */
    public WaterConfig(int desiredFill, List<Integer> bucketSizes, List<Integer> bucketFills) {
        this.desiredFill = desiredFill;
        this.bucketSizes = bucketSizes;
        this.bucketFills = bucketFills;
    }

    /**
     * Constructor for creating a WaterConfig object with the given the desired fill and bucket sizes.
     * Fills are defaulted to empty.
     *
     * @param desiredFill Desired fill to reach
     * @param bucketSizes Sizes of the buckets
     */
    public WaterConfig(int desiredFill, List<Integer> bucketSizes) {
        this.desiredFill = desiredFill;
        this.bucketSizes = bucketSizes;
        bucketFills = new ArrayList<>();
        bucketSizes.forEach(ignored -> bucketFills.add(0));
    }

    /**
     * @return the fluid level in the buckets.
     */
    public List<Integer> getBucketFills(){
        return bucketFills;
    }

    /**
     * Create a new list, cloning values from bucketFills, and empty the bucket at the given index.
     *
     * @param idx to empty a bucket at.
     * @return a new list with an empty bucket at idx and cloned other values.
     */
    private List<Integer> emptyBucketAt(int idx){
        List<Integer> newFills = new ArrayList<>(bucketFills);
        newFills.set(idx, 0);
        return newFills;
    }

    /**
     * Create a new list, cloning values from bucketFills, and fill the bucket at the given index.
     *
     * @param idx to fill a bucket at.
     * @return a new list with a filled bucket at idx and cloned other values.
     */
    private List<Integer> fillBucketAt(int idx){
        List<Integer> newFills = new ArrayList<>(bucketFills);
        newFills.set(idx, bucketSizes.get(idx));
        return newFills;
    }

    /**
     * Create a new list, cloning values from bucketFills, and pouring water from one index into another.
     *
     * @param from bucket index to pour water from.
     * @param into bucket index to pour water into
     * @return the same fill if 'into' is full already or a new list with 'from' poured into 'into'
     */
    private List<Integer> pourWater(int from, int into){
        if (bucketFills.get(into).equals(bucketSizes.get(into))) return bucketFills; // Return the same fill
        List<Integer> newFills = new ArrayList<>(bucketFills);
        newFills.set(into, Math.min(bucketSizes.get(into), bucketFills.get(from) + bucketFills.get(into)));
        newFills.set(from, Math.abs(newFills.get(into) - bucketFills.get(into) - bucketFills.get(from)));
        return newFills;
    }

    /**
     * Is any of the buckets equal to the desired fill?
     *
     * @return true if the configuration is a puzzle's solution; false, otherwise
     */
    @Override
    public boolean isSolution() {
        for (int bucketFill : bucketFills)
            if (bucketFill == desiredFill)
                return true;
        return false;
    }

    /**
     * Get the collection of neighbors from the current configuration.
     * Neighbors of all buckets can be:
     *  - filling the bucket itself
     *  - emptying the bucket itself
     *  - pouring the bucket into the other buckets
     *
     * @return All the neighbors
     */
    @Override
    public Collection<Configuration> getNeighbors() {
        List<Configuration> neighbors = new LinkedList<>();
        for (int i = 0; i < bucketFills.size(); i++){
            if (bucketFills.get(i) == 0) //  Only want to fill empty buckets
                neighbors.add(new WaterConfig(desiredFill, bucketSizes, fillBucketAt(i)));
            else{  // Not an empty bucket so empty and try pouring
                neighbors.add(new WaterConfig(desiredFill, bucketSizes, emptyBucketAt(i)));
                for (int j = 0; j < bucketFills.size(); j++)
                    if (i != j) neighbors.add(new WaterConfig(desiredFill, bucketSizes, pourWater(i, j)));
            }
        }

        return neighbors;
    }

    /**
     * If the current WaterConfig equals the other.
     *
     * @param other to test if both objects are equal.
     * @return true if the two objects have the same fills, bucket sizes, and bucket fills.
     */
    @Override
    public boolean equals(Object other){
        if (other instanceof WaterConfig casted)
            return desiredFill == casted.desiredFill && bucketSizes.equals(casted.bucketSizes) &&
                    bucketFills.equals(casted.bucketFills);
        return false;
    }

    /**
     * @return the hash code for this object.
     */
    @Override
    public int hashCode(){
        return desiredFill + bucketSizes.hashCode() + bucketFills.hashCode();
    }
}
