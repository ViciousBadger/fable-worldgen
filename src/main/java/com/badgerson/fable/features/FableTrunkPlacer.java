package com.badgerson.fable.features;

import com.badgerson.fable.Fable;
import com.google.common.collect.ImmutableList;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.List;
import java.util.function.BiConsumer;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
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
          instance ->
              // instance.group(BlockState.CODEC.fieldOf("state").forGetter(RockFeatureConfig::state))
              fillTrunkPlacerFields(instance).apply(instance, FableTrunkPlacer::new));

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

    // Iterate until the trunk height limit and place two blocks using the getAndSetState method
    // from TrunkPlacer
    for (int i = 0; i < height; i++) {
      this.getAndSetState(world, replacer, random, startPos.up(i), config);
      this.getAndSetState(world, replacer, random, startPos.up(i).east().north(), config);
    }

    // We create two TreeNodes - one for the first trunk, and the other for the second
    // Put the highest block in the trunk as the center position for the FoliagePlacer to use
    return ImmutableList.of(
        new FoliagePlacer.TreeNode(startPos.up(height), 0, false),
        new FoliagePlacer.TreeNode(startPos.east().north().up(height), 0, false));
  }
}
