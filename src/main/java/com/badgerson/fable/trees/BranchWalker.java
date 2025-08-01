package com.badgerson.fable.trees;

import com.badgerson.fable.trees.config.BranchBendingConfig;
import java.util.Iterator;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import org.joml.Vector3f;

public class BranchWalker implements Iterator<BlockPos> {
  // Adjust to increase/decrease chance of branch blocks being only diagonally connected
  private static final float STEP_SIZE = 1.0f;

  private Vector3f pos;
  private Vector3f dir;
  private float targetDist;
  private float currentDist = 0f;
  private BlockPos lastBlockPos = null;

  public BranchWalker(Vector3f startPos, Vector3f startDir, float targetDist) {
    this.pos = startPos;
    this.dir = startDir;
    this.targetDist = targetDist;
  }

  public boolean hasNext() {
    return currentDist < targetDist;
  }

  public BlockPos next() {
    // Move until new blockpos..
    var nextBlockPos = lastBlockPos;
    while (nextBlockPos.equals(lastBlockPos) && currentDist < targetDist) {
      float toMove = Math.min(STEP_SIZE, targetDist - currentDist);
      pos = pos.add(dir.mul(toMove));
      nextBlockPos = BlockPos.ofFloored(new Vec3d(pos));

      currentDist += toMove;
    }
    return nextBlockPos;
  }

  public void applyBending(BranchBendingConfig config, Random random) {
    if (random.nextFloat() < config.bendChance()) {
      dir =
          TrunkUtil.bend(
              dir,
              config.minBendAmount() * MathHelper.RADIANS_PER_DEGREE,
              config.maxBendAmount() * MathHelper.RADIANS_PER_DEGREE,
              random);
    } else {
      dir = TrunkUtil.bendTowardsUp(dir, config.straightenAmount() * MathHelper.RADIANS_PER_DEGREE);
    }
  }

  public Vector3f getCurrentVec() {
    return new Vector3f(pos);
  }

  public Vector3f getCurrentDir() {
    return new Vector3f(dir);
  }
}
