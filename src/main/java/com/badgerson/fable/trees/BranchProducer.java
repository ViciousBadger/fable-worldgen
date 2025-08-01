package com.badgerson.fable.trees;

import com.badgerson.fable.trees.config.BranchBendingConfig;
import com.badgerson.fable.trees.config.BranchLayer;
import com.google.common.collect.ImmutableList;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import net.minecraft.util.math.BlockPos;
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

  public ImmutableList<BranchProduct> produceMany(Random random) {
    List<BranchProduct> output = new ArrayList<>();
    int count = layer.generateBranchCount(random);
    for (int i = 0; i < count; i++) {
      output.add(produceOne(random));
    }
    return ImmutableList.copyOf(output);
  }

  public BranchProduct produceOne(Random random) {
    List<BranchProduct.TrunkBlock> blocks = new ArrayList<>();
    List<BranchProduct.FoliageNode> foliage = new ArrayList<>();
    List<BranchProducer> subBranches = new ArrayList<>();

    int thickness = this.layer.thickness().orElse(1);
    float length = this.layer.length().generate(random);
    Vector3f startDir = this.startDir;

    BranchWalker walker = new BranchWalker(startPos, startDir, length);
    while (walker.hasNext()) {
      BlockPos center = walker.next();
      for (BlockPos trunkPos : new TrunkPieceProducer(center, thickness)) {
        blocks.add(new BranchProduct.TrunkBlock(trunkPos));
      }
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
                        subBranches.add(new BranchProducer(branchLayer, bending, endPos, endDir));
                      });
              tipConfig
                  .foliage()
                  .ifPresent(
                      (foliageConfig) -> {
                        foliage.add(
                            new BranchProduct.FoliageNode(
                                TrunkUtil.vecToBlock(endPos), foliageConfig.radius()));
                      });
            });

    return new BranchProduct(
        ImmutableList.copyOf(blocks),
        ImmutableList.copyOf(foliage),
        ImmutableList.copyOf(subBranches));
  }
}
