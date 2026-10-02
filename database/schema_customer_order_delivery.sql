-- =========================================================================
-- LankaTex Smart Textile & Garment Management System
-- Module: 4.3 Customer Order & Delivery Management - Kalpa
-- Database: LankaTexDB (Microsoft SQL Server)
-- =========================================================================

-- 1. CUSTOMER
-- Schema: CUSTOMER(CustomerID PK, CustomerName, ContactPerson, PhoneNumber,
--                  EmailAddress, BillingAddress, DeliveryAddress, CustomerType, RegistrationDate)
IF NOT EXISTS (SELECT * FROM sys.tables WHERE name = 'customers')
BEGIN
    CREATE TABLE customers (
        customer_id BIGINT IDENTITY(1,1) PRIMARY KEY,
        customer_name NVARCHAR(100) NOT NULL,
        customer_type NVARCHAR(50) NOT NULL,
        contact_person NVARCHAR(100),
        phone NVARCHAR(50),
        email NVARCHAR(100),
        billing_address NVARCHAR(1000),
        delivery_address NVARCHAR(1000),
        registration_date DATE,
        status NVARCHAR(50) DEFAULT 'ACTIVE'
    );
END
GO

-- 2. LOCAL_CUSTOMER
-- Schema: LOCAL_CUSTOMER(CustomerID PK/FK -> CUSTOMER.CustomerID)
-- Implements CUSTOMER ISA specialization (ON DELETE CASCADE)
IF NOT EXISTS (SELECT * FROM sys.tables WHERE name = 'local_customers')
BEGIN
    CREATE TABLE local_customers (
        customer_id BIGINT PRIMARY KEY,
        CONSTRAINT fk_local_customers_customer FOREIGN KEY (customer_id) REFERENCES customers(customer_id) ON DELETE CASCADE ON UPDATE CASCADE
    );
END
GO

-- 3. EXPORT_CUSTOMER
-- Schema: EXPORT_CUSTOMER(CustomerID PK/FK -> CUSTOMER.CustomerID, Country,
--                         ExportRegistrationDetails, ShippingPreferences)
-- Implements CUSTOMER ISA specialization (ON DELETE CASCADE ON UPDATE CASCADE)
IF NOT EXISTS (SELECT * FROM sys.tables WHERE name = 'export_customers')
BEGIN
    CREATE TABLE export_customers (
        customer_id BIGINT PRIMARY KEY,
        country NVARCHAR(100),
        export_registration_details NVARCHAR(1000),
        shipping_preferences NVARCHAR(1000),
        CONSTRAINT fk_export_customers_customer FOREIGN KEY (customer_id) REFERENCES customers(customer_id) ON DELETE CASCADE ON UPDATE CASCADE
    );
END
GO

-- 4. QUOTATION
-- Schema: QUOTATION(QuotationID PK, CustomerID FK -> CUSTOMER.CustomerID,
--                   QuotationDate, DeliveryPeriod, ValidityPeriod, Status)
IF NOT EXISTS (SELECT * FROM sys.tables WHERE name = 'quotations')
BEGIN
    CREATE TABLE quotations (
        quotation_id BIGINT IDENTITY(1,1) PRIMARY KEY,
        customer_id BIGINT NOT NULL,
        quotation_date DATE NOT NULL,
        valid_until DATE NOT NULL,
        requested_delivery_date DATE,
        status NVARCHAR(50) DEFAULT 'PENDING',
        approved_by BIGINT,
        CONSTRAINT fk_quotations_customer FOREIGN KEY (customer_id) REFERENCES customers(customer_id) ON DELETE NO ACTION
    );
END
GO

-- 5. QUOTATION_ITEM
-- Schema: QUOTATION_ITEM(QuotationItemID PK, QuotationID FK -> QUOTATION.QuotationID,
--                        GarmentType, Specifications, Colour, Size, Quantity, UnitPrice)
-- Line item relation (ON DELETE CASCADE ON UPDATE CASCADE: deleting quotation deletes its items)
IF NOT EXISTS (SELECT * FROM sys.tables WHERE name = 'quotation_items')
BEGIN
    CREATE TABLE quotation_items (
        quotation_item_id BIGINT IDENTITY(1,1) PRIMARY KEY,
        quotation_id BIGINT NOT NULL,
        garment_type NVARCHAR(100) NOT NULL,
        specifications NVARCHAR(1000),
        colour NVARCHAR(50),
        size NVARCHAR(50),
        quantity DECIMAL(18,2) NOT NULL,
        unit_price DECIMAL(18,2) NOT NULL,
        CONSTRAINT fk_quotation_items_quotation FOREIGN KEY (quotation_id) REFERENCES quotations(quotation_id) ON DELETE CASCADE ON UPDATE CASCADE
    );
END
GO

-- 6. CUSTOMER_ORDER
-- Schema: CUSTOMER_ORDER(CustomerOrderID PK, CustomerID FK -> CUSTOMER.CustomerID,
--                        QuotationID FK NULL UQ -> QUOTATION.QuotationID, OrderDate, Status)
IF NOT EXISTS (SELECT * FROM sys.tables WHERE name = 'customer_orders')
BEGIN
    CREATE TABLE customer_orders (
        order_id BIGINT IDENTITY(1,1) PRIMARY KEY,
        customer_id BIGINT NOT NULL,
        quotation_id BIGINT,
        order_date DATE NOT NULL,
        required_delivery_date DATE NOT NULL,
        priority NVARCHAR(50) NOT NULL,
        status NVARCHAR(50) DEFAULT 'PENDING',
        approved_by BIGINT,
        CONSTRAINT fk_customer_orders_customer FOREIGN KEY (customer_id) REFERENCES customers(customer_id) ON DELETE NO ACTION,
        CONSTRAINT fk_customer_orders_quotation FOREIGN KEY (quotation_id) REFERENCES quotations(quotation_id) ON DELETE NO ACTION
    );

    -- Enforce QuotationID NULL UQ (filtered unique index allows multiple NULLs for direct orders)
    CREATE UNIQUE NONCLUSTERED INDEX uq_customer_orders_quotation 
    ON customer_orders(quotation_id) 
    WHERE quotation_id IS NOT NULL;
END
GO

-- 7. CUSTOMER_ORDER_ITEM
-- Schema: CUSTOMER_ORDER_ITEM(CustomerOrderItemID PK, CustomerOrderID FK -> CUSTOMER_ORDER.CustomerOrderID,
--                             GarmentType, Colour, Size, Quantity, UnitPrice)
-- Line item relation (ON DELETE CASCADE ON UPDATE CASCADE: deleting order deletes its items)
IF NOT EXISTS (SELECT * FROM sys.tables WHERE name = 'customer_order_items')
BEGIN
    CREATE TABLE customer_order_items (
        customer_order_item_id BIGINT IDENTITY(1,1) PRIMARY KEY,
        customer_order_id BIGINT NOT NULL,
        garment_type NVARCHAR(100) NOT NULL,
        colour NVARCHAR(50),
        size NVARCHAR(50),
        quantity DECIMAL(18,2) NOT NULL,
        unit_price DECIMAL(18,2) NOT NULL,
        CONSTRAINT fk_order_items_order FOREIGN KEY (customer_order_id) REFERENCES customer_orders(order_id) ON DELETE CASCADE ON UPDATE CASCADE
    );
END
GO

-- 8. DELIVERY
-- Schema: DELIVERY(DeliveryID PK, CustomerOrderID FK -> CUSTOMER_ORDER.CustomerOrderID,
--                  DeliveryDate, Status, Confirmation)
IF NOT EXISTS (SELECT * FROM sys.tables WHERE name = 'deliveries')
BEGIN
    CREATE TABLE deliveries (
        delivery_id BIGINT IDENTITY(1,1) PRIMARY KEY,
        order_id BIGINT NOT NULL,
        delivery_date DATE NOT NULL,
        delivery_address NVARCHAR(1000) NOT NULL,
        receiver_name NVARCHAR(100),
        status NVARCHAR(50) DEFAULT 'CONFIRMED',
        confirmation NVARCHAR(50) DEFAULT 'CONFIRMED',
        remarks NVARCHAR(1000),
        CONSTRAINT fk_deliveries_order FOREIGN KEY (order_id) REFERENCES customer_orders(order_id) ON DELETE NO ACTION
    );
END
GO

-- 9. DELIVERY_ITEM
-- Schema: DELIVERY_ITEM(DeliveryItemID PK, DeliveryID FK -> DELIVERY.DeliveryID,
--                       CustomerOrderItemID FK -> CUSTOMER_ORDER_ITEM.CustomerOrderItemID, QuantityDelivered)
-- Links each delivery line to original customer order item (ON DELETE CASCADE ON UPDATE CASCADE from deliveries)
IF NOT EXISTS (SELECT * FROM sys.tables WHERE name = 'delivery_items')
BEGIN
    CREATE TABLE delivery_items (
        delivery_item_id BIGINT IDENTITY(1,1) PRIMARY KEY,
        delivery_id BIGINT NOT NULL,
        customer_order_item_id BIGINT NOT NULL,
        quantity_delivered DECIMAL(18,2) NOT NULL,
        CONSTRAINT fk_delivery_items_delivery FOREIGN KEY (delivery_id) REFERENCES deliveries(delivery_id) ON DELETE CASCADE ON UPDATE CASCADE,
        CONSTRAINT fk_delivery_items_order_item FOREIGN KEY (customer_order_item_id) REFERENCES customer_order_items(customer_order_item_id) ON DELETE NO ACTION
    );
END
GO
