package pchub;

import java.util.Scanner;
import pchub.model.*;
import pchub.model.enums.*;
import pchub.service.*;
import pchub.dao.*;
import pchub.utils.*;
import java.sql.SQLException;

public class Main {
    private static Scanner scanner = new Scanner(System.in);
    private static User currentUser = null;
    private static ShoppingCart currentCart = null;
    private static ProductService productService = new ProductService();
    private static UserService userService = new UserService();
    private static OrderService orderService = new OrderService();
    private static CartService cartService = new CartService();
    private static AddressService addressService = new AddressService();

    public static void main(String[] args) {
        ConsoleUtils.displayLogo();

        initializeDatabase();

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
                if (currentUser.getRole() == UserRole.ADMIN) {
                    displayAdminMenu();
                    handleAdminChoice();
                } else {
                    displayCustomerMenu();
                    handleCustomerChoice();
                }
            }
        }

        System.out.println("Thank you for using PCHub Retail Management System. Goodbye!");
        scanner.close();
    }

    private static void initializeDatabase() {
        try {
            // Update all passwords to encrypted version of "123456"
            UserDao userDAO = new UserDao();
            userDAO.updateAllPasswords("123456");
            System.out.println("Database initialized successfully.");
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
        System.out.println("Welcome, " + currentUser.getUsername() + "!");
        System.out.println("1. Browse Products");
        System.out.println("2. Search Products");
        System.out.println("3. View Cart (" + (currentCart != null ? currentCart.getItemCount() : 0) + " items)");
        System.out.println("4. View Order History");
        System.out.println("5. Manage Profile");
        System.out.println("6. Logout");
    }

    private static void displayAdminMenu() {
        ConsoleUtils.printHeader("      Admin Menu      ");
        System.out.println("Welcome, Admin " + currentUser.getUsername() + "!");
        System.out.println("1. Manage Products");
        System.out.println("2. Manage Users");
        System.out.println("3. View All Orders");
        System.out.println("4. Generate Reports");
        System.out.println("5. Logout");
    }

    private static void login() {
        ConsoleUtils.printHeader("      Login      ");
        String username = ConsoleUtils.getStringInput(scanner, "Enter username: ");
        String password = ConsoleUtils.getPasswordInput(scanner, "Enter password: ");

        try {
            currentUser = userService.authenticateUser(username, password);
            if (currentUser != null) {
                System.out.println("Login successful!");
                if (currentUser.getRole() == UserRole.CUSTOMER) {
                    currentCart = cartService.getCartForUser(currentUser);
                }
            } else {
                System.out.println("Invalid username or password. Please try again.");
            }
        } catch (Exception e) {
            System.out.println("Error during login: " + e.getMessage());
        }
    }

    private static void register() {
        ConsoleUtils.printHeader("      Register      ");
        String username = ConsoleUtils.getStringInput(scanner, "Enter username: ");
        String email = ConsoleUtils.getStringInput(scanner, "Enter email: ");
        String password = ConsoleUtils.getPasswordInput(scanner, "Enter password: ");
        String confirmPassword = ConsoleUtils.getPasswordInput(scanner, "Confirm password: ");

        if (!password.equals(confirmPassword)) {
            System.out.println("Passwords do not match. Registration failed.");
            return;
        }

        try {
            User newUser = new User();
            newUser.setUsername(username);
            newUser.setEmail(email);
            newUser.setPassword(password); // Will be hashed in the service
            newUser.setRole(UserRole.CUSTOMER); // Default role for new registrations

            boolean success = userService.registerUser(newUser);
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
            System.out.println("3. Back to Main Menu");

            int choice = ConsoleUtils.getIntInput(scanner, "Enter your choice: ", 1, 3);

            switch (choice) {
                case 1:
                    displayAllProducts();
                    break;
                case 2:
                    searchProducts();
                    break;
                case 3:
                    back = true;
                    break;
            }
        }
    }

    private static void handleCustomerChoice() {
        int choice = ConsoleUtils.getIntInput(scanner, "Enter your choice: ", 1, 6);

        switch (choice) {
            case 1: // Browse Products
                displayAllProducts();
                handleProductSelection();
                break;
            case 2: // Search Products
                searchProducts();
                handleProductSelection();
                break;
            case 3: // View Cart
                viewCart();
                break;
            case 4: // View Order History
                viewOrderHistory();
                break;
            case 5: // Manage Profile
                manageProfile();
                break;
            case 6: // Logout
                logout();
                break;
        }
    }

    private static void handleAdminChoice() {
        int choice = ConsoleUtils.getIntInput(scanner, "Enter your choice: ", 1, 5);

        switch (choice) {
            case 1: // Manage Products
                manageProducts();
                break;
            case 2: // Manage Users
                manageUsers();
                break;
            case 3: // View All Order
                viewAllOrders();
                break;
            case 4: // Generate Reports
                generateReports();
                break;
            case 5: // Logout
                logout();
                break;
        }
    }

    private static void displayAllProducts() {
        ConsoleUtils.printHeader("      Product Catalog      ");
        try {
            Product[] products = productService.getAllProducts();
            if (products == null) {
                System.out.println("No products available.");
            } else {
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
            Product[] products = productService.searchProducts(keyword);
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
        System.out.println("\nID | Name | Category | Price | Stock");
        System.out.println("------------------------------------------");
        for (Product product : products) {
            System.out.printf("%s | %s | %s | $%.2f | %d\n",
                    product.getProductID(),
                    product.getName(),
                    product.getCategory(),
                    product.getUnitPrice(),
                    product.getCurrentQuantity());
        }
    }

    private static void handleProductSelection() {
        if (currentUser == null) {
            System.out.println("Please login to add products to cart.");
            return;
        }

        String productId = ConsoleUtils.getStringInput(scanner, "Enter product ID to add to cart (0 to skip): ");
        if (!productId.equals("0")) {
            int quantity = ConsoleUtils.getIntInput(scanner, "Enter quantity: ", 1, 100);
            try {
                Product product = productService.getProduct(productId);
                if (product != null) {
                    boolean added = cartService.addItemToCart(currentCart, product, quantity);
                    if (added) {
                        currentCart = cartService.getCart(currentCart.getCartId()); // Refresh cart
                        System.out.println("Product added to cart successfully!");
                    } else {
                        System.out.println("Failed to add product to cart. Product may not exist or insufficient stock.");
                    }
                } else {
                    System.out.println("Product not found.");
                }
            } catch (Exception e) {
                System.out.println("Error adding product to cart: " + e.getMessage());
            }
        }
    }

    private static void viewCart() {
        System.out.println("\n===== Your Shopping Cart =====");
        if (currentCart == null || currentCart.getItems() == null) {
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

    private static void displayCart(ShoppingCart cart) {
        if (cart == null || cart.getItems() == null || cart.getItems().length == 0) {
            System.out.println("Your cart is empty.");
            return;
        }

        System.out.println("\n===== Your Shopping Cart =====");
        System.out.println("Item | Quantity | Unit Price | Total");
        System.out.println("------------------------------------------");

        double total = 0;
        for (CartItem item : cart.getItems()) {
            if (item != null) {
                double itemTotal = item.getQuantity() * item.getUnitPrice();
                System.out.printf("%s | %d | $%.2f | $%.2f\n",
                        item.getProductName(),
                        item.getQuantity(),
                        item.getUnitPrice(),
                        itemTotal);
                total += itemTotal;
            }
        }

        System.out.println("------------------------------------------");
        System.out.printf("Total: $%.2f\n", total);
    }

    private static void updateCartItemQuantity() {
        String itemId = ConsoleUtils.getStringInput(scanner, "Enter item ID to update: ");
        int newQuantity = ConsoleUtils.getIntInput(scanner, "Enter new quantity: ", 1, 100);

        try {
            boolean updated = cartService.updateItemQuantity(currentCart, itemId, newQuantity);
            if (updated) {
                currentCart = cartService.getCart(currentCart.getCartId()); // Refresh cart
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
            boolean removed = cartService.removeItemFromCart(currentCart, itemId);
            if (removed) {
                currentCart = cartService.getCart(currentCart.getCartId()); // Refresh cart
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
            cartService.clearCart(currentCart);
            currentCart = cartService.getCart(currentCart.getCartId()); // Refresh cart
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

        System.out.println("\n===== Checkout =====");
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

        // Process order
        try {
            Order order = orderService.createOrderFromCart(currentUser, currentCart, shippingAddress, paymentMethod);
            if (order != null) {
                System.out.println("\nOrder created successfully!");
                System.out.println("Order ID: " + order.getOrderId());

                // Generate and display bill
                Bill bill = orderService.generateBill(order.getOrderId());
                displayBill(bill);

                // Clear cart after successful order
                cartService.clearCart(currentCart);
                currentCart = cartService.getCart(currentCart.getCartId()); // Refresh cart
            } else {
                System.out.println("Failed to create order. Please try again.");
            }
        } catch (Exception e) {
            System.out.println("Error during checkout: " + e.getMessage());
        }
    }

    private static Address selectShippingAddress() {
        try {
            Address[] addresses = addressService.getAddressesByUser(currentUser.getUserId());

            if (addresses == null || addresses.length == 0) {
                System.out.println("You don't have any saved addresses. Let's add one:");
                return addNewAddress();
            }

            System.out.println("\n===== Select Shipping Address =====");
            int index = 1;
            for (Address address : addresses) {
                System.out.println(index + ". " + address.getFormattedAddress());
                index++;
            }
            System.out.println(index + ". Add new address");
            System.out.println((index + 1) + ". Cancel checkout");

            int choice = ConsoleUtils.getIntInput(scanner, "Select an address: ", 1, index + 1);

            if (choice == index) {
                return addNewAddress();
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

    private static Address addNewAddress() {
        System.out.println("\n===== Add New Address =====");
        String street = ConsoleUtils.getStringInput(scanner, "Street: ");
        String city = ConsoleUtils.getStringInput(scanner, "City: ");
        String state = ConsoleUtils.getStringInput(scanner, "State: ");
        String zipCode = ConsoleUtils.getStringInput(scanner, "Zip Code: ");
        String country = ConsoleUtils.getStringInput(scanner, "Country: ");

        try {
            Address address = new Address();
            address.setUserId(currentUser.getUserId());
            address.setStreet(street);
            address.setCity(city);
            address.setState(state);
            address.setZipCode(zipCode);
            address.setCountry(country);

            boolean added = addressService.addAddress(address);
            if (added) {
                System.out.println("Address added successfully!");
                return address;
            } else {
                System.out.println("Failed to add address.");
                return null;
            }
        } catch (SQLException e) {
            System.out.println("Error adding address: " + e.getMessage());
            return null;
        }
    }

    private static PaymentMethod selectPaymentMethod() {
        System.out.println("\n===== Select Payment Method =====");
        System.out.println("1. Credit Card");
        System.out.println("2. PayPal");
        System.out.println("3. Bank Transfer");
        System.out.println("4. Cash on Delivery");
        System.out.println("5. Cancel checkout");

        int choice = ConsoleUtils.getIntInput(scanner, "Select payment method: ", 1, 5);

        if (choice == 5) {
            return null; // Cancel checkout
        }

        PaymentMethod paymentMethod = new PaymentMethod();
        paymentMethod.setName(currentUser.getUserId());

        switch (choice) {
            case 1:
                paymentMethod.setType(PaymentType.CREDIT_CARD);
                paymentMethod.setDescription(processCardDetails());
                break;
            case 2:
                paymentMethod.setType(PaymentType.PAYPAL);
                paymentMethod.setDescription(ConsoleUtils.getStringInput(scanner, "Enter PayPal email: "));
                break;
            case 3:
                paymentMethod.setType(PaymentType.BANK_TRANSFER);
                paymentMethod.setDescription("Bank transfer details will be emailed");
                break;
            case 4:
                paymentMethod.setType(PaymentType.CASH_ON_DELIVERY);
                paymentMethod.setDescription("Payment will be collected at delivery");
                break;
        }

        return paymentMethod;
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

    private static void displayBill(Bill bill) {
        System.out.println("\n========================================");
        System.out.println("             PCHub RECEIPT             ");
        System.out.println("========================================");
        System.out.println("Order ID: " + bill.getOrderId());
        System.out.println("Date: " + bill.getIssueDate());
        System.out.println("Customer: " + bill.getCustomerName());

        System.out.println("\nItems:");
        System.out.println("------------------------------------------");
        for (OrderItem item : bill.getItems()) {
            System.out.printf("%-20s %2d x $%6.2f = $%7.2f\n",
                    item.getProductName(),
                    item.getQuantity(),
                    item.getUnitPrice(),
                    item.getSubtotal());
        }
        System.out.println("------------------------------------------");
        System.out.printf("Subtotal:                      $%7.2f\n", bill.getSubtotal());
        System.out.printf("Tax:                           $%7.2f\n", bill.getTax());
        System.out.println("------------------------------------------");
        System.out.printf("TOTAL:                         $%7.2f\n", bill.getTotalAmount());
        System.out.println("------------------------------------------");
        System.out.println("Payment Method: " + bill.getPaymentMethod());
        System.out.println("Payment Status: " + bill.getPaymentStatus());
        System.out.println("\nThank you for shopping at PCHub!");
        System.out.println("========================================");
    }

    private static void viewOrderHistory() {
        System.out.println("\n===== Order History =====");
        try {
            Order[] orders = orderService.getOrdersByUser(currentUser.getUserId());
            if (orders == null || orders.length == 0) {
                System.out.println("You don't have any orders yet.");
                return;
            }

            displayOrderList(orders);

            int orderId = ConsoleUtils.getIntInput(scanner, "Enter order ID to view details (0 to go back): ", 0, Integer.MAX_VALUE);
            if (orderId > 0) {
                displayOrderDetails(orderId);
            }
        } catch (Exception e) {
            System.out.println("Error fetching order history: " + e.getMessage());
        }
    }

    private static void displayOrderList(Order[] orders) {
        System.out.println("\nOrder ID | Date | Status | Total Amount");
        System.out.println("------------------------------------------");
        for (Order order : orders) {
            if (order != null) {
                System.out.printf("%s | %s | %s | $%.2f\n",
                        order.getOrderId(),
                        order.getOrderDate(),
                        order.getStatus(),
                        order.getTotalAmount());
            }
        }
    }

    private static void displayOrderDetails(int orderId) {
        try {
            Order order = orderService.getOrderById(String.valueOf(orderId));
            if (order == null) {
                System.out.println("Order not found.");
                return;
            }

            System.out.println("\n===== Order Details =====");
            System.out.println("Order ID: " + order.getOrderId());
            System.out.println("Date: " + order.getOrderDate());
            System.out.println("Status: " + order.getStatus());

            System.out.println("\nItems:");
            for (OrderItem item : order.getItems()) {
                System.out.printf("%s - %d x $%.2f = $%.2f\n",
                        item.getProductName(),
                        item.getQuantity(),
                        item.getUnitPrice(),
                        item.getSubtotal());
            }

            System.out.println("\nShipping Address: " + order.getShippingAddress().getFormattedAddress());
            System.out.println("Payment Method: " + order.getPaymentMethod().getType());
            System.out.printf("Total Amount: $%.2f\n", order.getTotalAmount());

            // Option to view receipt/bill
            String choice = ConsoleUtils.getStringInput(scanner, "View receipt? (y/n): ");
            if (choice.equalsIgnoreCase("y")) {
                Bill bill = orderService.generateBill(String.valueOf(orderId));
                displayBill(bill);
            }
        } catch (Exception e) {
            System.out.println("Error displaying order details: " + e.getMessage());
        }
    }

    private static void manageProfile() {
        System.out.println("\n===== Manage Profile =====");
        System.out.println("1. Update Profile Information");
        System.out.println("2. Change Password");
        System.out.println("3. Manage Addresses");
        System.out.println("4. Back to Menu");

        int choice = ConsoleUtils.getIntInput(scanner, "Enter your choice: ", 1, 4);

        switch (choice) {
            case 1:
                updateProfileInfo();
                break;
            case 2:
                changePassword();
                break;
            case 3:
                manageAddresses();
                break;
            case 4:
                break;
        }
    }

    private static void updateProfileInfo() {
        System.out.println("\n===== Update Profile Information =====");
        String newEmail = ConsoleUtils.getStringInput(scanner, "Enter new email (leave blank to keep current): ");
        String newName = ConsoleUtils.getStringInput(scanner, "Enter new full name (leave blank to keep current): ");
        String newPhone = ConsoleUtils.getStringInput(scanner, "Enter new phone number (leave blank to keep current): ");

        try {
            if (!newEmail.isBlank()) {
                currentUser.setEmail(newEmail);
            }
            if (!newName.isBlank()) {
                currentUser.setFullName(newName);
            }
            if (!newPhone.isBlank()) {
                currentUser.setPhone(newPhone);
            }

            boolean updated = userService.updateUser(currentUser);
            if (updated) {
                System.out.println("Profile updated successfully!");
            } else {
                System.out.println("Failed to update profile.");
            }
        } catch (Exception e) {
            System.out.println("Error updating profile: " + e.getMessage());
        }
    }

    private static void changePassword() {
        System.out.println("\n===== Change Password =====");
        String oldPassword = ConsoleUtils.getStringInput(scanner, "Enter old password: ");
        String newPassword = ConsoleUtils.getStringInput(scanner, "Enter new password: ");

        try {
            boolean changed = userService.updatePassword(currentUser.getUserId(), oldPassword, newPassword);
            if (changed) {
                System.out.println("Password changed successfully!");
            } else {
                System.out.println("Failed to change password. Old password may be incorrect.");
            }
        } catch (Exception e) {
            System.out.println("Error changing password: " + e.getMessage());
        }
    }

    private static void manageAddresses() {
        System.out.println("\n===== Manage Addresses =====");

        try {
            Address[] addresses = addressService.getAddressesByUser(currentUser.getUserId());

            if (addresses == null || addresses.length == 0) {
                System.out.println("You don't have any saved addresses.");
            } else {
                System.out.println("Your saved addresses:");
                int index = 1;
                for (Address address : addresses) {
                    System.out.println(index + ". " + address.getFormattedAddress());
                    index++;
                }
            }

            System.out.println("\n1. Add New Address");
            System.out.println("2. Remove Address");
            System.out.println("3. Back to Profile");

            int choice = ConsoleUtils.getIntInput(scanner, "Enter your choice: ", 1, 3);

            switch (choice) {
                case 1:
                    addNewAddress();
                    break;
                case 2:
                    if (addresses != null && addresses.length > 0) {
                        int addressIndex = ConsoleUtils.getIntInput(scanner, "Enter address number to remove: ", 1, addresses.length);
                        Address addressToRemove = addresses[addressIndex - 1];
                        boolean removed = addressService.deleteAddress(addressToRemove.getAddressId());
                        if (removed) {
                            System.out.println("Address removed successfully!");
                        } else {
                            System.out.println("Failed to remove address.");
                        }
                    } else {
                        System.out.println("No addresses to remove.");
                    }
                    break;
                case 3:
                    break;
            }
        } catch (SQLException e) {
            System.out.println("Error managing addresses: " + e.getMessage());
        }
    }

    private static void manageProducts() {
        System.out.println("\n===== Product Management =====");
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

    private static void viewAllProducts() {
        System.out.println("\n===== All Products =====");
        try {
            Product[] products = productService.getAllProducts();
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

    private static void addNewProduct() {
        System.out.println("\n===== Add New Product =====");
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

            boolean success = productService.addProduct(product);
            if (success) {
                System.out.println("Product added successfully!");
            } else {
                System.out.println("Failed to add product.");
            }
        } catch (Exception e) {
            System.out.println("Error adding product: " + e.getMessage());
        }
    }

    private static void updateProduct() {
        System.out.println("\n===== Update Product =====");
        String productId = ConsoleUtils.getStringInput(scanner, "Enter product ID to update: ");

        try {
            Product product = productService.getProduct(productId);
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

            boolean success = productService.updateProduct(product);
            if (success) {
                System.out.println("Product updated successfully!");
            } else {
                System.out.println("Failed to update product.");
            }
        } catch (Exception e) {
            System.out.println("Error updating product: " + e.getMessage());
        }
    }

    private static void deleteProduct() {
        System.out.println("\n===== Delete Product =====");
        String productId = ConsoleUtils.getStringInput(scanner, "Enter product ID to delete: ");

        try {
            boolean success = productService.deleteProduct(productId);
            if (success) {
                System.out.println("Product deleted successfully!");
            } else {
                System.out.println("Failed to delete product.");
            }
        } catch (Exception e) {
            System.out.println("Error deleting product: " + e.getMessage());
        }
    }

    private static void manageUsers() {
        System.out.println("\n===== Manage Users =====");
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
                deleteUser();
                break;
            case 5:
                break;
        }
    }

    private static void viewAllUsers() {
        try {
            User[] users = userService.getAllUsers();
            if (users == null || users.length == 0) {
                System.out.println("No users found.");
                return;
            }
            
            System.out.println("\n=== All Users ===");
            for (User user : users) {
                System.out.println("ID: " + user.getUserId());
                System.out.println("Username: " + user.getUsername());
                System.out.println("Email: " + user.getEmail());
                System.out.println("Role: " + user.getRole());
                System.out.println("-------------------");
            }
        } catch (SQLException e) {
            System.out.println("Error retrieving users: " + e.getMessage());
        }
    }

    private static void addUser() {
        System.out.println("\n===== Add New User =====");
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

            boolean added = userService.registerUser(user);
            if (added) {
                System.out.println("User added successfully!");
            } else {
                System.out.println("Failed to add user. Username or email may already be in use.");
            }
        } catch (Exception e) {
            System.out.println("Error adding user: " + e.getMessage());
        }
    }

    private static void updateUser() {
        System.out.println("\n===== Update User =====");
        int userId = ConsoleUtils.getIntInput(scanner, "Enter user ID to update: ", 1, Integer.MAX_VALUE);

        try {
            User user = userService.getUserById(String.valueOf(userId));
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

            boolean updated = userService.updateUser(user);
            if (updated) {
                System.out.println("User updated successfully!");
            } else {
                System.out.println("Failed to update user.");
            }
        } catch (Exception e) {
            System.out.println("Error updating user: " + e.getMessage());
        }
    }

    private static void deleteUser() {
        System.out.println("\n===== Delete User =====");
        int userId = ConsoleUtils.getIntInput(scanner, "Enter user ID to delete: ", 1, Integer.MAX_VALUE);

        try {
            User user = userService.getUserById(String.valueOf(userId));
            if (user == null) {
                System.out.println("User not found.");
                return;
            }

            // Prevent deleting self
            if (user.getUserId().equals(currentUser.getUserId())) {
                System.out.println("You cannot delete your own account.");
                return;
            }

            System.out.println("Are you sure you want to delete user: " + user.getUsername() + "?");
            String confirm = ConsoleUtils.getStringInput(scanner, "Type 'yes' to confirm: ");

            if (confirm.equalsIgnoreCase("yes")) {
                boolean deleted = userService.deleteUser(String.valueOf(userId));
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

    private static void viewAllOrders() {
        System.out.println("\n===== All Orders =====");
        try {
            Order[] orders = orderService.getAllOrders();
            if (orders == null || orders.length == 0) {
                System.out.println("No orders found.");
            } else {
                System.out.println("Order ID | Customer | Date | Status | Total Amount");
                System.out.println("------------------------------------------");
                for (Order order : orders) {
                    if (order != null) {
                        System.out.printf("%s | %s | %s | %s | $%.2f\n",
                                order.getOrderId(),
                                order.getUserName(),
                                order.getOrderDate(),
                                order.getStatus(),
                                order.getTotalAmount());
                    }
                }

                int orderId = ConsoleUtils.getIntInput(scanner, "Enter order ID to view details (0 to go back): ", 0, Integer.MAX_VALUE);
                if (orderId > 0) {
                    displayOrderDetails(orderId);

                    // Admin can update order status
                    System.out.println("\nDo you want to update the order status?");
                    String update = ConsoleUtils.getStringInput(scanner, "Type 'yes' to update: ");

                    if (update.equalsIgnoreCase("yes")) {
                        updateOrderStatus(orderId);
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Error fetching orders: " + e.getMessage());
        }
    }

    private static void updateOrderStatus(int orderId) {
        System.out.println("\n===== Update Order Status =====");
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
            boolean updated = orderService.updateOrderStatus(String.valueOf(orderId), newStatus);
            if (updated) {
                System.out.println("Order status updated successfully!");
            } else {
                System.out.println("Failed to update order status.");
            }
        } catch (Exception e) {
            System.out.println("Error updating order status: " + e.getMessage());
        }
    }

    private static void generateReports() {
        System.out.println("\n===== Generate Reports =====");
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

    private static void generateSalesReport() {
        System.out.println("\n===== Sales Report =====");
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

    private static void generateInventoryReport() {
        System.out.println("\n===== Inventory Report =====");

        try {
            Product[] products = productService.getAllProducts();

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

    private static void generateCustomerActivityReport() {
        System.out.println("\n===== Customer Activity Report =====");

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

    private static void logout() {
        currentUser = null;
        currentCart = null;
        System.out.println("Logged out successfully.");
    }

    private static boolean checkStock(Product product, int quantity) {
        if (product == null) {
            return false;
        }
        return product.getCurrentQuantity() >= quantity;
    }

    private static boolean updateStock(Product product, int quantity) {
        if (product == null) {
            return false;
        }
        int newQuantity = product.getCurrentQuantity() - quantity;
        if (newQuantity < 0) {
            return false;
        }
        product.setCurrentQuantity(newQuantity);
        return true;
    }

    private static void displayProducts() {
        System.out.println("\n===== Products =====");
        try {
            Product[] products = productService.getAllProducts();
            if (products == null || products.length == 0) {
                System.out.println("No products found.");
                return;
            }

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

            String productId = ConsoleUtils.getStringInput(scanner, "Enter product ID to view details (or press Enter to go back): ");
            if (!productId.isEmpty()) {
                Product product = productService.getProduct(productId);
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

    private static void displayProductDetails(Product product) {
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

    private static void displayLowStockProducts() {
        System.out.println("\n===== Low Stock Products =====");
        try {
            Product[] products = productService.getAllProducts();
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

    private static void displayOutOfStockProducts() {
        System.out.println("\n===== Out of Stock Products =====");
        try {
            Product[] products = productService.getAllProducts();
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

    private static void displayStockStatus() {
        System.out.println("\n===== Stock Status =====");
        try {
            Product[] products = productService.getAllProducts();
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