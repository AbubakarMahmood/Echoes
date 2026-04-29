package com.echoes.app.data.local.model

import com.echoes.app.data.local.entity.CommentEntity

data class CapsuleSocialState(
    val isFavorite: Boolean = false,
    val comments: List<CommentEntity> = emptyList()
)
