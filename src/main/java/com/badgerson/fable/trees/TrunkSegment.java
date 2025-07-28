package com.badgerson.fable.trees;

import java.util.Iterator;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class TrunkSegment implements Iterator<BlockPos> {

  private static final double MOVE_INTERVAL = 1.0;

  private Quaternionf dir;
  private double length;

  private Vec3d current;
  private double distTraveled = 0.0;
  private BlockPos lastBlockPos = null;

  public TrunkSegment(Vec3d start, Quaternionf dir, double length) {
    this.current = start;
    this.dir = dir;
    this.length = length;
  }

  public boolean hasNext() {
    return distTraveled < length;
  }

  public BlockPos next() {
    if (distTraveled >= length) {
      return null;
    } else if (lastBlockPos == null) {
      // Special case for first point, don't move, ensure it is populated
      BlockPos rootPos = BlockPos.ofFloored(current);
      lastBlockPos = rootPos;
      return rootPos;
    } else {
      // Move until new blockpos..
      var nextBlockPos = lastBlockPos;
      while (nextBlockPos == lastBlockPos && distTraveled < length) {
        double toMove = Math.min(MOVE_INTERVAL, length - distTraveled);
        var m = dir.transform(new Vector3f(0f, 1f, 0f));
        current = current.add(new Vec3d(m.x, m.y, m.z).multiply(toMove));
        nextBlockPos = BlockPos.ofFloored(current);

        distTraveled += MOVE_INTERVAL;
      }
      return nextBlockPos;
    }
  }
}
