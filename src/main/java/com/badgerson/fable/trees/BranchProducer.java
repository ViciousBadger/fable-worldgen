package com.badgerson.fable.trees;

import com.badgerson.fable.trees.config.BranchBendingConfig;
import com.badgerson.fable.trees.config.BranchLayer;
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
      for (int i = 0; i < count; i++) {
        float branchDirAngle = (i / (float) count) * MathHelper.TAU;
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

    BranchWalker walker = new BranchWalker(startPos, startDir, length);
    while (walker.hasNext()) {
      BlockPos center = walker.next();
      for (BlockPos trunkPos : new TrunkSegment(center, thickness)) {
        blocks.add(new BranchProduct.TrunkBlock(trunkPos));
      }

      // bending.ifPresent(
      //     (bendingConfig) -> {
      //       walker.applyBending(bendingConfig, random);
      //     });

      // TODO: Place side branches .......
      // TODO: Place side foliage ....... (separately or together?)
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
