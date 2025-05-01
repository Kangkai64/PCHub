package pchub;

import java.sql.SQLException;
import java.util.Scanner;

import pchub.dao.ProductCatalogueDao;
import pchub.dao.ProductCatalogueItemDao;
import pchub.dao.UserDao;
import pchub.model.*;
import pchub.model.enums.UserRole;
import pchub.utils.ConsoleUtils;
import pchub.utils.EmailDeliveryService;
import pchub.utils.GenerateOTP;
import pchub.utils.ProductSorter;

public class Main {
    private static Scanner scanner = new Scanner(System.in);
    private static User currentUser = null;
    private static Cart currentCart = null;
    private static Admin admin = null;
    private static Customer customer = null;

    public static void main(String[] args) {
        ConsoleUtils.clearScreen();
        ConsoleUtils.displayLogo();

        // TODO: Only run this if you want to reset the database
        // resetPassword("123456");

        boolean exit = false;
        while (!exit) {
            if (currentUser == null) {
                displayMainMenu();
                int choice = ConsoleUtils.getIntInput(scanner, "Enter your choice: ", 1, 4);

                switch (choice) {
                    case 1:
                        login();
                        break;
                    case 2:
                        register();
                        break;
                    case 3:
                        browseAsGuest();
                        break;
                    case 4:
                        exit = true;
                        break;
                }
            } else {
                ConsoleUtils.displayLogo();
                if (currentUser.getRole() == UserRole.ADMIN) {
                    admin = new Admin(currentUser);
                    displayAdminMenu();
                    handleAdminChoice();
                } else {
                    customer = new Customer(currentUser);
                    displayCustomerMenu();
                    handleCustomerChoice();
                }
            }
        }

        System.out.println("Thank you for using PCHub Retail Management System. Goodbye!");
        scanner.close();
    }

    private static void resetPassword(String password) {
        try {
            UserDao userDAO = new UserDao();
            userDAO.updateAllPasswords(password);
            System.out.println("Password reset successfully.");
        } catch (Exception e) {
            System.out.println("Error initializing database: " + e.getMessage());
        }
    }

    private static void displayMainMenu() {
        ConsoleUtils.printHeader("      PCHub Main Menu      ");
        System.out.println("1. Login");
        System.out.println("2. Register");
        System.out.println("3. Browse as Guest");
        System.out.println("4. Exit");
    }

    private static void displayCustomerMenu() {
        ConsoleUtils.printHeader("      Customer Menu      ");
        System.out.println("Welcome, " + customer.getUsername() + "!");
        System.out.println("1. Browse Products");
        System.out.println("2. Search Products");
        System.out.println("3. View Catalogues");
        System.out.println("4. View Cart (" + (currentCart != null ? currentCart.getItemCount() : 0) + " items)");
        System.out.println("5. View Order History");
        System.out.println("6. Manage Profile");
        System.out.println("7. Logout");
    }

    private static void displayAdminMenu() {
        ConsoleUtils.printHeader("      Admin Menu      ");
        System.out.println("Welcome, Admin " + admin.getUsername() + "!");
        System.out.println("1. Manage Products");
        System.out.println("2. Manage Product Categories");
        System.out.println("3. Manage Product Catalogues");
        System.out.println("4. Manage Users");
        System.out.println("5. View All Orders");
        System.out.println("6. Generate Reports");
        System.out.println("7. Logout");
    }

    private static void login() {
        ConsoleUtils.printHeader("      Login      ");
        String username = ConsoleUtils.getStringInput(scanner, "Enter username: ");
        String password = ConsoleUtils.getPasswordInput(scanner, "Enter password: ");

        try {
            currentUser = User.authenticateUser(username, password);
            if (currentUser != null) {
                System.out.println("Login successful!");
                ConsoleUtils.waitMessage();
                if (currentUser.getRole() == UserRole.CUSTOMER) {
                    currentCart = Cart.getCartForUser(currentUser);
                }
            } else {
                System.out.println("Invalid username or password. Please try again.");
                ConsoleUtils.waitMessage();
            }
        } catch (Exception e) {
            System.out.println("Error during login: " + e.getMessage());
        }
    }

    private static void register() {
        ConsoleUtils.printHeader("      Register      ");
        String fullname = ConsoleUtils.getStringInput(scanner, "Enter full name: ");
        String username = ConsoleUtils.getStringInput(scanner, "Enter username: ");
        String email = ConsoleUtils.getStringInput(scanner, "Enter email: ");
        String phone = ConsoleUtils.getStringInput(scanner, "Enter phone number (format: 0XX-XXXXXXX): ");
        String password = ConsoleUtils.getPasswordInput(scanner, "Enter password: ");
        String confirmPassword = ConsoleUtils.getPasswordInput(scanner, "Confirm password: ");

        if (!password.equals(confirmPassword)) {
            System.out.println("Passwords do not match. Registration failed.");
            return;
        }

        try {
            User newUser = new User();
            newUser.setUsername(username);
            newUser.setFullName(fullname);
            newUser.setEmail(email);
            newUser.setPhone(phone);
            newUser.setPassword(password); // Will be hashed during insertion
            newUser.setRole(UserRole.CUSTOMER); // Default role for new registrations

            boolean success = User.registerUser(newUser);
            if (success) {
                System.out.println("Registration successful! You can now login.");
            } else {
                System.out.println("Registration failed. Username or email may already be in use.");
            }
        } catch (Exception e) {
            System.out.println("Error during registration: " + e.getMessage());
        }
    }

    private static void browseAsGuest() {
        ConsoleUtils.printHeader("      Browse as Guest      ");
        boolean back = false;

        while (!back) {
            System.out.println("\n1. View All Products");
            System.out.println("2. Search Products");
            System.out.println("3. View Product Catalogues");
            System.out.println("4. Back to Main Menu");

            int choice = ConsoleUtils.getIntInput(scanner, "Enter your choice: ", 1, 4);

            switch (choice) {
                case 1:
                    displayAllProducts();
                    break;
                case 2:
                    searchProducts();
                    break;
                case 3:
                    viewCatalogues();
                    break;
                case 4:
                    back = true;
                    break;
            }
        }
    }

    private static void handleCustomerChoice() {
        int choice = ConsoleUtils.getIntInput(scanner, "Enter your choice: ", 1, 7);

        switch (choice) {
            case 1: // Browse Products
                displayAllProducts();
                handleProductSelection();
                break;
            case 2: // Search Products
                searchProducts();
                handleProductSelection();
                break;
            case 3: // View Catalogues
                viewCatalogues();
                break;
            case 4: // View Cart
                viewCart();
                break;
            case 5: // View Order History
                customer.viewOrderHistory();
                break;
            case 6: // Manage Profile
                customer.manageProfile();
                break;
            case 7: // Logout
                logout();
                break;
        }
    }

    private static void handleAdminChoice() {
        int choice = ConsoleUtils.getIntInput(scanner, "Enter your choice: ", 1, 7);

        switch (choice) {
            case 1: // Manage Products
                Admin.manageProducts();
                break;
            case 2: // Manage Product Categories
                Admin.manageCategories();
                break;
            case 3: // Manage Product Catalogues
                Admin.manageProductCatalogues();
                break;
            case 4: // Manage Users, is not static to prevent deleting admin own account
                admin.manageUsers();
                break;
            case 5: // View All Orders
                Admin.viewAllOrders();
                break;
            case 6: // Generate Reports
                Admin.generateReports();
                break;
            case 7: // Logout
                logout();
                break;
        }
    }

    public static void displayAllProducts() {
        ConsoleUtils.printHeader("      Product Catalogue      ");
        try {
            Product[] products = Product.getAllProducts();
            if (products == null) {
                System.out.println("No products available.");
            } else {
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

                displayProductList(products);
            }
        } catch (Exception e) {
            System.out.println("Error fetching products: " + e.getMessage());
        }
    }

    private static void searchProducts() {
        ConsoleUtils.printHeader("      Search Products      ");
        String keyword = ConsoleUtils.getStringInput(scanner, "Enter search keyword: ");

        try {
            Product[] products = Product.searchProducts(keyword);
            if (products == null) {
                System.out.println("No products found matching: " + keyword);
            } else {
                System.out.println("Found " + products.length + " products matching: " + keyword);
                displayProductList(products);
            }
        } catch (Exception e) {
            System.out.println("Error searching products: " + e.getMessage());
        }
    }

    private static void displayProductList(Product[] products) {
        System.out.printf("\n%-6s | %-40s | %-15s | %-7s | %s\n", "ID", "Name", "Category", "Price", "Stock");
        System.out.println("-----------------------------------------------------------------------------------------");
        for (Product product : products) {
            if (product != null) {
                System.out.printf("%s | %-40s | %-15s | $%.2f | %d\n",
                        product.getProductID(),
                        product.getName(),
                        product.getCategory(),
                        product.getUnitPrice(),
                        product.getCurrentQuantity());
            }
        }
    }

    private static void handleProductSelection() {
        if (currentUser == null) {
            System.out.println("Please login to add products to cart.");
            return;
        }

        String productId = ConsoleUtils.getStringInput(scanner, "Enter product ID to add to cart (0 to skip): ");
        if (productId.equals("0")) {
            return;  // Early return if user wants to skip
        }

        int quantity = ConsoleUtils.getIntInput(scanner, "Enter quantity: ", 1, 100);
        try {
            Product product = Product.getProduct(productId);
            if (product != null) {
                // Create a new CartItem
                CartItem item = new CartItem();
                item.setCartId(currentCart.getCartId());
                item.setProductId(product.getProductID());
                item.setProductName(product.getName());
                item.setUnitPrice(product.getUnitPrice());
                item.setQuantity(quantity);

                // Check if product already exists in cart and update quantity instead
                boolean itemFound = false;
                CartItem[] items = currentCart.getItems();

                if (items != null) {
                    for (CartItem existingItem : items) {
                        if (existingItem.getProductId().equals(product.getProductID())) {
                            // Update quantity of existing product
                            existingItem.setQuantity(existingItem.getQuantity() + quantity);
                            itemFound = true;
                            System.out.println("Product quantity updated in cart!");
                            break;
                        }
                    }
                }

                // If it's a new product, add it to cart
                if (!itemFound) {
                    // Initialize the array if it's null
                    if (items == null) {
                        items = new CartItem[1];
                        items[0] = item;
                    } else {
                        // Create a new array with one more element
                        CartItem[] newItems = new CartItem[items.length + 1];
                        System.arraycopy(items, 0, newItems, 0, items.length);
                        newItems[items.length] = item;
                        items = newItems;
                    }
                    currentCart.setItems(items);
                    System.out.println("Product added to cart successfully!");
                }
            } else {
                System.out.println("Product not found.");
            }
        } catch (Exception e) {
            System.out.println("Error adding product to cart: " + e.getMessage());
        }
    }

    private static void viewCart() {
        ConsoleUtils.printHeader("                                 Your Shopping Cart                                       ");
        if (currentCart == null || currentCart.getItems() == null
                || currentCart.getItems().length == 0 || currentCart.getItems()[0] == null) {
            System.out.println("Your cart is empty.");
            return;
        }

        displayCart(currentCart);

        System.out.println("\n1. Update Item Quantity");
        System.out.println("2. Remove Item");
        System.out.println("3. Clear Cart");
        System.out.println("4. Proceed to Checkout");
        System.out.println("5. Back to Menu");

        int choice = ConsoleUtils.getIntInput(scanner, "Enter your choice: ", 1, 5);

        switch (choice) {
            case 1: // Update Quantity
                updateCartItemQuantity();
                break;
            case 2: // Remove Item
                removeCartItem();
                break;
            case 3: // Clear Cart
                clearCart();
                break;
            case 4: // Checkout
                checkout();
                break;
            case 5: // Back
                break;
        }
    }

    private static void displayCart(Cart cart) {
        System.out.printf("%-40s | %-8s | %-13s | %-13s\n", "Item", "Quantity", "Unit Price", "Total");
        System.out.println("-------------------------------------------------------------------------------------------");

        double total = 0;
        for (CartItem item : cart.getItems()) {
            if (item != null) {
                double itemTotal = item.getQuantity() * item.getUnitPrice();
                System.out.printf("%-40s | %-8d | RM%-12.2f | RM%-12.2f\n",
                        item.getProductName(),
                        item.getQuantity(),
                        item.getUnitPrice(),
                        itemTotal);
                total += itemTotal;
            }
        }

        System.out.println("-------------------------------------------------------------------------------------------");
        System.out.printf("Total: %66s%.2f\n", "RM", total);
    }

    private static void updateCartItemQuantity() {
        String itemId = ConsoleUtils.getStringInput(scanner, "Enter item ID to update: ");
        int newQuantity = ConsoleUtils.getIntInput(scanner, "Enter new quantity: ", 1, 100);

        try {
            boolean updated = Cart.updateItemQuantity(currentCart, itemId, newQuantity);
            if (updated) {
                currentCart = Cart.getCart(currentCart.getCartId()); // Refresh cart
                System.out.println("Cart updated successfully!");
                displayCart(currentCart);
            } else {
                System.out.println("Failed to update cart. Item may not exist.");
            }
        } catch (Exception e) {
            System.out.println("Error updating cart: " + e.getMessage());
        }
    }

    private static void removeCartItem() {
        String itemId = ConsoleUtils.getStringInput(scanner, "Enter item ID to remove: ");

        try {
            boolean removed = Cart.removeItemFromCart(currentCart, itemId);
            if (removed) {
                currentCart = Cart.getCart(currentCart.getCartId()); // Refresh cart
                System.out.println("Item removed from cart successfully!");
                displayCart(currentCart);
            } else {
                System.out.println("Failed to remove item. Item may not exist.");
            }
        } catch (Exception e) {
            System.out.println("Error removing item from cart: " + e.getMessage());
        }
    }

    private static void clearCart() {
        try {
            Cart.clearCart(currentCart);
            currentCart = Cart.getCart(currentCart.getCartId()); // Refresh cart
            System.out.println("Cart cleared successfully!");
        } catch (Exception e) {
            System.out.println("Error clearing cart: " + e.getMessage());
        }
    }

    private static void checkout() {
        if (currentCart == null || currentCart.getItems() == null) {
            System.out.println("Your cart is empty. Cannot proceed to checkout.");
            return;
        }

        ConsoleUtils.printHeader("      Checkout      ");
        // Confirm shipping address
        Address shippingAddress = selectShippingAddress();
        if (shippingAddress == null) {
            return; // User canceled checkout
        }

        // Choose payment method
        PaymentMethod paymentMethod = selectPaymentMethod();
        if (paymentMethod == null) {
            return; // User canceled checkout
        }

        // TODO: Remember to uncomment this part before submission
//        // Initialize OTP manager
//        GenerateOTP otpManager = new GenerateOTP();
//
//        // Initialize delivery service (e.g., email)
//        EmailDeliveryService emailService = new EmailDeliveryService(
//            "smtp.gmail.com", 587, "lcheekang33@gmail.com", "pdeu tpau dihs xdxz"
//        );
//
//        // Generate OTP and send it
//        String otp = otpManager.generateOTP(customer.getEmail());
//        emailService.sendOTP(customer.getEmail(), otp);
//
//        String userInputOtp = ConsoleUtils.getStringInput(scanner, "Enter OTP: ");
//        // Verify OTP
//        boolean isValid = otpManager.verifyOTP(customer.getEmail(), userInputOtp);
//        if (!isValid) {
//            return;
//        }
//        System.out.println("OTP verified successfully.");

        // Process order
        try {
            Order order = Order.createOrderFromCart(customer, currentCart, shippingAddress, paymentMethod);
            if (order != null) {
                System.out.println("\nOrder created successfully!");
                System.out.println("Order ID: " + order.getOrderId());

                // Generate and display bill
                Bill bill = Order.generateBill(order.getOrderId());
                customer.displayBill(bill);

                // Clear cart after successful order
                Cart.clearCart(currentCart);
                currentCart = Cart.getCart(currentCart.getCartId()); // Refresh cart
            } else {
                System.out.println("Failed to create order. Please try again.");
            }
        } catch (Exception e) {
            System.out.println("Error during checkout: " + e.getMessage());
        }
    }

    private static Address selectShippingAddress() {
        try {
            Address[] addresses = Address.getAddressesByUser(customer.getUserId());

            if (addresses == null || addresses.length == 0) {
                System.out.println("You don't have any saved addresses. Let's add one:");
                return customer.addNewAddress();
            }

            ConsoleUtils.printHeader("      Select Shipping Address      ");
            int index = 1;
            for (Address address : addresses) {
                System.out.println(index + ". " + address.getFormattedAddress());
                index++;
            }
            System.out.println(index + ". Add new address");
            System.out.println((index + 1) + ". Cancel checkout");

            int choice = ConsoleUtils.getIntInput(scanner, "Select an address: ", 1, index + 1);

            if (choice == index) {
                return customer.addNewAddress();
            } else if (choice == index + 1) {
                return null; // Cancel checkout
            } else {
                return addresses[choice - 1];
            }
        } catch (SQLException e) {
            System.out.println("Error fetching addresses: " + e.getMessage());
            return null;
        }
    }

    private static PaymentMethod selectPaymentMethod() {
        ConsoleUtils.printHeader("      Select Payment Method      ");
        PaymentMethod[] paymentMethods = Bill.getAllPaymentMethods();

        int choiceUpperBound = 1;
        if (paymentMethods[0] != null) {
            for (PaymentMethod paymentMethod : paymentMethods) {
                if (paymentMethod != null) {
                    System.out.println(choiceUpperBound +  "." + paymentMethod.getName());
                    choiceUpperBound++;
                }
            }
            System.out.println(choiceUpperBound + ". Cancel checkout");

            int choice = ConsoleUtils.getIntInput(scanner, "Select payment method: ", 1, choiceUpperBound);

            if (choice == choiceUpperBound) {
                return null; // Cancel checkout
            }

            PaymentMethod paymentMethod = paymentMethods[choice - 1];
            paymentMethod.setName(customer.getUserId());

            return paymentMethod;
        } else {
            System.out.println("\nThere's something wrong with our server. Please try again later.");
            return null;
        }
    }

    private static String processCardDetails() {
        String cardNumber = ConsoleUtils.getStringInput(scanner, "Enter card number: ");
        String expiryDate = ConsoleUtils.getStringInput(scanner, "Enter expiry date (MM/YY): ");
        String cvv = ConsoleUtils.getStringInput(scanner, "Enter CVV: ");
        String cardholderName = ConsoleUtils.getStringInput(scanner, "Enter cardholder name: ");

        // In a real system, we would validate and securely store these details
        // Here we're just returning a masked version for demonstration
        return "Card ending with " + cardNumber.substring(cardNumber.length() - 4);
    }

    public static void displayAllCatalogues(ProductCatalogueDao catalogueDao) throws SQLException {
        ConsoleUtils.printHeader("      All Product Catalogues      ");
        ProductCatalogue[] catalogues = catalogueDao.findAll();

        if (catalogues == null || catalogues.length == 0) {
            System.out.println("No catalogues found.");
            return;
        }

        System.out.printf("%-15s %-30s %-20s %-20s\n",
                "Catalogue ID", "Name", "Start Date", "End Date");
        System.out.println("-".repeat(85));

        boolean hasCatalogues = false;
        for (ProductCatalogue catalogue : catalogues) {
            if (catalogue != null) {
                System.out.printf("%-15s %-30s %-20s %-20s\n",
                        catalogue.getCatalogueID(),
                        catalogue.getName(),
                        catalogue.getStartDate().toLocalDate(),
                        catalogue.getEndDate().toLocalDate());
                hasCatalogues = true;
            }
        }

        if (!hasCatalogues) {
            System.out.println("No catalogues found.");
        }
    }

    public static void displayCatalogueItems(ProductCatalogue catalogue) {
        ConsoleUtils.printHeader("      Catalogue Items      ");
        try {
            ProductCatalogueItemDao itemDao = new ProductCatalogueItemDao();
            ProductCatalogueItem[] items = itemDao.findByCatalogue(catalogue.getCatalogueID());

            if (items == null || items.length == 0) {
                System.out.println("No items in this catalogue.");
                return;
            }

            System.out.printf("%-15s %-30s %-15s %-15s\n",
                "Item ID", "Product Name", "Regular Price", "Special Price");
            System.out.println("-".repeat(75));

            for (ProductCatalogueItem item : items) {
                if (item != null && item.getProduct() != null) {
                    System.out.printf("%-15s %-30s $%-14.2f $%-14.2f\n",
                        item.getItemID(),
                        item.getProduct().getName(),
                        item.getProduct().getUnitPrice(),
                        item.getSpecialPrice());
                }
            }
        } catch (SQLException e) {
            System.out.println("Error displaying catalogue items: " + e.getMessage());
        }
    }

    private static void logout() {
        currentUser = null;
        customer = null;
        admin = null;
        currentCart = null;
        System.out.println("Logged out successfully.");
    }

    private static void viewCatalogues() {
        ConsoleUtils.printHeader("      Available Catalogues      ");
        try {
            ProductCatalogueDao catalogueDao = new ProductCatalogueDao();
            ProductCatalogue[] catalogues = catalogueDao.findAll();

            if (catalogues == null || catalogues.length == 0) {
                System.out.println("No catalogues available.");
                return;
            }

            System.out.printf("%-15s %-30s %-20s %-20s\n",
                "Catalogue ID", "Name", "Start Date", "End Date");
            System.out.println("-".repeat(85));

            for (ProductCatalogue catalogue : catalogues) {
                if (catalogue != null) {
                    System.out.printf("%-15s %-30s %-20s %-20s\n",
                        catalogue.getCatalogueID(),
                        catalogue.getName(),
                        catalogue.getStartDate().toLocalDate(),
                        catalogue.getEndDate().toLocalDate());
                }
            }

            String catalogueID = ConsoleUtils.getStringInput(scanner, "\nEnter catalogue ID to view items (or press Enter to go back): ");
            if (!catalogueID.isEmpty()) {
                ProductCatalogue selectedCatalogue = catalogueDao.findById(catalogueID);
                if (selectedCatalogue != null) {
                    displayCatalogueItems(selectedCatalogue);
                    handleCatalogueItemSelection(selectedCatalogue);
                } else {
                    System.out.println("Catalogue not found.");
                }
            }
        } catch (SQLException e) {
            System.out.println("Error viewing catalogues: " + e.getMessage());
        }
    }

    private static void handleCatalogueItemSelection(ProductCatalogue catalogue) {
        String itemID = ConsoleUtils.getStringInput(scanner, "Enter item ID to add to cart (or press Enter to go back): ");
        if (!itemID.isEmpty()) {
            try {
                ProductCatalogueItemDao itemDao = new ProductCatalogueItemDao();
                ProductCatalogueItem item = itemDao.findById(itemID);

                if (item != null && item.getCatalogue().getCatalogueID().equals(catalogue.getCatalogueID())) {
                    int quantity = ConsoleUtils.getIntInput(scanner, "Enter quantity: ", 1, 100);

                    // Create a new CartItem
                    CartItem cartItem = new CartItem();
                    cartItem.setProductId(item.getProduct().getProductID());
                    cartItem.setProductName(item.getProduct().getName());
                    cartItem.setUnitPrice(item.getSpecialPrice()); // Use special price from catalogue
                    cartItem.setQuantity(quantity);

                    // Add to cart
                    if (currentCart == null) {
                        currentCart = new Cart();
                        currentCart.setCustomerId(customer.getUserId());
                    }

                    CartItem[] items = currentCart.getItems();
                    if (items == null) {
                        items = new CartItem[1];
                        items[0] = cartItem;
                    } else {
                        CartItem[] newItems = new CartItem[items.length + 1];
                        System.arraycopy(items, 0, newItems, 0, items.length);
                        newItems[items.length] = cartItem;
                        items = newItems;
                    }
                    currentCart.setItems(items);

                    System.out.println("Product added to cart successfully!");
                } else {
                    System.out.println("Item not found in this catalogue.");
                }
            } catch (SQLException e) {
                System.out.println("Error adding item to cart: " + e.getMessage());
            }
        }
    }

    public static void displayOrderDetails(String orderId) {
        try {
            Order order = Order.getOrderById(orderId);
            if (order == null) {
                System.out.println("Order not found.");
                return;
            }

            ConsoleUtils.printHeader("      Order Details     ");
            System.out.println("Order ID: " + order.getOrderId());
            System.out.println("Date: " + order.getOrderDate());
            System.out.println("Status: " + order.getStatus());

            System.out.println("\nItems:");
            for (OrderItem item : order.getItems()) {
                if (item != null) {
                    System.out.printf("%s - %d x $%.2f = $%.2f\n",
                            item.getProductName(),
                            item.getQuantity(),
                            item.getUnitPrice(),
                            item.getSubtotal());
                }
            }

            System.out.println("\nShipping Address: " + order.getShippingAddress().getFormattedAddress());
            System.out.println("Payment Method: " + order.getPaymentMethod().getName());
            System.out.printf("Total Amount: $%.2f\n", order.getTotalAmount());

            // Option to view receipt/bill
            String choice = ConsoleUtils.getStringInput(scanner, "View receipt? (y/n): ");
            if (choice.equalsIgnoreCase("y")) {
                Bill bill = Order.generateBill(orderId);
                customer.displayBill(bill);
            }
        } catch (Exception e) {
            System.out.println("Error displaying order details: " + e.getMessage());
        }
    }
}