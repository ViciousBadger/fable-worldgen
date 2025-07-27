package com.badgerson.fable.features;

import com.badgerson.fable.Fable;
import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.function.BiConsumer;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.TestableWorld;
import net.minecraft.world.gen.feature.TreeFeatureConfig;
import net.minecraft.world.gen.foliage.FoliagePlacer;
import net.minecraft.world.gen.foliage.FoliagePlacer.TreeNode;
import net.minecraft.world.gen.trunk.TrunkPlacer;
import net.minecraft.world.gen.trunk.TrunkPlacerType;
import org.joml.Math;

public class FableTrunkPlacer extends TrunkPlacer {
  private final int radiusTop;
  private final int radiusBottom;

  public static final MapCodec<FableTrunkPlacer> CODEC =
      RecordCodecBuilder.mapCodec(
          instance ->
              fillTrunkPlacerFields(instance)
                  .and(
                      instance.group(
                          Codec.INT
                              .fieldOf("radius_top")
                              .forGetter(
                                  (placer) -> {
                                    return placer.radiusTop;
                                  }),
                          Codec.INT
                              .fieldOf("radius_bottom")
                              .forGetter(
                                  (placer) -> {
                                    return placer.radiusBottom;
                                  })))
                  .apply(instance, FableTrunkPlacer::new));

  public FableTrunkPlacer(
      int baseHeight,
      int firstRandomHeight,
      int secondRandomHeight,
      int radiusTop,
      int radiusBottom) {
    super(baseHeight, firstRandomHeight, secondRandomHeight);
    this.radiusTop = radiusTop;
    this.radiusBottom = radiusBottom;
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

    Vec3d pos = Vec3d.of(startPos);
    Vec3d move = new Vec3d(0.0, 1.0, 0.0);

    int skewCounter = 2;
    int thickCounter = random.nextBetween(1, 3);

    for (int i = 0; i < height; i++) {
      BlockPos p = BlockPos.ofFloored(pos);

      this.getAndSetState(world, replacer, random, p, config);
      if (thickCounter > 0) {
        this.getAndSetState(world, replacer, random, p.east(), config);
        this.getAndSetState(world, replacer, random, p.west(), config);
        this.getAndSetState(world, replacer, random, p.north(), config);
        this.getAndSetState(world, replacer, random, p.south(), config);

        thickCounter--;
      }

      // Modify move and pos
      skewCounter -= 1;
      double maxSkew = 0.75;
      if (skewCounter <= 0) {
        move =
            new Vec3d(
                Math.clamp(move.x + (random.nextDouble() * 2.0 - 1.0) * maxSkew, -1.0, 1.0),
                move.y,
                Math.clamp(move.z + (random.nextDouble() * 2.0 - 1.0) * maxSkew, -1.0, 1.0));

        move = moveTowards(move, new Vec3d(0.0, 1.0, 0.0), 0.2);

        skewCounter = 2;
      }

      pos = pos.add(move);
    }

    return ImmutableList.of(new FoliagePlacer.TreeNode(BlockPos.ofFloored(pos), 0, false));
  }

  Vec3d moveTowards(Vec3d from, Vec3d to, double delta) {
    var v = to.subtract(from);
    var len = v.length();
    if (len <= delta || len < 0.00001) {
      return to;
    }
    return from.add(v.multiply(1.0 / len).multiply(delta));
  }
}
