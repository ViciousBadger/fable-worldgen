package com.badgerson.fable.trees;

import com.badgerson.fable.Fable;
import com.badgerson.fable.trees.config.AdvancedTrunkConfig;
import com.badgerson.fable.trees.config.BranchBendingConfig;
import com.badgerson.fable.trees.config.BranchLayer;
import com.badgerson.fable.trees.config.UpBranchConfig;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.ArrayList;
import java.util.HashMap;
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

    List<FoliagePlacer.TreeNode> treeNodes = new ArrayList<FoliagePlacer.TreeNode>();

    buildBranch(
        world,
        replacer,
        random,
        config,
        treeNodes,
        BranchMode.Trunk,
        this.config.trunkThickness(),
        initialPosition,
        initialDirection,
        this.config.toSegmentCount(height),
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
      int thickness,
      Vec3d startPos,
      Vec3d startDir,
      int segmentCount,
      int foliageRadius,
      int recursionDepth) {
    Vec3d here = new Vec3d(startPos.x, startPos.y, startPos.z);
    Vec3d dir = startDir;

    HashMap<Integer, Integer> sideBranchPoints = new HashMap<>();

    // Treat side branch layers as what layer of trunk/top to put them on..
    if (mode != BranchMode.Side
        && this.config.sideBranchConfig().isPresent()
        && recursionDepth < this.config.sideBranchConfig().get().layers().size()) {

      // Distribute side branches evenly along this branch..
      BranchLayer thisLayer = this.config.sideBranchConfig().get().layers().get(recursionDepth);
      int toDistribute = thisLayer.generateBranchCount(random);
      for (int i = 0; i < toDistribute; i++) {
        if (segmentCount > 1) {
          int segmentToPutOn =
              random.nextBetween(
                  this.config.sideBranchConfig().get().startAt().orElse(0), segmentCount - 1);
          sideBranchPoints.compute(segmentToPutOn, (k, v) -> (v == null) ? 0 : v + 1);
        } else {
          sideBranchPoints.compute(0, (k, v) -> (v == null) ? 0 : v + 1);
        }
      }
    }

    for (int i = 0; i < segmentCount; i++) {
      TrunkSegment seg = new TrunkSegment(here, dir, this.config.segmentLength());

      while (seg.hasNext()) {
        if (thickness <= 1) {
          this.getAndSetState(world, replacer, random, seg.next(), config);
        } else if (thickness == 2) {
          BlockPos next = seg.next();
          this.getAndSetState(world, replacer, random, next, config);
          this.getAndSetState(world, replacer, random, next.east(), config);
          this.getAndSetState(world, replacer, random, next.south(), config);
          this.getAndSetState(world, replacer, random, next.east().south(), config);
        } else if (thickness == 3) {
          BlockPos next = seg.next();
          this.getAndSetState(world, replacer, random, next, config);
          this.getAndSetState(world, replacer, random, next.east(), config);
          this.getAndSetState(world, replacer, random, next.south(), config);
          this.getAndSetState(world, replacer, random, next.north(), config);
          this.getAndSetState(world, replacer, random, next.west(), config);
        } else if (thickness == 4) {
          BlockPos next = seg.next();
          this.getAndSetState(world, replacer, random, next.north(), config);
          this.getAndSetState(world, replacer, random, next.north().east(), config);
          this.getAndSetState(world, replacer, random, next.west(), config);
          // this.getAndSetState(world, replacer, random, next, config);
          // this.getAndSetState(world, replacer, random, next.east(), config);
          this.getAndSetState(world, replacer, random, next.east(2), config);
          this.getAndSetState(world, replacer, random, next.south().west(), config);
          // this.getAndSetState(world, replacer, random, next.south(), config);
          // this.getAndSetState(world, replacer, random, next.south().east(), config);
          this.getAndSetState(world, replacer, random, next.south().east(2), config);
          this.getAndSetState(world, replacer, random, next.south(2), config);
          this.getAndSetState(world, replacer, random, next.south(2).east(), config);
        }
      }
      here = seg.getCurrentVec();

      if (this.config.bendingConfig().isPresent()) {
        BranchBendingConfig bendingConfig = this.config.bendingConfig().get();
        if (random.nextFloat() < bendingConfig.bendChance()) {
          dir =
              TrunkUtil.bend(
                  dir,
                  MathHelper.RADIANS_PER_DEGREE * bendingConfig.minBendAmount(),
                  MathHelper.RADIANS_PER_DEGREE * bendingConfig.maxBendAmount(),
                  random);
        } else {
          dir =
              TrunkUtil.bendTowardsUp(
                  dir, bendingConfig.straightenAmount() * MathHelper.RADIANS_PER_DEGREE);
        }
      }

      // "Sideways" branches along the base trunk
      int sideBranchesHere = sideBranchPoints.getOrDefault(i, 0);
      if (sideBranchesHere > 0) {
        BranchLayer thisLayer = this.config.sideBranchConfig().get().layers().get(recursionDepth);

        for (int j = 0; j < sideBranchesHere; j++) {
          int newSegmentCount = this.config.toSegmentCount(thisLayer.length().generate(random));
          buildBranch(
              world,
              replacer,
              random,
              config,
              treeNodes,
              //
              BranchMode.Side,
              thisLayer.thickness().orElse(1),
              here,
              TrunkUtil.bendWithAngle(dir, thisLayer.angle().generate(random), random),
              newSegmentCount,
              0, // Smaller foliage clumps (?)
              recursionDepth + 1);
        }
      }
    }

    // "Upwards" branches at the end of trunks (and other top branches, recursively)
    boolean putFoliageHere = true;

    if (this.config.upBranchConfig().isPresent()) {
      UpBranchConfig upBranchConfig = this.config.upBranchConfig().get();

      if (recursionDepth < upBranchConfig.layers().size()) {
        BranchLayer thisLayer = upBranchConfig.layers().get(recursionDepth);
        int branchCount = thisLayer.generateBranchCount(random);

        if (mode != BranchMode.Side && branchCount > 0) {
          putFoliageHere = false;
          for (int j = 0; j < branchCount; j++) {
            int newSegmentCount = this.config.toSegmentCount(thisLayer.length().generate(random));
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
                thisLayer.thickness().orElse(1),
                here,
                TrunkUtil.bendInDirectionWithAngle(
                    dir,
                    branchDirAngle,
                    MathHelper.RADIANS_PER_DEGREE * thisLayer.angle().generate(random)),
                newSegmentCount,
                foliageRadius,
                recursionDepth + 1);
          }
        }
      }
    }

    if (putFoliageHere) {
      treeNodes.add(
          new FoliagePlacer.TreeNode(
              BlockPos.ofFloored(here), foliageRadius, this.config.trunkThickness() % 2 == 0));
    }

    return here;
  }
}
