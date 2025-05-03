package pchub.model;

import pchub.utils.ConsoleUtils;
import pchub.utils.PasswordUtils;
import pchub.Main;
import java.util.Scanner;
import java.sql.SQLException;
import pchub.model.enums.OrderStatus;

public class Customer extends User {
    private Address address;
    private static final Scanner scanner = new Scanner(System.in);
    
    public Customer(User user) {
        super(user.getUserId(), user.getUsername(), user.getEmail(), user.getPassword(),
                user.getRegistrationDate(), user.getLastLogin(), user.getStatus(),
                user.getFullName(), user.getPhone(), user.getRole());
    }

    public Customer(Address address) {
        super();
        this.address = address;
    }

    public Customer(){
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public void viewOrderHistory() {
        ConsoleUtils.printHeader("                        Order History                         ");
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

            // Option to cancel order
            String cancel = ConsoleUtils.getStringInput(scanner, "Do you want to cancel this order? (y/n): ");
            if (cancel.equalsIgnoreCase("y")) {
                Order.updateOrderStatus(orderId, OrderStatus.CANCELLED);
                System.out.println("Order cancelled successfully!");
            }
        } catch (Exception e) {
            System.out.println("Error fetching order history: " + e.getMessage());
        }
    }

    public static void displayOrderList(Order[] orders) {
        System.out.printf("%-8s | %-23s | %-10s | %-10s\n", "Order ID", "Date", "Status", "Total Amount");
        ConsoleUtils.printDivider('-', 62);
        for (Order order : orders) {
            if (order != null) {
                System.out.printf("%-8s | %-23s | %-10s | RM%.2f\n",
                        order.getOrderId(),
                        order.getOrderDate(),
                        order.getStatus(),
                        order.getTotalAmount());
            }
        }
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
        ConsoleUtils.printHeader("Change Password");
        boolean continueLoop = true;

        while (continueLoop) {
            System.out.println("\n1. Change password");
            System.out.println("0. Exit to main menu");
            System.out.print("\nSelect an option: ");

            String choice = scanner.nextLine().trim();

            if (choice.equals("0")) {
                System.out.println("Password change cancelled. Returning to main menu...");
                return; // Exit with code 0
            } else if (choice.equals("1")) {
                // Verify old password
                String oldPassword = ConsoleUtils.getPasswordInput(scanner, "Enter current password: ");
                if (oldPassword == null || oldPassword.trim().isEmpty()) {
                    System.out.println("Current password cannot be empty. Please try again.");
                    continue;
                }

                // Check if old password is correct before proceeding
                try {
                    if (!PasswordUtils.verifyPassword(oldPassword, this.getPassword())) {
                        System.out.println("Incorrect password. Please try again.");
                        continue;
                    }
                } catch (Exception e) {
                    System.out.println("Error verifying password: " + e.getMessage());
                    continue;
                }

                // Get new password
                System.out.println("\nPassword requirements:");
                System.out.println("- At least 8 characters long");
                System.out.println("- Must contain uppercase and lowercase letters");
                System.out.println("- Must contain at least one number");
                System.out.println("- Must contain at least one special character");
                System.out.println("- Must be different from your current password\n");

                String newPassword = ConsoleUtils.getPasswordInput(scanner, "Enter new password: ");
                if (newPassword == null || newPassword.trim().isEmpty()) {
                    System.out.println("New password cannot be empty. Please try again.");
                    continue;
                }

                // Validate password complexity
                String complexityError = User.checkPasswordComplexity(newPassword);
                if (complexityError != null) {
                    System.out.println(complexityError);
                    continue;
                }

                if (oldPassword.equals(newPassword)) {
                    System.out.println("New password must be different from your current password.");
                    continue;
                }

                // Confirm new password
                String confirmPassword = ConsoleUtils.getPasswordInput(scanner, "Confirm new password: ");
                if (!confirmPassword.equals(newPassword)) {
                    System.out.println("Passwords don't match. Please try again.");
                    continue;
                }

                // Update password
                try {
                    boolean changed = User.updatePassword(this.getUserId(), oldPassword, newPassword);
                    if (changed) {
                        System.out.println("Password successfully changed!");
                        continueLoop = false; // Exit the loop after successful password change
                    } else {
                        System.out.println("Failed to change password. Please try again later.");
                        continue;
                    }
                } catch (Exception e) {
                    System.out.println("Error changing password: " + e.getMessage());
                    continue;
                }
            } else {
                System.out.println("Invalid option. Please select 0 or 1.");
            }
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

        // Get and validate street
        String street = null;
        while (street == null || street.trim().isEmpty()) {
            street = ConsoleUtils.getStringInput(scanner, "Street: ");
            if (street == null || street.trim().isEmpty()) {
                System.out.println("Street cannot be empty. Please try again.");
            }
        }

        // Get and validate city
        String city = null;
        while (city == null || city.trim().isEmpty()) {
            city = ConsoleUtils.getStringInput(scanner, "City: ");
            if (city == null || city.trim().isEmpty()) {
                System.out.println("City cannot be empty. Please try again.");
            }
        }

        // Get and validate state
        String state = null;
        while (state == null || state.trim().isEmpty()) {
            state = ConsoleUtils.getStringInput(scanner, "State: ");
            if (state == null || state.trim().isEmpty()) {
                System.out.println("State cannot be empty. Please try again.");
            }
        }

        // Get and validate zip code
        String zipCode = null;
        while (zipCode == null || zipCode.trim().isEmpty()) {
            zipCode = ConsoleUtils.getStringInput(scanner, "Zip Code: ");
            if (zipCode == null || zipCode.trim().isEmpty()) {
                System.out.println("Zip Code cannot be empty. Please try again.");
            }
        }

        // Get and validate country
        String country = null;
        while (country == null || country.trim().isEmpty()) {
            country = ConsoleUtils.getStringInput(scanner, "Country: ");
            if (country == null || country.trim().isEmpty()) {
                System.out.println("Country cannot be empty. Please try again.");
            }
        }

        try {
            // All fields are now validated and non-empty
            Address address = new Address(null, this.getUserId(), street, city, state, zipCode, country);

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