-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: May 03, 2025 at 01:12 PM
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
-- Table structure for table `bill`
--

DROP TABLE IF EXISTS `bill`;
CREATE TABLE `bill` (
  `billID` varchar(10) NOT NULL,
  `orderID` varchar(10) NOT NULL,
  `amount` decimal(10,2) NOT NULL,
  `payment_MethodID` varchar(10) NOT NULL,
  `paymentStatus` enum('PENDING','AWAITING_PAYMENT','PAID','FAILED') NOT NULL DEFAULT 'PENDING',
  `issueDate` datetime NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `bill`
--

INSERT INTO `bill` (`billID`, `orderID`, `amount`, `payment_MethodID`, `paymentStatus`, `issueDate`) VALUES
('B000001', 'O0006', 1750.11, 'PM001', 'PENDING', '2025-05-03 12:24:13'),
('B000002', 'O0005', 1750.11, 'PM001', 'PENDING', '2025-05-03 12:41:35'),
('B000003', 'O0004', 7241.94, 'PM001', 'PENDING', '2025-05-03 14:19:03'),
('B000004', 'O0006', 1750.11, 'PM001', 'PENDING', '2025-05-03 14:28:26'),
('B000005', 'O0006', 1750.11, 'PM001', 'PENDING', '2025-05-03 14:28:52'),
('B000006', 'O0006', 1750.11, 'PM001', 'PENDING', '2025-05-03 14:34:19'),
('B000007', 'O0005', 1750.11, 'PM001', 'PENDING', '2025-05-03 14:37:05'),
('B000008', 'O0007', 1139.93, 'PM001', 'PENDING', '2025-05-03 15:08:47'),
('B000009', 'O0005', 1750.11, 'PM001', 'PENDING', '2025-05-03 17:39:50'),
('B000010', 'O0008', 913.93, 'PM001', 'PENDING', '2025-05-03 19:05:02'),
('B000011', 'O0009', 1230.34, 'PM001', 'PENDING', '2025-05-03 19:08:55'),
('B000012', 'O0010', 518.46, 'PM001', 'PENDING', '2025-05-03 19:10:36');

--
-- Triggers `bill`
--
DROP TRIGGER IF EXISTS `before_insert_bill`;
DELIMITER $$
CREATE TRIGGER `before_insert_bill` BEFORE INSERT ON `bill` FOR EACH ROW BEGIN
    DECLARE next_id INT;
    SET next_id = (SELECT IFNULL(MAX(SUBSTRING(billID, 2)), 0) + 1 FROM bill);
    SET NEW.billID = CONCAT('B', LPAD(next_id, 6, '0'));
END
$$
DELIMITER ;

-- --------------------------------------------------------

--
-- Table structure for table `cart`
--

DROP TABLE IF EXISTS `cart`;
CREATE TABLE `cart` (
  `cartID` varchar(10) NOT NULL,
  `customerID` varchar(10) NOT NULL,
  `createdDate` datetime NOT NULL DEFAULT current_timestamp(),
  `lastUpdated` datetime NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  `itemCount` int(11) DEFAULT 0,
  `subtotal` decimal(10,2) DEFAULT 0.00
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `cart`
--

INSERT INTO `cart` (`cartID`, `customerID`, `createdDate`, `lastUpdated`, `itemCount`, `subtotal`) VALUES
('CA00001', 'C0027', '2025-05-01 03:14:29', '2025-05-01 03:14:29', 0, 0.00),
('CA00002', 'C0001', '2025-05-01 10:36:17', '2025-05-01 10:36:17', 0, 0.00),
('CA00003', 'C0028', '2025-05-01 17:41:07', '2025-05-01 17:41:07', 0, 0.00),
('CA00004', 'C0002', '2025-05-03 19:04:44', '2025-05-03 19:04:44', 0, 0.00);

--
-- Triggers `cart`
--
DROP TRIGGER IF EXISTS `before_insert_cart`;
DELIMITER $$
CREATE TRIGGER `before_insert_cart` BEFORE INSERT ON `cart` FOR EACH ROW BEGIN
    DECLARE next_id INT;
    SET next_id = (SELECT IFNULL(MAX(SUBSTRING(cartID, 3)), 0) + 1 FROM cart);
    SET NEW.cartID = CONCAT('CA', LPAD(next_id, 5, '0'));
END
$$
DELIMITER ;

-- --------------------------------------------------------

--
-- Table structure for table `cart_item`
--

DROP TABLE IF EXISTS `cart_item`;
CREATE TABLE `cart_item` (
  `cartItemID` varchar(10) NOT NULL,
  `cartID` varchar(10) NOT NULL,
  `productID` varchar(10) NOT NULL,
  `quantity` int(11) NOT NULL DEFAULT 1,
  `price` decimal(10,2) NOT NULL,
  `subtotal` decimal(10,2) GENERATED ALWAYS AS (`price` * `quantity`) STORED
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `cart_item`
--

INSERT INTO `cart_item` (`cartItemID`, `cartID`, `productID`, `quantity`, `price`) VALUES
('CI000001', 'CA00003', 'P00001', 4, 599.99),
('CI000002', 'CA00003', 'P00001', 5, 599.99),
('CI000003', 'CA00003', 'P00001', 1, 599.99),
('CI000004', 'CA00003', 'P00001', 4, 599.99),
('CI000005', 'CA00003', 'P00001', 2, 599.99),
('CI000006', 'CA00003', 'P00001', 3, 599.99),
('CI000007', 'CA00003', 'P00002', 4, 549.99),
('CI000008', 'CA00003', 'P00003', 4, 1599.99),
('CI000009', 'CA00003', 'P00001', 5, 599.99),
('CI000010', 'CA00003', 'P00005', 5, 199.99),
('CI000011', 'CA00003', 'P00001', 6, 599.99),
('CI000012', 'CA00003', 'P00001', 5, 599.99),
('CI000013', 'CA00003', 'P00001', 20, 599.99),
('CI000014', 'CA00003', 'P00003', 4, 1599.99),
('CI000015', 'CA00003', 'P00001', 3, 599.99),
('CI000016', 'CA00002', 'P00013', 5, 159.99);

--
-- Triggers `cart_item`
--
DROP TRIGGER IF EXISTS `before_insert_cart_item`;
DELIMITER $$
CREATE TRIGGER `before_insert_cart_item` BEFORE INSERT ON `cart_item` FOR EACH ROW BEGIN
    DECLARE next_id INT;
    SET next_id = (SELECT IFNULL(MAX(SUBSTRING(cartItemID, 3)), 0) + 1 FROM cart_item);
    SET NEW.cartItemID = CONCAT('CI', LPAD(next_id, 6, '0'));
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
  `orderStatus` enum('PENDING','PROCESSING','SHIPPED','DELIVERED','CANCELLED','REFUNDED') DEFAULT 'PENDING',
  `totalAmount` decimal(10,2) NOT NULL,
  `shipping_addressID` varchar(255) NOT NULL,
  `payment_MethodID` varchar(10) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `order`
--

INSERT INTO `order` (`orderID`, `customerID`, `orderDate`, `orderStatus`, `totalAmount`, `shipping_addressID`, `payment_MethodID`) VALUES
('O0001', 'C0028', '2025-05-01 19:05:11', 'PENDING', 999.95, 'SA000027', 'PM001'),
('O0002', 'C0028', '2025-05-01 19:14:07', 'PENDING', 2999.95, 'SA000027', 'PM001'),
('O0003', 'C0028', '2025-05-01 19:34:06', 'PENDING', 11999.80, 'SA000027', 'PM001'),
('O0004', 'C0028', '2025-05-01 19:43:33', 'PENDING', 6399.96, 'SA000027', 'PM001'),
('O0005', 'C0001', '2025-05-03 12:03:15', 'CANCELLED', 1539.93, 'SA000021', 'PM001'),
('O0006', 'C0001', '2025-05-03 12:24:13', 'PROCESSING', 1539.93, 'SA000021', 'PM001'),
('O0007', 'C0001', '2025-05-03 15:08:47', 'PENDING', 999.95, 'SA000001', 'PM001'),
('O0008', 'C0002', '2025-05-03 19:05:02', 'PENDING', 799.95, 'SA000002', 'PM001');

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
-- Table structure for table `order_item`
--

DROP TABLE IF EXISTS `order_item`;
CREATE TABLE `order_item` (
  `orderItemID` varchar(10) NOT NULL,
  `orderID` varchar(10) NOT NULL,
  `productID` varchar(10) NOT NULL,
  `quantity` int(11) NOT NULL,
  `price` decimal(10,2) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `order_item`
--

INSERT INTO `order_item` (`orderItemID`, `orderID`, `productID`, `quantity`, `price`) VALUES
('OI0000001', 'O0001', 'P00005', 5, 199.99),
('OI0000002', 'O0002', 'P00001', 5, 599.99),
('OI0000003', 'O0003', 'P00001', 20, 599.99),
('OI0000004', 'O0004', 'P00003', 4, 1599.99),
('OI0000006', 'O0006', 'P00033', 7, 219.99),
('OI0000007', 'O0007', 'P00005', 5, 199.99),
('OI0000008', 'O0005', 'P00033', 7, 219.99),
('OI0000009', 'O0008', 'P00034', 5, 159.99);

--
-- Triggers `order_item`
--
DROP TRIGGER IF EXISTS `before_insert_order_item`;
DELIMITER $$
CREATE TRIGGER `before_insert_order_item` BEFORE INSERT ON `order_item` FOR EACH ROW BEGIN
    DECLARE next_id INT;
    SET next_id = (SELECT IFNULL(MAX(SUBSTRING(orderItemID, 3)), 0) + 1 FROM order_item);
    SET NEW.orderItemID = CONCAT('OI', LPAD(next_id, 7, '0'));
END
$$
DELIMITER ;

-- --------------------------------------------------------

--
-- Table structure for table `payment_method`
--

DROP TABLE IF EXISTS `payment_method`;
CREATE TABLE `payment_method` (
  `payment_methodID` varchar(10) NOT NULL,
  `name` varchar(50) NOT NULL,
  `description` text DEFAULT NULL,
  `addedDate` datetime NOT NULL DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `payment_method`
--

INSERT INTO `payment_method` (`payment_methodID`, `name`, `description`, `addedDate`) VALUES
('PM001', 'Cash', 'Traditional cash payment upon delivery or collection', '2025-05-01 09:40:05'),
('PM002', 'Credit Card', 'Payment using major credit cards including Visa, Mastercard, and American Express', '2025-05-01 09:40:05'),
('PM003', 'Debit Card', 'Direct payment from customer bank account using debit cards', '2025-05-01 09:40:05'),
('PM004', 'TouchNGo eWallet', 'Malaysia\'s popular digital wallet for contactless payments', '2025-05-01 09:40:05'),
('PM005', 'Maybank2U', 'Online banking platform for Maybank customers', '2025-05-01 09:40:05'),
('PM006', 'CIMB Clicks', 'Internet banking service for CIMB Bank customers', '2025-05-01 09:40:05'),
('PM007', 'GrabPay', 'Mobile wallet by Grab for cashless transactions', '2025-05-01 09:40:05'),
('PM008', 'Boost', 'Homegrown Malaysian e-wallet with cashback rewards', '2025-05-01 09:40:05'),
('PM009', 'FPX (Financial Process Exchange)', 'Secure online payment gateway linking financial institutions', '2025-05-01 09:40:05'),
('PM010', 'PayPal', 'International online payment system supporting multiple currencies', '2025-05-01 09:40:05'),
('PM011', 'Apple Pay', 'Digital wallet platform and mobile payment service by Apple', '2025-05-01 09:40:05'),
('PM012', 'Google Pay', 'Mobile payment service by Google for contactless payments', '2025-05-01 09:40:05'),
('PM013', 'Samsung Pay', 'Mobile payment and digital wallet service by Samsung', '2025-05-01 09:40:05'),
('PM014', 'Bank Transfer', 'Direct transfer between bank accounts via ATM, online or mobile banking', '2025-05-01 09:40:05'),
('PM015', 'DuitNow QR', 'National QR standard payment method in Malaysia', '2025-05-01 09:40:05');

--
-- Triggers `payment_method`
--
DROP TRIGGER IF EXISTS `before_insert_payment_method`;
DELIMITER $$
CREATE TRIGGER `before_insert_payment_method` BEFORE INSERT ON `payment_method` FOR EACH ROW BEGIN
    DECLARE next_id INT;
    SET next_id = (SELECT IFNULL(MAX(SUBSTRING(payment_MethodID, 3)), 0) + 1 FROM payment_method);
    SET NEW.payment_MethodID = CONCAT('PM', LPAD(next_id, 3, '0'));
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
  `product_categoryID` varchar(10) DEFAULT NULL,
  `unitPrice` decimal(10,2) NOT NULL,
  `currentQuantity` int(11) NOT NULL DEFAULT 0,
  `specifications` text DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `product`
--

INSERT INTO `product` (`productID`, `name`, `description`, `brand`, `product_categoryID`, `unitPrice`, `currentQuantity`, `specifications`) VALUES
('P00001', 'Intel Core i9-13900K', '14th Gen Intel Core i9 Processor', 'Intel', 'CAT008', 599.99, 50, '{\"cores\":24,\"threads\":32,\"base_clock\":\"3.0GHz\",\"boost_clock\":\"5.8GHz\"}'),
('P00002', 'AMD Ryzen 9 7950X', 'AMD Ryzen 9 Processor', 'AMD', 'CAT008', 549.99, 45, '{\"cores\":16,\"threads\":32,\"base_clock\":\"4.5GHz\",\"boost_clock\":\"5.7GHz\"}'),
('P00003', 'NVIDIA RTX 4090', 'NVIDIA GeForce RTX 4090 Graphics Card', 'NVIDIA', 'CAT012', 1599.99, 30, '{\"vram\":\"24GB\",\"memory_type\":\"GDDR6X\",\"boost_clock\":\"2.52GHz\"}'),
('P00004', 'AMD RX 7900 XTX', 'AMD Radeon RX 7900 XTX Graphics Card', 'AMD', 'CAT012', 999.99, 35, '{\"vram\":\"24GB\",\"memory_type\":\"GDDR6\",\"boost_clock\":\"2.5GHz\"}'),
('P00005', 'Samsung 980 Pro 2TB', 'Samsung 980 Pro NVMe SSD', 'Samsung', 'CAT011', 199.99, 100, '{\"capacity\":\"2TB\",\"interface\":\"PCIe 4.0\",\"read_speed\":\"7000MB/s\",\"write_speed\":\"5000MB/s\"}'),
('P00006', 'Corsair Vengeance RGB 32GB', 'DDR5 Memory Kit', 'Corsair', 'CAT010', 149.99, 75, '{\"capacity\":\"32GB\",\"speed\":\"6000MHz\",\"latency\":\"CL36\"}'),
('P00007', 'ASUS ROG Strix Z790-E', 'Intel Z790 Motherboard', 'ASUS', 'CAT009', 399.99, 40, '{\"socket\":\"LGA1700\",\"chipset\":\"Z790\",\"memory_slots\":4}'),
('P00008', 'MSI MPG B650 Carbon', 'AMD B650 Motherboard', 'MSI', 'CAT009', 299.99, 45, '{\"socket\":\"AM5\",\"chipset\":\"B650\",\"memory_slots\":4}'),
('P00009', 'Corsair RM1000x', '1000W Power Supply', 'Corsair', 'CAT013', 189.99, 60, '{\"wattage\":\"1000W\",\"efficiency\":\"80+ Gold\",\"modular\":\"Full\"}'),
('P00010', 'NZXT H7 Elite', 'Mid-Tower Case', 'NZXT', 'CAT014', 179.99, 55, '{\"form_factor\":\"Mid-Tower\",\"material\":\"Steel/Tempered Glass\",\"fans_included\":3}'),
('P00011', 'Intel Core i7-13700K', '13th Gen Intel Core i7 Processor', 'Intel', 'CAT008', 409.99, 60, '{\"cores\":16,\"threads\":24,\"base_clock\":\"3.4GHz\",\"boost_clock\":\"5.4GHz\"}'),
('P00012', 'AMD Ryzen 7 7800X3D', 'AMD Ryzen 7 Processor with 3D V-Cache', 'AMD', 'CAT008', 449.99, 40, '{\"cores\":8,\"threads\":16,\"base_clock\":\"4.2GHz\",\"boost_clock\":\"5.0GHz\"}'),
('P00013', 'Intel Core i5-13600K', '13th Gen Intel Core i5 Processor', 'Intel', 'CAT008', 319.99, 70, '{\"cores\":14,\"threads\":20,\"base_clock\":\"3.5GHz\",\"boost_clock\":\"5.1GHz\"}'),
('P00014', 'AMD Ryzen 5 7600X', 'AMD Ryzen 5 Processor', 'AMD', 'CAT008', 299.99, 65, '{\"cores\":6,\"threads\":12,\"base_clock\":\"4.7GHz\",\"boost_clock\":\"5.3GHz\"}'),
('P00015', 'NVIDIA RTX 4080', 'NVIDIA GeForce RTX 4080 Graphics Card', 'NVIDIA', 'CAT012', 1199.99, 25, '{\"vram\":\"16GB\",\"memory_type\":\"GDDR6X\",\"boost_clock\":\"2.51GHz\"}'),
('P00016', 'AMD RX 7800 XT', 'AMD Radeon RX 7800 XT Graphics Card', 'AMD', 'CAT012', 499.99, 40, '{\"vram\":\"16GB\",\"memory_type\":\"GDDR6\",\"boost_clock\":\"2.4GHz\"}'),
('P00017', 'NVIDIA RTX 4070 Ti', 'NVIDIA GeForce RTX 4070 Ti Graphics Card', 'NVIDIA', 'CAT012', 799.99, 35, '{\"vram\":\"12GB\",\"memory_type\":\"GDDR6X\",\"boost_clock\":\"2.61GHz\"}'),
('P00018', 'AMD RX 7600', 'AMD Radeon RX 7600 Graphics Card', 'AMD', 'CAT012', 269.99, 50, '{\"vram\":\"8GB\",\"memory_type\":\"GDDR6\",\"boost_clock\":\"2.25GHz\"}'),
('P00019', 'Western Digital Black SN850X 1TB', 'WD Black SN850X NVMe SSD', 'Western Digital', 'CAT011', 149.99, 85, '{\"capacity\":\"1TB\",\"interface\":\"PCIe 4.0\",\"read_speed\":\"7300MB/s\",\"write_speed\":\"6300MB/s\"}'),
('P00020', 'Crucial P3 Plus 2TB', 'Crucial P3 Plus NVMe SSD', 'Crucial', 'CAT011', 129.99, 95, '{\"capacity\":\"2TB\",\"interface\":\"PCIe 4.0\",\"read_speed\":\"5000MB/s\",\"write_speed\":\"4200MB/s\"}'),
('P00021', 'Seagate Barracuda 4TB', 'Seagate Barracuda Hard Drive', 'Seagate', 'CAT011', 89.99, 120, '{\"capacity\":\"4TB\",\"interface\":\"SATA 6Gb/s\",\"rpm\":\"5400\"}'),
('P00022', 'Samsung 870 EVO 1TB', 'Samsung 870 EVO SATA SSD', 'Samsung', 'CAT011', 89.99, 110, '{\"capacity\":\"1TB\",\"interface\":\"SATA 6Gb/s\",\"read_speed\":\"560MB/s\",\"write_speed\":\"530MB/s\"}'),
('P00023', 'G.Skill Trident Z5 RGB 32GB', 'DDR5 Memory Kit', 'G.Skill', 'CAT010', 179.99, 65, '{\"capacity\":\"32GB\",\"speed\":\"6400MHz\",\"latency\":\"CL32\"}'),
('P00024', 'Kingston Fury Beast 16GB', 'DDR4 Memory Kit', 'Kingston', 'CAT010', 69.99, 90, '{\"capacity\":\"16GB\",\"speed\":\"3600MHz\",\"latency\":\"CL18\"}'),
('P00025', 'Crucial Ballistix 64GB', 'DDR5 Memory Kit', 'Crucial', 'CAT010', 289.99, 40, '{\"capacity\":\"64GB\",\"speed\":\"5200MHz\",\"latency\":\"CL40\"}'),
('P00026', 'TeamGroup T-Force Delta RGB 32GB', 'DDR4 Memory Kit', 'TeamGroup', 'CAT010', 109.99, 70, '{\"capacity\":\"32GB\",\"speed\":\"3600MHz\",\"latency\":\"CL18\"}'),
('P00027', 'Gigabyte Z790 Aorus Master', 'Intel Z790 Motherboard', 'Gigabyte', 'CAT009', 449.99, 35, '{\"socket\":\"LGA1700\",\"chipset\":\"Z790\",\"memory_slots\":4}'),
('P00028', 'ASRock X670E Taichi', 'AMD X670E Motherboard', 'ASRock', 'CAT009', 499.99, 30, '{\"socket\":\"AM5\",\"chipset\":\"X670E\",\"memory_slots\":4}'),
('P00029', 'MSI PRO B760M-A', 'Intel B760 Motherboard', 'MSI', 'CAT009', 139.99, 55, '{\"socket\":\"LGA1700\",\"chipset\":\"B760\",\"memory_slots\":4}'),
('P00030', 'ASUS TUF Gaming A620M-PLUS', 'AMD A620 Motherboard', 'ASUS', 'CAT009', 119.99, 60, '{\"socket\":\"AM5\",\"chipset\":\"A620\",\"memory_slots\":2}'),
('P00031', 'EVGA SuperNOVA 850 G6', '850W Power Supply', 'EVGA', 'CAT013', 149.99, 65, '{\"wattage\":\"850W\",\"efficiency\":\"80+ Gold\",\"modular\":\"Full\"}'),
('P00032', 'Seasonic Focus GX-750', '750W Power Supply', 'Seasonic', 'CAT013', 129.99, 70, '{\"wattage\":\"750W\",\"efficiency\":\"80+ Gold\",\"modular\":\"Full\"}'),
('P00033', 'be quiet! Straight Power 11 1000W', '1000W Power Supply', 'be quiet!', 'CAT013', 219.99, 45, '{\"wattage\":\"1000W\",\"efficiency\":\"80+ Platinum\",\"modular\":\"Full\"}'),
('P00034', 'Thermaltake Toughpower GF3 850W', '850W Power Supply', 'Thermaltake', 'CAT013', 159.99, 55, '{\"wattage\":\"850W\",\"efficiency\":\"80+ Gold\",\"modular\":\"Full\"}'),
('P00035', 'Fractal Design Meshify 2', 'Mid-Tower Case', 'Fractal Design', 'CAT014', 149.99, 50, '{\"form_factor\":\"Mid-Tower\",\"material\":\"Steel/Tempered Glass\",\"fans_included\":2}'),
('P00036', 'Corsair 5000D Airflow', 'Mid-Tower Case', 'Corsair', 'CAT014', 174.99, 45, '{\"form_factor\":\"Mid-Tower\",\"material\":\"Steel/Tempered Glass\",\"fans_included\":2}'),
('P00037', 'Lian Li O11 Dynamic EVO', 'Mid-Tower Case', 'Lian Li', 'CAT014', 189.99, 40, '{\"form_factor\":\"Mid-Tower\",\"material\":\"Aluminum/Tempered Glass\",\"fans_included\":0}'),
('P00038', 'Phanteks Eclipse G360A', 'Mid-Tower Case', 'Phanteks', 'CAT014', 99.99, 65, '{\"form_factor\":\"Mid-Tower\",\"material\":\"Steel/Tempered Glass\",\"fans_included\":3}'),
('P00039', 'LG UltraGear 27GP850-B', '27\" Gaming Monitor', 'LG', 'CAT015', 399.99, 40, '{\"size\":\"27 inch\",\"resolution\":\"2560x1440\",\"refresh_rate\":\"165Hz\",\"panel_type\":\"IPS\"}'),
('P00040', 'Samsung Odyssey G7', '32\" Curved Gaming Monitor', 'Samsung', 'CAT015', 649.99, 30, '{\"size\":\"32 inch\",\"resolution\":\"2560x1440\",\"refresh_rate\":\"240Hz\",\"panel_type\":\"VA\"}');

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
-- Table structure for table `product_catalogue`
--

DROP TABLE IF EXISTS `product_catalogue`;
CREATE TABLE `product_catalogue` (
  `catalogueID` varchar(10) NOT NULL,
  `name` varchar(50) NOT NULL,
  `description` text DEFAULT NULL,
  `startDate` datetime NOT NULL,
  `endDate` datetime NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `product_catalogue`
--

INSERT INTO `product_catalogue` (`catalogueID`, `name`, `description`, `startDate`, `endDate`) VALUES
('CATL001', '2025 Clearance', 'Clear stock', '2025-05-01 09:00:00', '2025-06-01 18:00:00'),
('CATL002', 'Samsung Discount', 'Discount laaaaaaaaa', '2025-06-05 08:00:00', '2025-06-10 18:00:00'),
('CATL003', 'Student Discount', 'Student Starter Pack', '2025-12-15 06:00:00', '2026-01-30 18:30:00');

--
-- Triggers `product_catalogue`
--
DROP TRIGGER IF EXISTS `before_insert_catalogue`;
DELIMITER $$
CREATE TRIGGER `before_insert_catalogue` BEFORE INSERT ON `product_catalogue` FOR EACH ROW BEGIN
    DECLARE next_id INT;
    -- Get the highest existing catalogue ID, defaulting to 0 if none exist
    SELECT IFNULL(MAX(CAST(SUBSTRING(catalogueID, 5) AS UNSIGNED)), 0) INTO next_id FROM product_catalogue;
    -- Increment the ID
    SET next_id = next_id + 1;
    -- Set the new catalogue ID
    SET NEW.catalogueID = CONCAT('CATL', LPAD(next_id, 3, '0'));
END
$$
DELIMITER ;

-- --------------------------------------------------------

--
-- Table structure for table `product_catalogue_item`
--

DROP TABLE IF EXISTS `product_catalogue_item`;
CREATE TABLE `product_catalogue_item` (
  `itemID` varchar(10) NOT NULL,
  `catalogueID` varchar(10) NOT NULL,
  `productID` varchar(10) NOT NULL,
  `specialPrice` decimal(10,2) NOT NULL,
  `notes` text DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `product_catalogue_item`
--

INSERT INTO `product_catalogue_item` (`itemID`, `catalogueID`, `productID`, `specialPrice`, `notes`) VALUES
('CI000001', 'CATL001', 'P00018', 179.99, 'Good'),
('CI000002', 'CATL001', 'P00020', 69.99, 'Very Good'),
('CI000003', 'CATL001', 'P00021', 39.99, 'Fast'),
('CI000004', 'CATL002', 'P00022', 69.99, 'Samsung good'),
('CI000005', 'CATL002', 'P00040', 499.99, 'Samsung nice'),
('CI000006', 'CATL002', 'P00005', 99.99, 'Samsung best'),
('CI000007', 'CATL003', 'P00006', 69.99, 'Only For Student'),
('CI000008', 'CATL003', 'P00013', 159.99, 'Only for student'),
('CI000009', 'CATL003', 'P00019', 99.99, 'Only for student');

--
-- Triggers `product_catalogue_item`
--
DROP TRIGGER IF EXISTS `before_insert_catalogue_item`;
DELIMITER $$
CREATE TRIGGER `before_insert_catalogue_item` BEFORE INSERT ON `product_catalogue_item` FOR EACH ROW BEGIN
    DECLARE next_id INT;
    SET next_id = (SELECT IFNULL(MAX(SUBSTRING(itemID, 3)), 0) + 1 FROM product_catalogue_item);
    SET NEW.itemID = CONCAT('CI', LPAD(next_id, 6, '0'));
END
$$
DELIMITER ;

-- --------------------------------------------------------

--
-- Table structure for table `product_category`
--

DROP TABLE IF EXISTS `product_category`;
CREATE TABLE `product_category` (
  `product_categoryID` varchar(10) NOT NULL,
  `name` varchar(50) NOT NULL,
  `parent_category_id` varchar(10) DEFAULT NULL,
  `description` text DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `product_category`
--

INSERT INTO `product_category` (`product_categoryID`, `name`, `parent_category_id`, `description`) VALUES
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
-- Triggers `product_category`
--
DROP TRIGGER IF EXISTS `before_insert_product_category`;
DELIMITER $$
CREATE TRIGGER `before_insert_product_category` BEFORE INSERT ON `product_category` FOR EACH ROW BEGIN
    DECLARE next_id INT;
    SET next_id = (SELECT IFNULL(MAX(SUBSTRING(categoryID, 4)), 0) + 1 FROM product_category);
    SET NEW.product_categoryID = CONCAT('CAT', LPAD(next_id, 3, '0'));
END
$$
DELIMITER ;

-- --------------------------------------------------------

--
-- Table structure for table `shipping_address`
--

DROP TABLE IF EXISTS `shipping_address`;
CREATE TABLE `shipping_address` (
  `shipping_addressID` varchar(10) NOT NULL,
  `customerID` varchar(10) NOT NULL,
  `street` varchar(100) DEFAULT NULL,
  `city` varchar(50) DEFAULT NULL,
  `state` varchar(50) DEFAULT NULL,
  `zipCode` varchar(20) DEFAULT NULL,
  `country` varchar(50) DEFAULT NULL,
  `isDefault` tinyint(1) DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `shipping_address`
--

INSERT INTO `shipping_address` (`shipping_addressID`, `customerID`, `street`, `city`, `state`, `zipCode`, `country`, `isDefault`) VALUES
('SA000001', 'C0001', '123 Main St', 'Anytown', 'CA', '12345', 'USA', 1),
('SA000002', 'C0002', '456 Oak Ave', 'Sometown', 'NY', '23456', 'USA', 1),
('SA000003', 'C0003', '789 Pine Rd', 'Othertown', 'TX', '34567', 'USA', 1),
('SA000004', 'C0004', '101 Maple Dr', 'Newtown', 'FL', '45678', 'USA', 1),
('SA000005', 'C0005', '202 Birch Ln', 'Oldtown', 'WA', '56789', 'USA', 1),
('SA000006', 'C0006', '303 Cedar St', 'Hometown', 'OR', '67890', 'USA', 1),
('SA000007', 'C0007', '404 Elm Pl', 'Yourtown', 'MA', '78901', 'USA', 1),
('SA000008', 'C0008', '505 Pine Ave', 'Theirtown', 'IL', '89012', 'USA', 1),
('SA000009', 'C0009', '606 Oak St', 'Thattown', 'OH', '90123', 'USA', 1),
('SA000010', 'C0010', '707 Maple Ave', 'Thistown', 'MI', '01234', 'USA', 1),
('SA000011', 'C0011', '809 Elm St', 'Anyplace', 'PA', '12345', 'USA', 1),
('SA000012', 'C0012', '910 Cedar Rd', 'Someplace', 'GA', '23456', 'USA', 1),
('SA000013', 'C0013', '1011 Birch Dr', 'Otherplace', 'AZ', '34567', 'USA', 1),
('SA000014', 'C0014', '1112 Oak Lane', 'Newplace', 'CO', '45678', 'USA', 1),
('SA000015', 'C0015', '1213 Pine St', 'Oldplace', 'NV', '56789', 'USA', 1),
('SA000016', 'C0016', '1314 Maple Blvd', 'Homeplace', 'MN', '67890', 'USA', 1),
('SA000017', 'C0017', '1415 Cedar Ave', 'Yourplace', 'WI', '78901', 'USA', 1),
('SA000018', 'C0018', '1516 Elm Rd', 'Theirplace', 'IN', '89012', 'USA', 1),
('SA000019', 'C0019', '1617 Birch Ave', 'Thatplace', 'MO', '90123', 'USA', 1),
('SA000020', 'C0020', '1718 Cedar Blvd', 'Thistown', 'KY', '01234', 'USA', 1),
('SA000021', 'C0001', '123 Work Building', 'Downtown', 'CA', '12345', 'USA', 0),
('SA000022', 'C0003', '789 Office Complex', 'Business District', 'TX', '34568', 'USA', 0),
('SA000023', 'C0006', '303 Second Home', 'Vacation Spot', 'HI', '67891', 'USA', 0),
('SA000024', 'C0010', '707 Secondary Address', 'Nearby', 'MI', '01235', 'USA', 0),
('SA000025', 'C0014', '1112 Work Place', 'Business Park', 'CO', '45679', 'USA', 0),
('SA000026', 'C0018', '1516 PO Box', 'Mail Center', 'IN', '89013', 'USA', 0),
('SA000027', 'C0028', '123, Main Street', 'Morioh Cho', 'Duwang', '60000', 'Japan', 0);

--
-- Triggers `shipping_address`
--
DROP TRIGGER IF EXISTS `before_insert_address`;
DELIMITER $$
CREATE TRIGGER `before_insert_address` BEFORE INSERT ON `shipping_address` FOR EACH ROW BEGIN
    DECLARE next_id INT;
    SET next_id = (SELECT IFNULL(MAX(CAST(SUBSTRING(shipping_addressID, 3) AS UNSIGNED)), 0) + 1 FROM shipping_address);
    SET NEW.shipping_addressID = CONCAT('SA', LPAD(next_id, 6, '0'));
END
$$
DELIMITER ;

-- --------------------------------------------------------

--
-- Table structure for table `transaction`
--

DROP TABLE IF EXISTS `transaction`;
CREATE TABLE `transaction` (
  `transactionID` varchar(10) NOT NULL,
  `billID` varchar(10) NOT NULL,
  `amount` decimal(10,2) NOT NULL,
  `payment_methodID` varchar(10) NOT NULL,
  `status` varchar(20) NOT NULL,
  `transactionDate` timestamp NOT NULL DEFAULT current_timestamp(),
  `lastModifiedDate` timestamp NOT NULL DEFAULT current_timestamp() ON UPDATE current_timestamp(),
  `description` text DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Triggers `transaction`
--
DROP TRIGGER IF EXISTS `before_insert_transaction`;
DELIMITER $$
CREATE TRIGGER `before_insert_transaction` BEFORE INSERT ON `transaction` FOR EACH ROW BEGIN
    DECLARE next_id INT;
    SET next_id = (SELECT IFNULL(MAX(SUBSTRING(transactionID, 2)), 0) + 1 FROM transaction);
    SET NEW.transactionID = CONCAT('T', LPAD(next_id, 6, '0'));
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
  `status` enum('ACTIVE','INACTIVE','BANNED') DEFAULT 'ACTIVE',
  `phone` varchar(20) DEFAULT NULL,
  `fullName` varchar(255) DEFAULT NULL,
  `role` varchar(50) DEFAULT NULL,
  `loginAttempt` tinyint(1) NOT NULL DEFAULT 0,
  `firstLoginAttemptTimestamp` timestamp NULL DEFAULT NULL ON UPDATE current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `user`
--

INSERT INTO `user` (`userID`, `username`, `email`, `password`, `registrationDate`, `lastLogin`, `status`, `phone`, `fullName`, `role`, `loginAttempt`, `firstLoginAttemptTimestamp`) VALUES
('A0001', 'admin_jones', 'admin.j@pchub.com', '$2a$12$0d4qvcYBw2CoqnntE856AeAbp6USdb4yYVMyWQE4dXMi3wT5rkWV6', '2024-10-01 08:00:00', '2025-03-22 18:00:00', 'ACTIVE', '012-2345678', 'Alex Jones', 'ADMIN', 0, NULL),
('A0002', 'admin_smith', 'admin.s@pchub.com', '$2a$12$0d4qvcYBw2CoqnntE856AeAbp6USdb4yYVMyWQE4dXMi3wT5rkWV6', '2024-10-01 08:15:00', '2025-03-22 17:45:00', 'ACTIVE', '012-2345678', 'Sam Smith', 'ADMIN', 0, NULL),
('A0003', 'admin_johnson', 'admin.j2@pchub.com', '$2a$12$0d4qvcYBw2CoqnntE856AeAbp6USdb4yYVMyWQE4dXMi3wT5rkWV6', '2024-10-01 08:30:00', '2025-03-21 18:30:00', 'ACTIVE', '012-2345678', 'Jordan Johnson', 'ADMIN', 0, NULL),
('A0004', 'admin_brown', 'admin.b@pchub.com', '$2a$12$0d4qvcYBw2CoqnntE856AeAbp6USdb4yYVMyWQE4dXMi3wT5rkWV6', '2024-10-01 08:45:00', '2025-03-22 16:15:00', 'ACTIVE', '012-2345678', 'Blake Brown', 'ADMIN', 0, NULL),
('A0005', 'admin_garcia', 'admin.g@pchub.com', '$2a$12$0d4qvcYBw2CoqnntE856AeAbp6USdb4yYVMyWQE4dXMi3wT5rkWV6', '2024-10-01 09:00:00', '2025-03-20 15:30:00', 'ACTIVE', '012-2345678', 'Gabriel Garcia', 'ADMIN', 0, NULL),
('C0001', 'john_doe', 'john.doe@email.com', '$2a$12$xyEsckfifgm/0Vtmesu/qOrA5I/AuKatJy33u4wZwNlO8E5EboQV2', '2024-10-01 08:30:00', '2025-05-03 17:40:19', 'ACTIVE', '012-2345678', 'John Doe', 'CUSTOMER', 2, '2025-05-03 11:04:23'),
('C0002', 'jane_smith', 'jane.smith@email.com', '$2a$12$0d4qvcYBw2CoqnntE856AeAbp6USdb4yYVMyWQE4dXMi3wT5rkWV6', '2024-10-02 09:45:00', '2025-03-21 16:10:00', 'ACTIVE', '012-2345678', 'Jane Smith', 'CUSTOMER', 0, NULL),
('C0003', 'mike_johnson', 'mike.j@email.com', '$2a$12$0d4qvcYBw2CoqnntE856AeAbp6USdb4yYVMyWQE4dXMi3wT5rkWV6', '2024-10-03 10:15:00', '2025-03-22 09:05:00', 'ACTIVE', '012-2345678', 'Michael Johnson', 'CUSTOMER', 0, NULL),
('C0004', 'sarah_williams', 'sarah.w@email.com', '$2a$12$0d4qvcYBw2CoqnntE856AeAbp6USdb4yYVMyWQE4dXMi3wT5rkWV6', '2024-10-04 11:30:00', '2025-03-20 11:40:00', 'ACTIVE', '012-2345678', 'Sarah Williams', 'CUSTOMER', 0, NULL),
('C0005', 'david_brown', 'david.b@email.com', '$2a$12$0d4qvcYBw2CoqnntE856AeAbp6USdb4yYVMyWQE4dXMi3wT5rkWV6', '2024-10-05 12:45:00', '2025-03-19 15:35:00', 'ACTIVE', '012-2345678', 'David Brown', 'CUSTOMER', 0, NULL),
('C0006', 'lisa_taylor', 'lisa.t@email.com', '$2a$12$0d4qvcYBw2CoqnntE856AeAbp6USdb4yYVMyWQE4dXMi3wT5rkWV6', '2024-10-06 13:15:00', '2025-03-22 17:15:00', 'ACTIVE', '012-2345678', 'Lisa Taylor', 'CUSTOMER', 0, NULL),
('C0007', 'kevin_davis', 'kevin.d@email.com', '$2a$12$0d4qvcYBw2CoqnntE856AeAbp6USdb4yYVMyWQE4dXMi3wT5rkWV6', '2024-10-07 14:30:00', '2025-03-21 10:25:00', 'ACTIVE', '012-2345678', 'Kevin Davis', 'CUSTOMER', 0, NULL),
('C0008', 'amy_miller', 'amy.m@email.com', '$2a$12$0d4qvcYBw2CoqnntE856AeAbp6USdb4yYVMyWQE4dXMi3wT5rkWV6', '2024-10-08 15:45:00', '2025-03-20 14:50:00', 'ACTIVE', '012-2345678', 'Amy Miller', 'CUSTOMER', 0, NULL),
('C0009', 'brian_wilson', 'brian.w@email.com', '$2a$12$0d4qvcYBw2CoqnntE856AeAbp6USdb4yYVMyWQE4dXMi3wT5rkWV6', '2024-10-09 16:15:00', '2025-03-18 12:30:00', 'ACTIVE', '012-2345678', 'Brian Wilson', 'CUSTOMER', 0, NULL),
('C0010', 'jessica_moore', 'jessica.m@email.com', '$2a$12$0d4qvcYBw2CoqnntE856AeAbp6USdb4yYVMyWQE4dXMi3wT5rkWV6', '2024-10-10 17:30:00', '2025-03-22 08:45:00', 'ACTIVE', '012-2345678', 'Jessica Moore', 'CUSTOMER', 0, NULL),
('C0011', 'chris_taylor', 'chris.t@email.com', '$2a$12$0d4qvcYBw2CoqnntE856AeAbp6USdb4yYVMyWQE4dXMi3wT5rkWV6', '2024-10-11 09:00:00', '2025-03-21 15:20:00', 'ACTIVE', '012-2345678', 'Christopher Taylor', 'CUSTOMER', 0, NULL),
('C0012', 'rachel_anderson', 'rachel.a@email.com', '$2a$12$0d4qvcYBw2CoqnntE856AeAbp6USdb4yYVMyWQE4dXMi3wT5rkWV6', '2024-10-12 10:30:00', '2025-03-19 13:10:00', 'ACTIVE', '012-2345678', 'Rachel Anderson', 'CUSTOMER', 0, NULL),
('C0013', 'mark_thomas', 'mark.t@email.com', '$2a$12$0d4qvcYBw2CoqnntE856AeAbp6USdb4yYVMyWQE4dXMi3wT5rkWV6', '2024-10-13 11:45:00', '2025-03-22 11:50:00', 'ACTIVE', '012-2345678', 'Mark Thomas', 'CUSTOMER', 0, NULL),
('C0014', 'emily_jackson', 'emily.j@email.com', '$2a$12$0d4qvcYBw2CoqnntE856AeAbp6USdb4yYVMyWQE4dXMi3wT5rkWV6', '2024-10-14 12:15:00', '2025-03-20 17:35:00', 'ACTIVE', '012-2345678', 'Emily Jackson', 'CUSTOMER', 0, NULL),
('C0015', 'steven_white', 'steven.w@email.com', '$2a$12$0d4qvcYBw2CoqnntE856AeAbp6USdb4yYVMyWQE4dXMi3wT5rkWV6', '2024-10-15 13:30:00', '2025-03-17 09:40:00', 'ACTIVE', '012-2345678', 'Steven White', 'CUSTOMER', 0, NULL),
('C0016', 'laura_harris', 'laura.h@email.com', '$2a$12$0d4qvcYBw2CoqnntE856AeAbp6USdb4yYVMyWQE4dXMi3wT5rkWV6', '2024-10-16 14:45:00', '2025-03-22 16:25:00', 'ACTIVE', '012-2345678', 'Laura Harris', 'CUSTOMER', 0, NULL),
('C0017', 'daniel_martin', 'daniel.m@email.com', '$2a$12$0d4qvcYBw2CoqnntE856AeAbp6USdb4yYVMyWQE4dXMi3wT5rkWV6', '2024-10-17 15:15:00', '2025-03-21 12:55:00', 'ACTIVE', '012-2345678', 'Daniel Martin', 'CUSTOMER', 0, NULL),
('C0018', 'michelle_garcia', 'michelle.g@email.com', '$2a$12$0d4qvcYBw2CoqnntE856AeAbp6USdb4yYVMyWQE4dXMi3wT5rkWV6', '2024-10-18 16:30:00', '2025-03-18 14:20:00', 'ACTIVE', '012-2345678', 'Michelle Garcia', 'CUSTOMER', 0, NULL),
('C0019', 'robert_lee', 'robert.l@email.com', '$2a$12$0d4qvcYBw2CoqnntE856AeAbp6USdb4yYVMyWQE4dXMi3wT5rkWV6', '2024-10-19 17:45:00', '2025-03-16 10:15:00', 'ACTIVE', '012-2345678', 'Robert Lee', 'CUSTOMER', 0, NULL),
('C0020', 'jennifer_king', 'jennifer.k@email.com', '$2a$12$0d4qvcYBw2CoqnntE856AeAbp6USdb4yYVMyWQE4dXMi3wT5rkWV6', '2024-10-20 09:15:00', '2025-03-22 13:05:00', 'ACTIVE', '012-2345678', 'Jennifer King', 'CUSTOMER', 0, NULL),
('C0021', 'ryan_scott', 'ryan.s@email.com', '$2a$12$0d4qvcYBw2CoqnntE856AeAbp6USdb4yYVMyWQE4dXMi3wT5rkWV6', '2024-10-21 10:30:00', '2025-03-20 09:50:00', 'ACTIVE', '012-2345678', 'Ryan Scott', 'CUSTOMER', 0, NULL),
('C0022', 'nicole_adams', 'nicole.a@email.com', '$2a$12$0d4qvcYBw2CoqnntE856AeAbp6USdb4yYVMyWQE4dXMi3wT5rkWV6', '2024-10-22 11:45:00', '2025-03-19 15:30:00', 'ACTIVE', '012-2345678', 'Nicole Adams', 'CUSTOMER', 0, NULL),
('C0023', 'tyler_baker', 'tyler.b@email.com', '$2a$12$0d4qvcYBw2CoqnntE856AeAbp6USdb4yYVMyWQE4dXMi3wT5rkWV6', '2024-10-23 12:15:00', '2025-03-21 17:15:00', 'ACTIVE', '012-2345678', 'Tyler Baker', 'CUSTOMER', 0, NULL),
('C0024', 'olivia_gonzalez', 'olivia.g@email.com', '$2a$12$0d4qvcYBw2CoqnntE856AeAbp6USdb4yYVMyWQE4dXMi3wT5rkWV6', '2024-10-24 13:30:00', '2025-03-22 10:40:00', 'ACTIVE', '012-2345678', 'Olivia Gonzalez', 'CUSTOMER', 0, NULL),
('C0025', 'eric_nelson', 'eric.n@email.com', '$2a$12$0d4qvcYBw2CoqnntE856AeAbp6USdb4yYVMyWQE4dXMi3wT5rkWV6', '2024-10-25 14:45:00', '2025-03-18 13:25:00', 'ACTIVE', '012-2345678', 'Eric Nelson', 'CUSTOMER', 0, NULL),
('C0026', 'Oni', 'lee@gmail.com', '$2a$12$0d4qvcYBw2CoqnntE856AeAbp6USdb4yYVMyWQE4dXMi3wT5rkWV6', '2025-05-01 00:00:00', '2025-05-01 02:32:27', 'ACTIVE', '011-13109924', 'Lee', 'ADMIN', 0, NULL),
('C0027', 'Lee', 'handsome@gmail.com', '$2a$12$0d4qvcYBw2CoqnntE856AeAbp6USdb4yYVMyWQE4dXMi3wT5rkWV6', '2025-05-01 00:00:00', '2025-05-01 01:56:46', 'ACTIVE', '011-13109925', 'Handsome', 'CUSTOMER', 0, NULL),
('C0028', 'john', 'xobod24228@bocapies.com', '$2a$12$SI21K0O4THl38jMHkC9Vi./.2dKM5pehxEpUnCYymMh.U3UmdRn5O', '2025-05-01 00:00:00', '2025-05-01 16:24:00', 'ACTIVE', '012-3345687', 'John China', 'CUSTOMER', 2, '2025-05-03 06:18:10');

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
-- Indexes for dumped tables
--

--
-- Indexes for table `bill`
--
ALTER TABLE `bill`
  ADD PRIMARY KEY (`billID`),
  ADD KEY `orderID` (`orderID`),
  ADD KEY `payment_MethodID` (`payment_MethodID`);

--
-- Indexes for table `cart`
--
ALTER TABLE `cart`
  ADD PRIMARY KEY (`cartID`),
  ADD KEY `customerID` (`customerID`);

--
-- Indexes for table `cart_item`
--
ALTER TABLE `cart_item`
  ADD PRIMARY KEY (`cartItemID`),
  ADD KEY `cartID` (`cartID`),
  ADD KEY `productID` (`productID`);

--
-- Indexes for table `order`
--
ALTER TABLE `order`
  ADD PRIMARY KEY (`orderID`),
  ADD KEY `customerID` (`customerID`),
  ADD KEY `payment_MethodID` (`payment_MethodID`);

--
-- Indexes for table `order_item`
--
ALTER TABLE `order_item`
  ADD PRIMARY KEY (`orderItemID`),
  ADD KEY `orderID` (`orderID`),
  ADD KEY `productID` (`productID`);

--
-- Indexes for table `payment_method`
--
ALTER TABLE `payment_method`
  ADD PRIMARY KEY (`payment_methodID`);

--
-- Indexes for table `product`
--
ALTER TABLE `product`
  ADD PRIMARY KEY (`productID`),
  ADD KEY `product_categoryID` (`product_categoryID`);

--
-- Indexes for table `product_catalogue`
--
ALTER TABLE `product_catalogue`
  ADD PRIMARY KEY (`catalogueID`);

--
-- Indexes for table `product_catalogue_item`
--
ALTER TABLE `product_catalogue_item`
  ADD PRIMARY KEY (`itemID`),
  ADD KEY `catalogueID` (`catalogueID`),
  ADD KEY `productID` (`productID`);

--
-- Indexes for table `product_category`
--
ALTER TABLE `product_category`
  ADD PRIMARY KEY (`product_categoryID`),
  ADD KEY `parentCategory` (`parent_category_id`);

--
-- Indexes for table `shipping_address`
--
ALTER TABLE `shipping_address`
  ADD PRIMARY KEY (`shipping_addressID`),
  ADD KEY `customerID` (`customerID`);

--
-- Indexes for table `transaction`
--
ALTER TABLE `transaction`
  ADD PRIMARY KEY (`transactionID`),
  ADD KEY `billID` (`billID`),
  ADD KEY `payment_methodID` (`payment_methodID`);

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
-- Constraints for table `bill`
--
ALTER TABLE `bill`
  ADD CONSTRAINT `payment_ibfk_1` FOREIGN KEY (`orderID`) REFERENCES `order` (`orderID`),
  ADD CONSTRAINT `payment_ibfk_2` FOREIGN KEY (`payment_MethodID`) REFERENCES `payment_method` (`payment_methodID`);

--
-- Constraints for table `cart`
--
ALTER TABLE `cart`
  ADD CONSTRAINT `cart_ibfk_1` FOREIGN KEY (`customerID`) REFERENCES `user` (`userID`);

--
-- Constraints for table `cart_item`
--
ALTER TABLE `cart_item`
  ADD CONSTRAINT `cart_item_ibfk_1` FOREIGN KEY (`cartID`) REFERENCES `cart` (`cartID`),
  ADD CONSTRAINT `cart_item_ibfk_2` FOREIGN KEY (`productID`) REFERENCES `product` (`productID`);

--
-- Constraints for table `order`
--
ALTER TABLE `order`
  ADD CONSTRAINT `order_ibfk_1` FOREIGN KEY (`customerID`) REFERENCES `user` (`userID`),
  ADD CONSTRAINT `order_ibfk_2` FOREIGN KEY (`payment_MethodID`) REFERENCES `payment_method` (`payment_methodID`);

--
-- Constraints for table `order_item`
--
ALTER TABLE `order_item`
  ADD CONSTRAINT `order_item_ibfk_1` FOREIGN KEY (`orderID`) REFERENCES `order` (`orderID`),
  ADD CONSTRAINT `order_item_ibfk_2` FOREIGN KEY (`productID`) REFERENCES `product` (`productID`);

--
-- Constraints for table `product`
--
ALTER TABLE `product`
  ADD CONSTRAINT `product_ibfk_1` FOREIGN KEY (`product_categoryID`) REFERENCES `product_category` (`product_categoryID`);

--
-- Constraints for table `product_category`
--
ALTER TABLE `product_category`
  ADD CONSTRAINT `category_ibfk_1` FOREIGN KEY (`parent_category_id`) REFERENCES `product_category` (`product_categoryID`);

--
-- Constraints for table `shipping_address`
--
ALTER TABLE `shipping_address`
  ADD CONSTRAINT `customer_ibfk_1` FOREIGN KEY (`customerID`) REFERENCES `user` (`userID`);

--
-- Constraints for table `transaction`
--
ALTER TABLE `transaction`
  ADD CONSTRAINT `transaction_ibfk_1` FOREIGN KEY (`billID`) REFERENCES `bill` (`billID`),
  ADD CONSTRAINT `transaction_ibfk_2` FOREIGN KEY (`payment_methodID`) REFERENCES `payment_method` (`payment_methodID`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
