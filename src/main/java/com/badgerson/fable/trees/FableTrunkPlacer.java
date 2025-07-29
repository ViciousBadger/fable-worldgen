package com.badgerson.fable.trees;

import com.badgerson.fable.Fable;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.TestableWorld;
import net.minecraft.world.gen.feature.TreeFeatureConfig;
import net.minecraft.world.gen.foliage.FoliagePlacer;
import net.minecraft.world.gen.foliage.FoliagePlacer.TreeNode;
import net.minecraft.world.gen.trunk.TrunkPlacer;
import net.minecraft.world.gen.trunk.TrunkPlacerType;

public class FableTrunkPlacer extends TrunkPlacer {

  private FableTrunkConfig config;

  public static final MapCodec<FableTrunkPlacer> CODEC =
      RecordCodecBuilder.mapCodec(
          instance ->
              fillTrunkPlacerFields(instance)
                  .and(
                      FableTrunkConfig.CODEC
                          .fieldOf("config")
                          .forGetter(
                              (placer) -> {
                                return placer.config;
                              }))
                  .apply(instance, FableTrunkPlacer::new));

  public FableTrunkPlacer(
      int baseHeight, int firstRandomHeight, int secondRandomHeight, FableTrunkConfig config) {
    super(baseHeight, firstRandomHeight, secondRandomHeight);
    this.config = config;
  }

  @Override
  protected TrunkPlacerType<?> getType() {
    return Fable.TRUNK_PLACER;
  }

  @Override
  public List<TreeNode> generate(
      TestableWorld world,
      BiConsumer<BlockPos, BlockState> replacer,
      Random random,
      int height,
      BlockPos startPos,
      TreeFeatureConfig config) {
    setToDirt(world, replacer, random, startPos.down(), config);

    Vec3d initialPosition = Vec3d.of(startPos.down()).add(0.5, 0.5, 0.5);
    Vec3d initialDirection = new Vec3d(0.0, 1.0, 0.0);

    double trunkSegLength = 3.0;
    int trunkSegCount = (int) (height / trunkSegLength);

    List<FoliagePlacer.TreeNode> treeNodes = new ArrayList<FoliagePlacer.TreeNode>();

    buildBranch(
        world,
        replacer,
        random,
        config,
        initialPosition,
        initialDirection,
        trunkSegLength,
        trunkSegCount,
        1, // Foliage
        0, // Recursion depth
        false, // Minor
        treeNodes);

    return treeNodes;
  }

  private Vec3d buildBranch(
      TestableWorld world,
      BiConsumer<BlockPos, BlockState> replacer,
      Random random,
      TreeFeatureConfig config,
      Vec3d startPos,
      Vec3d startDir,
      double segmentLength,
      int segmentCount,
      int foliageRadius,
      int recursionDepth,
      boolean isMinor,
      List<FoliagePlacer.TreeNode> treeNodes) {
    Vec3d here = new Vec3d(startPos.x, startPos.y, startPos.z);
    Vec3d dir = startDir;

    for (int i = 0; i < segmentCount; i++) {
      TrunkSegment seg = new TrunkSegment(here, dir, segmentLength);

      while (seg.hasNext()) {
        this.getAndSetState(world, replacer, random, seg.next(), config);
      }
      here = seg.getCurrentVec();
      if (random.nextFloat() > this.config.bendChance()) {
        dir =
            TrunkUtil.bend(
                dir,
                MathHelper.RADIANS_PER_DEGREE * this.config.minBendAmount(),
                MathHelper.RADIANS_PER_DEGREE * this.config.maxBendAmount(),
                random);
      }
      dir =
          TrunkUtil.bendTowardsUp(
              dir, this.config.straightenAmount() * MathHelper.RADIANS_PER_DEGREE);

      // "Sideways" branches along the base trunk
      if (!isMinor && random.nextDouble() > this.config.sideBranchChance()) {

        buildBranch(
            world,
            replacer,
            random,
            config,
            //
            here,
            TrunkUtil.bend(
                dir, this.config.sideBranchMinAngle(), this.config.sideBranchMaxAngle(), random),
            segmentLength,
            (int) (this.config.sideBranchLength() / segmentLength),
            0, // Smaller foliage clumps (?)
            recursionDepth + 1,
            true, // Always minor branches
            treeNodes);
      }
    }

    // "Upwards" branches at the end of trunks
    int branchCount = random.nextBetween(this.config.minUpBranches(), this.config.maxUpBranches());
    int upSegmentCount = (int) (segmentCount * this.config.upBranchLengthFactor());
    if (!isMinor && branchCount > 0 && upSegmentCount > 0) {
      for (int j = 0; j < branchCount; j++) {

        float branchDirAngle =
            (j / (float) branchCount) * MathHelper.TAU + (random.nextFloat() * 0.1f - 0.05f);
        buildBranch(
            world,
            replacer,
            random,
            config,
            //
            here,
            TrunkUtil.bendWithAngle(
                dir,
                branchDirAngle,
                MathHelper.RADIANS_PER_DEGREE * this.config.upBranchMinAngle(),
                MathHelper.RADIANS_PER_DEGREE * this.config.upBranchMaxAngle(),
                random),
            segmentLength,
            upSegmentCount,
            foliageRadius,
            recursionDepth + 1,
            upSegmentCount < 3 || recursionDepth < 2,
            treeNodes);
      }
    } else {
      treeNodes.add(new FoliagePlacer.TreeNode(BlockPos.ofFloored(here), foliageRadius, false));
    }

    return here;
  }
}
