package com.badgerson.fable.trees;

import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public final class TrunkUtil {
  public static Vec3d bend(Vec3d input, float minRadians, float maxRadians, Random random) {

    return bendWithAngle(
        input, random.nextFloat() * MathHelper.TAU, minRadians, maxRadians, random);
  }

  public static Vec3d bendWithAngle(
      Vec3d input, float directionAngle, float minRadians, float maxRadians, Random random) {
    Vector3f original = input.toVector3f();

    float bendAngle = minRadians + random.nextFloat() * (maxRadians - minRadians);

    // Create a consistent orthogonal basis
    Vector3f u = new Vector3f();
    Vector3f v = new Vector3f();

    // Try to use world X-axis as reference, fall back to Z-axis if parallel
    Vector3f worldX = new Vector3f(1, 0, 0);
    Vector3f worldZ = new Vector3f(0, 0, 1);

    // Check if original is nearly parallel to worldX
    float dotX = Math.abs(original.dot(worldX) / original.length());
    Vector3f reference = (dotX > 0.9f) ? worldZ : worldX;

    // Create orthogonal basis
    original.cross(reference, u);
    u.normalize();

    original.cross(u, v);
    v.normalize();

    // Construct the perpendicular axis from directionAngle
    Vector3f perpAxis =
        new Vector3f(
            u.x * MathHelper.cos(directionAngle) + v.x * MathHelper.sin(directionAngle),
            u.y * MathHelper.cos(directionAngle) + v.y * MathHelper.sin(directionAngle),
            u.z * MathHelper.cos(directionAngle) + v.z * MathHelper.sin(directionAngle));

    // Apply the bend rotation
    Quaternionf bendRotation = new Quaternionf().rotateAxis(bendAngle, perpAxis);
    bendRotation.transform(original);
    return new Vec3d(original);
  }

  public static Vec3d bendTowardsUp(Vec3d current, float maxAngle) {
    Vector3f normalizedCurrent = current.toVector3f().normalize();
    Vector3f normalizedUp = new Vector3f(0, 1, 0);

    // Calculate angle between vectors
    float currentAngle = normalizedCurrent.angle(normalizedUp);

    // If already close enough, rotate fully to target
    if (currentAngle <= maxAngle || currentAngle < 1e-6f) {
      return new Vec3d(normalizedUp).multiply(current.length());
    }

    // Find rotation axis (cross product)
    Vector3f rotationAxis = new Vector3f(normalizedCurrent).cross(normalizedUp);

    // Handle edge case where vectors are opposite
    if (rotationAxis.lengthSquared() < 1e-6f) {
      // Find any perpendicular vector for rotation axis
      if (Math.abs(normalizedCurrent.x) < 0.9f) {
        rotationAxis.set(1, 0, 0);
      } else {
        rotationAxis.set(0, 0, 1);
      }
      rotationAxis.cross(normalizedCurrent).normalize();
    } else {
      rotationAxis.normalize();
    }

    // Create quaternion for rotation by maxAngle
    Quaternionf rotation = new Quaternionf().rotateAxis(maxAngle, rotationAxis);

    // Apply rotation and restore original magnitude
    Vector3f result = new Vector3f(normalizedCurrent);
    rotation.transform(result);
    return new Vec3d(result).multiply(current.length());
  }
}
