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
import org.joml.Quaternionf;
import org.joml.Vector3f;

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

    BlockPos here = startPos;
    // We assume the identity direction to be up
    Quaternionf initialDirection = new Quaternionf();

    double trunkSegLength = 2.5;
    int trunkSegCount = (int) (height / trunkSegLength);

    List<FoliagePlacer.TreeNode> treeNodes = new ArrayList<FoliagePlacer.TreeNode>();

    buildBranch(
        world,
        replacer,
        random,
        config,
        here,
        initialDirection,
        trunkSegLength,
        trunkSegCount,
        0.0,
        4.0,
        0,
        treeNodes);

    return treeNodes;
  }

  private BlockPos buildBranch(
      TestableWorld world,
      BiConsumer<BlockPos, BlockState> replacer,
      Random random,
      TreeFeatureConfig config,
      BlockPos startPos,
      Quaternionf startDir,
      double segmentLength,
      int segmentCount,
      double branchinessStart,
      double branchinessEnd,
      int recursionDepth,
      List<FoliagePlacer.TreeNode> treeNodes) {
    BlockPos here = startPos;
    Quaternionf dir = startDir;

    for (int i = 0; i < segmentCount; i++) {
      TrunkSegment seg = new TrunkSegment(Vec3d.of(here), dir, segmentLength);

      while (seg.hasNext()) {
        here = seg.next();
        this.getAndSetState(world, replacer, random, here, config);
      }

      if (random.nextInt() % 4 == 0) {
        dir = bend(dir, 0f, MathHelper.RADIANS_PER_DEGREE * 90f, random);
      }
      // } else {
      //   dir = straighten(dir, new Quaternionf(), 0.5f);
      // }
      dir = straighten(dir, new Quaternionf(), 0.5f);

      if (recursionDepth < 2) {
        double branchChance =
            MathHelper.lerp((i / (double) segmentCount), branchinessStart, branchinessEnd);
        int branchCount = (int) branchChance;
        double fracPart = branchChance - branchCount;
        if (random.nextDouble() < fracPart) {
          branchCount++;
        }

        for (int j = 0; j < branchCount; j++) {
          buildBranch(
              world,
              replacer,
              random,
              config,
              //
              here,
              // new Vec3d(random.nextDouble() * 2.0 - 1.0, 1.0, random.nextDouble() * 2.0 - 1.0),
              bend(dir, 35f, 75f, random),
              segmentLength,
              (int) (segmentCount * 0.55),
              branchinessStart * 0.5,
              branchinessEnd * 0.5,
              recursionDepth + 1,
              treeNodes);
        }
      }
    }

    treeNodes.add(new FoliagePlacer.TreeNode(here, 0, false));

    return here;
  }

  private Quaternionf bend(Quaternionf input, float minRadians, float maxRadians, Random random) {
    float angle = random.nextFloat() * MathHelper.TAU;
    // float amount = random.nextFloat() * factor;
    float amount = minRadians + random.nextFloat() * (maxRadians - minRadians);

    // Bend around XZ to "steer" the rotation away from up...
    // Tryin to get this.. we go up by default.. Y......maybe
    Vector3f bendAxis =
        new Vector3f((float) Math.cos(angle), 0.0f, (float) Math.sin(angle)).normalize();
    Quaternionf bendQuat = new Quaternionf().fromAxisAngleRad(bendAxis, amount);

    Quaternionf result = new Quaternionf(input);
    return result.mul(bendQuat);
  }

  private Quaternionf straighten(Quaternionf input, Quaternionf target, float amount) {
    Quaternionf result = new Quaternionf(input);
    return result.slerp(target, amount);
  }
}
