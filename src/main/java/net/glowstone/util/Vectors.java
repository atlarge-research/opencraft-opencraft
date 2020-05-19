package net.glowstone.util;

import org.bukkit.util.Vector;

/**
 * This class adds utility functions for the bukkit vector class
 */
public class Vectors {

    /**
     * Floors every parameter in the vector
     *
     * @param vector the vector that will be floored
     * @return A new floored vector
     */
    public static Vector floor(Vector vector) {
        return new Vector(
                Math.floor(vector.getX()),
                Math.floor(vector.getY()),
                Math.floor(vector.getZ())
        );
    }

    /**
     * Ceils every parameter in the vector
     *
     * @param vector the vector that will be ceiled
     * @return A new ceiled vector
     */
    public static Vector ceil(Vector vector) {
        return new Vector(
                Math.ceil(vector.getX()),
                Math.ceil(vector.getY()),
                Math.ceil(vector.getZ())
        );
    }

    /**
     * Projects a vector onto another vector
     *
     * @param vector The vector that will be projected
     * @param normal The vector on which the projection will occur
     * @return A projected vector
     */
    public static Vector project(Vector vector, Vector normal) {
        double dot = vector.dot(normal);
        return normal.clone().multiply(dot);
    }

    /**
     * An equals method that allows matching two vectors per parameter with a certain tolerance
     *
     * @param vector one of the vectors to compare
     * @param otherVector one of the vectors to compare
     * @param tolerance an allowed tolerance
     * @return A boolean that is true if the vectors are equal within tolerance
     */
    public static boolean equals(Vector vector, Vector otherVector, double tolerance) {
        double dx = Math.abs(vector.getX() - otherVector.getX());

        if (dx >= tolerance) {
            return false;
        }

        double dy = Math.abs(vector.getY() - otherVector.getY());

        if (dy >= tolerance) {
            return false;
        }

        double dz = Math.abs(vector.getZ() - otherVector.getZ());

        if (dz >= tolerance) {
            return false;
        }

        return true;
    }

    /**
     * An equals method that allows matching two vectors per parameter with a certain tolerance prefilled
     *
     * @param vector one of the vectors to compare
     * @param otherVector one of the vectors to compare
     * @return A boolean that is true if the vectors are equal within tolerance
     */
    public static boolean equals(Vector vector, Vector otherVector) {
        return equals(vector, otherVector, Double.MIN_VALUE);
    }
}
