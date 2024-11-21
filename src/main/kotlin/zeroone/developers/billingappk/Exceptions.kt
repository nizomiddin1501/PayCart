package zeroone.developers.billingappk

import org.springframework.context.i18n.LocaleContextHolder
import org.springframework.context.support.ResourceBundleMessageSource

sealed class BillingExceptionHandler() : RuntimeException() {
    abstract fun errorCode(): ErrorCodes
    open fun getArguments(): Array<Any?>? = null

    fun getErrorMessage(resourceBundleMessageSource: ResourceBundleMessageSource): BaseMessage {
        val message = try {
            resourceBundleMessageSource.getMessage(
                errorCode().name, getArguments(), LocaleContextHolder.getLocale()
            )
        } catch (e: Exception) {
            e.message
        }
        return BaseMessage(errorCode().code, message)
    }
}

class UserAlreadyExistsException : BillingExceptionHandler() {
    override fun errorCode(): ErrorCodes {
        return ErrorCodes.USER_ALREADY_EXISTS
    }
}

class UserNotFoundException : BillingExceptionHandler() {
    override fun errorCode(): ErrorCodes {
        return ErrorCodes.USER_NOT_FOUND
    }
}

class UserPaymentTransactionAlreadyExistsException : BillingExceptionHandler() {
    override fun errorCode(): ErrorCodes {
        return ErrorCodes.USER_PAYMENT_TRANSACTION_ALREADY_EXISTS
    }
}

class UserPaymentTransactionNotFoundException : BillingExceptionHandler() {
    override fun errorCode(): ErrorCodes {
        return ErrorCodes.USER_PAYMENT_TRANSACTION_NOT_FOUND
    }
}

class TransactionAlreadyExistsException : BillingExceptionHandler() {
    override fun errorCode(): ErrorCodes {
        return ErrorCodes.TRANSACTION_ALREADY_EXISTS
    }
}

class TransactionNotFoundException : BillingExceptionHandler() {
    override fun errorCode(): ErrorCodes {
        return ErrorCodes.TRANSACTION_NOT_FOUND
    }
}

class TransactionItemAlreadyExistsException : BillingExceptionHandler() {
    override fun errorCode(): ErrorCodes {
        return ErrorCodes.TRANSACTION_ITEM_ALREADY_EXISTS
    }
}

class TransactionItemNotFoundException : BillingExceptionHandler() {
    override fun errorCode(): ErrorCodes {
        return ErrorCodes.TRANSACTION_ITEM_NOT_FOUND
    }
}

class ProductAlreadyExistsException : BillingExceptionHandler() {
    override fun errorCode(): ErrorCodes {
        return ErrorCodes.PRODUCT_ALREADY_EXISTS
    }
}

class ProductNotFoundException : BillingExceptionHandler() {
    override fun errorCode(): ErrorCodes {
        return ErrorCodes.PRODUCT_NOT_FOUND
    }
}


class CategoryAlreadyExistsException : BillingExceptionHandler() {
    override fun errorCode(): ErrorCodes {
        return ErrorCodes.CATEGORY_ALREADY_EXISTS
    }
}

class CategoryNotFoundException : BillingExceptionHandler() {
    override fun errorCode(): ErrorCodes {
        return ErrorCodes.CATEGORY_NOT_FOUND
    }
}





