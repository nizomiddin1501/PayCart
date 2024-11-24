package zeroone.developers.paycart

import jakarta.persistence.EntityManager
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional

interface CategoryService {
    fun getAll(pageable: Pageable): Page<CategoryResponse>
    fun getAll(): List<CategoryResponse>
    fun getOne(id: Long): CategoryResponse
    fun create(request: CategoryCreateRequest)
    fun update(id: Long, request: CategoryUpdateRequest)
    fun delete(id: Long)
}

interface ProductService {
    fun getAll(pageable: Pageable): Page<ProductResponse>
    fun getAll(): List<ProductResponse>
    fun getOne(id: Long): ProductResponse
    fun create(request: ProductCreateRequest)
    fun update(id: Long, request: ProductUpdateRequest)
    fun delete(id: Long)
}

//interface PurchaseService {
//    fun create(request: PurchaseCreateRequest)
//    fun getAll(pageable: Pageable): Page<CategoryResponse>
//    fun getAll(): List<CategoryResponse>
//    fun getOne(id:Long): CategoryResponse
//    fun update(id:Long, request: CategoryUpdateRequest)
//    fun delete(id:Long)
//}

interface TransactionItemService {
    fun getAll(pageable: Pageable): Page<TransactionItemResponse>
    fun getAll(): List<TransactionItemResponse>
    fun getOne(id: Long): TransactionItemResponse
    fun create(request: TransactionItemCreateRequest)
    fun update(id: Long, request: TransactionItemUpdateRequest)
    fun delete(id: Long)
}

interface TransactionService {
    fun getAll(pageable: Pageable): Page<TransactionResponse>
    fun getAll(): List<TransactionResponse>
    fun getOne(id: Long): TransactionResponse
    fun create(request: TransactionCreateRequest)
    fun update(id: Long, request: TransactionUpdateRequest)
    fun delete(id: Long)
}

interface UserPaymentTransactionService {
    fun getAll(pageable: Pageable): Page<UserPaymentTransactionResponse>
    fun getAll(): List<UserPaymentTransactionResponse>
    fun getOne(id: Long): UserPaymentTransactionResponse
    fun create(request: UserPaymentTransactionCreateRequest)
    fun update(id: Long, request: UserPaymentTransactionUpdateRequest)
    fun delete(id: Long)
}

interface UserService {
    fun getAll(pageable: Pageable): Page<UserResponse>
    fun getAll(): List<UserResponse>
    fun getOne(id: Long): UserResponse
    fun create(request: UserCreateRequest)
    fun update(id: Long, request: UserUpdateRequest)
    fun delete(id: Long)
}


@Service
class CategoryServiceImpl(
    private val categoryRepository: CategoryRepository,
    private val entityManager: EntityManager
) : CategoryService {

    override fun getAll(pageable: Pageable): Page<CategoryResponse> {
        return categoryRepository.findAllNotDeletedForPageable(pageable).map {
            CategoryResponse.toResponse(it)
        }
    }

    override fun getAll(): List<CategoryResponse> {
        return categoryRepository.findAllNotDeleted().map {
            CategoryResponse.toResponse(it)
        }
    }

    override fun getOne(id: Long): CategoryResponse {
        categoryRepository.findByIdAndDeletedFalse(id)?.let {
            return CategoryResponse.toResponse(it)
        } ?: throw CategoryNotFoundException()
    }

    override fun create(request: CategoryCreateRequest) {
        request.run {
            val existsByName = categoryRepository.existsByName(name)
            if(existsByName) throw CategoryAlreadyExistsException()
            categoryRepository.save(this.toEntity())
        }
    }

    override fun update(id: Long, request: CategoryUpdateRequest) {
        val category = categoryRepository.findByIdAndDeletedFalse(id)
        request.run {
            name?.let { category!!.name = it }
            orderValue?.let { category!!.orderValue = it }
        }
        categoryRepository.save(category!!)
    }

    override fun delete(id: Long) {
        categoryRepository.trash(id) ?: throw CategoryNotFoundException()
    }
}

@Service
class ProductServiceImpl(
    private val productRepository: ProductRepository,
    private val entityManager: EntityManager
) : ProductService {

    override fun getAll(pageable: Pageable): Page<ProductResponse> {
        return productRepository.findAllNotDeletedForPageable(pageable).map {
            ProductResponse.toResponse(it)
        }
    }

    override fun getAll(): List<ProductResponse> {
        return productRepository.findAllNotDeleted().map {
            ProductResponse.toResponse(it)
        }
    }

    override fun getOne(id: Long): ProductResponse {
        productRepository.findByIdAndDeletedFalse(id)?.let {
            return ProductResponse.toResponse(it)
        } ?: throw ProductNotFoundException()
    }

    override fun create(request: ProductCreateRequest) {
        request.run {
            val existsProduct = productRepository.existsByName(name)
            if (existsProduct) throw ProductAlreadyExistsException()
            val referenceCategory = entityManager.getReference(
                Category::class.java, categoryId
            )
            productRepository.save(this.toEntity(referenceCategory))
        }
    }

    //Process 4.
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    override fun update(id: Long, request: ProductUpdateRequest) {
        val product = productRepository.findByIdAndDeletedFalse(id) ?: throw ProductNotFoundException()
        request.run {
            name?.let { product.name = it }
            count?.let { product.count = it }
        }
        productRepository.save(product)
    }

    override fun delete(id: Long) {
        productRepository.trash(id) ?: throw ProductNotFoundException()
    }
}

@Service
class TransactionServiceImpl(
    private val transactionRepository: TransactionRepository,
    private val entityManager: EntityManager
) : TransactionService {

    override fun getAll(pageable: Pageable): Page<TransactionResponse> {
        return transactionRepository.findAllNotDeletedForPageable(pageable).map {
            TransactionResponse.toResponse(it)
        }
    }

    override fun getAll(): List<TransactionResponse> {
        return transactionRepository.findAllNotDeleted().map {
            TransactionResponse.toResponse(it)
        }
    }

    override fun getOne(id: Long): TransactionResponse {
        transactionRepository.findByIdAndDeletedFalse(id)?.let {
            return TransactionResponse.toResponse(it)
        } ?: throw TransactionNotFoundException()
    }

    //Process 1.
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    override fun create(request: TransactionCreateRequest) {
        request.run {
            transactionRepository.findByUserId(userId).orElseThrow { UserNotFoundException() }
            val referenceUser = entityManager.getReference(User::class.java, userId)
            transactionRepository.save(this.toEntity(referenceUser))
        }
    }

    override fun update(id: Long, request: TransactionUpdateRequest) {
        TODO("Not yet implemented")
    }

    override fun delete(id: Long) {
        transactionRepository.trash(id) ?: throw TransactionNotFoundException()
    }
}

@Service
class TransactionItemServiceImpl(
    private val transactionItemRepository: TransactionItemRepository,
    private val entityManager: EntityManager
) : TransactionItemService {

    override fun getAll(pageable: Pageable): Page<TransactionItemResponse> {
        return transactionItemRepository.findAllNotDeletedForPageable(pageable).map {
            TransactionItemResponse.toResponse(it)
        }
    }

    override fun getAll(): List<TransactionItemResponse> {
        return transactionItemRepository.findAllNotDeleted().map {
            TransactionItemResponse.toResponse(it)
        }
    }

    override fun getOne(id: Long): TransactionItemResponse {
        transactionItemRepository.findByIdAndDeletedFalse(id)?.let {
            return TransactionItemResponse.toResponse(it)
        } ?: throw TransactionItemNotFoundException()
    }


    //Process 2.
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    override fun create(request: TransactionItemCreateRequest) {
        request.run {
            transactionItemRepository.findByProductId(productId).orElseThrow { ProductNotFoundException() }
            transactionItemRepository.findByTransactionId(transactionId).orElseThrow { TransactionNotFoundException() }
            val referenceProduct = entityManager.getReference(Product::class.java, productId)
            val referenceTransaction = entityManager.getReference(Transaction::class.java, transactionId)
            transactionItemRepository.save(this.toEntity(referenceProduct, referenceTransaction))
        }
    }

    override fun update(id: Long, request: TransactionItemUpdateRequest) {
        TODO("Not yet implemented")
    }

    override fun delete(id: Long) {
        transactionItemRepository.trash(id) ?: throw TransactionItemNotFoundException()
    }
}

@Service
class UserServiceImpl(
    private val userRepository: UserRepository,
) : UserService {

    override fun getAll(pageable: Pageable): Page<UserResponse> {
        return userRepository.findAllNotDeletedForPageable(pageable).map {
            UserResponse.toResponse(it)
        }
    }

    override fun getAll(): List<UserResponse> {
        return userRepository.findAllNotDeleted().map {
            UserResponse.toResponse(it)
        }
    }

    override fun getOne(id: Long): UserResponse {
        userRepository.findByIdAndDeletedFalse(id)?.let {
            return UserResponse.toResponse(it)
        } ?: throw UserNotFoundException()
    }

    override fun create(request: UserCreateRequest) {
        request.run {
            val user = userRepository.findByUsernameAndDeletedFalse(username)
            if (user != null) throw UserAlreadyExistsException()
            userRepository.save(this.toEntity())
        }
    }

    override fun update(id: Long, request: UserUpdateRequest) {
        val user = userRepository.findByIdAndDeletedFalse(id) ?: throw UserNotFoundException()
        request.run {
            fullname?.let { user.fullname = it }
            username?.let {
                val userExist = userRepository.findByUsernameNotId(id, it)
                if (userExist != null) throw UserAlreadyExistsException()
                user.username = it
            }
            balance?.let { user.balance = it }
        }
        userRepository.save(user)
    }

    override fun delete(id: Long) {
        userRepository.trash(id) ?: throw UserNotFoundException()
    }
}

@Service
class UserPaymentTransactionServiceImpl(
    private val userPaymentTransactionRepository: UserPaymentTransactionRepository,
    private val userRepository: UserRepository,
    private val entityManager: EntityManager
) : UserPaymentTransactionService {

    override fun getAll(pageable: Pageable): Page<UserPaymentTransactionResponse> {
        return userPaymentTransactionRepository.findAllNotDeletedForPageable(pageable).map {
            UserPaymentTransactionResponse.toResponse(it)
        }
    }

    override fun getAll(): List<UserPaymentTransactionResponse> {
        return userPaymentTransactionRepository.findAllNotDeleted().map {
            UserPaymentTransactionResponse.toResponse(it)
        }
    }

    override fun getOne(id: Long): UserPaymentTransactionResponse {
        userPaymentTransactionRepository.findByIdAndDeletedFalse(id)?.let {
            return UserPaymentTransactionResponse.toResponse(it)
        } ?: throw UserPaymentTransactionNotFoundException()
    }

    //Process 3.
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    override fun create(request: UserPaymentTransactionCreateRequest) {
        request.run {
            val user = userPaymentTransactionRepository.findByUserId(userId)
                .orElseThrow { UserNotFoundException() }
            if (user.balance.compareTo(request.amount) < 0) throw InvalidBalanceException()
            user.balance = user.balance.subtract(request.amount)
            userRepository.save(user)
            val referenceUser = entityManager.getReference(
                User::class.java,
                userId
            )
            userPaymentTransactionRepository.save(this.toEntity(referenceUser))
        }
    }

    override fun update(id: Long, request: UserPaymentTransactionUpdateRequest) {
        val userPaymentTransaction = userPaymentTransactionRepository.findByIdAndDeletedFalse(id)
            ?: throw UserPaymentTransactionNotFoundException()
        request.run {
            amount?.let { userPaymentTransaction.amount = it }
        }
        userPaymentTransactionRepository.save(userPaymentTransaction)
    }

    @Transactional
    override fun delete(id: Long) {
        userPaymentTransactionRepository.trash(id) ?: throw UserPaymentTransactionNotFoundException()
    }
}




