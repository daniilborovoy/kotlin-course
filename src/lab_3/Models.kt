enum class Category {
    ELECTRONICS,
    GROCERIES,
    FASHION,
    HOME,
    TOYS
}

data class Product(
    val name: String,
    val category: Category,
    var price: Double,
    var stock: Int
)

data class Client(
    val name: String,
    val contact: String,
    val orderHistory: MutableList<Order> = mutableListOf()
)

data class Order(
    val products: MutableList<Product>,
    val client: Client,
    var status: String = "Pending"
) {
    override fun toString(): String {
        val productList = products.joinToString { "${it.name} (цена: ${it.price}, остаток: ${it.stock})" }
        return "Заказ для клиента: ${client.name}, Статус: $status, Продукты: [$productList]"
    }
}