package zeroone.developers.billingappk

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.data.jpa.repository.config.EnableJpaRepositories

@SpringBootApplication
@EnableJpaRepositories(repositoryBaseClass = BaseRepositoryImpl::class)
class BillingAppkApplication

fun main(args: Array<String>) {
	runApplication<BillingAppkApplication>(*args)
}
