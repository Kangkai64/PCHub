package pchub.model;

import pchub.utils.ConsoleUtils;
import java.util.Scanner;
import java.sql.SQLException;
import pchub.dao.ProductCategoryDao;
import pchub.dao.ProductCatalogueDao;
import pchub.dao.ProductCatalogueItemDao;
import java.util.List;
import java.time.LocalDateTime;
import pchub.Main;
import pchub.model.enums.OrderStatus;
import pchub.model.enums.UserRole;
import pchub.utils.ProductSorter;

public class Admin extends User {
    private static Scanner scanner = new Scanner(System.in);

    public Admin(User user) {
        super(user.getUserId(), user.getUsername(), user.getEmail(), user.getPassword(), user.getRegistrationDate(),
                user.getLastLogin(), user.getStatus(), user.getFullName(), user.getPhone(), user.getRole());
    }

    public static void manageProducts() {
        ConsoleUtils.printHeader("      Product Management     ");
        System.out.println("1. View All Products");
        System.out.println("2. Add New Product");
        System.out.println("3. Update Product");
        System.out.println("4. Delete Product");
        System.out.println("0. Back to Admin Menu");

        int choice = ConsoleUtils.getIntInput(scanner, "Enter your choice: ", 0, 4);

        switch (choice) {
            case 1:
                viewAllProducts();
                break;
            case 2:
                addNewProduct();
                break;
            case 3:
                updateProduct();
                break;
            case 4:
                deleteProduct();
                break;
            case 0:
                return;
        }
    }

    public static void viewAllProducts() {
        ConsoleUtils.printHeader("      All Products     ");
        try {
            Product[] products = Product.getAllProducts();
            if (products == null || products.length == 0) {
                System.out.println("No products found.");
            } else {
                System.out.println("ID | Name | Price | Stock | Category");
                System.out.println("------------------------------------------");
                for (Product product : products) {
                    if (product != null) {
                        System.out.printf("%s | %s | $%.2f | %d | %s\n",
                                product.getProductID(),
                                product.getName(),
                                product.getUnitPrice(),
                                product.getCurrentQuantity(),
                                product.getCategory());
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Error fetching products: " + e.getMessage());
        }
    }

    public static void addNewProduct() {
        ConsoleUtils.printHeader("      Add New Product      ");
        String name = ConsoleUtils.getStringInput(scanner, "Enter product name: ");
        double price = ConsoleUtils.getDoubleInput(scanner, "Enter product price: ", 0.01, Double.MAX_VALUE);
        int stockQuantity = ConsoleUtils.getIntInput(scanner, "Enter stock quantity: ", 0, Integer.MAX_VALUE);
        String category = ConsoleUtils.getStringInput(scanner, "Enter product category: ");
        String description = ConsoleUtils.getStringInput(scanner, "Enter product description: ");

        try {
            Product product = new Product();
            product.setName(name);
            product.setUnitPrice(price);
            product.setCurrentQuantity(stockQuantity);
            product.setCategory(category);
            product.setDescription(description);

            boolean success = Product.addProduct(product);
            if (success) {
                System.out.println("Product added successfully!");
            } else {
                System.out.println("Failed to add product.");
            }
        } catch (Exception e) {
            System.out.println("Error adding product: " + e.getMessage());
        }
    }

    public static void updateProduct() {
        ConsoleUtils.printHeader("      Update Product      ");
        String productId = ConsoleUtils.getStringInput(scanner, "Enter product ID to update: ");

        try {
            Product product = Product.getProduct(productId);
            if (product == null) {
                System.out.println("Product not found.");
                return;
            }

            System.out.println("Current product details:");
            System.out.println("Name: " + product.getName());
            System.out.println("Price: $" + product.getUnitPrice());
            System.out.println("Stock: " + product.getCurrentQuantity());
            System.out.println("Category: " + product.getCategory());
            System.out.println("Description: " + product.getDescription());

            System.out.println("\nEnter new details (press Enter to keep current value):");
            String name = ConsoleUtils.getStringInput(scanner, "New name: ");
            if (!name.isEmpty()) {
                product.setName(name);
            }

            String priceStr = ConsoleUtils.getStringInput(scanner, "New price: ");
            if (!priceStr.isEmpty()) {
                double price = Double.parseDouble(priceStr);
                product.setUnitPrice(price);
            }

            String stockStr = ConsoleUtils.getStringInput(scanner, "New stock quantity: ");
            if (!stockStr.isEmpty()) {
                int stock = Integer.parseInt(stockStr);
                product.setCurrentQuantity(stock);
            }

            String category = ConsoleUtils.getStringInput(scanner, "New category: ");
            if (!category.isEmpty()) {
                product.setCategory(category);
            }

            String description = ConsoleUtils.getStringInput(scanner, "New description: ");
            if (!description.isEmpty()) {
                product.setDescription(description);
            }

            boolean success = Product.updateProduct(product);
            if (success) {
                System.out.println("Product updated successfully!");
            } else {
                System.out.println("Failed to update product.");
            }
        } catch (Exception e) {
            System.out.println("Error updating product: " + e.getMessage());
        }
    }

    public static void deleteProduct() {
        ConsoleUtils.printHeader("      Delete Product      ");
        String productId = ConsoleUtils.getStringInput(scanner, "Enter product ID to delete: ");

        try {
            boolean success = Product.deleteProduct(productId);
            if (success) {
                System.out.println("Product deleted successfully!");
            } else {
                System.out.println("Failed to delete product.");
            }
        } catch (Exception e) {
            System.out.println("Error deleting product: " + e.getMessage());
        }
    }

    public static void manageCategories() {
        ProductCategoryDao categoryDao = new ProductCategoryDao();
        try {
            List<ProductCategory> categories = categoryDao.findAll();

            ConsoleUtils.printHeader("     Category Management     ");
            System.out.println("1. View Categories");
            System.out.println("2. Add Category");
            System.out.println("3. Update Category");
            System.out.println("4. Delete Category");
            System.out.println("5. Back");

            int choice = ConsoleUtils.getIntInput(scanner, "Enter your choice: ", 1, 5);

            switch (choice) {
                case 1:
                    viewCategories(categories);
                    break;
                case 2:
                    addCategory(categoryDao);
                    break;
                case 3:
                    updateCategory(categoryDao, categories);
                    break;
                case 4:
                    deleteCategory(categoryDao, categories);
                    break;
                case 5:
                    return;
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    public static void viewCategories(List<ProductCategory> categories) {
        ConsoleUtils.printHeader("     All Categories     ");
        for (ProductCategory category : categories) {
            System.out.println("ID: " + category.getProduct_categoryID());
            System.out.println("Name: " + category.getName());
            System.out.println("Description: " + category.getDescription());
            System.out.println("Parent Category: " + category.getParentCategory());
            System.out.println("-------------------");
        }
    }

    public static void addCategory(ProductCategoryDao categoryDao) {
        ConsoleUtils.printHeader("     Add New Category     ");
        String id = ConsoleUtils.getStringInput(scanner, "Enter category ID: ");
        String name = ConsoleUtils.getStringInput(scanner, "Enter category name: ");
        String description = ConsoleUtils.getStringInput(scanner, "Enter category description: ");
        String parentId = ConsoleUtils.getStringInput(scanner, "Enter parent category ID (leave empty if none): ");

        ProductCategory category = new ProductCategory(id, name, description);
        if (!parentId.isEmpty()) {
            category.setParentCategory(parentId);
        }

        try {
            categoryDao.insert(category);
            System.out.println("Category added successfully!");
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    public static void updateCategory(ProductCategoryDao categoryDao, List<ProductCategory> categories) {
        ConsoleUtils.printHeader("     Update Category     ");
        String id = ConsoleUtils.getStringInput(scanner, "Enter category ID to update: ");

        ProductCategory category = null;
        for (ProductCategory c : categories) {
            if (c.getProduct_categoryID().equals(id)) {
                category = c;
                break;
            }
        }

        if (category == null) {
            System.out.println("Category not found!");
            return;
        }

        String name = ConsoleUtils.getStringInput(scanner, "Enter new name (leave empty to keep current): ");
        String description = ConsoleUtils.getStringInput(scanner, "Enter new description (leave empty to keep current): ");
        String parentId = ConsoleUtils.getStringInput(scanner, "Enter new parent category ID (leave empty to keep current): ");

        if (!name.isEmpty()) {
            category.setName(name);
        }
        if (!description.isEmpty()) {
            category.setDescription(description);
        }
        if (!parentId.isEmpty()) {
            category.setParentCategory(parentId);
        }

        try {
            categoryDao.update(category);
            System.out.println("Category updated successfully!");
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    public static void deleteCategory(ProductCategoryDao categoryDao, List<ProductCategory> categories) {
        ConsoleUtils.printHeader("     Delete Category     ");
        String id = ConsoleUtils.getStringInput(scanner, "Enter category ID to delete: ");

        ProductCategory category = null;
        for (ProductCategory c : categories) {
            if (c.getProduct_categoryID().equals(id)) {
                category = c;
                break;
            }
        }

        if (category == null) {
            System.out.println("Category not found!");
            return;
        }

        try {
            categoryDao.delete(id);
            System.out.println("Category deleted successfully!");
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    public static void manageProductCatalogues() {
        ConsoleUtils.printHeader("      Manage Product Catalogues      ");
        boolean back = false;
        try {
            ProductCatalogueDao catalogueDao = new ProductCatalogueDao();
            ProductCatalogueItemDao itemDao = new ProductCatalogueItemDao();

            while (!back) {
                System.out.println("\n1. View All Catalogues");
                System.out.println("2. Add New Catalogue");
                System.out.println("3. Update Catalogue");
                System.out.println("4. Delete Catalogue");
                System.out.println("5. Manage Catalogue Items");
                System.out.println("6. Back to Admin Menu");

                int choice = ConsoleUtils.getIntInput(scanner, "Enter your choice: ", 1, 6);

                switch (choice) {
                    case 1:
                        Main.displayAllCatalogues(catalogueDao);
                        break;
                    case 2:
                        addNewCatalogue(catalogueDao);
                        break;
                    case 3:
                        updateCatalogue(catalogueDao);
                        break;
                    case 4:
                        deleteCatalogue(catalogueDao);
                        break;
                    case 5:
                        manageCatalogueItems(catalogueDao, itemDao);
                        break;
                    case 6:
                        back = true;
                        break;
                }
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    public static void addNewCatalogue(ProductCatalogueDao catalogueDao) throws SQLException {
        ConsoleUtils.printHeader("      Add New Catalogue      ");

        String name = ConsoleUtils.getStringInput(scanner, "Enter catalogue name: ");
        String description = ConsoleUtils.getStringInput(scanner, "Enter description: ");
        LocalDateTime startDate = ConsoleUtils.getDateTimeInput(scanner, "Enter start date (yyyy-MM-dd HH:mm): ");
        LocalDateTime endDate = ConsoleUtils.getDateTimeInput(scanner, "Enter end date (yyyy-MM-dd HH:mm): ");

        ProductCatalogue catalogue = new ProductCatalogue();
        catalogue.setName(name);
        catalogue.setDescription(description);
        catalogue.setStartDate(startDate);
        catalogue.setEndDate(endDate);

        if (catalogueDao.insert(catalogue)) {
            System.out.println("Catalogue added successfully!");
            System.out.println("Catalogue ID: " + catalogue.getCatalogueID());
        } else {
            System.out.println("Failed to add catalogue.");
        }
    }

    public static void updateCatalogue(ProductCatalogueDao catalogueDao) throws SQLException {
        ConsoleUtils.printHeader("      Update Catalogue      ");
        Main.displayAllCatalogues(catalogueDao);

        String catalogueID = ConsoleUtils.getStringInput(scanner, "Enter catalogue ID to update: ");
        ProductCatalogue catalogue = catalogueDao.findById(catalogueID);

        if (catalogue == null) {
            System.out.println("Catalogue not found.");
            return;
        }

        System.out.println("\nCurrent details:");
        System.out.println("Name: " + catalogue.getName());
        System.out.println("Description: " + catalogue.getDescription());
        System.out.println("Start Date: " + catalogue.getStartDate());
        System.out.println("End Date: " + catalogue.getEndDate());

        String name = ConsoleUtils.getStringInput(scanner, "Enter new name (press Enter to keep current): ");
        String description = ConsoleUtils.getStringInput(scanner, "Enter new description (press Enter to keep current): ");
        LocalDateTime startDate = ConsoleUtils.getDateTimeInput(scanner, "Enter new start date (yyyy-MM-dd HH:mm) (press Enter to keep current): ");
        LocalDateTime endDate = ConsoleUtils.getDateTimeInput(scanner, "Enter new end date (yyyy-MM-dd HH:mm) (press Enter to keep current): ");

        if (!name.isEmpty()) catalogue.setName(name);
        if (!description.isEmpty()) catalogue.setDescription(description);
        if (startDate != null) catalogue.setStartDate(startDate);
        if (endDate != null) catalogue.setEndDate(endDate);

        if (catalogueDao.update(catalogue)) {
            System.out.println("Catalogue updated successfully!");
        } else {
            System.out.println("Failed to update catalogue.");
        }
    }

    public static void deleteCatalogue(ProductCatalogueDao catalogueDao) throws SQLException {
        ConsoleUtils.printHeader("      Delete Catalogue      ");
        Main.displayAllCatalogues(catalogueDao);

        String catalogueID = ConsoleUtils.getStringInput(scanner, "Enter catalogue ID to delete: ");

        if (catalogueDao.delete(catalogueID)) {
            System.out.println("Catalogue deleted successfully!");
        } else {
            System.out.println("Failed to delete catalogue.");
        }
    }

    public static void manageCatalogueItems(ProductCatalogueDao catalogueDao, ProductCatalogueItemDao itemDao) throws SQLException {
        ConsoleUtils.printHeader("      Manage Catalogue Items      ");
        Main.displayAllCatalogues(catalogueDao);

        String catalogueID = ConsoleUtils.getStringInput(scanner, "Enter catalogue ID to manage items: ");
        ProductCatalogue catalogue = catalogueDao.findById(catalogueID);

        if (catalogue == null) {
            System.out.println("Catalogue not found.");
            return;
        }

        boolean back = false;
        while (!back) {
            System.out.println("\n1. View Items");
            System.out.println("2. Add Item");
            System.out.println("3. Update Item Price");
            System.out.println("4. Remove Item");
            System.out.println("5. Back to Catalogue Menu");

            int choice = ConsoleUtils.getIntInput(scanner, "Enter your choice: ", 1, 5);

            switch (choice) {
                case 1:
                    Main.displayCatalogueItems(catalogue);
                    break;
                case 2:
                    addItemToCatalogue(catalogue, itemDao);
                    break;
                case 3:
                    updateItemPrice(catalogue, itemDao);
                    break;
                case 4:
                    removeItemFromCatalogue(catalogue, itemDao);
                    break;
                case 5:
                    back = true;
                    break;
            }
        }
    }

    public static void addItemToCatalogue(ProductCatalogue catalogue, ProductCatalogueItemDao itemDao) throws SQLException {
        ConsoleUtils.printHeader("      Add Item to Catalogue      ");
        // Display available products
        Main.displayAllProducts();

        String productID = ConsoleUtils.getStringInput(scanner, "Enter product ID to add: ");

        // Get and validate the product
        Product product = Product.getProduct(productID);
        if (product == null) {
            System.out.println("Product not found. Please enter a valid product ID.");
            return;
        }

        double specialPrice = ConsoleUtils.getDoubleInput(scanner, "Enter special price: ");
        String notes = ConsoleUtils.getStringInput(scanner, "Enter notes (optional): ");

        ProductCatalogueItem item = new ProductCatalogueItem();
        item.setCatalogue(catalogue);
        item.setProduct(product);
        item.setSpecialPrice(specialPrice);
        item.setNotes(notes);

        if (itemDao.insert(item)) {
            System.out.println("Item added to catalogue successfully!");
        } else {
            System.out.println("Failed to add item to catalogue.");
        }
    }

    public static void updateItemPrice(ProductCatalogue catalogue, ProductCatalogueItemDao itemDao) throws SQLException {
        ConsoleUtils.printHeader("      Update Item Price      ");
        Main.displayCatalogueItems(catalogue);

        String itemID = ConsoleUtils.getStringInput(scanner, "Enter item ID to update: ");
        ProductCatalogueItem item = itemDao.findById(itemID);

        if (item == null) {
            System.out.println("Item not found.");
            return;
        }

        double newPrice = ConsoleUtils.getDoubleInput(scanner, "Enter new special price: ");
        item.setSpecialPrice(newPrice);

        if (itemDao.update(item)) {
            System.out.println("Item price updated successfully!");
        } else {
            System.out.println("Failed to update item price.");
        }
    }

    public static void removeItemFromCatalogue(ProductCatalogue catalogue, ProductCatalogueItemDao itemDao) throws SQLException {
        ConsoleUtils.printHeader("      Remove Item from Catalogue      ");
        Main.displayCatalogueItems(catalogue);

        String itemID = ConsoleUtils.getStringInput(scanner, "Enter item ID to remove: ");

        if (itemDao.delete(itemID)) {
            System.out.println("Item removed from catalogue successfully!");
        } else {
            System.out.println("Failed to remove item from catalogue.");
        }
    }

    public void manageUsers() {
        ConsoleUtils.printHeader("      Manage Users      ");
        System.out.println("1. View All Users");
        System.out.println("2. Add New User");
        System.out.println("3. Update User");
        System.out.println("4. Delete User");
        System.out.println("5. Back to Admin Menu");

        int choice = ConsoleUtils.getIntInput(scanner, "Enter your choice: ", 1, 5);

        switch (choice) {
            case 1:
                viewAllUsers();
                break;
            case 2:
                addUser();
                break;
            case 3:
                updateUser();
                break;
            case 4:
                new Admin(this).deleteUser();
                break;
            case 5:
                break;
        }
    }

    public static void viewAllUsers() {
        try {
            User[] users = User.getAllUsers();
            if (users == null || users.length == 0) {
                System.out.println("No users found.");
                return;
            }
            
            System.out.println("\n=== All Users ===");
            for (User user : users) {
                if (user != null) {  // Add null check for each user
                    System.out.println("ID: " + (user.getUserId() != null ? user.getUserId() : "N/A"));
                    System.out.println("Username: " + (user.getUsername() != null ? user.getUsername() : "N/A"));
                    System.out.println("Email: " + (user.getEmail() != null ? user.getEmail() : "N/A"));
                    System.out.println("Role: " + (user.getRole() != null ? user.getRole() : "N/A"));
                    System.out.println("-------------------");
                }
            }
        } catch (SQLException e) {
            System.out.println("Error retrieving users: " + e.getMessage());
        }
    }

    public static void addUser() {
        ConsoleUtils.printHeader("      Add New User      ");
        String username = ConsoleUtils.getStringInput(scanner, "Username: ");
        String email = ConsoleUtils.getStringInput(scanner, "Email: ");
        String password = ConsoleUtils.getStringInput(scanner, "Password: ");

        System.out.println("Select role:");
        System.out.println("1. Customer");
        System.out.println("2. Admin");
        int roleChoice = ConsoleUtils.getIntInput(scanner, "Choice: ", 1, 2);
        UserRole role = (roleChoice == 1) ? UserRole.CUSTOMER : UserRole.ADMIN;

        try {
            User user = new User();
            user.setUsername(username);
            user.setEmail(email);
            user.setPassword(password); // Will be hashed in the service
            user.setRole(role);

            boolean added = User.registerUser(user);
            if (added) {
                System.out.println("User added successfully!");
            } else {
                System.out.println("Failed to add user. Username or email may already be in use.");
            }
        } catch (Exception e) {
            System.out.println("Error adding user: " + e.getMessage());
        }
    }

    public static void updateUser() {
        ConsoleUtils.printHeader("      Update User      ");
        String userId = ConsoleUtils.getStringInput(scanner, "Enter user ID to update (format: C0001-C9999): ");

        // Validate user ID format
        if (!userId.matches("C\\d{4}")) {
            System.out.println("Invalid user ID format. Please use format C0001-C9999.");
            return;
        }

        try {
            User user = User.getUserById(userId);
            if (user == null) {
                System.out.println("User not found.");
                return;
            }

            System.out.println("Updating user: " + user.getUsername());
            System.out.println("(Leave fields blank to keep current values)");

            String email = ConsoleUtils.getStringInput(scanner, "New Email [" + user.getEmail() + "]: ");

            System.out.println("Current role: " + user.getRole());
            System.out.println("Select new role (or press Enter to keep current):");
            System.out.println("1. Customer");
            System.out.println("2. Admin");
            String roleChoiceStr = ConsoleUtils.getStringInput(scanner, "Choice: ");

            if (!email.isBlank()) user.setEmail(email);

            if (!roleChoiceStr.isBlank()) {
                int roleChoice = Integer.parseInt(roleChoiceStr);
                UserRole role = (roleChoice == 1) ? UserRole.CUSTOMER : UserRole.ADMIN;
                user.setRole(role);
            }

            boolean updated = User.updateUser(user);
            if (updated) {
                System.out.println("User updated successfully!");
            } else {
                System.out.println("Failed to update user.");
            }
        } catch (Exception e) {
            System.out.println("Error updating user: " + e.getMessage());
        }
    }

    public void deleteUser() {
        ConsoleUtils.printHeader("      Delete User      ");
        String userId = ConsoleUtils.getStringInput(scanner, "Enter user ID to delete (format: C0001-C9999): ");

        // Validate user ID format
        if (!userId.matches("C\\d{4}")) {
            System.out.println("Invalid user ID format. Please use format C0001-C9999.");
            return;
        }

        try {
            User user = User.getUserById(userId);
            if (user == null) {
                System.out.println("User not found.");
                return;
            }

            // Prevent deleting self
            if (user.getUserId().equals(this.getUserId())) {
                System.out.println("You cannot delete your own account.");
                return;
            }

            System.out.println("Are you sure you want to delete user: " + user.getUsername() + "?");
            String confirm = ConsoleUtils.getStringInput(scanner, "Type 'yes' to confirm: ");

            if (confirm.equalsIgnoreCase("yes")) {
                boolean deleted = User.deleteUser(userId);
                if (deleted) {
                    System.out.println("User deleted successfully!");
                } else {
                    System.out.println("Failed to delete user.");
                }
            } else {
                System.out.println("Deletion cancelled.");
            }
        } catch (Exception e) {
            System.out.println("Error deleting user: " + e.getMessage());
        }
    }

    public static void viewAllOrders() {
        ConsoleUtils.printHeader("      All Orders      ");
        try {
            Order[] orders = Order.getAllOrders();
            if (orders == null || orders.length == 0) {
                System.out.println("No orders found.");
            } else {
                System.out.println("Order ID | Customer | Date | Status | Total Amount");
                System.out.println("------------------------------------------");
                for (Order order : orders) {
                    if (order != null) {
                        System.out.printf("%s | %s | %s | %s | $%.2f\n",
                                order.getOrderId(),
                                order.getCustomerName(),
                                order.getOrderDate(),
                                order.getStatus(),
                                order.getTotalAmount());
                    }
                }

                int orderId = ConsoleUtils.getIntInput(scanner, "Enter order ID to view details (0 to go back): ", 0, Integer.MAX_VALUE);
                if (orderId > 0) {
                    Main.displayOrderDetails(orderId);

                    // Admin can update order status
                    System.out.println("\nDo you want to update the order status?");
                    String update = ConsoleUtils.getStringInput(scanner, "Type 'yes' to update: ");

                    if (update.equalsIgnoreCase("yes")) {
                        Admin.updateOrderStatus(orderId);
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Error fetching orders: " + e.getMessage());
        }
    }

    public static void updateOrderStatus(int orderId) {
        ConsoleUtils.printHeader("      Update Order Status      ");
        System.out.println("1. Processing");
        System.out.println("2. Shipped");
        System.out.println("3. Delivered");
        System.out.println("4. Cancelled");

        int statusChoice = ConsoleUtils.getIntInput(scanner, "Select new status: ", 1, 4);
        OrderStatus newStatus;

        switch (statusChoice) {
            case 1:
                newStatus = OrderStatus.PROCESSING;
                break;
            case 2:
                newStatus = OrderStatus.SHIPPED;
                break;
            case 3:
                newStatus = OrderStatus.DELIVERED;
                break;
            case 4:
                newStatus = OrderStatus.CANCELLED;
                break;
            default:
                newStatus = OrderStatus.PROCESSING;
        }

        try {
            boolean updated = Order.updateOrderStatus(String.valueOf(orderId), newStatus);
            if (updated) {
                System.out.println("Order status updated successfully!");
            } else {
                System.out.println("Failed to update order status.");
            }
        } catch (Exception e) {
            System.out.println("Error updating order status: " + e.getMessage());
        }
    }

    public static void generateReports() {
        ConsoleUtils.printHeader("      Generate Reports      ");
        System.out.println("1. Sales Report");
        System.out.println("2. Inventory Report");
        System.out.println("3. Customer Activity Report");
        System.out.println("4. Back to Admin Menu");

        int choice = ConsoleUtils.getIntInput(scanner, "Enter your choice: ", 1, 4);

        switch (choice) {
            case 1:
                generateSalesReport();
                break;
            case 2:
                generateInventoryReport();
                break;
            case 3:
                generateCustomerActivityReport();
                break;
            case 4:
                break;
        }
    }

    public static void generateSalesReport() {
        ConsoleUtils.printHeader("      Sales Report      ");
        System.out.println("1. Daily Sales");
        System.out.println("2. Weekly Sales");
        System.out.println("3. Monthly Sales");

        int periodChoice = ConsoleUtils.getIntInput(scanner, "Select period: ", 1, 3);
        String period;

        switch (periodChoice) {
            case 1:
                period = "daily";
                break;
            case 2:
                period = "weekly";
                break;
            case 3:
                period = "monthly";
                break;
            default:
                period = "monthly";
        }

        try {
            // In a real system, we would fetch actual sales data
            // Here we'll just simulate a report
            System.out.println("\n========================================");
            System.out.println("           PCHub Sales Report           ");
            System.out.println("========================================");
            System.out.println("Period: " + period.toUpperCase());
            System.out.println("Generated on: " + java.time.LocalDate.now());

            // Sample data
            System.out.println("\nTotal Sales: $15,782.45");
            System.out.println("Number of Orders: 43");
            System.out.println("Average Order Value: $367.03");

            System.out.println("\nTop Selling Products:");
            System.out.println("1. Dell XPS 15 Laptop - 12 units");
            System.out.println("2. Logitech MX Master Mouse - 28 units");
            System.out.println("3. Samsung 27\" Monitor - 15 units");

            System.out.println("\nSales by Category:");
            System.out.println("- Laptops: $8,245.00 (52.2%)");
            System.out.println("- Peripherals: $3,127.45 (19.8%)");
            System.out.println("- Components: $2,410.00 (15.3%)");
            System.out.println("- Monitors: $2,000.00 (12.7%)");

            System.out.println("========================================");
            System.out.println("Report generated successfully!");
        } catch (Exception e) {
            System.out.println("Error generating sales report: " + e.getMessage());
        }
    }

    public static void generateInventoryReport() {
        ConsoleUtils.printHeader("      Inventory Report      ");

        try {
            Product[] products = Product.getAllProducts();

            System.out.println("\n========================================");
            System.out.println("         PCHub Inventory Report         ");
            System.out.println("========================================");
            System.out.println("Generated on: " + java.time.LocalDate.now());

            int totalProducts = products.length;
            int lowStockProducts = 0;
            int outOfStockProducts = 0;

            for (Product product : products) {
                if (product.getCurrentQuantity() == 0) {
                    outOfStockProducts++;
                } else if (product.getCurrentQuantity() < 5) {
                    lowStockProducts++;
                }
            }

            System.out.println("\nInventory Summary:");
            System.out.println("Total Products: " + totalProducts);
            System.out.println("Low Stock Products: " + lowStockProducts);
            System.out.println("Out of Stock Products: " + outOfStockProducts);

            System.out.println("\nLow Stock Items (Less than 5 units):");
            System.out.println("------------------------------------------");
            for (Product product : products) {
                if (product.getCurrentQuantity() > 0 && product.getCurrentQuantity() < 5) {
                    System.out.printf("%s - %d units remaining\n", product.getName(), product.getCurrentQuantity());
                }
            }

            System.out.println("\nOut of Stock Items:");
            System.out.println("------------------------------------------");
            for (Product product : products) {
                if (product.getCurrentQuantity() == 0) {
                    System.out.println(product.getName());
                }
            }

            System.out.println("========================================");
            System.out.println("Report generated successfully!");
        } catch (Exception e) {
            System.out.println("Error generating inventory report: " + e.getMessage());
        }
    }

    public static void generateCustomerActivityReport() {
        ConsoleUtils.printHeader("      Customer Activity Report      ");

        try {
            // In a real system, we would fetch actual customer data
            // Here we'll just simulate a report
            System.out.println("\n========================================");
            System.out.println("     PCHub Customer Activity Report     ");
            System.out.println("========================================");
            System.out.println("Generated on: " + java.time.LocalDate.now());

            System.out.println("\nCustomer Summary:");
            System.out.println("Total Customers: 128");
            System.out.println("New Customers (Last 30 days): 15");
            System.out.println("Active Customers (Last 30 days): 47");

            System.out.println("\nTop Customers (By Purchase Amount):");
            System.out.println("1. John Smith - $2,450.00");
            System.out.println("2. Emily Johnson - $1,875.50");
            System.out.println("3. Michael Brown - $1,635.25");

            System.out.println("\nCustomer Retention Rate: 78%");
            System.out.println("Average Order Frequency: 1.8 orders/month");

            System.out.println("========================================");
            System.out.println("Report generated successfully!");
        } catch (Exception e) {
            System.out.println("Error generating customer activity report: " + e.getMessage());
        }
    }

    public static void displayProducts() {
        ConsoleUtils.printHeader("      Products      ");
        try {
            Product[] products = Product.getAllProducts();
            if (products == null || products.length == 0) {
                System.out.println("No products found.");
                return;
            }

            // Display sorting options
            System.out.println("\nSort by:");
            System.out.println("1. Name (A-Z)");
            System.out.println("2. Name (Z-A)");
            System.out.println("3. Price (Low to High)");
            System.out.println("4. Price (High to Low)");
            System.out.println("5. Stock (Low to High)");
            System.out.println("6. Stock (High to Low)");
            System.out.println("7. Category (A-Z)");
            System.out.println("8. Category (Z-A)");
            System.out.println("9. No Sorting");

            int sortChoice = ConsoleUtils.getIntInput(scanner, "Enter your choice: ", 1, 9);
            ProductSorter.SortCriteria criteria = null;

            switch (sortChoice) {
                case 1:
                    criteria = ProductSorter.SortCriteria.NAME_ASC;
                    break;
                case 2:
                    criteria = ProductSorter.SortCriteria.NAME_DESC;
                    break;
                case 3:
                    criteria = ProductSorter.SortCriteria.PRICE_ASC;
                    break;
                case 4:
                    criteria = ProductSorter.SortCriteria.PRICE_DESC;
                    break;
                case 5:
                    criteria = ProductSorter.SortCriteria.STOCK_ASC;
                    break;
                case 6:
                    criteria = ProductSorter.SortCriteria.STOCK_DESC;
                    break;
                case 7:
                    criteria = ProductSorter.SortCriteria.CATEGORY_ASC;
                    break;
                case 8:
                    criteria = ProductSorter.SortCriteria.CATEGORY_DESC;
                    break;
                case 9:
                    break;
            }

            // Sort products if a criteria was selected
            if (criteria != null) {
                products = ProductSorter.sortProducts(products, criteria);
            }

            System.out.println("\nID | Name | Price | Stock | Category");
            System.out.println("------------------------------------------");
            for (Product product : products) {
                if (product != null) {
                    System.out.printf("%s | %s | $%.2f | %d | %s\n",
                            product.getProductID(),
                            product.getName(),
                            product.getUnitPrice(),
                            product.getCurrentQuantity(),
                            product.getCategory());
                }
            }

            String productId = ConsoleUtils.getStringInput(scanner, "Enter product ID to view details (or press Enter to go back): ");
            if (!productId.isEmpty()) {
                Product product = Product.getProduct(productId);
                if (product != null) {
                    displayProductDetails(product);
                } else {
                    System.out.println("Product not found.");
                }
            }
        } catch (Exception e) {
            System.out.println("Error fetching products: " + e.getMessage());
        }
    }

    public static void displayProductDetails(Product product) {
        if (product == null) {
            System.out.println("Product not found.");
            return;
        }

        System.out.println("\n===== Product Details =====");
        System.out.println("ID: " + product.getProductID());
        System.out.println("Name: " + product.getName());
        System.out.println("Brand: " + product.getBrand());
        System.out.println("Category: " + product.getCategory());
        System.out.println("Price: $" + product.getUnitPrice());
        System.out.println("Stock: " + product.getCurrentQuantity());
        System.out.println("Description: " + product.getDescription());
        System.out.println("Specifications: " + product.getSpecifications());
    }

    public static void displayLowStockProducts() {
        ConsoleUtils.printHeader("      Low Stock Products      ");
        try {
            Product[] products = Product.getAllProducts();
            if (products == null || products.length == 0) {
                System.out.println("No products found.");
                return;
            }

            boolean hasLowStock = false;
            for (Product product : products) {
                if (product != null) {
                    if (product.getCurrentQuantity() == 0) {
                        System.out.printf("%s - Out of stock\n", product.getName());
                        hasLowStock = true;
                    } else if (product.getCurrentQuantity() < 5) {
                        System.out.printf("%s - %d units remaining\n", product.getName(), product.getCurrentQuantity());
                        hasLowStock = true;
                    }
                }
            }

            if (!hasLowStock) {
                System.out.println("All products have sufficient stock.");
            }
        } catch (Exception e) {
            System.out.println("Error checking stock: " + e.getMessage());
        }
    }

    public static void displayOutOfStockProducts() {
        ConsoleUtils.printHeader("      Out of Stock Products      ");
        try {
            Product[] products = Product.getAllProducts();
            if (products == null || products.length == 0) {
                System.out.println("No products found.");
                return;
            }

            boolean hasOutOfStock = false;
            for (Product product : products) {
                if (product != null) {
                    if (product.getCurrentQuantity() == 0) {
                        System.out.printf("%s - Out of stock\n", product.getName());
                        hasOutOfStock = true;
                    }
                }
            }

            if (!hasOutOfStock) {
                System.out.println("No products are out of stock.");
            }
        } catch (Exception e) {
            System.out.println("Error checking out of stock products: " + e.getMessage());
        }
    }

    public static void displayStockStatus() {
        ConsoleUtils.printHeader("      Stock Status      ");
        try {
            Product[] products = Product.getAllProducts();
            if (products == null || products.length == 0) {
                System.out.println("No products found.");
                return;
            }

            for (Product product : products) {
                if (product != null) {
                    if (product.getCurrentQuantity() == 0) {
                        System.out.printf("%s - Out of stock\n", product.getName());
                    } else {
                        System.out.printf("%s - %d units in stock\n", product.getName(), product.getCurrentQuantity());
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Error checking stock status: " + e.getMessage());
        }
    }
}
