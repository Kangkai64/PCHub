-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Apr 20, 2025 at 06:04 AM
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
  `department` varchar(50) DEFAULT NULL,
  `accessLevel` varchar(30) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `adminrole`
--

INSERT INTO `adminrole` (`adminID`, `userID`, `department`, `accessLevel`) VALUES
('ADM001', 'A0001', 'IT', 'Super Admin'),
('ADM002', 'A0002', 'Customer Service', 'Admin'),
('ADM003', 'A0003', 'Inventory', 'Manager'),
('ADM004', 'A0004', 'Sales', 'Admin'),
('ADM005', 'A0005', 'Marketing', 'Manager');

--
-- Triggers `adminrole`
--
DROP TRIGGER IF EXISTS `before_insert_adminrole`;
DELIMITER $$
CREATE TRIGGER `before_insert_adminrole` BEFORE INSERT ON `adminrole` FOR EACH ROW BEGIN
    DECLARE next_id INT;
    SET next_id = (SELECT IFNULL(MAX(SUBSTRING(adminID, 4)), 0) + 1 FROM adminrole);
    SET NEW.adminID = CONCAT('ADM', LPAD(next_id, 3, '0'));
END
$$
DELIMITER ;

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

--
-- Triggers `cartitem`
--
DROP TRIGGER IF EXISTS `before_insert_cartitem`;
DELIMITER $$
CREATE TRIGGER `before_insert_cartitem` BEFORE INSERT ON `cartitem` FOR EACH ROW BEGIN
    DECLARE next_id INT;
    SET next_id = (SELECT IFNULL(MAX(SUBSTRING(cartItemID, 3)), 0) + 1 FROM cartitem);
    SET NEW.cartItemID = CONCAT('CI', LPAD(next_id, 6, '0'));
END
$$
DELIMITER ;

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
('CAT015', 'Cooling', 'CAT001', 'Fans and cooling systems for computers'),
('CAT016', 'Monitors', 'CAT002', 'Computer displays of various sizes and resolutions'),
('CAT017', 'Keyboards', 'CAT002', 'Computer keyboards including mechanical and membrane'),
('CAT018', 'Mice', 'CAT002', 'Computer mice and trackpads'),
('CAT019', 'Printers', 'CAT002', 'Inkjet and laser printers and scanners'),
('CAT020', 'Speakers', 'CAT002', 'Computer speakers and audio systems'),
('CAT021', 'Headsets', 'CAT002', 'Headphones and headsets with microphones'),
('CAT022', 'Webcams', 'CAT002', 'Cameras for video conferencing and streaming'),
('CAT023', 'Routers', 'CAT003', 'Wired and wireless routers'),
('CAT024', 'Switches', 'CAT003', 'Network switches for home and office'),
('CAT025', 'Network Cards', 'CAT003', 'Ethernet and WiFi network adapter cards'),
('CAT026', 'Cables', 'CAT003', 'Networking and computer cables'),
('CAT027', 'Operating Systems', 'CAT004', 'Windows Linux and other OS'),
('CAT028', 'Office Software', 'CAT004', 'Productivity software suites'),
('CAT029', 'Security Software', 'CAT004', 'Antivirus and security applications'),
('CAT030', 'Gaming Accessories', 'CAT005', 'Gaming mice keyboards and controllers');

--
-- Triggers `category`
--
DROP TRIGGER IF EXISTS `before_insert_category`;
DELIMITER $$
CREATE TRIGGER `before_insert_category` BEFORE INSERT ON `category` FOR EACH ROW BEGIN
    DECLARE next_id INT;
    SET next_id = (SELECT IFNULL(MAX(SUBSTRING(categoryID, 4)), 0) + 1 FROM category);
    SET NEW.categoryID = CONCAT('CAT', LPAD(next_id, 3, '0'));
END
$$
DELIMITER ;

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

--
-- Triggers `order`
--
DROP TRIGGER IF EXISTS `before_insert_order`;
DELIMITER $$
CREATE TRIGGER `before_insert_order` BEFORE INSERT ON `order` FOR EACH ROW BEGIN
    DECLARE next_id INT;
    SET next_id = (SELECT IFNULL(MAX(SUBSTRING(orderID, 2)), 0) + 1 FROM `order`);
    SET NEW.orderID = CONCAT('O', LPAD(next_id, 4, '0'));
END
$$
DELIMITER ;

-- --------------------------------------------------------

--
-- Table structure for table `orderhistory`
--

DROP TABLE IF EXISTS `orderhistory`;
CREATE TABLE `orderhistory` (
  `historyID` varchar(10) NOT NULL,
  `customerID` varchar(10) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Triggers `orderhistory`
--
DROP TRIGGER IF EXISTS `before_insert_orderhistory`;
DELIMITER $$
CREATE TRIGGER `before_insert_orderhistory` BEFORE INSERT ON `orderhistory` FOR EACH ROW BEGIN
    DECLARE next_id INT;
    SET next_id = (SELECT IFNULL(MAX(SUBSTRING(historyID, 3)), 0) + 1 FROM orderhistory);
    SET NEW.historyID = CONCAT('OH', LPAD(next_id, 7, '0'));
END
$$
DELIMITER ;

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

--
-- Triggers `orderitem`
--
DROP TRIGGER IF EXISTS `before_insert_orderitem`;
DELIMITER $$
CREATE TRIGGER `before_insert_orderitem` BEFORE INSERT ON `orderitem` FOR EACH ROW BEGIN
    DECLARE next_id INT;
    SET next_id = (SELECT IFNULL(MAX(SUBSTRING(orderItemID, 3)), 0) + 1 FROM orderitem);
    SET NEW.orderItemID = CONCAT('OI', LPAD(next_id, 7, '0'));
END
$$
DELIMITER ;

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

--
-- Triggers `payment`
--
DROP TRIGGER IF EXISTS `before_insert_payment`;
DELIMITER $$
CREATE TRIGGER `before_insert_payment` BEFORE INSERT ON `payment` FOR EACH ROW BEGIN
    DECLARE next_id INT;
    SET next_id = (SELECT IFNULL(MAX(SUBSTRING(billID, 2)), 0) + 1 FROM payment);
    SET NEW.billID = CONCAT('B', LPAD(next_id, 6, '0'));
END
$$
DELIMITER ;

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

--
-- Triggers `paymentmethod`
--
DROP TRIGGER IF EXISTS `before_insert_paymentmethod`;
DELIMITER $$
CREATE TRIGGER `before_insert_paymentmethod` BEFORE INSERT ON `paymentmethod` FOR EACH ROW BEGIN
    DECLARE next_id INT;
    SET next_id = (SELECT IFNULL(MAX(SUBSTRING(paymentMethodID, 3)), 0) + 1 FROM paymentmethod);
    SET NEW.paymentMethodID = CONCAT('PM', LPAD(next_id, 3, '0'));
END
$$
DELIMITER ;

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
-- Triggers `product`
--
DROP TRIGGER IF EXISTS `before_insert_product`;
DELIMITER $$
CREATE TRIGGER `before_insert_product` BEFORE INSERT ON `product` FOR EACH ROW BEGIN
    DECLARE next_id INT;
    SET next_id = (SELECT IFNULL(MAX(SUBSTRING(productID, 2)), 0) + 1 FROM product);
    SET NEW.productID = CONCAT('P', LPAD(next_id, 5, '0'));
END
$$
DELIMITER ;

-- --------------------------------------------------------

--
-- Table structure for table `productcatalog`
--

DROP TABLE IF EXISTS `productcatalog`;
CREATE TABLE `productcatalog` (
  `catalogID` varchar(10) NOT NULL,
  `categories` text DEFAULT NULL,
  `brands` text DEFAULT NULL,
  `filters` text DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Triggers `productcatalog`
--
DROP TRIGGER IF EXISTS `before_insert_productcatalog`;
DELIMITER $$
CREATE TRIGGER `before_insert_productcatalog` BEFORE INSERT ON `productcatalog` FOR EACH ROW BEGIN
    DECLARE next_id INT;
    SET next_id = (SELECT IFNULL(MAX(SUBSTRING(catalogID, 3)), 0) + 1 FROM productcatalog);
    SET NEW.catalogID = CONCAT('PC', LPAD(next_id, 5, '0'));
END
$$
DELIMITER ;

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

--
-- Dumping data for table `shippingaddress`
--

INSERT INTO `shippingaddress` (`shippingaddressID`, `customerID`, `shippingAddresses`) VALUES
('SA000001', 'C0001', '[{\"address\":\"123 Main St, Anytown, USA\",\"default\":true},{\"address\":\"123 Work Building, Downtown, USA\",\"default\":false}]'),
('SA000002', 'C0002', '[{\"address\":\"456 Oak Ave, Sometown, USA\",\"default\":true}]'),
('SA000003', 'C0003', '[{\"address\":\"789 Pine Rd, Othertown, USA\",\"default\":true},{\"address\":\"789 Office Complex, Business District, USA\",\"default\":false}]'),
('SA000004', 'C0004', '[{\"address\":\"101 Maple Dr, Newtown, USA\",\"default\":true}]'),
('SA000005', 'C0005', '[{\"address\":\"202 Birch Ln, Oldtown, USA\",\"default\":true}]'),
('SA000006', 'C0006', '[{\"address\":\"303 Cedar St, Hometown, USA\",\"default\":true},{\"address\":\"303 Second Home, Vacation Spot, USA\",\"default\":false}]'),
('SA000007', 'C0007', '[{\"address\":\"404 Elm Pl, Yourtown, USA\",\"default\":true}]'),
('SA000008', 'C0008', '[{\"address\":\"505 Pine Ave, Theirtown, USA\",\"default\":true}]'),
('SA000009', 'C0009', '[{\"address\":\"606 Oak St, Thattown, USA\",\"default\":true}]'),
('SA000010', 'C0010', '[{\"address\":\"707 Maple Ave, Thistown, USA\",\"default\":true},{\"address\":\"707 Secondary Address, Nearby, USA\",\"default\":false}]'),
('SA000011', 'C0011', '[{\"address\":\"809 Elm St, Anyplace, USA\",\"default\":true}]'),
('SA000012', 'C0012', '[{\"address\":\"910 Cedar Rd, Someplace, USA\",\"default\":true}]'),
('SA000013', 'C0013', '[{\"address\":\"1011 Birch Dr, Otherplace, USA\",\"default\":true}]'),
('SA000014', 'C0014', '[{\"address\":\"1112 Oak Lane, Newplace, USA\",\"default\":true},{\"address\":\"1112 Work Place, Business Park, USA\",\"default\":false}]'),
('SA000015', 'C0015', '[{\"address\":\"1213 Pine St, Oldplace, USA\",\"default\":true}]'),
('SA000016', 'C0016', '[{\"address\":\"1314 Maple Blvd, Homeplace, USA\",\"default\":true}]'),
('SA000017', 'C0017', '[{\"address\":\"1415 Cedar Ave, Yourplace, USA\",\"default\":true}]'),
('SA000018', 'C0018', '[{\"address\":\"1516 Elm Rd, Theirplace, USA\",\"default\":true},{\"address\":\"1516 PO Box, Mail Center, USA\",\"default\":false}]'),
('SA000019', 'C0019', '[{\"address\":\"1617 Birch Ave, Thatplace, USA\",\"default\":true}]'),
('SA000020', 'C0020', '[{\"address\":\"1718 Cedar Blvd, Thistown, USA\",\"default\":true}]');

--
-- Triggers `shippingaddress`
--
DROP TRIGGER IF EXISTS `before_insert_shippingaddress`;
DELIMITER $$
CREATE TRIGGER `before_insert_shippingaddress` BEFORE INSERT ON `shippingaddress` FOR EACH ROW BEGIN
    DECLARE next_id INT;
    SET next_id = (SELECT IFNULL(MAX(CAST(SUBSTRING(shippingaddressID, 3) AS UNSIGNED)), 0) + 1 FROM shippingaddress);
    SET NEW.shippingaddressID = CONCAT('SA', LPAD(next_id, 6, '0'));
END
$$
DELIMITER ;

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
-- Triggers `shoppingcart`
--
DROP TRIGGER IF EXISTS `before_insert_shoppingcart`;
DELIMITER $$
CREATE TRIGGER `before_insert_shoppingcart` BEFORE INSERT ON `shoppingcart` FOR EACH ROW BEGIN
    DECLARE next_id INT;
    SET next_id = (SELECT IFNULL(MAX(SUBSTRING(cartID, 3)), 0) + 1 FROM shoppingcart);
    SET NEW.cartID = CONCAT('CA', LPAD(next_id, 5, '0'));
END
$$
DELIMITER ;

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
('A0001', 'admin_jones', 'admin.j@pchub.com', '$2a$12$bq5W3JgAXVAMxDLbZUpupu5BMF1JhPzJSRcm8DfLUU0WvJSzWsrjW', '2024-10-01 08:00:00', '2025-03-22 18:00:00', 'active', '555-987-6543', 'Alex Jones', 'Admin'),
('A0002', 'admin_smith', 'admin.s@pchub.com', '$2a$12$gQGJCZRrFfcv76L1Jy2q7.qKkPzgeR1/x0Vo1RrYdnaVupwJzALLm', '2024-10-01 08:15:00', '2025-03-22 17:45:00', 'active', '555-876-5432', 'Sam Smith', 'Admin'),
('A0003', 'admin_johnson', 'admin.j2@pchub.com', '$2a$12$QY6J4t.yF734uACPLmz/YOSp5CZlZeZoWYEj3xdS33KLoxeJRe.BC', '2024-10-01 08:30:00', '2025-03-21 18:30:00', 'active', '555-765-4321', 'Jordan Johnson', 'Admin'),
('A0004', 'admin_brown', 'admin.b@pchub.com', '$2a$12$WBPu2kXXME5aUlPxtQ49LuRB.WdwXKEy3uRkzzsOHR5iC7qMB0Uya', '2024-10-01 08:45:00', '2025-03-22 16:15:00', 'active', '555-654-3210', 'Blake Brown', 'Admin'),
('A0005', 'admin_garcia', 'admin.g@pchub.com', '$2a$12$eLNkBrPpPnFSL.0TBEGf0eSbpX3aCZCMhLbmWKQXcr2aA.6DRUTdK', '2024-10-01 09:00:00', '2025-03-20 15:30:00', 'active', '555-543-2109', 'Gabriel Garcia', 'Admin'),
('C0001', 'john_doe', 'john.doe@email.com', '$2a$12$YFkHmZr1ZEtIk6Co42xt8Od3HQpMZFMYPcH9HQ5cGclpL/V6sPrwK', '2024-10-01 08:30:00', '2025-03-22 14:22:00', 'active', '555-123-4567', 'John Doe', 'Customer'),
('C0002', 'jane_smith', 'jane.smith@email.com', '$2a$12$DGnTLHy5aFvTZC4sF8/vJuNGF7tpEb/aWjgr1Yh2fj4PSjzR0AMbi', '2024-10-02 09:45:00', '2025-03-21 16:10:00', 'active', '555-234-5678', 'Jane Smith', 'Customer'),
('C0003', 'mike_johnson', 'mike.j@email.com', '$2a$12$KjB4Xds.vTdXsL2ZKEj/2.e/LMT.q2KqaM2fEDHcThgKYz/cM2l.i', '2024-10-03 10:15:00', '2025-03-22 09:05:00', 'active', '555-345-6789', 'Michael Johnson', 'Customer'),
('C0004', 'sarah_williams', 'sarah.w@email.com', '$2a$12$8QPnp4yNrO4GSxvL4V7MfeluS4EPV5ZQGkZLcVJl8UDbXEOSKcAiO', '2024-10-04 11:30:00', '2025-03-20 11:40:00', 'active', '555-456-7890', 'Sarah Williams', 'Customer'),
('C0005', 'david_brown', 'david.b@email.com', '$2a$12$SBD6nPjvX9qNdFJTYMGxw.qdB6hJj/snAQSEXeF5YHP4iR7JrIUpa', '2024-10-05 12:45:00', '2025-03-19 15:35:00', 'active', '555-567-8901', 'David Brown', 'Customer'),
('C0006', 'lisa_taylor', 'lisa.t@email.com', '$2a$12$LcEW7OdFCrHa3uBh9FLjGeu2lNMX4QxDMfVEsT6Q/w.gjRZd0qpLO', '2024-10-06 13:15:00', '2025-03-22 17:15:00', 'active', '555-678-9012', 'Lisa Taylor', 'Customer'),
('C0007', 'kevin_davis', 'kevin.d@email.com', '$2a$12$q.ZsGqwzpSt0R08F29.Ceu6c3/1SNpuSHlxdE.iK8NqIUWJ/TfVZi', '2024-10-07 14:30:00', '2025-03-21 10:25:00', 'active', '555-789-0123', 'Kevin Davis', 'Customer'),
('C0008', 'amy_miller', 'amy.m@email.com', '$2a$12$OMsdUxgVOtFB4q2l4CDCae9BrqZzxGSEEoMT3ASAi5/0ppNIoF3TG', '2024-10-08 15:45:00', '2025-03-20 14:50:00', 'active', '555-890-1234', 'Amy Miller', 'Customer'),
('C0009', 'brian_wilson', 'brian.w@email.com', '$2a$12$wUuVxuIWlvgV9YlS/FMxJ.erjRbHIqSNF9TDyD1g6SnOqZ1gp.OZm', '2024-10-09 16:15:00', '2025-03-18 12:30:00', 'active', '555-901-2345', 'Brian Wilson', 'Customer'),
('C0010', 'jessica_moore', 'jessica.m@email.com', '$2a$12$hhcWfZkTBXQmGZtKmDBvHOJK.8wUTQGu8n8AyAm9BjUcBFWaDnw3C', '2024-10-10 17:30:00', '2025-03-22 08:45:00', 'active', '555-012-3456', 'Jessica Moore', 'Customer'),
('C0011', 'chris_taylor', 'chris.t@email.com', '$2a$12$OTUCUbYzK9g1ZZkZFDxdXOw7yJhk5.Mme0BzGBh3ZLtjP1w.8ePUK', '2024-10-11 09:00:00', '2025-03-21 15:20:00', 'active', '555-123-7890', 'Christopher Taylor', 'Customer'),
('C0012', 'rachel_anderson', 'rachel.a@email.com', '$2a$12$3iImYQKKODBWD.PCIRiuQuiqbYdI0lKDDSEm/JVkOq4wGSxo.RnuO', '2024-10-12 10:30:00', '2025-03-19 13:10:00', 'active', '555-234-8901', 'Rachel Anderson', 'Customer'),
('C0013', 'mark_thomas', 'mark.t@email.com', '$2a$12$e/nv7c6UVuEJnl9z5yDdEe4yvzGl.CZs.vGE.1Cn.m95eCFEA5F.W', '2024-10-13 11:45:00', '2025-03-22 11:50:00', 'active', '555-345-9012', 'Mark Thomas', 'Customer'),
('C0014', 'emily_jackson', 'emily.j@email.com', '$2a$12$7bXcWE8nFMbGYsGnP4aQVOQsxprLxVMdH3HO7k47K7oMWH0kQizEW', '2024-10-14 12:15:00', '2025-03-20 17:35:00', 'active', '555-456-0123', 'Emily Jackson', 'Customer'),
('C0015', 'steven_white', 'steven.w@email.com', '$2a$12$fzlhBnxGGSNT6bZbUXAFcORH0sBVVlfPMtfsXxq4/tFdlQyIadX7G', '2024-10-15 13:30:00', '2025-03-17 09:40:00', 'active', '555-567-1234', 'Steven White', 'Customer'),
('C0016', 'laura_harris', 'laura.h@email.com', '$2a$12$sB45XpxL8qI7ztYqxeaWLOlHhPrYe1xNCFGsXMF/dZPcjAilOoZHK', '2024-10-16 14:45:00', '2025-03-22 16:25:00', 'active', '555-678-2345', 'Laura Harris', 'Customer'),
('C0017', 'daniel_martin', 'daniel.m@email.com', '$2a$12$RpqjIJi3w/iGRkQgDtF44OxmVOdBRfcQfR1Uwe8nbA11KUZ.eDkri', '2024-10-17 15:15:00', '2025-03-21 12:55:00', 'active', '555-789-3456', 'Daniel Martin', 'Customer'),
('C0018', 'michelle_garcia', 'michelle.g@email.com', '$2a$12$wPBpNRxl60BO2bqSSG/9GeB8nY8P.tdFcWJ/vDXBnCqDk5OWJ9DT.', '2024-10-18 16:30:00', '2025-03-18 14:20:00', 'active', '555-890-4567', 'Michelle Garcia', 'Customer'),
('C0019', 'robert_lee', 'robert.l@email.com', '$2a$12$UZVbkJo5Xxa.WJalRVw8TuW9DWvMU3EeIzVNfwZKdPB/R0E9J10Gy', '2024-10-19 17:45:00', '2025-03-16 10:15:00', 'active', '555-901-5678', 'Robert Lee', 'Customer'),
('C0020', 'jennifer_king', 'jennifer.k@email.com', '$2a$12$LD/lYdWR1V1r6JVLTwrGpueK6mXu5h8rnjpDaXbmLICc2fmiVWUlG', '2024-10-20 09:15:00', '2025-03-22 13:05:00', 'active', '555-012-6789', 'Jennifer King', 'Customer'),
('C0021', 'ryan_scott', 'ryan.s@email.com', '$2a$12$cFTx8J3FVBfVeXvK.3TBdOoKJZY6xIxKhBFRnBSgcYTRIK9.QkBSm', '2024-10-21 10:30:00', '2025-03-20 09:50:00', 'active', '555-123-8901', 'Ryan Scott', 'Customer'),
('C0022', 'nicole_adams', 'nicole.a@email.com', '$2a$12$7PWO7wnF58U.J3IInBXdXOnG3zX.Bfm42Z9lvVTBURNwKGbGz5AQG', '2024-10-22 11:45:00', '2025-03-19 15:30:00', 'active', '555-234-9012', 'Nicole Adams', 'Customer'),
('C0023', 'tyler_baker', 'tyler.b@email.com', '$2a$12$I6XqFNg5hfI6n0YF/CRwnu/7/jdOsMxYZqVmrXSWd7/uJGEq8aodW', '2024-10-23 12:15:00', '2025-03-21 17:15:00', 'active', '555-345-0123', 'Tyler Baker', 'Customer'),
('C0024', 'olivia_gonzalez', 'olivia.g@email.com', '$2a$12$AcZwlm3.4hxA.zR/Ct3xAOwVbxO4a1v7rlv3jOqqSQF7.x03jFX8e', '2024-10-24 13:30:00', '2025-03-22 10:40:00', 'active', '555-456-1234', 'Olivia Gonzalez', 'Customer'),
('C0025', 'eric_nelson', 'eric.n@email.com', '$2a$12$rM7fPZsYqKjQYZaVNODWk.VhPWS4aJmPdpbRQ25qxZVG8Z9E4tl.O', '2024-10-25 14:45:00', '2025-03-18 13:25:00', 'active', '555-567-2345', 'Eric Nelson', 'Customer');

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
  `addedDate` datetime NOT NULL DEFAULT current_timestamp(),
  PRIMARY KEY (`userPaymentMethodID`),
  KEY `userID` (`userID`),
  KEY `paymentMethodID` (`paymentMethodID`),
  CONSTRAINT `user_payment_method_ibfk_1` FOREIGN KEY (`userID`) REFERENCES `user` (`userID`),
  CONSTRAINT `user_payment_method_ibfk_2` FOREIGN KEY (`paymentMethodID`) REFERENCES `paymentmethod` (`paymentMethodID`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Triggers `user_payment_method`
--
DROP TRIGGER IF EXISTS `before_insert_user_payment_method`;
DELIMITER $$
CREATE TRIGGER `before_insert_user_payment_method` BEFORE INSERT ON `user_payment_method` FOR EACH ROW BEGIN
    DECLARE next_id INT;
    SET next_id = (SELECT IFNULL(MAX(SUBSTRING(userPaymentMethodID, 3)), 0) + 1 FROM user_payment_method);
    SET NEW.userPaymentMethodID = CONCAT('UP', LPAD(next_id, 7, '0'));
END
$$
DELIMITER ;

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
  ADD KEY `productID` (`productID`);

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
  ADD KEY `paymentMethodID` (`paymentMethodID`);

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
  ADD PRIMARY KEY (`paymentMethodID`),
  ADD KEY `customerID` (`customerID`);

--
-- Indexes for table `product`
--
ALTER TABLE `product`
  ADD PRIMARY KEY (`productID`),
  ADD KEY `category` (`category`);

--
-- Indexes for table `productcatalog`
--
ALTER TABLE `productcatalog`
  ADD PRIMARY KEY (`catalogID`);
ALTER TABLE `productcatalog` ADD FULLTEXT KEY `search_idx` (`categories`,`brands`,`filters`);

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
-- Constraints for dumped tables
--

--
-- Constraints for table `adminrole`
--
ALTER TABLE `adminrole`
  ADD CONSTRAINT `admin_ibfk_1` FOREIGN KEY (`userID`) REFERENCES `user` (`userID`);

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
  ADD CONSTRAINT `order_ibfk_1` FOREIGN KEY (`customerID`) REFERENCES `shippingaddress` (`customerID`),
  ADD CONSTRAINT `order_ibfk_2` FOREIGN KEY (`paymentMethodID`) REFERENCES `paymentmethod` (`paymentMethodID`);

--
-- Constraints for table `orderhistory`
--
ALTER TABLE `orderhistory`
  ADD CONSTRAINT `orderhistory_ibfk_1` FOREIGN KEY (`customerID`) REFERENCES `shippingaddress` (`customerID`);

--
-- Constraints for table `orderitem`
--
ALTER TABLE `orderitem`
  ADD CONSTRAINT `orderitem_ibfk_1` FOREIGN KEY (`orderID`) REFERENCES `order` (`orderID`),
  ADD CONSTRAINT `orderitem_ibfk_2` FOREIGN KEY (`productID`) REFERENCES `product`