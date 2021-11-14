package splines;

import math.Angle;
import math.Point2D;
import math.Pose2D;
import math.Vector2D;

public class QuinticHermiteSpline {
    public Pose2D pose0, pose1;
    public Vector2D velocity0, velocity1;
    public Vector2D acceleration0, acceleration1;

    public QuinticHermiteSpline(Pose2D pose0, Pose2D pose1, Vector2D velocity0, Vector2D velocity1,
                                Vector2D acceleration0, Vector2D acceleration1) {
        this.pose0 = pose0;
        this.pose1 = pose1;
        this.velocity0 = velocity0;
        this.velocity1 = velocity1;
        this.acceleration0 = acceleration0;
        this.acceleration1 = acceleration1;

        pose0.print();
        pose1.print();
        velocity0.print();
        velocity1.print();
        acceleration0.print();
        acceleration1.print();

    }

    public QuinticHermiteSpline(Pose2D pose0, Pose2D pose1, Vector2D velocity0, Vector2D velocity1) {
        this(pose0, pose1, velocity0, velocity1, new Vector2D(), new Vector2D());
    }

    public QuinticHermiteSpline(Pose2D pose0, Pose2D pose1) {
        this(pose0, pose1,
                new Vector2D(new Angle(pose0.getAngle()), pose0.distance(pose1)),
                new Vector2D(new Angle(pose1.getAngle()), pose1.distance(pose0)));
    }

    public QuinticHermiteSpline(Pose2D pose0, Pose2D pose1, double curvature0, double curvature1) {
        this(pose0, pose1);
        this.acceleration0 = new Vector2D(new Angle(pose0.getAngle()),
                getAccelerationMagnitudeFromCurvature(curvature0, pose0.distance(pose1)));
        this.acceleration1 = new Vector2D(new Angle(pose1.getAngle()),
                getAccelerationMagnitudeFromCurvature(curvature1, pose1.distance(pose0)));
    }

    public Point2D getPoint(double t) {
        if(t >= 0 && t <= 1) {
            double h0 = -6 * t * t * t * t * t + 15 * t * t * t * t - 10 * t * t * t + 1;
            double h1 = -3 * t * t * t * t * t + 8 * t * t * t * t - 6 * t * t * t + t;
            double h2 = -(t * t * t * t * t) / 2 + 3 * t * t * t * t / 2 - 3 * t * t * t / 2 + t * t / 2;
            double h3 = t * t * t * t * t / 2 - t * t * t * t + t * t * t / 2;
            double h4 = -3 * t * t * t * t * t + 7 * t * t * t * t - 4 * t * t * t;
            double h5 = 6 * t * t * t * t * t - 15 * t * t * t * t + 10 * t * t * t;

            return getPointFromCoefficients(h0, h1, h2, h3, h4, h5);

        } else if (t < 0) {
            return new Point2D(pose0.getPosition());
        } else {
            return new Point2D(pose1.getPosition());
        }
    }

    public Point2D getDerivative(double t, int n) {
        switch(n) {
            case 1:
                double h0 = -30 * t * t * t * t + 60 * t * t * t - 30 * t * t;
                double h1 = -15 * t * t * t * t + 32 * t * t * t - 18 * t * t + 1;
                double h2 = -(5 * t * t * t * t) / 2 + 6 * t * t * t - 9 * t * t / 2 + t;
                double h3 = 5 * t * t * t * t / 2 - 4 * t * t * t + 3 * t * t / 2;
                double h4 = -15 * t * t * t * t + 28 * t * t * t - 12 * t * t;
                double h5 = 30 * t * t * t * t - 60 * t * t * t + 30 * t * t;

                return getPointFromCoefficients(h0, h1, h2, h3, h4, h5);
            default:
                return new Point2D(0, 0);
        }
    }

    public Point2D getPointFromCoefficients(double h0, double h1, double h2, double h3, double h4, double h5) {
        return new Point2D(
                h0 * pose0.getPosition().getX() + h1 * velocity0.getX() + h2 * acceleration0.getX() +
                        h3 * acceleration1.getX() + h4 * velocity1.getX() + h5 * pose1.getPosition().getX(),
                h0 * pose0.getPosition().getY() + h1 * velocity0.getY() + h2 * acceleration0.getY() +
                        h3 * acceleration1.getY() + h4 * velocity1.getY() + h5 * pose1.getPosition().getY());
    }


    public double getAccelerationMagnitudeFromCurvature(double curvature, double velocityMagnitude) {
        //a_rad = |V|^2 / R, curvature = 1/R
        return curvature * velocityMagnitude * velocityMagnitude;
    }
}
