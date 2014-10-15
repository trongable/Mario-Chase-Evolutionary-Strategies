package de.manualoverri.mariochase.gamelogic;

/**
 * User: Trong
 * Date: 7/15/2014
 * Time: 10:38 PM
 */
public class Point {

    private double x;
    private double y;

    public Point(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public void addToX(double x) {
        this.x += x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public void addToY(double y) {
        this.y += y;
    }

    public double distanceFrom(Point point) {
        return Math.sqrt(Math.pow(point.x - this.x, 2) + Math.pow(point.y - this.y, 2));
    }

    public double getSlope(Point point) {
        if (point.getX() - this.x == 0) {
            return Double.NaN;
        }

        return (point.getY() - this.y) / (point.getX() - this.x);
    }

    public double getDegreesTo(Point point) {
        return Math.toDegrees(Math.atan(this.getSlope(point))) % 360;
    }

    public Point getPointWithSlopeAndDistance(double slope, double distance) {
        if (slope == Double.NaN) {

        }

        double newX = distance * Math.cos(Math.atan(slope)) + this.x;
        double newY = distance * Math.sin(Math.atan(slope)) + this.y;

        return new Point(newX, newY);
    }

    @Override
    public String toString() {
        return String.format("(%f, %f)", x, y);
    }

    public boolean equalsWithPrecision(Point p, double precision) {
        return (Math.round(x * Math.pow(10, precision)) / Math.pow(10, precision)) ==
                (Math.round(p.x * Math.pow(10, precision)) / Math.pow(10, precision)) &&
                (Math.round(y * Math.pow(10, precision)) / Math.pow(10, precision)) ==
                        (Math.round(p.y * Math.pow(10, precision)) / Math.pow(10, precision));
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Point))
            return false;
        if (obj == this)
            return true;

        Point p = (Point) obj;

        return x == p.x && y == p.y;
    }
}
