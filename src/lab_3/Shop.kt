class Shop {
    val catalog = mutableListOf<Product>()
    private val clients = mutableListOf<Client>()
    private val orders = mutableListOf<Order>()

    fun addProduct(product: Product) {
        catalog.add(product)
        println("Продукт добавлен в каталог: $product")
    }

    fun registerClient(client: Client) {
        clients.add(client)
        println("Клиент зарегистрирован: ${client.name}")
    }

    fun createOrder(client: Client, products: List<Product>): Order {
        val selectedProducts = mutableListOf<Product>()
        val unavailableProducts = mutableListOf<Product>()

        // Проверяем каждый продукт на наличие в достаточном количестве
        for (product in products) {
            if (product.stock > 0) {
                selectedProducts.add(product)
            } else {
                unavailableProducts.add(product)
            }
        }

        // Если есть продукты, которых нет в наличии
        if (unavailableProducts.isNotEmpty()) {
            println("Следующие продукты недоступны в достаточном количестве и не могут быть добавлены в заказ:")
            unavailableProducts.forEach { println("${it.name} - Нет в наличии") }
            return Order(emptyList<MutableList<Product>>() as MutableList<Product>, client)  // Возвращаем пустой заказ, так как не можем оформить
        }

        // Уменьшаем количество на складе после того, как все продукты добавлены в заказ
        selectedProducts.forEach { it.stock-- }

        // Создаем заказ
        val order = Order(selectedProducts, client)
        client.orderHistory.add(order)
        orders.add(order)

        println("Заказ успешно создан: $order")
        return order
    }

    fun calculateOrderTotalWithDiscount(order: Order, discountRules: List<(Order) -> Double>): Double {
        val baseTotal = order.products.sumOf { it.price }
        var discount = 0.0

        for (rule in discountRules) {
            discount += rule(order)
        }

        val totalWithDiscount = (baseTotal - discount).coerceAtLeast(0.0)
        println("Базовая стоимость заказа: $baseTotal, скидка: $discount, итоговая стоимость: $totalWithDiscount")
        return totalWithDiscount
    }


    fun searchProductsByCategory(category: Category): List<Product> {
        return catalog.filter { it.category == category }
    }

    fun searchProductsByPrice(minPrice: Double, maxPrice: Double): List<Product> {
        return catalog.filter { it.price in minPrice..maxPrice }
    }

    fun calculateOrderTotal(order: Order): Double {
        return order.products.sumOf { it.price }
    }

    fun viewCatalog() {
        println("Каталог продуктов:")
        catalog.forEach { println(it) }
        println()
    }

    fun viewClients() {
        println("Список клиентов:")
        clients.forEach { println(it.name) }
    }
}