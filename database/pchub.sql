-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Apr 26, 2025 at 06:41 AM
-- Server version: 10.4.32-MariaDB
-- PHP Version: 8.2.12

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `pchub`
--
CREATE DATABASE IF NOT EXISTS `pchub` DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE `pchub`;

-- --------------------------------------------------------

--
-- Table structure for table `adminrole`
--

DROP TABLE IF EXISTS `adminrole`;
CREATE TABLE `adminrole` (
  `adminID` varchar(10) NOT NULL,
  `userID` varchar(10) NOT NULL,
  `role` varchar(50) NOT NULL,
  `permissions` text DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `cartitem`
--

DROP TABLE IF EXISTS `cartitem`;
CREATE TABLE `cartitem` (
  `cartItemID` varchar(10) NOT NULL,
  `cartID` varchar(10) NOT NULL,
  `productID` varchar(10) NOT NULL,
  `quantity` int(11) NOT NULL DEFAULT 1,
  `price` decimal(10,2) NOT NULL,
  `subtotal` decimal(10,2) GENERATED ALWAYS AS (`price` * `quantity`) STORED
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `category`
--

DROP TABLE IF EXISTS `category`;
CREATE TABLE `category` (
  `categoryID` varchar(10) NOT NULL,
  `name` varchar(50) NOT NULL,
  `parentCategory` varchar(10) DEFAULT NULL,
  `description` text DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `category`
--

INSERT INTO `category` (`categoryID`, `name`, `parentCategory`, `description`) VALUES
('CAT001', 'Computer Components', NULL, 'All components for building and upgrading computers'),
('CAT002', 'Peripherals', NULL, 'External devices for computers'),
('CAT003', 'Networking', NULL, 'Devices for home and office networking'),
('CAT004', 'Software', NULL, 'Computer software and applications'),
('CAT005', 'Gaming', NULL, 'PC gaming hardware and accessories'),
('CAT006', 'Mobile Accessories', NULL, 'Accessories for smartphones and tablets'),
('CAT007', 'Office Electronics', NULL, 'Electronics for home and business offices'),
('CAT008', 'CPUs', 'CAT001', 'Computer processors from leading manufacturers'),
('CAT009', 'Motherboards', 'CAT001', 'Motherboards for various CPU sockets and form factors'),
('CAT010', 'Memory', 'CAT001', 'RAM memory modules for computers'),
('CAT011', 'Storage', 'CAT001', 'Hard drives and solid state drives'),
('CAT012', 'Graphics Cards', 'CAT001', 'Video cards for gaming and professional work'),
('CAT013', 'Power Supplies', 'CAT001', 'Power supply units for computers'),
('CAT014', 'Cases', 'CAT001', 'Computer tower cases in various sizes'),
('CAT015', 'Cooling', 'CAT001', 'Fans and cooling systems for computers');

-- --------------------------------------------------------

--
-- Table structure for table `order`
--

DROP TABLE IF EXISTS `order`;
CREATE TABLE `order` (
  `orderID` varchar(10) NOT NULL,
  `customerID` varchar(10) NOT NULL,
  `orderDate` datetime NOT NULL DEFAULT current_timestamp(),
  `status` varchar(20) NOT NULL DEFAULT 'pending',
  `totalAmount` decimal(10,2) NOT NULL,
  `shippingAddress` varchar(255) NOT NULL,
  `paymentMethodID` varchar(10) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `orderhistory`
--

DROP TABLE IF EXISTS `orderhistory`;
CREATE TABLE `orderhistory` (
  `historyID` varchar(10) NOT NULL,
  `customerID` varchar(10) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `orderitem`
--

DROP TABLE IF EXISTS `orderitem`;
CREATE TABLE `orderitem` (
  `orderItemID` varchar(10) NOT NULL,
  `orderID` varchar(10) NOT NULL,
  `productID` varchar(10) NOT NULL,
  `quantity` int(11) NOT NULL,
  `unitPrice` decimal(10,2) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `payment`
--

DROP TABLE IF EXISTS `payment`;
CREATE TABLE `payment` (
  `billID` varchar(10) NOT NULL,
  `orderID` varchar(10) NOT NULL,
  `amount` decimal(10,2) NOT NULL,
  `paymentMethodID` varchar(10) NOT NULL,
  `transactionID` varchar(50) DEFAULT NULL,
  `status` varchar(20) NOT NULL DEFAULT 'pending',
  `date` datetime NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `paymentmethod`
--

DROP TABLE IF EXISTS `paymentmethod`;
CREATE TABLE `paymentmethod` (
  `paymentMethodID` varchar(10) NOT NULL,
  `name` varchar(50) NOT NULL,
  `type` varchar(20) NOT NULL,
  `description` text DEFAULT NULL,
  `addedDate` datetime NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `product`
--

DROP TABLE IF EXISTS `product`;
CREATE TABLE `product` (
  `productID` varchar(10) NOT NULL,
  `name` varchar(100) NOT NULL,
  `description` text DEFAULT NULL,
  `brand` varchar(50) DEFAULT NULL,
  `category` varchar(10) DEFAULT NULL,
  `unitPrice` decimal(10,2) NOT NULL,
  `currentQuantity` int(11) NOT NULL DEFAULT 0,
  `specifications` text DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `product`
--

INSERT INTO `product` (`productID`, `name`, `description`, `brand`, `category`, `unitPrice`, `currentQuantity`, `specifications`) VALUES
('P00001', 'Intel Core i9-13900K', '14th Gen Intel Core i9 Processor', 'Intel', 'CAT008', 599.99, 50, '{\"cores\":24,\"threads\":32,\"base_clock\":\"3.0GHz\",\"boost_clock\":\"5.8GHz\"}'),
('P00002', 'AMD Ryzen 9 7950X', 'AMD Ryzen 9 Processor', 'AMD', 'CAT008', 549.99, 45, '{\"cores\":16,\"threads\":32,\"base_clock\":\"4.5GHz\",\"boost_clock\":\"5.7GHz\"}'),
('P00003', 'NVIDIA RTX 4090', 'NVIDIA GeForce RTX 4090 Graphics Card', 'NVIDIA', 'CAT012', 1599.99, 30, '{\"vram\":\"24GB\",\"memory_type\":\"GDDR6X\",\"boost_clock\":\"2.52GHz\"}'),
('P00004', 'AMD RX 7900 XTX', 'AMD Radeon RX 7900 XTX Graphics Card', 'AMD', 'CAT012', 999.99, 35, '{\"vram\":\"24GB\",\"memory_type\":\"GDDR6\",\"boost_clock\":\"2.5GHz\"}'),
('P00005', 'Samsung 980 Pro 2TB', 'Samsung 980 Pro NVMe SSD', 'Samsung', 'CAT011', 199.99, 100, '{\"capacity\":\"2TB\",\"interface\":\"PCIe 4.0\",\"read_speed\":\"7000MB/s\",\"write_speed\":\"5000MB/s\"}'),
('P00006', 'Corsair Vengeance RGB 32GB', 'DDR5 Memory Kit', 'Corsair', 'CAT010', 149.99, 75, '{\"capacity\":\"32GB\",\"speed\":\"6000MHz\",\"latency\":\"CL36\"}'),
('P00007', 'ASUS ROG Strix Z790-E', 'Intel Z790 Motherboard', 'ASUS', 'CAT009', 399.99, 40, '{\"socket\":\"LGA1700\",\"chipset\":\"Z790\",\"memory_slots\":4}'),
('P00008', 'MSI MPG B650 Carbon', 'AMD B650 Motherboard', 'MSI', 'CAT009', 299.99, 45, '{\"socket\":\"AM5\",\"chipset\":\"B650\",\"memory_slots\":4}'),
('P00009', 'Corsair RM1000x', '1000W Power Supply', 'Corsair', 'CAT013', 189.99, 60, '{\"wattage\":\"1000W\",\"efficiency\":\"80+ Gold\",\"modular\":\"Full\"}'),
('P00010', 'NZXT H7 Elite', 'Mid-Tower Case', 'NZXT', 'CAT014', 179.99, 55, '{\"form_factor\":\"Mid-Tower\",\"material\":\"Steel/Tempered Glass\",\"fans_included\":3}');

-- --------------------------------------------------------

--
-- Table structure for table `shippingaddress`
--

DROP TABLE IF EXISTS `shippingaddress`;
CREATE TABLE `shippingaddress` (
  `shippingaddressID` varchar(10) NOT NULL CHECK (`shippingaddressID` regexp 'SA[0-9]{6}'),
  `customerID` varchar(10) NOT NULL,
  `shippingAddresses` text DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `shoppingcart`
--

DROP TABLE IF EXISTS `shoppingcart`;
CREATE TABLE `shoppingcart` (
  `cartID` varchar(10) NOT NULL,
  `customerID` varchar(10) NOT NULL,
  `createdDate` datetime NOT NULL DEFAULT current_timestamp(),
  `lastUpdated` datetime NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  `itemCount` int(11) DEFAULT 0,
  `subtotal` decimal(10,2) DEFAULT 0.00
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `shoppingcart`
--

INSERT INTO `shoppingcart` (`cartID`, `customerID`, `createdDate`, `lastUpdated`, `itemCount`, `subtotal`) VALUES
('02e210e721', 'C0001', '2025-04-26 12:38:25', '2025-04-26 12:38:25', 0, 0.00);

-- --------------------------------------------------------

--
-- Table structure for table `user`
--

DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
  `userID` varchar(10) NOT NULL,
  `username` varchar(50) NOT NULL,
  `email` varchar(100) NOT NULL,
  `password` varchar(255) NOT NULL,
  `registrationDate` datetime NOT NULL DEFAULT current_timestamp(),
  `lastLogin` datetime DEFAULT NULL,
  `status` varchar(20) DEFAULT 'active',
  `phone` varchar(20) DEFAULT NULL,
  `fullName` varchar(255) DEFAULT NULL,
  `role` varchar(50) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `user`
--

INSERT INTO `user` (`userID`, `username`, `email`, `password`, `registrationDate`, `lastLogin`, `status`, `phone`, `fullName`, `role`) VALUES
('A0001', 'admin_jones', 'admin.j@pchub.com', '$2a$12$9P53HPQlmGANDeQXfa9Nuewo.sCuWd6.STDral9g5ZSP9gYZCEqz.', '2024-10-01 08:00:00', '2025-03-22 18:00:00', 'active', '012-2345678', 'Alex Jones', 'ADMIN'),
('A0002', 'admin_smith', 'admin.s@pchub.com', '$2a$12$9P53HPQlmGANDeQXfa9Nuewo.sCuWd6.STDral9g5ZSP9gYZCEqz.', '2024-10-01 08:15:00', '2025-03-22 17:45:00', 'active', '012-2345678', 'Sam Smith', 'ADMIN'),
('A0003', 'admin_johnson', 'admin.j2@pchub.com', '$2a$12$9P53HPQlmGANDeQXfa9Nuewo.sCuWd6.STDral9g5ZSP9gYZCEqz.', '2024-10-01 08:30:00', '2025-03-21 18:30:00', 'active', '012-2345678', 'Jordan Johnson', 'ADMIN'),
('A0004', 'admin_brown', 'admin.b@pchub.com', '$2a$12$9P53HPQlmGANDeQXfa9Nuewo.sCuWd6.STDral9g5ZSP9gYZCEqz.', '2024-10-01 08:45:00', '2025-03-22 16:15:00', 'active', '012-2345678', 'Blake Brown', 'ADMIN'),
('A0005', 'admin_garcia', 'admin.g@pchub.com', '$2a$12$9P53HPQlmGANDeQXfa9Nuewo.sCuWd6.STDral9g5ZSP9gYZCEqz.', '2024-10-01 09:00:00', '2025-03-20 15:30:00', 'active', '012-2345678', 'Gabriel Garcia', 'ADMIN'),
('C0001', 'john_doe', 'john.doe@email.com', '$2a$12$9P53HPQlmGANDeQXfa9Nuewo.sCuWd6.STDral9g5ZSP9gYZCEqz.', '2024-10-01 08:30:00', '2025-03-22 14:22:00', 'active', '012-2345678', 'John Doe', 'CUSTOMER'),
('C0002', 'jane_smith', 'jane.smith@email.com', '$2a$12$9P53HPQlmGANDeQXfa9Nuewo.sCuWd6.STDral9g5ZSP9gYZCEqz.', '2024-10-02 09:45:00', '2025-03-21 16:10:00', 'active', '012-2345678', 'Jane Smith', 'CUSTOMER'),
('C0003', 'mike_johnson', 'mike.j@email.com', '$2a$12$9P53HPQlmGANDeQXfa9Nuewo.sCuWd6.STDral9g5ZSP9gYZCEqz.', '2024-10-03 10:15:00', '2025-03-22 09:05:00', 'active', '012-2345678', 'Michael Johnson', 'CUSTOMER'),
('C0004', 'sarah_williams', 'sarah.w@email.com', '$2a$12$9P53HPQlmGANDeQXfa9Nuewo.sCuWd6.STDral9g5ZSP9gYZCEqz.', '2024-10-04 11:30:00', '2025-03-20 11:40:00', 'active', '012-2345678', 'Sarah Williams', 'CUSTOMER'),
('C0005', 'david_brown', 'david.b@email.com', '$2a$12$9P53HPQlmGANDeQXfa9Nuewo.sCuWd6.STDral9g5ZSP9gYZCEqz.', '2024-10-05 12:45:00', '2025-03-19 15:35:00', 'active', '012-2345678', 'David Brown', 'CUSTOMER'),
('C0006', 'lisa_taylor', 'lisa.t@email.com', '$2a$12$9P53HPQlmGANDeQXfa9Nuewo.sCuWd6.STDral9g5ZSP9gYZCEqz.', '2024-10-06 13:15:00', '2025-03-22 17:15:00', 'active', '012-2345678', 'Lisa Taylor', 'CUSTOMER'),
('C0007', 'kevin_davis', 'kevin.d@email.com', '$2a$12$9P53HPQlmGANDeQXfa9Nuewo.sCuWd6.STDral9g5ZSP9gYZCEqz.', '2024-10-07 14:30:00', '2025-03-21 10:25:00', 'active', '012-2345678', 'Kevin Davis', 'CUSTOMER'),
('C0008', 'amy_miller', 'amy.m@email.com', '$2a$12$9P53HPQlmGANDeQXfa9Nuewo.sCuWd6.STDral9g5ZSP9gYZCEqz.', '2024-10-08 15:45:00', '2025-03-20 14:50:00', 'active', '012-2345678', 'Amy Miller', 'CUSTOMER'),
('C0009', 'brian_wilson', 'brian.w@email.com', '$2a$12$9P53HPQlmGANDeQXfa9Nuewo.sCuWd6.STDral9g5ZSP9gYZCEqz.', '2024-10-09 16:15:00', '2025-03-18 12:30:00', 'active', '012-2345678', 'Brian Wilson', 'CUSTOMER'),
('C0010', 'jessica_moore', 'jessica.m@email.com', '$2a$12$9P53HPQlmGANDeQXfa9Nuewo.sCuWd6.STDral9g5ZSP9gYZCEqz.', '2024-10-10 17:30:00', '2025-03-22 08:45:00', 'active', '012-2345678', 'Jessica Moore', 'CUSTOMER'),
('C0011', 'chris_taylor', 'chris.t@email.com', '$2a$12$9P53HPQlmGANDeQXfa9Nuewo.sCuWd6.STDral9g5ZSP9gYZCEqz.', '2024-10-11 09:00:00', '2025-03-21 15:20:00', 'active', '012-2345678', 'Christopher Taylor', 'CUSTOMER'),
('C0012', 'rachel_anderson', 'rachel.a@email.com', '$2a$12$9P53HPQlmGANDeQXfa9Nuewo.sCuWd6.STDral9g5ZSP9gYZCEqz.', '2024-10-12 10:30:00', '2025-03-19 13:10:00', 'active', '012-2345678', 'Rachel Anderson', 'CUSTOMER'),
('C0013', 'mark_thomas', 'mark.t@email.com', '$2a$12$9P53HPQlmGANDeQXfa9Nuewo.sCuWd6.STDral9g5ZSP9gYZCEqz.', '2024-10-13 11:45:00', '2025-03-22 11:50:00', 'active', '012-2345678', 'Mark Thomas', 'CUSTOMER'),
('C0014', 'emily_jackson', 'emily.j@email.com', '$2a$12$9P53HPQlmGANDeQXfa9Nuewo.sCuWd6.STDral9g5ZSP9gYZCEqz.', '2024-10-14 12:15:00', '2025-03-20 17:35:00', 'active', '012-2345678', 'Emily Jackson', 'CUSTOMER'),
('C0015', 'steven_white', 'steven.w@email.com', '$2a$12$9P53HPQlmGANDeQXfa9Nuewo.sCuWd6.STDral9g5ZSP9gYZCEqz.', '2024-10-15 13:30:00', '2025-03-17 09:40:00', 'active', '012-2345678', 'Steven White', 'CUSTOMER'),
('C0016', 'laura_harris', 'laura.h@email.com', '$2a$12$9P53HPQlmGANDeQXfa9Nuewo.sCuWd6.STDral9g5ZSP9gYZCEqz.', '2024-10-16 14:45:00', '2025-03-22 16:25:00', 'active', '012-2345678', 'Laura Harris', 'CUSTOMER'),
('C0017', 'daniel_martin', 'daniel.m@email.com', '$2a$12$9P53HPQlmGANDeQXfa9Nuewo.sCuWd6.STDral9g5ZSP9gYZCEqz.', '2024-10-17 15:15:00', '2025-03-21 12:55:00', 'active', '012-2345678', 'Daniel Martin', 'CUSTOMER'),
('C0018', 'michelle_garcia', 'michelle.g@email.com', '$2a$12$9P53HPQlmGANDeQXfa9Nuewo.sCuWd6.STDral9g5ZSP9gYZCEqz.', '2024-10-18 16:30:00', '2025-03-18 14:20:00', 'active', '012-2345678', 'Michelle Garcia', 'CUSTOMER'),
('C0019', 'robert_lee', 'robert.l@email.com', '$2a$12$9P53HPQlmGANDeQXfa9Nuewo.sCuWd6.STDral9g5ZSP9gYZCEqz.', '2024-10-19 17:45:00', '2025-03-16 10:15:00', 'active', '012-2345678', 'Robert Lee', 'CUSTOMER'),
('C0020', 'jennifer_king', 'jennifer.k@email.com', '$2a$12$9P53HPQlmGANDeQXfa9Nuewo.sCuWd6.STDral9g5ZSP9gYZCEqz.', '2024-10-20 09:15:00', '2025-03-22 13:05:00', 'active', '012-2345678', 'Jennifer King', 'CUSTOMER'),
('C0021', 'ryan_scott', 'ryan.s@email.com', '$2a$12$9P53HPQlmGANDeQXfa9Nuewo.sCuWd6.STDral9g5ZSP9gYZCEqz.', '2024-10-21 10:30:00', '2025-03-20 09:50:00', 'active', '012-2345678', 'Ryan Scott', 'CUSTOMER'),
('C0022', 'nicole_adams', 'nicole.a@email.com', '$2a$12$9P53HPQlmGANDeQXfa9Nuewo.sCuWd6.STDral9g5ZSP9gYZCEqz.', '2024-10-22 11:45:00', '2025-03-19 15:30:00', 'active', '012-2345678', 'Nicole Adams', 'CUSTOMER'),
('C0023', 'tyler_baker', 'tyler.b@email.com', '$2a$12$9P53HPQlmGANDeQXfa9Nuewo.sCuWd6.STDral9g5ZSP9gYZCEqz.', '2024-10-23 12:15:00', '2025-03-21 17:15:00', 'active', '012-2345678', 'Tyler Baker', 'CUSTOMER'),
('C0024', 'olivia_gonzalez', 'olivia.g@email.com', '$2a$12$9P53HPQlmGANDeQXfa9Nuewo.sCuWd6.STDral9g5ZSP9gYZCEqz.', '2024-10-24 13:30:00', '2025-03-22 10:40:00', 'active', '012-2345678', 'Olivia Gonzalez', 'CUSTOMER'),
('C0025', 'eric_nelson', 'eric.n@email.com', '$2a$12$9P53HPQlmGANDeQXfa9Nuewo.sCuWd6.STDral9g5ZSP9gYZCEqz.', '2024-10-25 14:45:00', '2025-03-18 13:25:00', 'active', '012-2345678', 'Eric Nelson', 'CUSTOMER');

--
-- Triggers `user`
--
DROP TRIGGER IF EXISTS `before_insert_user`;
DELIMITER $$
CREATE TRIGGER `before_insert_user` BEFORE INSERT ON `user` FOR EACH ROW BEGIN
    DECLARE next_id INT;
    DECLARE prefix CHAR(1);
    
    -- Convert role to lowercase for case-insensitive comparison
    IF LOWER(NEW.role) = 'admin' THEN
        -- Get the highest existing admin ID number
        SELECT IFNULL(MAX(CAST(SUBSTRING(userID, 2, 4) AS UNSIGNED)), 0) + 1 INTO next_id
        FROM `user`
        WHERE userID LIKE 'A%';
        
        SET prefix = 'A';
    ELSE 
        -- Default to customer for all other roles
        -- Get the highest existing customer ID number
        SELECT IFNULL(MAX(CAST(SUBSTRING(userID, 2, 4) AS UNSIGNED)), 0) + 1 INTO next_id
        FROM `user`
        WHERE userID LIKE 'C%';
        
        SET prefix = 'C';
    END IF;
    
    -- Format the ID with leading zeros to ensure 4 digits
    SET NEW.userID = CONCAT(prefix, LPAD(next_id, 4, '0'));
END
$$
DELIMITER ;

-- --------------------------------------------------------

--
-- Table structure for table `user_payment_method`
--

DROP TABLE IF EXISTS `user_payment_method`;
CREATE TABLE `user_payment_method` (
  `userPaymentMethodID` varchar(10) NOT NULL,
  `userID` varchar(10) NOT NULL,
  `paymentMethodID` varchar(10) NOT NULL,
  `details` text DEFAULT NULL,
  `isDefault` tinyint(1) NOT NULL DEFAULT 0,
  `addedDate` datetime NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Indexes for dumped tables
--

--
-- Indexes for table `adminrole`
--
ALTER TABLE `adminrole`
  ADD PRIMARY KEY (`adminID`),
  ADD KEY `userID` (`userID`);

--
-- Indexes for table `cartitem`
--
ALTER TABLE `cartitem`
  ADD PRIMARY KEY (`cartItemID`),
  ADD KEY `cartID` (`cartID`),
  ADD KEY `productID` (`productID`),
  ADD KEY `idx_cart_product` (`cartID`,`productID`);

--
-- Indexes for table `category`
--
ALTER TABLE `category`
  ADD PRIMARY KEY (`categoryID`),
  ADD KEY `parentCategory` (`parentCategory`);

--
-- Indexes for table `order`
--
ALTER TABLE `order`
  ADD PRIMARY KEY (`orderID`),
  ADD KEY `customerID` (`customerID`),
  ADD KEY `paymentMethodID` (`paymentMethodID`),
  ADD KEY `idx_customer_date` (`customerID`,`orderDate`);

--
-- Indexes for table `orderhistory`
--
ALTER TABLE `orderhistory`
  ADD PRIMARY KEY (`historyID`),
  ADD KEY `customerID` (`customerID`);

--
-- Indexes for table `orderitem`
--
ALTER TABLE `orderitem`
  ADD PRIMARY KEY (`orderItemID`),
  ADD KEY `orderID` (`orderID`),
  ADD KEY `productID` (`productID`);

--
-- Indexes for table `payment`
--
ALTER TABLE `payment`
  ADD PRIMARY KEY (`billID`),
  ADD KEY `orderID` (`orderID`),
  ADD KEY `paymentMethodID` (`paymentMethodID`);

--
-- Indexes for table `paymentmethod`
--
ALTER TABLE `paymentmethod`
  ADD PRIMARY KEY (`paymentMethodID`);

--
-- Indexes for table `product`
--
ALTER TABLE `product`
  ADD PRIMARY KEY (`productID`),
  ADD KEY `category` (`category`),
  ADD KEY `idx_category` (`category`);

--
-- Indexes for table `shippingaddress`
--
ALTER TABLE `shippingaddress`
  ADD PRIMARY KEY (`shippingaddressID`),
  ADD KEY `customerID` (`customerID`);

--
-- Indexes for table `shoppingcart`
--
ALTER TABLE `shoppingcart`
  ADD PRIMARY KEY (`cartID`),
  ADD KEY `customerID` (`customerID`);

--
-- Indexes for table `user`
--
ALTER TABLE `user`
  ADD PRIMARY KEY (`userID`),
  ADD UNIQUE KEY `username` (`username`),
  ADD UNIQUE KEY `email` (`email`);

--
-- Indexes for table `user_payment_method`
--
ALTER TABLE `user_payment_method`
  ADD PRIMARY KEY (`userPaymentMethodID`),
  ADD KEY `userID` (`userID`),
  ADD KEY `paymentMethodID` (`paymentMethodID`);

--
-- Constraints for dumped tables
--

--
-- Constraints for table `adminrole`
--
ALTER TABLE `adminrole`
  ADD CONSTRAINT `adminrole_ibfk_1` FOREIGN KEY (`userID`) REFERENCES `user` (`userID`);

--
-- Constraints for table `cartitem`
--
ALTER TABLE `cartitem`
  ADD CONSTRAINT `cartitem_ibfk_1` FOREIGN KEY (`cartID`) REFERENCES `shoppingcart` (`cartID`),
  ADD CONSTRAINT `cartitem_ibfk_2` FOREIGN KEY (`productID`) REFERENCES `product` (`productID`);

--
-- Constraints for table `category`
--
ALTER TABLE `category`
  ADD CONSTRAINT `category_ibfk_1` FOREIGN KEY (`parentCategory`) REFERENCES `category` (`categoryID`);

--
-- Constraints for table `order`
--
ALTER TABLE `order`
  ADD CONSTRAINT `order_ibfk_1` FOREIGN KEY (`customerID`) REFERENCES `user` (`userID`),
  ADD CONSTRAINT `order_ibfk_2` FOREIGN KEY (`paymentMethodID`) REFERENCES `paymentmethod` (`paymentMethodID`);

--
-- Constraints for table `orderhistory`
--
ALTER TABLE `orderhistory`
  ADD CONSTRAINT `orderhistory_ibfk_1` FOREIGN KEY (`customerID`) REFERENCES `user` (`userID`);

--
-- Constraints for table `orderitem`
--
ALTER TABLE `orderitem`
  ADD CONSTRAINT `orderitem_ibfk_1` FOREIGN KEY (`orderID`) REFERENCES `order` (`orderID`),
  ADD CONSTRAINT `orderitem_ibfk_2` FOREIGN KEY (`productID`) REFERENCES `product` (`productID`);

--
-- Constraints for table `payment`
--
ALTER TABLE `payment`
  ADD CONSTRAINT `payment_ibfk_1` FOREIGN KEY (`orderID`) REFERENCES `order` (`orderID`),
  ADD CONSTRAINT `payment_ibfk_2` FOREIGN KEY (`paymentMethodID`) REFERENCES `paymentmethod` (`paymentMethodID`);

--
-- Constraints for table `product`
--
ALTER TABLE `product`
  ADD CONSTRAINT `product_ibfk_1` FOREIGN KEY (`category`) REFERENCES `category` (`categoryID`);

--
-- Constraints for table `shippingaddress`
--
ALTER TABLE `shippingaddress`
  ADD CONSTRAINT `shippingaddress_ibfk_1` FOREIGN KEY (`customerID`) REFERENCES `user` (`userID`);

--
-- Constraints for table `shoppingcart`
--
ALTER TABLE `shoppingcart`
  ADD CONSTRAINT `shoppingcart_ibfk_1` FOREIGN KEY (`customerID`) REFERENCES `user` (`userID`);

--
-- Constraints for table `user_payment_method`
--
ALTER TABLE `user_payment_method`
  ADD CONSTRAINT `user_payment_method_ibfk_1` FOREIGN KEY (`userID`) REFERENCES `user` (`userID`),
  ADD CONSTRAINT `user_payment_method_ibfk_2` FOREIGN KEY (`paymentMethodID`) REFERENCES `paymentmethod` (`paymentMethodID`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
