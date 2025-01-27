package com.letthemcook.editor.domain.cooking.track

import com.letthemcook.editor.domain.editor.components.block.BlockComponent

sealed interface TrackData

data object EmptyTrackData : TrackData

data class SingleTrackData(val ref: BlockComponent) : TrackData

data class ParallelTrackData(val children: List<TrackData>) : TrackData