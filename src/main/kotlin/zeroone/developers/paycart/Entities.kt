package zeroone.developers.paycart

import io.swagger.v3.oas.annotations.media.Schema
import org.hibernate.annotations.ColumnDefault
import org.springframework.data.annotation.CreatedBy
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedBy
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.math.BigDecimal
import java.util.*
import jakarta.persistence.*

@MappedSuperclass
@EntityListeners(AuditingEntityListener::class)
open class BaseEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "ID", example = "1")
    var id: Long? = null,
    @CreatedDate @Temporal(TemporalType.TIMESTAMP) var createdDate: Date? = null,
    @LastModifiedDate @Temporal(TemporalType.TIMESTAMP) var modifiedDate: Date? = null,
    @CreatedBy var createdBy: Long? = null,
    @LastModifiedBy var lastModifiedBy: Long? = null,
    @Column(nullable = false) @ColumnDefault(value = "false") var deleted: Boolean = false
)

@Entity(name = "category")
@Table
@Schema(description = "Category of the product")
class Category (

    @Column(nullable = false, unique = true)
    @Schema(description = "Category name", example = "Electronics")
    var name: String,

    @Column(name = "order_value")
    @Schema(description = "Category order", example = "1")
    var orderValue: Long,

    @Schema(description = "Category description", example = "All electronic products")
    var description: String
) : BaseEntity()

@Entity(name = "product")
@Table
@Schema(description = "Product details")
class Product (

    @Column(nullable = false)
    @Schema(description = "Product name", example = "Laptop")
    var name: String,

    @Column(nullable = false)
    @Schema(description = "Product count", example = "50")
    var count: Long,

    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    @Schema(description = "Product category", example = "Category ID: 3")
    val category: Category
) : BaseEntity()

@Entity(name = "transaction")
@Table
@Schema(description = "Transaction information")
class Transaction (

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    @Schema(description = "User who made the transaction", example = "User ID: 3")
    val user: User,

    @Column(nullable = false)
    @Schema(description = "Total amount of the transaction", example = "150.00")
    var totalAmount: BigDecimal,

    @Column(nullable = false)
    @Schema(description = "Transaction date", example = "2023-07-11")
    var date: Date
) : BaseEntity()


@Entity(name = "transaction_item")
@Table
@Schema(description = "Details of items in a transaction")
class TransactionItem (

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    @Schema(description = "Product in the transaction", example = "Product ID: 10")
    val product: Product,

    @Column(nullable = false)
    @Schema(description = "Quantity of the product", example = "2")
    var count: Long,

    @Column(nullable = false)
    @Schema(description = "Price of the product", example = "75.00")
    var amount: BigDecimal,

    @Column(nullable = false)
    @Schema(description = "Total amount for this item", example = "150.00")
    var totalAmount: BigDecimal,

    @ManyToOne
    @JoinColumn(name = "transaction_id", nullable = false)
    @Schema(description = "Reference to the transaction", example = "Transaction ID: 5")
    val transaction: Transaction
) : BaseEntity()


@Entity(name = "users")
@Table
@Schema(description = "User information")
class User (

    @Column(nullable = false)
    @Schema(description = "User full name", example = "Nizomiddin Mirzanazarov")
    var fullname: String,

    @Column(unique = true, nullable = false)
    @Schema(description = "Unique username", example = "nizomiddin097")
    var username: String,

    @Column(nullable = false)
    @Schema(description = "User balance", example = "1000.00")
    var balance: BigDecimal,
) : BaseEntity()

@Entity(name = "user_payment_transaction")
@Table
@Schema(description = "User payment transaction history")
class UserPaymentTransaction (

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    @Schema(description = "User who made the payment", example = "User ID: 3")
    val user: User,

    @Column(nullable = false)
    @Schema(description = "Amount of the payment", example = "200.00")
    var amount: BigDecimal,

    @Column(nullable = false)
    @Schema(description = "Date of the payment", example = "2023-07-11")
    var date: Date
) : BaseEntity()

@Schema(description = "Details for completing a purchase, including transaction, items, and payment information.")
class PurchaseRequest(
    @Schema(
        description = "Transaction details for the purchase.",
        example = "{ 'id': 1, 'user': { 'id': 3 }, 'totalAmount': 150.00, 'date': '2023-07-11' }"
    )
    val createTransaction: TransactionCreateRequest,
    val responseTransaction: TransactionResponse,
    val updateTransaction: TransactionUpdateRequest,

    @Schema(
        description = "Item involved in the transaction.",
        example = "{ 'productId': 5, 'quantity': 2, 'price': 50.00 }"
    )
    val createItem: TransactionItemCreateRequest,
    val responseItem: TransactionItemResponse,
    val updateItem: TransactionItemUpdateRequest,

    @Schema(
        description = "Payment details for the purchase.",
        example = "{ 'id': 1, 'userId': 2, 'amount': 150.00, 'date': '2023-07-11' }"
    )
    val createPayment: UserPaymentTransactionCreateRequest,
    val responsePayment: UserPaymentTransactionResponse,
    val updatePayment: UserPaymentTransactionUpdateRequest

) : BaseEntity()









