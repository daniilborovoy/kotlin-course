fun main() {
    val shop = Shop()
    val menu = ShopMenu(shop)

    shop.addProduct(Product("iPhone 15 Pro", Category.ELECTRONICS, 999.99, 10))
    shop.addProduct(Product("MacBook Pro", Category.ELECTRONICS, 1999.99, 5))
    shop.addProduct(Product("Milk", Category.GROCERIES, 1.99, 100))
    shop.addProduct(Product("Bread", Category.GROCERIES, 0.99, 50))
    shop.addProduct(Product("Toy Car", Category.TOYS, 19.99, 20))

    menu.display()
}