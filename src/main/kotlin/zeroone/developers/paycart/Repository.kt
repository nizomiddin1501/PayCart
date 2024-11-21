package zeroone.developers.paycart

import jakarta.persistence.EntityManager
import jakarta.transaction.Transactional
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.domain.Specification
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.data.jpa.repository.Query
import org.springframework.data.jpa.repository.support.JpaEntityInformation
import org.springframework.data.jpa.repository.support.SimpleJpaRepository
import org.springframework.data.repository.NoRepositoryBean
import org.springframework.data.repository.findByIdOrNull
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.util.*

@NoRepositoryBean
interface BaseRepository<T : BaseEntity> : JpaRepository<T, Long>, JpaSpecificationExecutor<T> {
    fun findByIdAndDeletedFalse(id: Long): T?
    fun trash(id: Long): T?
    fun trashList(ids: List<Long>): List<T?>
    fun findAllNotDeleted(): List<T>
    fun findAllNotDeleted(pageable: Pageable): List<T>
    fun findAllNotDeletedForPageable(pageable: Pageable): Page<T>
    fun saveAndRefresh(t: T): T
}

class BaseRepositoryImpl<T : BaseEntity>(
    entityInformation: JpaEntityInformation<T, Long>,
    private val entityManager: EntityManager
) : SimpleJpaRepository<T, Long>(entityInformation, entityManager), BaseRepository<T> {

    val isNotDeletedSpecification = Specification<T> { root, _, cb -> cb.equal(root.get<Boolean>("deleted"), false) }

    override fun findByIdAndDeletedFalse(id: Long) = findByIdOrNull(id)?.run { if (deleted) null else this }

    @Transactional
    override fun trash(id: Long): T? = findByIdOrNull(id)?.run {
        deleted = true
        save(this)
    }

    override fun findAllNotDeleted(): List<T> = findAll(isNotDeletedSpecification)
    override fun findAllNotDeleted(pageable: Pageable): List<T> = findAll(isNotDeletedSpecification, pageable).content
    override fun findAllNotDeletedForPageable(pageable: Pageable): Page<T> =
        findAll(isNotDeletedSpecification, pageable)

    override fun trashList(ids: List<Long>): List<T?> = ids.map { trash(it) }

    @Transactional
    override fun saveAndRefresh(t: T): T {
        return save(t).apply { entityManager.refresh(this) }
    }
}


@Repository
interface CategoryRepository : BaseRepository<Category> {}

@Repository
interface ProductRepository : BaseRepository<Product> {}

@Repository
interface TransactionItemRepository : BaseRepository<TransactionItem> {

    @Query("select p from product p where p.id = :productId")
    fun findByProductId(@Param("productId") productId: Long): Optional<Product>

    @Query("select t from transaction t where t.id = :transactionId")
    fun findByTransactionId(@Param("transactionId") transactionId: Long): Optional<Transaction>
}

@Repository
interface TransactionRepository : BaseRepository<Transaction> {


    @Query("select u from users u where u.id = :userId")
    fun findByUserId(@Param("userId") userId: Long): Optional<User>


}

@Repository
interface UserPaymentTransactionRepository : BaseRepository<UserPaymentTransaction> {
    fun findByUserIdAndDeletedFalse(userId: Long): User?

    @Query("select u from users u where u.id = :userId")
    fun findByUserId(@Param("userId") userId: Long): Optional<User>

}

@Repository
interface UserRepository : BaseRepository<User> {
    fun findByUsernameAndDeletedFalse(username: String): User?

    @Query("""
        select u from users u
        where u.id != :id
        and u.username = :username
        and u.deleted = false 
    """)
    fun findByUsernameNotId(id: Long, username: String): User?
}



