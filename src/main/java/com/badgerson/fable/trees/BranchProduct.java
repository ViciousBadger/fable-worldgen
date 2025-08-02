package com.badgerson.fable.trees;

import com.google.common.collect.ImmutableList;
import net.minecraft.util.math.BlockPos;

public record BranchProduct(
    ImmutableList<TrunkBlock> trunkBlocks,
    ImmutableList<FoliageNode> foliageNodes,
    ImmutableList<BranchProducer> subBranches) {
  public record TrunkBlock(BlockPos pos) {}

  public record FoliageNode(BlockPos pos, int radius, boolean isOnGiantTrunk) {}
}
