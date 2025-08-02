package com.badgerson.fable.trees;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public final class TrunkUtil {
  public static Vector3f bend(Vector3f input, float minRadians, float maxRadians, Random random) {
    return bendInDirection(
        input, random.nextFloat() * MathHelper.TAU, minRadians, maxRadians, random);
  }

  public static Vector3f bendInDirection(
      Vector3f input, float directionAngle, float minRadians, float maxRadians, Random random) {
    float bendAngle = minRadians + random.nextFloat() * (maxRadians - minRadians);
    return bendInDirectionWithAngle(input, random.nextFloat() * MathHelper.TAU, bendAngle);
  }

  public static Vector3f bendWithAngle(Vector3f input, float bendAngle, Random random) {
    return bendInDirectionWithAngle(input, random.nextFloat() * MathHelper.TAU, bendAngle);
  }

  public static Vector3f bendInDirectionWithAngle(
      Vector3f input, float directionAngle, float bendAngle) {
    Vector3f outputVec = new Vector3f(input);

    // Create a consistent orthogonal basis
    Vector3f u = new Vector3f();
    Vector3f v = new Vector3f();

    // Try to use world X-axis as reference, fall back to Z-axis if parallel
    Vector3f worldX = new Vector3f(1, 0, 0);
    Vector3f worldZ = new Vector3f(0, 0, 1);

    // Check if original is nearly parallel to worldX
    float dotX = Math.abs(outputVec.dot(worldX) / outputVec.length());
    Vector3f reference = (dotX > 0.9f) ? worldZ : worldX;

    // Create orthogonal basis
    outputVec.cross(reference, u);
    u.normalize();

    outputVec.cross(u, v);
    v.normalize();

    // Construct the perpendicular axis from directionAngle
    Vector3f perpAxis =
        new Vector3f(
            u.x * MathHelper.cos(directionAngle) + v.x * MathHelper.sin(directionAngle),
            u.y * MathHelper.cos(directionAngle) + v.y * MathHelper.sin(directionAngle),
            u.z * MathHelper.cos(directionAngle) + v.z * MathHelper.sin(directionAngle));

    // Apply the bend rotation
    Quaternionf bendRotation = new Quaternionf().rotateAxis(bendAngle, perpAxis);
    bendRotation.transform(outputVec);
    return outputVec;
  }

  public static Vector3f bendTowardsUp(Vector3f input, float maxAngle) {
    Vector3f output = input.normalize(new Vector3f());
    Vector3f normalizedUp = new Vector3f(0, 1, 0);

    // Calculate angle between vectors
    float currentAngle = output.angle(normalizedUp);

    // If already close enough, rotate fully to target
    if (currentAngle <= maxAngle || currentAngle < 1e-6f) {
      return output.mul(input.length());
    }

    // Find rotation axis (cross product)
    Vector3f rotationAxis = output.cross(normalizedUp, new Vector3f());

    // Handle edge case where vectors are opposite
    if (rotationAxis.lengthSquared() < 1e-6f) {
      // Find any perpendicular vector for rotation axis
      if (Math.abs(output.x) < 0.9f) {
        rotationAxis.set(1, 0, 0);
      } else {
        rotationAxis.set(0, 0, 1);
      }
      rotationAxis.cross(output).normalize();
    } else {
      rotationAxis.normalize();
    }

    // Create quaternion for rotation by maxAngle
    Quaternionf rotation = new Quaternionf().rotateAxis(maxAngle, rotationAxis);

    // Apply rotation and restore original magnitude
    Vector3f result = new Vector3f(output);
    rotation.transform(result);
    return output.mul(input.length());
  }

  public static BlockPos vecToBlock(Vector3f vec) {
    return new BlockPos((int) vec.x, (int) vec.y, (int) vec.z);
  }
}
