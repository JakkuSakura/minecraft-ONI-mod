package mconi.common.world

interface OniChunkDataHolder {
    fun `mconi$getOniChunkData`(): OniChunkData?
    fun `mconi$setOniChunkData`(data: OniChunkData?)
}
