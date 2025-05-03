package pchub.model;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Scanner;

import pchub.Main;
import pchub.dao.ProductCategoryDao;
import pchub.model.enums.OrderStatus;
import pchub.model.enums.UserRole;
import pchub.utils.ConsoleUtils;

public class Admin extends User {
    private static Scanner scanner = new Scanner(System.in);

    public Admin(User user) {
        super(user.getUserId(), user.getUsername(), user.getEmail(), user.getPassword(),
                user.getRegistrationDate(), user.getLastLogin(), user.getStatus(), user.getFullName(),
                user.getPhone(), user.getRole());
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
        Main.displayAllProducts();
        handleViewProductDetails();
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
            ProductCategory[] categories = new ProductCategory[1000]; // Assuming max 100 categories
            int count = 0;

            // Get all categories and store in array
            for (ProductCategory category : categoryDao.findAll()) {
                if (category != null) {
                    categories[count++] = category;
                }
            }

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

    public static void viewCategories(ProductCategory[] categories) {
        ConsoleUtils.printHeader("     All Categories     ");
        for (ProductCategory category : categories) {
            if (category != null) {
                System.out.println("ID: " + category.getProduct_categoryID());
                System.out.println("Name: " + category.getName());
                System.out.println("Description: " + category.getDescription());
                System.out.println("Parent Category: " + category.getParentCategory());
                ConsoleUtils.printDivider('-', 24);
            }
        }
    }

    public static void addCategory(ProductCategoryDao categoryDao) {
        ConsoleUtils.printHeader("     Add New Category     ");
        String product_categoryID = ConsoleUtils.getStringInput(scanner, "Enter category ID: ");
        String name = ConsoleUtils.getStringInput(scanner, "Enter category name: ");
        String description = ConsoleUtils.getStringInput(scanner, "Enter category description: ");
        String parentId = ConsoleUtils.getStringInput(scanner, "Enter parent category ID (leave empty if none): ");

        ProductCategory category = new ProductCategory(product_categoryID, name, description);
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

    public static void updateCategory(ProductCategoryDao categoryDao, ProductCategory[] categories) {
        ConsoleUtils.printHeader("     Update Category     ");
        String product_categoryID = ConsoleUtils.getStringInput(scanner, "Enter category ID to update: ");

        ProductCategory category = null;
        for (ProductCategory c : categories) {
            if (c != null && c.getProduct_categoryID().equals(product_categoryID)) {
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

    public static void deleteCategory(ProductCategoryDao categoryDao, ProductCategory[] categories) {
        ConsoleUtils.printHeader("     Delete Category     ");
        String product_categoryID = ConsoleUtils.getStringInput(scanner, "Enter category ID to delete: ");

        ProductCategory category = null;
        for (ProductCategory c : categories) {
            if (c != null && c.getProduct_categoryID().equals(product_categoryID)) {
                category = c;
                break;
            }
        }

        if (category == null) {
            System.out.println("Category not found!");
            return;
        }

        try {
            categoryDao.delete(product_categoryID);
            System.out.println("Category deleted successfully!");
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    public static void manageProductCatalogues() {
        ConsoleUtils.printHeader("      Manage Product Catalogues      ");
        try {
            boolean back = false;
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
                        Main.displayAllCatalogues();
                        break;
                    case 2:
                        addNewCatalogue();
                        break;
                    case 3:
                        updateCatalogue();
                        break;
                    case 4:
                        deleteCatalogue();
                        break;
                    case 5:
                        manageCatalogueItems();
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

    public static void addNewCatalogue() throws SQLException {
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

        if (ProductCatalogue.addCatalogue(catalogue)) {
            System.out.println("Catalogue added successfully!");
            System.out.println("Catalogue ID: " + catalogue.getCatalogueID());
        } else {
            System.out.println("Failed to add catalogue.");
        }
    }

    public static void updateCatalogue() throws SQLException {
        ConsoleUtils.printHeader("      Update Catalogue      ");
        Main.displayAllCatalogues();

        String catalogueID = ConsoleUtils.getStringInput(scanner, "Enter catalogue ID to update: ");
        ProductCatalogue catalogue = ProductCatalogue.getCatalogueById(catalogueID);

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

        if (ProductCatalogue.updateCatalogue(catalogue)) {
            System.out.println("Catalogue updated successfully!");
        } else {
            System.out.println("Failed to update catalogue.");
        }
    }

    public static void deleteCatalogue() throws SQLException {
        ConsoleUtils.printHeader("      Delete Catalogue      ");
        Main.displayAllCatalogues();

        String catalogueID = ConsoleUtils.getStringInput(scanner, "Enter catalogue ID to delete: ");

        if (ProductCatalogue.deleteCatalogue(catalogueID)) {
            System.out.println("Catalogue deleted successfully!");
        } else {
            System.out.println("Failed to delete catalogue.");
        }
    }

    public static void manageCatalogueItems() throws SQLException {
        ConsoleUtils.printHeader("      Manage Catalogue Items      ");
        Main.displayAllCatalogues();

        String catalogueID = ConsoleUtils.getStringInput(scanner, "Enter catalogue ID to manage items: ");
        ProductCatalogue catalogue = ProductCatalogue.getCatalogueById(catalogueID);

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
                    addItemToCatalogue(catalogue);
                    break;
                case 3:
                    updateItemPrice(catalogue);
                    break;
                case 4:
                    removeItemFromCatalogue(catalogue);
                    break;
                case 5:
                    back = true;
                    break;
            }
        }
    }

    public static void addItemToCatalogue(ProductCatalogue catalogue) throws SQLException {
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

        if (ProductCatalogue.addCatalogueItem(item)) {
            System.out.println("Item added to catalogue successfully!");
        } else {
            System.out.println("Failed to add item to catalogue.");
        }
    }

    public static void updateItemPrice(ProductCatalogue catalogue) throws SQLException {
        ConsoleUtils.printHeader("      Update Item Price      ");
        Main.displayCatalogueItems(catalogue);

        String itemID = ConsoleUtils.getStringInput(scanner, "Enter item ID to update: ");
        ProductCatalogueItem item = ProductCatalogue.getCatalogueItemById(itemID);

        if (item == null) {
            System.out.println("Item not found.");
            return;
        }

        double newPrice = ConsoleUtils.getDoubleInput(scanner, "Enter new special price: ");
        item.setSpecialPrice(newPrice);

        if (ProductCatalogue.updateCatalogueItem(item)) {
            System.out.println("Item price updated successfully!");
        } else {
            System.out.println("Failed to update item price.");
        }
    }

    public static void removeItemFromCatalogue(ProductCatalogue catalogue) throws SQLException {
        ConsoleUtils.printHeader("      Remove Item from Catalogue      ");
        Main.displayCatalogueItems(catalogue);

        String itemID = ConsoleUtils.getStringInput(scanner, "Enter item ID to remove: ");

        if (ProductCatalogue.deleteCatalogueItem(itemID)) {
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
        System.out.println("5. Block/Unblock User");
        System.out.println("6. Back to Admin Menu");

        int choice = ConsoleUtils.getIntInput(scanner, "Enter your choice: ", 1, 6);

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
                manageUserBlocking();
                break;
            case 6:
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

            ConsoleUtils.printHeader("      All Users      ");
            for (User user : users) {
                if (user != null) {  // Add null check for each user
                    System.out.println("ID: " + (user.getUserId() != null ? user.getUserId() : "N/A"));
                    System.out.println("Username: " + (user.getUsername() != null ? user.getUsername() : "N/A"));
                    System.out.println("Email: " + (user.getEmail() != null ? user.getEmail() : "N/A"));
                    System.out.println("Role: " + (user.getRole() != null ? user.getRole() : "N/A"));
                    ConsoleUtils.printDivider('-', 21);
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
            String phone = ConsoleUtils.getStringInput(scanner, "New Phone [" + user.getPhone() + "]: ");
            String fullName = ConsoleUtils.getStringInput(scanner, "New Full Name [" + user.getFullName() + "]: ");
            String username = ConsoleUtils.getStringInput(scanner, "New Username [" + user.getUsername() + "]: ");
            String password = ConsoleUtils.getStringInput(scanner, "New Password [" + user.getPassword() + "]: ");

            System.out.println("Current role: " + user.getRole());
            System.out.println("Select new role (or press Enter to keep current):");
            System.out.println("1. Customer");
            System.out.println("2. Admin");
            String roleChoiceStr = ConsoleUtils.getStringInput(scanner, "Choice: ");

            if (!email.isBlank()) user.setEmail(email);
            if (!phone.isBlank()) user.setPhone(phone);
            if (!fullName.isBlank()) user.setFullName(fullName);
            if (!username.isBlank()) user.setUsername(username);
            if (!password.isBlank()) user.setPassword(password);

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

    private void manageUserBlocking() {
        ConsoleUtils.printHeader("      Block/Unblock User      ");
        String userId = ConsoleUtils.getStringInput(scanner, "Enter user ID (format: C0001-C9999): ");

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

            // Prevent blocking/unblocking self
            if (user.getUserId().equals(this.getUserId())) {
                System.out.println("You cannot block/unblock your own account.");
                return;
            }

            System.out.println("Current user status: " + user.getStatus());
            System.out.println("1. Block User");
            System.out.println("2. Unblock User");
            System.out.println("3. Back");

            int choice = ConsoleUtils.getIntInput(scanner, "Enter your choice: ", 1, 3);

            switch (choice) {
                case 1:
                    if (userDao.blockUser(userId)) {
                        System.out.println("User blocked successfully!");
                    } else {
                        System.out.println("Failed to block user.");
                    }
                    break;
                case 2:
                    if (userDao.unblockUser(userId)) {
                        System.out.println("User unblocked successfully!");
                    } else {
                        System.out.println("Failed to unblock user.");
                    }
                    break;
                case 3:
                    return;
            }
        } catch (Exception e) {
            System.out.println("Error managing user block status: " + e.getMessage());
        }
    }

    public static void viewAllOrders() {
        ConsoleUtils.printHeader("                                     All Orders                                    ");
        try {
            Order[] orders = Order.getAllOrders();
            if (orders == null || orders.length == 0) {
                System.out.println("No orders found.");
            } else {
                System.out.printf("%-8s | %-20s | %-23s | %-8s | %-10s\n", "Order ID", "Customer", "Date", "Status", "Total Amount");
                ConsoleUtils.printDivider('-', 83);
                for (Order order : orders) {
                    if (order != null) {
                        System.out.printf("%-8s | %-20s | %-23s | %-8s | RM%.2f\n",
                                order.getOrderId(),
                                order.getCustomer().getFullName(),
                                order.getOrderDate(),
                                order.getStatus(),
                                order.getTotalAmount());
                    }
                }

                String orderId = ConsoleUtils.getStringInput(scanner, "Enter order ID to view details (0 to go back): ");
                if (!orderId.equals("0")) {
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

    public static void updateOrderStatus(String orderId) {
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
            boolean updated = Order.updateOrderStatus(orderId, newStatus);
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
            // Get all orders
            Order[] orders = Order.getAllOrders();
            if (orders == null || orders.length == 0) {
                System.out.println("No sales data available.");
                return;
            }

            // Calculate total sales and number of orders
            double totalSales = 0;
            int orderCount = 0;
            for (Order order : orders) {
                if (order != null && order.getStatus() != OrderStatus.CANCELLED) {
                    totalSales += order.getTotalAmount();
                    orderCount++;
                }
            }

            double averageOrderValue = orderCount > 0 ? totalSales / orderCount : 0;

            // Get top selling products
            Product[] allProducts = Product.getAllProducts();
            int[] productSales = new int[allProducts.length];
            double[] categorySales = new double[allProducts.length];
            String[] categories = new String[allProducts.length];
            int categoryCount = 0;
            double totalCategorySales = 0;

            // Initialize arrays
            for (int i = 0; i < allProducts.length; i++) {
                if (allProducts[i] != null) {
                    productSales[i] = 0;
                    categories[categoryCount] = allProducts[i].getCategory();
                    categorySales[categoryCount] = 0;
                    categoryCount++;
                }
            }

            // Process orders
            for (Order order : orders) {
                if (order != null && order.getStatus() != OrderStatus.CANCELLED) {
                    for (OrderItem item : order.getItems()) {
                        // Count product sales
                        for (int i = 0; i < allProducts.length; i++) {
                            if (allProducts[i] != null && allProducts[i].getProductID().equals(item.getProduct().getProductID())) {
                                productSales[i] += item.getQuantity();
                                break;
                            }
                        }

                        // Calculate category sales
                        Product product = item.getProduct();
                        if (product != null) {
                            String category = product.getCategory();
                            double itemTotal = item.getQuantity() * item.getUnitPrice();
                            for (int i = 0; i < categoryCount; i++) {
                                if (categories[i].equals(category)) {
                                    categorySales[i] += itemTotal;
                                    totalCategorySales += itemTotal;
                                    break;
                                }
                            }
                        }
                    }
                }
            }

            // Sort products by sales quantity (simple bubble sort)
            for (int i = 0; i < allProducts.length - 1; i++) {
                for (int j = 0; j < allProducts.length - i - 1; j++) {
                    if (productSales[j] < productSales[j + 1]) {
                        // Swap sales
                        int tempSales = productSales[j];
                        productSales[j] = productSales[j + 1];
                        productSales[j + 1] = tempSales;
                        // Swap products
                        Product tempProduct = allProducts[j];
                        allProducts[j] = allProducts[j + 1];
                        allProducts[j + 1] = tempProduct;
                    }
                }
            }

            // Display report
            System.out.println("\n========================================");
            System.out.println("           PCHub Sales Report           ");
            System.out.println("========================================");
            System.out.println("Period: " + period.toUpperCase());
            System.out.println("Generated on: " + java.time.LocalDate.now());

            System.out.println("\nTotal Sales: $" + String.format("%.2f", totalSales));
            System.out.println("Number of Orders: " + orderCount);
            System.out.println("Average Order Value: $" + String.format("%.2f", averageOrderValue));

            System.out.println("\nTop Selling Products:");
            int count = 0;
            for (int i = 0; i < allProducts.length && count < 3; i++) {
                if (allProducts[i] != null && productSales[i] > 0) {
                    System.out.println((count + 1) + ". " + allProducts[i].getName() + " - " + productSales[i] + " units");
                    count++;
                }
            }

            System.out.println("\nSales by Category:");
            for (int i = 0; i < categoryCount; i++) {
                if (categorySales[i] > 0) {
                    double percentage = (categorySales[i] / totalCategorySales) * 100;
                    System.out.printf("- %s: $%.2f (%.1f%%)\n",
                            categories[i],
                            categorySales[i],
                            percentage);
                }
            }

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
            if (products == null || products.length == 0) {
                System.out.println("No products found.");
                return;
            }

            System.out.println("\n========================================");
            System.out.println("         PCHub Inventory Report         ");
            System.out.println("========================================");
            System.out.println("Generated on: " + java.time.LocalDate.now());

            int totalProducts = products.length;
            int lowStockProducts = 0;
            int outOfStockProducts = 0;
            double totalInventoryValue = 0;

            // First pass to count products and calculate inventory value
            for (Product product : products) {
                if (product != null) {
                    if (product.getCurrentQuantity() == 0) {
                        outOfStockProducts++;
                    } else if (product.getCurrentQuantity() < 10) {
                        lowStockProducts++;
                    }
                    totalInventoryValue += product.getCurrentQuantity() * product.getUnitPrice();
                }
            }

            System.out.println("\nInventory Summary:");
            System.out.println("Total Products: " + totalProducts);
            System.out.println("Low Stock Products: " + lowStockProducts);
            System.out.println("Out of Stock Products: " + outOfStockProducts);
            System.out.println("Total Inventory Value: $" + String.format("%.2f", totalInventoryValue));

            System.out.println("\nLow Stock Items (Less than 5 units):");
            ConsoleUtils.printDivider('-', 42);
            boolean hasLowStock = false;
            for (Product product : products) {
                if (product != null && product.getCurrentQuantity() > 0 && product.getCurrentQuantity() < 5) {
                    System.out.printf("%s - %d units remaining (Value: $%.2f)\n",
                            product.getName(),
                            product.getCurrentQuantity(),
                            product.getCurrentQuantity() * product.getUnitPrice());
                    hasLowStock = true;
                }
            }
            if (!hasLowStock) {
                System.out.println("No low stock items found.");
            }

            System.out.println("\nOut of Stock Items:");
            ConsoleUtils.printDivider('-', 42);
            boolean hasOutOfStock = false;
            for (Product product : products) {
                if (product != null && product.getCurrentQuantity() == 0) {
                    System.out.printf("%s (Last Price: $%.2f)\n",
                            product.getName(),
                            product.getUnitPrice());
                    hasOutOfStock = true;
                }
            }
            if (!hasOutOfStock) {
                System.out.println("No out of stock items found.");
            }

            ConsoleUtils.printDivider('=', 42);
            System.out.println("Report generated successfully!");
        } catch (Exception e) {
            System.out.println("Error generating inventory report: " + e.getMessage());
        }
    }

    public static void generateCustomerActivityReport() {
        ConsoleUtils.printHeader("      Customer Activity Report      ");

        try {
            // Get all users and orders
            User[] users = User.getAllUsers();
            Order[] orders = Order.getAllOrders();

            if (users == null || orders == null) {
                System.out.println("Error retrieving data.");
                return;
            }

            // Calculate customer metrics
            int totalCustomers = 0;
            int newCustomers = 0;
            int activeCustomers = 0;
            LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);

            // Track customer spending
            double[] customerSpending = new double[users.length];
            int[] customerOrderCount = new int[users.length];
            String[] customerIds = new String[users.length];
            int customerCount = 0;

            for (User user : users) {
                if (user != null && user.getRole() == UserRole.CUSTOMER) {
                    totalCustomers++;
                    customerIds[customerCount] = user.getUserId();
                    customerSpending[customerCount] = 0;
                    customerOrderCount[customerCount] = 0;
                    customerCount++;

                    // Check for new customers
                    LocalDateTime registrationDate = user.getRegistrationDate().toInstant()
                            .atZone(java.time.ZoneId.systemDefault())
                            .toLocalDateTime();
                    if (registrationDate.isAfter(thirtyDaysAgo)) {
                        newCustomers++;
                    }
                }
            }

            // Process orders
            for (Order order : orders) {
                if (order != null && order.getStatus() != OrderStatus.CANCELLED) {
                    String customerId = order.getCustomer().getUserId();

                    // Update customer spending and order count
                    for (int i = 0; i < customerCount; i++) {
                        if (customerIds[i].equals(customerId)) {
                            customerSpending[i] += order.getTotalAmount();
                            customerOrderCount[i]++;

                            // Check for active customers
                            LocalDateTime orderDate = order.getOrderDate().toInstant()
                                    .atZone(java.time.ZoneId.systemDefault())
                                    .toLocalDateTime();
                            if (orderDate.isAfter(thirtyDaysAgo)) {
                                activeCustomers++;
                            }
                            break;
                        }
                    }
                }
            }

            // Sort customers by spending (simple bubble sort)
            for (int i = 0; i < customerCount - 1; i++) {
                for (int j = 0; j < customerCount - i - 1; j++) {
                    if (customerSpending[j] < customerSpending[j + 1]) {
                        // Swap spending
                        double tempSpending = customerSpending[j];
                        customerSpending[j] = customerSpending[j + 1];
                        customerSpending[j + 1] = tempSpending;
                        // Swap customer IDs
                        String tempId = customerIds[j];
                        customerIds[j] = customerIds[j + 1];
                        customerIds[j + 1] = tempId;
                    }
                }
            }

            // Calculate retention rate
            double retentionRate = totalCustomers > 0 ?
                    ((double) activeCustomers / totalCustomers) * 100 : 0;

            // Calculate average order frequency
            double totalOrders = 0;
            for (int i = 0; i < customerCount; i++) {
                totalOrders += customerOrderCount[i];
            }
            double avgOrderFrequency = totalCustomers > 0 ? totalOrders / totalCustomers : 0;

            // Display report
            System.out.println("\n========================================");
            System.out.println("     PCHub Customer Activity Report     ");
            System.out.println("========================================");
            System.out.println("Generated on: " + java.time.LocalDate.now());

            System.out.println("\nCustomer Summary:");
            System.out.println("Total Customers: " + totalCustomers);
            System.out.println("New Customers (Last 30 days): " + newCustomers);
            System.out.println("Active Customers (Last 30 days): " + activeCustomers);

            System.out.println("\nTop Customers (By Purchase Amount):");
            int count = 0;
            for (int i = 0; i < customerCount && count < 3; i++) {
                if (customerSpending[i] > 0) {
                    User customer = User.getUserById(customerIds[i]);
                    if (customer != null) {
                        System.out.printf("%d. %s - $%.2f\n",
                                count + 1,
                                customer.getFullName(),
                                customerSpending[i]);
                        count++;
                    }
                }
            }

            System.out.printf("\nCustomer Retention Rate: %.1f%%\n", retentionRate);
            System.out.printf("Average Order Frequency: %.1f orders/month\n", avgOrderFrequency);

            System.out.println("========================================");
            System.out.println("Report generated successfully!");
        } catch (Exception e) {
            System.out.println("Error generating customer activity report: " + e.getMessage());
        }
    }

    public static void handleViewProductDetails() {
        ConsoleUtils.printHeader("      Products      ");
        try {
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

    public static void managePaymentMethod() {
        ConsoleUtils.printHeader("      Manage Payment Methods      ");
        System.out.println("1. View All Payment Methods");
        System.out.println("2. Add New Payment Method");
        System.out.println("3. Update Payment Method");
        System.out.println("4. Delete Payment Method");
        System.out.println("5. Back to Admin Menu");

        int choice = ConsoleUtils.getIntInput(scanner, "Enter your choice: ", 1, 5);

        switch (choice) {
            case 1:
                viewAllPaymentMethods();
                break;
            case 2:
                addPaymentMethod();
                break;
            case 3:
                updatePaymentMethod();
                break;
            case 4:
                deletePaymentMethod();
                break;
            case 5:
                return;
        }
    }

    private static void viewAllPaymentMethods() {
        ConsoleUtils.printHeader("      All Payment Methods      ");
        try {
            PaymentMethod[] methods = Bill.getAllPaymentMethods();
            if (methods == null || methods.length == 0) {
                System.out.println("No payment methods found.");
                return;
            }

            System.out.printf("%-10s | %-20s | %-20s | %-20s\n", "ID", "Name", "Description", "Added Date");
            ConsoleUtils.printDivider('-', 42);
            for (PaymentMethod method : methods) {
                if (method != null) {
                    System.out.printf("%-10s | %-20s | %-20s | %-20s\n",
                            method.getPaymentMethodId(),
                            method.getName(),
                            method.getDescription(),
                            method.getAddedDate());
                }
            }
        } catch (Exception e) {
            System.out.println("Error retrieving payment methods: " + e.getMessage());
        }
    }

    private static void addPaymentMethod() {
        ConsoleUtils.printHeader("      Add New Payment Method      ");
        String name = ConsoleUtils.getStringInput(scanner, "Enter payment method name: ");
        String description = ConsoleUtils.getStringInput(scanner, "Enter description: ");

        try {
            boolean success = Bill.addPaymentMethod(name, description);
            if (success) {
                System.out.println("Payment method added successfully!");
            } else {
                System.out.println("Failed to add payment method.");
            }
        } catch (Exception e) {
            System.out.println("Error adding payment method: " + e.getMessage());
        }
    }

    private static void updatePaymentMethod() {
        ConsoleUtils.printHeader("      Update Payment Method      ");
        String methodId = ConsoleUtils.getStringInput(scanner, "Enter payment method ID to update: ");

        try {
            PaymentMethod method = Bill.getPaymentMethodById(methodId);
            if (method == null) {
                System.out.println("Payment method not found.");
                return;
            }

            System.out.println("Current payment method details:");
            System.out.println("Name: " + method.getName());
            System.out.println("Description: " + method.getDescription());

            System.out.println("\nEnter new details (press Enter to keep current value):");
            String name = ConsoleUtils.getStringInput(scanner, "New name: ");
            if (!name.isEmpty()) {
                method.setName(name);
            }

            String description = ConsoleUtils.getStringInput(scanner, "New description: ");
            if (!description.isEmpty()) {
                method.setDescription(description);
            }

            boolean success = Bill.updatePaymentMethod(methodId, method.getName(), method.getDescription());
            if (success) {
                System.out.println("Payment method updated successfully!");
            } else {
                System.out.println("Failed to update payment method.");
            }
        } catch (Exception e) {
            System.out.println("Error updating payment method: " + e.getMessage());
        }
    }

    private static void deletePaymentMethod() {
        ConsoleUtils.printHeader("      Delete Payment Method      ");
        String methodId = ConsoleUtils.getStringInput(scanner, "Enter payment method ID to delete: ");

        try {
            boolean success = Bill.deletePaymentMethod(methodId);
            if (success) {
                System.out.println("Payment method deleted successfully!");
            } else {
                System.out.println("Failed to delete payment method.");
            }
        } catch (Exception e) {
            System.out.println("Error deleting payment method: " + e.getMessage());
        }
    }
}
