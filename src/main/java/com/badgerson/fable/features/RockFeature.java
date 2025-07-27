package com.badgerson.fable.features;

import com.mojang.serialization.Codec;
import java.util.Iterator;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.util.FeatureContext;

public class RockFeature extends Feature<RockFeatureConfig> {
  public RockFeature(Codec<RockFeatureConfig> configCodec) {
    super(configCodec);
  }

  @Override
  public boolean generate(FeatureContext<RockFeatureConfig> context) {
    StructureWorldAccess world = context.getWorld();
    Random random = context.getRandom();
    RockFeatureConfig config = context.getConfig();

    BlockPos targetPos;
    for (targetPos = context.getOrigin();
        targetPos.getY() > world.getBottomY() + 3;
        targetPos = targetPos.down()) {
      if (!world.isAir(targetPos.down())) {
        BlockState blockState = world.getBlockState(targetPos.down());
        if (isSoil(blockState) || isStone(blockState)) {
          break;
        }
      }
    }

    if (targetPos.getY() <= world.getBottomY() + 3) {
      return false;
    } else {
      int r = random.nextBetween(1, 3);

      Iterator<BlockPos> iter =
          BlockPos.iterate(targetPos.add(-r, -r, -r), targetPos.add(r, r, r)).iterator();

      while (iter.hasNext()) {
        BlockPos placePos = (BlockPos) iter.next();

        int relY = placePos.getY() - targetPos.getY();
        double r2 = r - Math.max(0, relY) + 0.33;

        if (placePos.getSquaredDistance(targetPos) <= (r2 * r2)) {
          world.setBlockState(placePos, config.state(), 3);
        }
      }

      return true;
    }
  }
}
