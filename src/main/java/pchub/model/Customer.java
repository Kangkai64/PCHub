package pchub.model;

import pchub.utils.ConsoleUtils;
import pchub.Main;
import java.util.Scanner;
import java.sql.SQLException;

public class Customer extends User {
    Address address;
    private static Scanner scanner = new Scanner(System.in);
    public Customer(User user) {
        super(user.getUserId(), user.getUsername(), user.getEmail(), user.getPassword(), user.getRegistrationDate(),
                user.getLastLogin(), user.getStatus(), user.getFullName(), user.getPhone(), user.getRole());
        address = new Address();
    }

    public Customer(Address address) {
        super();
        this.address = address;
    }

    public void viewOrderHistory() {
        ConsoleUtils.printHeader("      Order History      ");
        try {
            Order[] orders = Order.getOrdersByUser(this.getUserId());
            if (orders == null || orders.length == 0) {
                System.out.println("You don't have any orders yet.");
                return;
            }

            displayOrderList(orders);

            String orderId = ConsoleUtils.getStringInput(scanner, "Enter order ID to view details (0 to go back): ");
            if (orderId.equals("0")) {
                return;
            }
            Main.displayOrderDetails(orderId);
        } catch (Exception e) {
            System.out.println("Error fetching order history: " + e.getMessage());
        }
    }

    public static void displayOrderList(Order[] orders) {
        System.out.println("\nOrder ID | Date | Status | Total Amount");
        System.out.println("------------------------------------------");
        for (Order order : orders) {
            if (order != null) {
                System.out.printf("%-8s | %s | %-10s | $%12.2f\n",
                        order.getOrderId(),
                        order.getOrderDate(),
                        order.getStatus(),
                        order.getTotalAmount());
            }
        }
    }

    public void displayBill(Bill bill) {
        ConsoleUtils.printHeader("             PCHub RECEIPT             ");
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

    public void manageProfile() {
        ConsoleUtils.printHeader("      Manage Profile      ");
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

    public void updateProfileInfo() {
        ConsoleUtils.printHeader("      Update Profile Information     ");
        String newEmail = ConsoleUtils.getStringInput(scanner, "Enter new email (leave blank to keep current): ");
        String newName = ConsoleUtils.getStringInput(scanner, "Enter new full name (leave blank to keep current): ");
        String newPhone = ConsoleUtils.getStringInput(scanner, "Enter new phone number (leave blank to keep current): ");

        try {
            if (!newEmail.isBlank()) {
                this.setEmail(newEmail);
            }
            if (!newName.isBlank()) {
                this.setFullName(newName);
            }
            if (!newPhone.isBlank()) {
                this.setPhone(newPhone);
            }

            boolean updated = User.updateUser(this);
            if (updated) {
                System.out.println("Profile updated successfully!");
            } else {
                System.out.println("Failed to update profile.");
            }
        } catch (Exception e) {
            System.out.println("Error updating profile: " + e.getMessage());
        }
    }

    public void changePassword() {
        ConsoleUtils.printHeader("      Change Password     ");
        String oldPassword = ConsoleUtils.getStringInput(scanner, "Enter old password: ");
        String newPassword = ConsoleUtils.getStringInput(scanner, "Enter new password: ");

        try {
            boolean changed = User.updatePassword(this.getUserId(), oldPassword, newPassword);
            if (changed) {
                System.out.println("Password changed successfully!");
            } else {
                System.out.println("Failed to change password. Old password may be incorrect.");
            }
        } catch (Exception e) {
            System.out.println("Error changing password: " + e.getMessage());
        }
    }

    public void manageAddresses() {
        ConsoleUtils.printHeader("      Manage Addresses     ");

        try {
            Address[] addresses = Address.getAddressesByUser(this.getUserId());

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
                        boolean removed = Address.deleteAddress(addressToRemove.getAddressId());
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

    public Address addNewAddress() {
        ConsoleUtils.printHeader("     Add New Address      ");
        String street = ConsoleUtils.getStringInput(scanner, "Street: ");
        String city = ConsoleUtils.getStringInput(scanner, "City: ");
        String state = ConsoleUtils.getStringInput(scanner, "State: ");
        String zipCode = ConsoleUtils.getStringInput(scanner, "Zip Code: ");
        String country = ConsoleUtils.getStringInput(scanner, "Country: ");

        try {
            Address address = new Address();
            address.setUserId(this.getUserId());
            address.setStreet(street);
            address.setCity(city);
            address.setState(state);
            address.setZipCode(zipCode);
            address.setCountry(country);

            boolean added = Address.addAddress(address);
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
}