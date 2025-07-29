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

  public static final MapCodec<FableTrunkPlacer> CODEC =
      RecordCodecBuilder.mapCodec(
          instance -> fillTrunkPlacerFields(instance).apply(instance, FableTrunkPlacer::new));

  // fillTrunkPlacerFields(instance)
  //     .and(
  //         instance.group(
  //             Codec.INT
  //                 .fieldOf("radius_top")
  //                 .forGetter(
  //                     (placer) -> {
  //                       return placer.radiusTop;
  //                     }),
  //             Codec.INT
  //                 .fieldOf("radius_bottom")
  //                 .forGetter(
  //                     (placer) -> {
  //                       return placer.radiusBottom;
  //                     })))
  //     .apply(instance, FableTrunkPlacer::new));

  public FableTrunkPlacer(int baseHeight, int firstRandomHeight, int secondRandomHeight) {
    super(baseHeight, firstRandomHeight, secondRandomHeight);
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
        1,
        0,
        false,
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
      dir = TrunkUtil.bend(dir, 0f, MathHelper.RADIANS_PER_DEGREE * 45f, random);
      dir = TrunkUtil.bendTowardsUp(dir, 10f * MathHelper.RADIANS_PER_DEGREE);

      double branchProg = (i / (double) segmentCount);

      // "Sideways" branches along the base trunk
      if (!isMinor && random.nextInt(3) == 0) {

        buildBranch(
            world,
            replacer,
            random,
            config,
            //
            here,
            TrunkUtil.bend(dir, 15f, 45f, random),
            segmentLength,
            Math.max((int) (segmentCount * branchProg * 0.85f), 1),
            0, // Smaller foliage clumps
            recursionDepth + 1,
            true, // Always minor branches
            treeNodes);
      }
    }

    // "Upwards" branches at the end of trunks
    if (!isMinor) {
      int branchCount = random.nextBetween(2, 4);
      for (int j = 0; j < branchCount; j++) {
        int newSegmentCount = Math.max((int) (segmentCount * 0.65), 1);

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
                MathHelper.RADIANS_PER_DEGREE * 15f,
                MathHelper.RADIANS_PER_DEGREE * 45f,
                random),
            segmentLength,
            Math.max((int) (segmentCount * 0.65), 1),
            foliageRadius,
            recursionDepth + 1,
            newSegmentCount < 3 || recursionDepth < 2,
            treeNodes);
      }
    } else {
      treeNodes.add(new FoliagePlacer.TreeNode(BlockPos.ofFloored(here), foliageRadius, false));
    }

    return here;
  }
}
