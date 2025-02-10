package com.letthemcook.editor.domain.cooking.track

import com.letthemcook.editor.domain.cooking.BlockCookingState
import com.letthemcook.editor.domain.editor.components.block.BlockComponent
import com.letthemcook.editor.domain.editor.components.composed.HorizontalComposedComponent
import com.letthemcook.editor.domain.editor.components.prototype.canCook
import com.letthemcook.editor.domain.editor.components.prototype.canFinish
import com.letthemcook.editor.domain.editor.components.prototype.canRestore
import com.letthemcook.editor.domain.editor.components.prototype.isFinished
import com.letthemcook.editor.domain.editor.components.prototype.isWaiting
import com.letthemcook.editor.domain.editor.components.prototype.nextInHierarchy
import com.letthemcook.editor.domain.editor.components.prototype.previousInHierarchy

// TrackData

fun TrackData.doForEveryChild(action: BlockComponent.() -> Unit) {
    when (this) {
        EmptyTrackData -> Unit
        is ParallelTrackData -> children.forEach { it.doForEveryChild(action) }
        is SingleTrackData -> ref.action()
    }
}

// UI

fun SingleTrackData.getOptions(): Pair<TrackDataOptions, TrackDataOptions> {
    val firstOption = when {
        ref.isFinished() || (ref.canRestore() && ref.previousInHierarchy() != null) -> TrackDataOptions.GO_TO_PREVIOUS
        else -> TrackDataOptions.NONE
    }

    val secondOption = when {
        ref.isFinished() -> {
            if (ref.nextInHierarchy() != null) {
                TrackDataOptions.WAITING
            } else {
                TrackDataOptions.NONE
            }
        }
        ref.canFinish() -> {
            ref.pretendCookingState(BlockCookingState.DONE)

            val parent = ref.parentComponent
            val parentCheck = if (parent is HorizontalComposedComponent) {
                parent.components.all { it.isWaiting() || it.isFinished() }
            } else {
                true
            }

            ref.pretendCookingState(BlockCookingState.DONE)

            if (parentCheck && ref.nextInHierarchy()?.canCook() == true) {
                TrackDataOptions.GO_TO_NEXT
            } else {
                TrackDataOptions.FINISH
            }
        }
        else -> TrackDataOptions.NONE
    }

    return firstOption to secondOption
}