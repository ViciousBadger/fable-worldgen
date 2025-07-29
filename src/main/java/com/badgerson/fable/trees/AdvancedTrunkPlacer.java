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

public class AdvancedTrunkPlacer extends TrunkPlacer {
  private enum BranchMode {
    Trunk,
    Side,
    Top
  }

  private AdvancedTrunkConfig config;

  public static final MapCodec<AdvancedTrunkPlacer> CODEC =
      RecordCodecBuilder.mapCodec(
          instance ->
              fillTrunkPlacerFields(instance)
                  .and(
                      AdvancedTrunkConfig.CODEC
                          .fieldOf("config")
                          .forGetter(
                              (placer) -> {
                                return placer.config;
                              }))
                  .apply(instance, AdvancedTrunkPlacer::new));

  public AdvancedTrunkPlacer(
      int baseHeight, int firstRandomHeight, int secondRandomHeight, AdvancedTrunkConfig config) {
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

    double trunkSegLength = 2.0;
    int trunkSegCount = (int) (height / trunkSegLength);

    List<FoliagePlacer.TreeNode> treeNodes = new ArrayList<FoliagePlacer.TreeNode>();

    buildBranch(
        world,
        replacer,
        random,
        config,
        treeNodes,
        BranchMode.Trunk,
        initialPosition,
        initialDirection,
        trunkSegLength,
        trunkSegCount,
        1, // Foliage
        0 // Recursion depth
        );

    return treeNodes;
  }

  private Vec3d buildBranch(
      TestableWorld world,
      BiConsumer<BlockPos, BlockState> replacer,
      Random random,
      TreeFeatureConfig config,
      List<FoliagePlacer.TreeNode> treeNodes,
      //
      BranchMode mode,
      Vec3d startPos,
      Vec3d startDir,
      double segmentLength,
      int segmentCount,
      int foliageRadius,
      int recursionDepth) {
    Vec3d here = new Vec3d(startPos.x, startPos.y, startPos.z);
    Vec3d dir = startDir;

    for (int i = 0; i < segmentCount; i++) {
      TrunkSegment seg = new TrunkSegment(here, dir, segmentLength);

      while (seg.hasNext()) {
        if (mode != BranchMode.Trunk || this.config.trunkThickness() <= 1) {
          this.getAndSetState(world, replacer, random, seg.next(), config);
        } else if (this.config.trunkThickness() == 2) {
          BlockPos next = seg.next();
          this.getAndSetState(world, replacer, random, next, config);
          this.getAndSetState(world, replacer, random, next.east(), config);
          this.getAndSetState(world, replacer, random, next.south(), config);
          this.getAndSetState(world, replacer, random, next.east().south(), config);
        } else if (this.config.trunkThickness() == 3) {
          BlockPos next = seg.next();
          this.getAndSetState(world, replacer, random, next, config);
          this.getAndSetState(world, replacer, random, next.east(), config);
          this.getAndSetState(world, replacer, random, next.south(), config);
          this.getAndSetState(world, replacer, random, next.north(), config);
          this.getAndSetState(world, replacer, random, next.west(), config);
        }
      }

      here = seg.getCurrentVec();
      if (random.nextFloat() < this.config.bendChance()) {
        dir =
            TrunkUtil.bend(
                dir,
                MathHelper.RADIANS_PER_DEGREE * this.config.minBendAmount(),
                MathHelper.RADIANS_PER_DEGREE * this.config.maxBendAmount(),
                random);
      } else {
        dir =
            TrunkUtil.bendTowardsUp(
                dir, this.config.straightenAmount() * MathHelper.RADIANS_PER_DEGREE);
      }

      // "Sideways" branches along the base trunk
      if (mode == BranchMode.Trunk && random.nextDouble() < this.config.sideBranchChance()) {
        buildBranch(
            world,
            replacer,
            random,
            config,
            treeNodes,
            //
            BranchMode.Side,
            here,
            TrunkUtil.bend(
                dir, this.config.sideBranchMinAngle(), this.config.sideBranchMaxAngle(), random),
            segmentLength,
            (int) (this.config.sideBranchLength() / segmentLength),
            0, // Smaller foliage clumps (?)
            recursionDepth + 1);
      }
    }

    // "Upwards" branches at the end of trunks (and other top branches, recursively)
    int branchCount = random.nextBetween(this.config.minUpBranches(), this.config.maxUpBranches());

    if (mode != BranchMode.Side
        && branchCount > 0
        && recursionDepth < this.config.upBranchDepth()) {
      for (int j = 0; j < branchCount; j++) {
        // Calc height fr this one
        float lengthScale =
            this.config.upBranchLengthMin()
                + (random.nextFloat() * this.config.upBranchLengthMax()
                    - this.config.upBranchLengthMin());
        int upSegmentCount = Math.max((int) (segmentCount * lengthScale), 1);
        float branchDirAngle =
            (j / (float) branchCount) * MathHelper.TAU + (random.nextFloat() * 0.1f - 0.05f);
        buildBranch(
            world,
            replacer,
            random,
            config,
            treeNodes,
            //
            BranchMode.Top,
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
            recursionDepth + 1);
      }
    } else {
      treeNodes.add(new FoliagePlacer.TreeNode(BlockPos.ofFloored(here), foliageRadius, false));
    }

    return here;
  }
}
