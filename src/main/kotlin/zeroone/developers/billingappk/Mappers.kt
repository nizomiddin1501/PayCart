package zeroone.developers.billingappk


object Mappers {

    class CategoryMapper {
        // Convert CategoryCreateRequest to Category (Entity)
        fun toEntity(createRequest: CategoryCreateRequest): Category {
            return Category(
                name = createRequest.name,
                orderValue = createRequest.orderValue,
                description = createRequest.description
            )
        }

        // Convert Category (Entity) to CategoryResponse (DTO)
        fun toResponse(category: Category): CategoryResponse {
            return CategoryResponse(
                id = category.id!!,
                name = category.name,
                orderValue = category.orderValue,
                description = category.description
            )
        }

        // Update existing Category (Entity) with CategoryUpdateRequest (DTO)
        fun updateEntityFromRequest(category: Category, updateRequest: CategoryUpdateRequest): Category {
            return category.apply {
                updateRequest.name?.let { this.name = it }
                updateRequest.orderValue?.let { this.orderValue = it }
                updateRequest.description?.let { this.description = it }
            }
        }
    }
}


