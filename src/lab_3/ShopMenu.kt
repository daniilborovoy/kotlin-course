class ShopMenu(private val shop: Shop) {
    private var currentClient: Client? = null
    private val cart = mutableListOf<Product>()

    private val discountRules = listOf<(Order) -> Double>(
        { order -> if (order.products.sumOf { it.price } > 500) order.products.sumOf { it.price } * 0.10 else 0.0 },
        { order -> order.products.filter { it.category == Category.TOYS }.sumOf { it.price } * 0.05 },
        { order -> if (order.products.size > 5) 50.0 else 0.0 }
    )

    fun display() {
        println("\nДобро пожаловать в магазин!\n")
        while (true) {
            val menuOptions = buildMenu()
            println(menuOptions.menuText)
            val userChoice = readlnOrNull()?.toIntOrNull()

            if (userChoice != null && userChoice in 1..menuOptions.actions.size) {
                menuOptions.actions[userChoice - 1].invoke()
                waitForUserInput()
            } else {
                println("Некорректный выбор. Попробуйте снова.")
            }
        }
    }

    private fun buildMenu(): MenuOptions {
        val menuBuilder = StringBuilder()
        val actions = mutableListOf<() -> Unit>()
        var optionNumber = 1

        menuBuilder.appendLine("Выберите действие:")

        menuBuilder.appendLine("${optionNumber++}. Просмотреть каталог")
        actions.add { shop.viewCatalog() }

        menuBuilder.appendLine("${optionNumber++}. Поиск продуктов по категории")
        actions.add { searchProductsByCategory() }

        menuBuilder.appendLine("${optionNumber++}. Поиск продуктов по цене")
        actions.add { searchProductsByPrice() }

        menuBuilder.appendLine("${optionNumber++}. Анализ продаж по категориям")
        actions.add { analyzeSalesByCategory() }

        if (currentClient == null) {
            // Пункты для неавторизованных пользователей
            menuBuilder.appendLine("${optionNumber++}. Зарегистрироваться как клиент")
            actions.add { registerClient() }
            menuBuilder.appendLine("${optionNumber++}. Выйти")
            actions.add { exitShop() }
        } else {
            // Пункты для авторизованных пользователей
            menuBuilder.appendLine("${optionNumber++}. Добавить продукт в корзину")
            actions.add { addProductToCart() }

            menuBuilder.appendLine("${optionNumber++}. Просмотреть корзину")
            actions.add { viewCart() }

            menuBuilder.appendLine("${optionNumber++}. Удалить продукт из корзины")
            actions.add { removeProductFromCart() }

            menuBuilder.appendLine("${optionNumber++}. Очистить корзину")
            actions.add { clearCart() }

            menuBuilder.appendLine("${optionNumber++}. Оформить заказ")
            actions.add { placeOrder() }

            menuBuilder.appendLine("${optionNumber++}. Просмотреть историю заказов")
            actions.add { viewOrderHistory() }

            menuBuilder.appendLine("${optionNumber++}. Выйти")
            actions.add { exitShop() }
        }

        return MenuOptions(menuBuilder.toString(), actions)
    }

    data class MenuOptions(val menuText: String, val actions: List<() -> Unit>)

    private fun analyzeSalesByCategory() {
        if (currentClient == null) {
            println("Вы не зарегистрированы! Пожалуйста, зарегистрируйтесь для анализа продаж.")
            return
        }

        val categorySales = mutableMapOf<Category, Double>()

        currentClient?.orderHistory?.forEach { order ->
            order.products.forEach { product ->
                val category = product.category
                val totalPrice = product.price

                // Добавляем стоимость продукта в соответствующую категорию
                categorySales[category] = categorySales.getOrDefault(category, 0.0) + totalPrice
            }
        }

        // Выводим результаты анализа
        if (categorySales.isEmpty()) {
            println("Нет данных для анализа продаж.")
        } else {
            println("Анализ продаж по категориям:")
            categorySales.forEach { (category, totalSales) ->
                println("Категория: $category, Общая сумма продаж: $totalSales")
            }
        }
    }

    private fun registerClient() {
        println("Введите ваше имя:")
        val name = readlnOrNull().orEmpty()
        println("Введите ваш контакт (email/телефон):")
        val contact = readlnOrNull().orEmpty()
        val client = Client(name, contact)
        shop.registerClient(client)
        currentClient = client
        println("Вы успешно зарегистрированы!")
    }

    private fun searchProductsByCategory() {
        println("Введите категорию для поиска (доступные: ELECTRONICS, GROCERIES, FASHION, HOME, TOYS):")
        val category = runCatching { Category.valueOf(readlnOrNull().orEmpty().uppercase()) }.getOrNull()
        if (category != null) {
            val products = shop.searchProductsByCategory(category)
            if (products.isEmpty()) {
                println("Продукты не найдены в категории '$category'.")
            } else {
                println("Найденные продукты:")
                products.forEach { println(it) }
            }
        } else {
            println("Некорректная категория.")
        }
    }

    private fun searchProductsByPrice() {
        println("Введите минимальную цену:")
        val minPriceInput = readlnOrNull()

        val minPrice = try {
            minPriceInput?.toDouble()?.takeIf { it >= 0 } ?: run {
                println("Некорректная минимальная цена. Убедитесь, что введено положительное число.")
                return
            }
        } catch (e: NumberFormatException) {
            println("Некорректная минимальная цена.")
            return
        }

        println("Введите максимальную цену:")
        val maxPriceInput = readlnOrNull()

        val maxPrice = try {
            maxPriceInput?.toDouble()?.takeIf { it >= 0 } ?: run {
                println("Некорректная максимальная цена. Убедитесь, что введено положительное число.")
                return
            }
        } catch (e: NumberFormatException) {
            println("Некорректная максимальная цена.")
            return
        }

        val products = shop.searchProductsByPrice(minPrice, maxPrice)
        if (products.isEmpty()) {
            println("Нет продуктов в заданном диапазоне.")
        } else {
            println("Найденные продукты:")
            products.forEach { println(it) }
        }
    }

    private fun addProductToCart() {
        println("Введите название продукта для добавления в корзину:")
        val productName = readlnOrNull().orEmpty()
        val product = shop.catalog.find { it.name.equals(productName, ignoreCase = true) }
        if (product != null) {
            cart.add(product)
            println("Продукт '${product.name}' добавлен в корзину.")
        } else {
            println("Продукт не найден.")
        }
    }

    private fun viewCart() {
        if (cart.isEmpty()) {
            println("Ваша корзина пуста.")
        } else {
            println("Содержимое вашей корзины:")
            cart.forEach { println(it) }
        }
    }

    private fun removeProductFromCart() {
        if (cart.isEmpty()) {
            println("Ваша корзина пуста.")
        } else {
            println("Введите название продукта для удаления из корзины:")
            val productName = readlnOrNull().orEmpty()
            val product = cart.find { it.name.equals(productName, ignoreCase = true) }
            if (product != null) {
                cart.remove(product)
                println("Продукт '${product.name}' удален из корзины.")
            } else {
                println("Продукт не найден в корзине.")
            }
        }
    }

    private fun clearCart() {
        cart.clear()
        println("Корзина очищена.")
    }

    private fun placeOrder() {
        if (currentClient == null) {
            println("Вы не зарегистрированы! Пожалуйста, зарегистрируйтесь.")
            return
        }

        if (cart.isEmpty()) {
            println("Ваша корзина пуста. Добавьте продукты перед оформлением заказа.")
        } else {
            val order = shop.createOrder(currentClient!!, cart)
            val totalWithDiscount = shop.calculateOrderTotalWithDiscount(order, discountRules)
            cart.clear()
            println("Ваш заказ оформлен! Итоговая стоимость с учетом скидок: $totalWithDiscount")
        }
    }

    private fun viewOrderHistory() {
        if (currentClient == null) {
            println("Вы не зарегистрированы! Пожалуйста, зарегистрируйтесь.")
            return
        }

        println("История ваших заказов:")
        currentClient!!.orderHistory.forEach { println(it) }
    }

    private fun exitShop() {
        println("Спасибо за использование нашего магазина!")
        kotlin.system.exitProcess(0)
    }

    private fun waitForUserInput() {
        println("\nНажмите любую клавишу для возврата в меню...")
        readlnOrNull()
    }
}
