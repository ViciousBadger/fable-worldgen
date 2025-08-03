package com.badgerson.fable.trees;

import com.badgerson.fable.trees.config.BranchBendingConfig;
import java.util.Iterator;
import java.util.Optional;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;
import org.joml.Vector3f;

public class BranchWalker implements Iterator<BlockPos> {
  // Adjust to increase/decrease chance of branch blocks being only diagonally connected
  private static final float STEP_SIZE = 1.0f;

  private Vector3f pos;
  private Vector3f dir;
  private final float targetDist;
  private float currentDist = 0f;
  private BlockPos lastBlockPos = null;
  private final Optional<BranchBendingConfig> bending;
  private final Random random;

  public BranchWalker(
      Vector3f startPos,
      Vector3f startDir,
      float targetDist,
      Optional<BranchBendingConfig> bending,
      Random random) {
    this.pos = new Vector3f(startPos);
    this.dir = new Vector3f(startDir);
    this.targetDist = targetDist;
    this.bending = bending;
    this.random = random;
  }

  public boolean hasNext() {
    return currentDist < targetDist;
  }

  public BlockPos next() {
    // Move until new blockpos..
    var nextBlockPos = lastBlockPos;
    while (nextBlockPos == null
        || (nextBlockPos.equals(lastBlockPos) && currentDist < targetDist)) {
      float toMove = Math.min(STEP_SIZE, targetDist - currentDist);
      pos.add(dir.mul(toMove, new Vector3f()));
      nextBlockPos = TrunkUtil.vecToBlock(pos);

      currentDist += toMove;

      // Apply bending to dir every move
      bending.ifPresent(
          (config) -> {
            dir =
                TrunkUtil.bendWithAngle(
                    dir,
                    config.sideways().generate(random) * MathHelper.RADIANS_PER_DEGREE,
                    random);
            dir =
                TrunkUtil.bendTowardsUp(
                    dir, config.upwards().generate(random) * MathHelper.RADIANS_PER_DEGREE);
          });
    }
    return nextBlockPos;
  }

  public float getCurrentDist() {
    return currentDist;
  }

  public Vector3f getCurrentVec() {
    return new Vector3f(pos);
  }

  public Vector3f getCurrentDir() {
    return new Vector3f(dir);
  }
}
