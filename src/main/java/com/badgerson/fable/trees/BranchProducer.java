package com.badgerson.fable.trees;

import com.badgerson.fable.trees.config.BranchBendingConfig;
import com.badgerson.fable.trees.config.BranchLayer;
import com.badgerson.fable.trees.config.FloatBounds;
import com.google.common.collect.ImmutableList;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;
import org.joml.Vector3f;

public class BranchProducer {
  private final BranchLayer layer;
  private final Optional<BranchBendingConfig> bending;
  private final Vector3f startPos;
  private final Vector3f startDir;

  public BranchProducer(
      BranchLayer layer,
      Optional<BranchBendingConfig> bending,
      Vector3f startPos,
      Vector3f startDir) {
    this.layer = layer;
    this.bending = bending;
    this.startPos = startPos;
    this.startDir = startDir;
  }

  public static ImmutableList<BranchProducer> evenlySpread(
      BranchLayer layer,
      Optional<BranchBendingConfig> bending,
      Vector3f pos,
      Vector3f dir,
      Random random) {
    List<BranchProducer> result = new ArrayList<>();
    int count = layer.generateBranchCount(random);
    if (count > 1) {
      float randomStart = random.nextFloat() * MathHelper.TAU;
      for (int i = 0; i < count; i++) {
        float branchDirAngle = randomStart + (i / (float) count) * MathHelper.TAU;
        Vector3f newDir =
            TrunkUtil.bendInDirectionWithAngle(
                dir, branchDirAngle, layer.generateBranchBendAngle(random));
        // Vector3f newDir =
        //     TrunkUtil.bendInDirectionWithAngle(
        //         dir, branchDirAngle, 45f * MathHelper.RADIANS_PER_DEGREE);
        result.add(new BranchProducer(layer, bending, pos, newDir));
      }
    } else {
      result.add(new BranchProducer(layer, bending, pos, dir));
    }
    return ImmutableList.copyOf(result);
  }

  public BranchProduct produce(Random random) {
    List<BranchProduct.TrunkBlock> blocks = new ArrayList<>();
    List<BranchProduct.FoliageNode> foliage = new ArrayList<>();
    List<BranchProducer> subBranches = new ArrayList<>();

    int thickness = this.layer.thickness().orElse(1);
    float length = this.layer.length().generate(random);
    // float length = this.layer.length().max();
    Optional<BranchBendingConfig> bending = this.layer.bending().or(() -> this.bending);

    // Distribute side branches evenly along this branch..
    List<Float> sideBranchPoints = new ArrayList<>();
    layer
        .side()
        .ifPresent(
            sideConfig -> {
              int toDistribute = sideConfig.branches().generateBranchCount(random);
              float min = sideConfig.startPadding().orElse(0f);
              float max = length - sideConfig.endPadding().orElse(0f);

              if (min > max) {
                max = min;
              }

              for (int i = 0; i < toDistribute; i++) {
                sideBranchPoints.add(new FloatBounds(min, max).generate(random));
              }
            });

    BranchWalker walker = new BranchWalker(startPos, startDir, length, bending, random);
    while (walker.hasNext()) {
      BlockPos center = walker.next();
      for (BlockPos trunkPos : new TrunkSegment(center, thickness)) {
        blocks.add(new BranchProduct.TrunkBlock(trunkPos));
      }

      layer
          .side()
          .ifPresent(
              (sideConfig) -> {
                for (int i = sideBranchPoints.size() - 1; i > 0; i--) {
                  if (walker.getCurrentDist() > sideBranchPoints.get(i)) {
                    subBranches.add(
                        new BranchProducer(
                            sideConfig.branches(),
                            bending,
                            walker.getCurrentVec(),
                            TrunkUtil.bendWithAngle(
                                walker.getCurrentDir(),
                                sideConfig.branches().generateBranchBendAngle(random),
                                random)));

                    sideBranchPoints.remove(i);
                  }
                }
              });
    }

    Vector3f endPos = walker.getCurrentVec();
    Vector3f endDir = walker.getCurrentDir();
    layer
        .tip()
        .ifPresent(
            (tipConfig) -> {
              tipConfig
                  .branches()
                  .ifPresent(
                      (branchLayer) -> {
                        for (BranchProducer child :
                            evenlySpread(branchLayer, bending, endPos, endDir, random)) {
                          subBranches.add(child);
                        }
                      });
              tipConfig
                  .foliage()
                  .ifPresent(
                      (foliageConfig) -> {
                        foliage.add(
                            new BranchProduct.FoliageNode(
                                TrunkUtil.vecToBlock(endPos),
                                foliageConfig.radius(),
                                thickness % 2 == 0));
                      });
            });

    return new BranchProduct(
        ImmutableList.copyOf(blocks),
        ImmutableList.copyOf(foliage),
        ImmutableList.copyOf(subBranches));
  }
}
