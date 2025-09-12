# Json Model Legacy

Json Model Legacy 提供了一套仿制的 1.21.6+ Json 模型兼容，这意味着你可以通过 Json Model Legacy 来提供一些由
BlockBench 制作的 1.21.6+ 模型，并直接将他们渲染为游戏内的方块与物品，而不需要预先转变为 Obj 模型或依赖于
GeckoLib Unofficial。

# 使用

创建一个具有模型渲染的方块，需要实现接口 `IBlockJsonModel`，并为 `Block#getRenderType` 提供 
`IBlockJsonModel#renderType` 作为返回值。  
随后，在 `JsonBlock` 完成后续的注册步骤。