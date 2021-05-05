package com.kaonstudio.imagesearchmvipagination.utils

interface EntityMapper<Entity, Domain> {
    fun mapFromEntity(entity: Entity) : Domain
}