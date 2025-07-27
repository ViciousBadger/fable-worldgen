package com.badgerson.fable.features;

import com.mojang.serialization.Codec;
import java.util.Iterator;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.noise.SimplexNoiseSampler;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.util.FeatureContext;

public class RockFeature extends Feature<RockFeatureConfig> {
  public RockFeature(Codec<RockFeatureConfig> configCodec) {
    super(configCodec);
  }

  private static final int[] ROCK_SIZES =
      new int[] {2, 2, 2, 2, 3, 3, 3, 3, 3, 3, 4, 4, 4, 5, 5, 6};

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
      int r = ROCK_SIZES[random.nextInt(16)];

      SimplexNoiseSampler noise = new SimplexNoiseSampler(random);

      Iterator<BlockPos> iter =
          BlockPos.iterate(targetPos.add(-r, -r, -r), targetPos.add(r, r, r)).iterator();

      while (iter.hasNext()) {
        BlockPos placePos = (BlockPos) iter.next();

        int relY = placePos.getY() - targetPos.getY();
        double r2 = r - Math.max(0, relY * 0.65) + 0.33;

        double s =
            noise.sample(placePos.getX() * 0.15, placePos.getY() * 0.15, placePos.getZ() * 0.15);

        double d = (r2 * r2) - placePos.getSquaredDistance(targetPos);

        double density = d + (s * 16.0);

        if (density > 0.0) {
          world.setBlockState(placePos, config.state(), 3);
        }
      }

      return true;
    }
  }
}
