-- =========================================================================
-- LankaTex Smart Textile & Garment Management System
-- Module: 4.3 Customer Order & Delivery Management - Kalpa
-- Deliverable: 4️⃣ 5 REQUIRED SQL QUERIES
-- Database: LankaTexDB (Microsoft SQL Server / SSMS)
-- =========================================================================

USE LankaTexDB;
GO

SET QUOTED_IDENTIFIER ON;
SET ANSI_NULLS ON;
GO

-- =========================================================================
-- 1. SELECT QUERY
-- Purpose: Retrieve all active local customers with their contact details
--          and billing/delivery addresses, sorted alphabetically by company name.
-- =========================================================================
SELECT 
    c.customer_id,
    c.customer_name,
    c.customer_type,
    c.contact_person,
    c.phone,
    c.email,
    c.delivery_address,
    c.registration_date,
    c.status
FROM customers c
WHERE c.status = 'ACTIVE' 
  AND c.customer_type = 'LOCAL'
ORDER BY c.customer_name ASC;
GO


-- =========================================================================
-- 2. JOIN QUERY (Multi-Table JOIN)
-- Purpose: Join Customer Orders, Customers, and Customer Order Items to generate 
--          a comprehensive Order Dispatch Specification report with computed line totals.
-- =========================================================================
SELECT 
    o.order_id,
    c.customer_name,
    c.customer_type,
    o.order_date,
    o.required_delivery_date,
    o.priority,
    coi.garment_type,
    coi.colour,
    coi.size,
    coi.quantity,
    coi.unit_price,
    (coi.quantity * coi.unit_price) AS line_total_lkr
FROM customer_orders o
INNER JOIN customers c 
    ON o.customer_id = c.customer_id
INNER JOIN customer_order_items coi 
    ON o.order_id = coi.customer_order_id
ORDER BY o.order_id ASC, coi.customer_order_item_id ASC;
GO


-- =========================================================================
-- 3. AGGREGATION QUERY (SUM, COUNT, AVG, MIN, MAX)
-- Purpose: Calculate overall sales order metrics across the entire system, 
--          including total orders placed, total line items, total garment volume, 
--          gross order value, average garment unit price, and price boundaries.
-- =========================================================================
SELECT 
    COUNT(DISTINCT o.order_id) AS total_orders,
    COUNT(coi.customer_order_item_id) AS total_line_items,
    SUM(coi.quantity) AS total_garments_ordered,
    SUM(coi.quantity * coi.unit_price) AS total_order_value_lkr,
    AVG(coi.unit_price) AS average_unit_price_lkr,
    MIN(coi.unit_price) AS min_garment_price_lkr,
    MAX(coi.unit_price) AS max_garment_price_lkr
FROM customer_orders o
INNER JOIN customer_order_items coi 
    ON o.order_id = coi.customer_order_id;
GO


-- =========================================================================
-- 4. GROUP BY / HAVING QUERY
-- Purpose: Group order quantities and revenue by customer and customer type,
--          filtering using HAVING to identify premium/high-value customers 
--          whose total placed order value exceeds 1,000,000 LKR.
-- =========================================================================
SELECT 
    c.customer_id,
    c.customer_name,
    c.customer_type,
    COUNT(DISTINCT o.order_id) AS orders_placed_count,
    SUM(coi.quantity) AS total_units_ordered,
    SUM(coi.quantity * coi.unit_price) AS total_spend_lkr,
    AVG(coi.unit_price) AS avg_unit_price_lkr
FROM customers c
INNER JOIN customer_orders o 
    ON c.customer_id = o.customer_id
INNER JOIN customer_order_items coi 
    ON o.order_id = coi.customer_order_id
GROUP BY c.customer_id, c.customer_name, c.customer_type
HAVING SUM(coi.quantity * coi.unit_price) >= 1000000.00
ORDER BY total_spend_lkr DESC;
GO


-- =========================================================================
-- 5. SUBQUERY (Nested & Benchmark Comparison)
-- Purpose: Identify high-tier garment order items priced ABOVE the overall 
--          company-wide average unit price, comparing each item's price with 
--          a dynamic subquery calculation and displaying the difference.
-- =========================================================================
SELECT 
    coi.customer_order_item_id,
    o.order_id,
    c.customer_name,
    coi.garment_type,
    coi.colour,
    coi.size,
    coi.unit_price AS item_price_lkr,
    (SELECT ROUND(AVG(unit_price), 2) FROM customer_order_items) AS company_avg_price_lkr,
    (coi.unit_price - (SELECT ROUND(AVG(unit_price), 2) FROM customer_order_items)) AS price_above_avg_lkr
FROM customer_order_items coi
INNER JOIN customer_orders o 
    ON coi.customer_order_id = o.order_id
INNER JOIN customers c 
    ON o.customer_id = c.customer_id
WHERE coi.unit_price > (
    SELECT AVG(unit_price) 
    FROM customer_order_items
)
ORDER BY coi.unit_price DESC;
GO
