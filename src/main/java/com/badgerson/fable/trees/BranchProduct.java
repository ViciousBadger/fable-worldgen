package com.badgerson.fable.trees;

import com.google.common.collect.ImmutableList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

public record BranchProduct(
    ImmutableList<TrunkBlock> trunkBlocks,
    ImmutableList<FoliageNode> foliageNodes,
    ImmutableList<BranchProducer> subBranches) {
  public record TrunkBlock(BlockPos pos, Direction.Axis axis) {}

  public record FoliageNode(BlockPos pos, int radius, boolean isOnGiantTrunk) {}
}
