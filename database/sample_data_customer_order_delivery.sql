-- =========================================================================
-- LankaTex Smart Textile & Garment Management System
-- Module: 4.3 Customer Order & Delivery Management - Kalpa
-- Script: Sample Data Insertion (DML)
-- Database: LankaTexDB (Microsoft SQL Server)
-- =========================================================================

-- Ensure required settings for tables with filtered unique indexes
SET QUOTED_IDENTIFIER ON;
SET ANSI_NULLS ON;
GO

BEGIN TRANSACTION;

-- =========================================================================
-- 1. Insert CUSTOMERS (4 Customers: 2 Local, 2 Export)
-- =========================================================================
SET IDENTITY_INSERT customers ON;

INSERT INTO customers (customer_id, customer_name, customer_type, contact_person, phone, email, billing_address, delivery_address, registration_date, status)
VALUES 
(1, 'ODEL PLC', 'LOCAL', 'Mr. Thilina Gunaratne', '0112345678', 'procurement@odel.lk', '475/32 Kandy Road, Peliyagoda', 'ODEL Central Logistics, Peliyagoda', '2026-01-15', 'ACTIVE'),
(2, 'Hameedia Stores (Pvt) Ltd', 'LOCAL', 'Mr. Fouzul Hameed', '0112876543', 'orders@hameedia.lk', '9 Dharmapala Mawatha, Colombo 03', 'Hameedia Distribution Center, Ratmalana', '2026-02-10', 'ACTIVE'),
(3, 'Marks and Spencer plc', 'EXPORT', 'Ms. Sarah Jenkins', '+442079354422', 'sourcing@marksandspencer.co.uk', 'Waterside House, 35 North Wharf Rd, London W2 1NW, UK', 'Colombo Port Bonded Warehouse, Gate 04', '2026-01-20', 'ACTIVE'),
(4, 'Zara Fashion Global S.A.', 'EXPORT', 'Mr. Alejandro Morales', '+34981185400', 'supplychain@inditex.es', 'Edificio Inditex, Av. de la Diputacion, Arteixo, Spain', 'Air Cargo Village, Bandaranaike Int Airport, Katunayake', '2026-03-01', 'ACTIVE');

SET IDENTITY_INSERT customers OFF;
GO

-- =========================================================================
-- 2. Insert LOCAL_CUSTOMERS (ISA Specialization)
-- =========================================================================
INSERT INTO local_customers (customer_id)
VALUES 
(1),
(2);
GO

-- =========================================================================
-- 3. Insert EXPORT_CUSTOMERS (ISA Specialization with Country & Shipping)
-- =========================================================================
INSERT INTO export_customers (customer_id, country, export_registration_details, shipping_preferences)
VALUES 
(3, 'United Kingdom', 'EXP-UK-2026-0045', 'FOB Colombo - Ocean Freight (40ft High Cube Container)'),
(4, 'Spain', 'EXP-EU-2026-0112', 'CIF Valencia - Air Cargo Priority Service');
GO

-- =========================================================================
-- 4. Insert QUOTATIONS
-- =========================================================================
SET IDENTITY_INSERT quotations ON;

INSERT INTO quotations (quotation_id, customer_id, quotation_date, valid_until, requested_delivery_date, status, approved_by)
VALUES 
(1, 1, '2026-09-10', '2026-10-10', '2026-10-25', 'APPROVED', 1),
(2, 3, '2026-09-15', '2026-10-15', '2026-11-05', 'APPROVED', 1),
(3, 4, '2026-09-20', '2026-10-20', '2026-11-15', 'PENDING', NULL);

SET IDENTITY_INSERT quotations OFF;
GO

-- =========================================================================
-- 5. Insert QUOTATION_ITEMS (Line items per quotation)
-- =========================================================================
SET IDENTITY_INSERT quotation_items ON;

INSERT INTO quotation_items (quotation_item_id, quotation_id, garment_type, specifications, colour, size, quantity, unit_price)
VALUES 
(1, 1, 'Men Crew Neck T-Shirt', '100% Combed Cotton 180 GSM Single Jersey', 'Navy Blue', 'M', 500.00, 1200.00),
(2, 1, 'Men Crew Neck T-Shirt', '100% Combed Cotton 180 GSM Single Jersey', 'Navy Blue', 'L', 500.00, 1250.00),
(3, 2, 'Women Pique Polo Shirt', '95% Cotton 5% Spandex Pique Mesh 220 GSM', 'Crimson Red', 'S', 1200.00, 1850.00),
(4, 2, 'Women Pique Polo Shirt', '95% Cotton 5% Spandex Pique Mesh 220 GSM', 'Crimson Red', 'M', 1800.00, 1850.00),
(5, 3, 'Classic Chino Shorts', 'Twill Woven Enzyme Washed with YKK Zippers', 'Khaki', '32', 800.00, 2200.00);

SET IDENTITY_INSERT quotation_items OFF;
GO

-- =========================================================================
-- 6. Insert CUSTOMER_ORDERS
-- (Order 1 from Quotation 1, Order 2 from Quotation 2, Order 3 direct order with NULL quotation_id)
-- =========================================================================
SET IDENTITY_INSERT customer_orders ON;

INSERT INTO customer_orders (order_id, customer_id, quotation_id, order_date, required_delivery_date, priority, status, approved_by)
VALUES 
(1, 1, 1, '2026-09-12', '2026-10-25', 'NORMAL', 'READY', 1),
(2, 3, 2, '2026-09-18', '2026-11-05', 'HIGH', 'IN_PRODUCTION', 1),
(3, 2, NULL, '2026-09-22', '2026-10-30', 'URGENT', 'READY', 1);

SET IDENTITY_INSERT customer_orders OFF;
GO

-- =========================================================================
-- 7. Insert CUSTOMER_ORDER_ITEMS (Line items per customer order)
-- =========================================================================
SET IDENTITY_INSERT customer_order_items ON;

INSERT INTO customer_order_items (customer_order_item_id, customer_order_id, garment_type, colour, size, quantity, unit_price)
VALUES 
(1, 1, 'Men Crew Neck T-Shirt', 'Navy Blue', 'M', 500.00, 1200.00),
(2, 1, 'Men Crew Neck T-Shirt', 'Navy Blue', 'L', 500.00, 1250.00),
(3, 2, 'Women Pique Polo Shirt', 'Crimson Red', 'S', 1200.00, 1850.00),
(4, 2, 'Women Pique Polo Shirt', 'Crimson Red', 'M', 1800.00, 1850.00),
(5, 3, 'Formal Slim Fit Shirt', 'Pure White', '40', 300.00, 2800.00);

SET IDENTITY_INSERT customer_order_items OFF;
GO

-- =========================================================================
-- 8. Insert DELIVERIES
-- (Deliveries 1 & 2 represent partial split dispatches for Order 1; Delivery 3 is for Order 3)
-- =========================================================================
SET IDENTITY_INSERT deliveries ON;

INSERT INTO deliveries (delivery_id, order_id, delivery_date, delivery_address, receiver_name, status, confirmation, remarks)
VALUES 
(1, 1, '2026-10-01', 'ODEL Central Logistics, Peliyagoda', 'Mr. Kamal Perera', 'CONFIRMED', 'CONFIRMED', 'Partial dispatch - Batch 1 of Order 1'),
(2, 1, '2026-10-15', 'ODEL Central Logistics, Peliyagoda', 'Mr. Kamal Perera', 'CONFIRMED', 'CONFIRMED', 'Final balance delivery for Order 1'),
(3, 3, '2026-10-05', 'Hameedia Distribution Center, Ratmalana', 'Mr. Nimal Silva', 'CONFIRMED', 'CONFIRMED', 'Full delivery for Direct Order 3');

SET IDENTITY_INSERT deliveries OFF;
GO

-- =========================================================================
-- 9. Insert DELIVERY_ITEMS
-- (Tracks exact quantity delivered per order line item)
-- =========================================================================
SET IDENTITY_INSERT delivery_items ON;

INSERT INTO delivery_items (delivery_item_id, delivery_id, customer_order_item_id, quantity_delivered)
VALUES 
(1, 1, 1, 500.00),  -- Delivery 1 shipped all 500 units of Medium
(2, 1, 2, 200.00),  -- Delivery 1 shipped 200 of 500 units of Large
(3, 2, 2, 300.00),  -- Delivery 2 shipped the remaining 300 units of Large
(4, 3, 5, 300.00);  -- Delivery 3 shipped all 300 units of Formal Shirts

SET IDENTITY_INSERT delivery_items OFF;
GO

COMMIT TRANSACTION;
GO

-- =========================================================================
-- Verification Queries (Optional check)
-- =========================================================================
SELECT 'customers' AS [Table], COUNT(*) AS [RowCount] FROM customers
UNION ALL
SELECT 'local_customers', COUNT(*) FROM local_customers
UNION ALL
SELECT 'export_customers', COUNT(*) FROM export_customers
UNION ALL
SELECT 'quotations', COUNT(*) FROM quotations
UNION ALL
SELECT 'quotation_items', COUNT(*) FROM quotation_items
UNION ALL
SELECT 'customer_orders', COUNT(*) FROM customer_orders
UNION ALL
SELECT 'customer_order_items', COUNT(*) FROM customer_order_items
UNION ALL
SELECT 'deliveries', COUNT(*) FROM deliveries
UNION ALL
SELECT 'delivery_items', COUNT(*) FROM delivery_items;
GO
