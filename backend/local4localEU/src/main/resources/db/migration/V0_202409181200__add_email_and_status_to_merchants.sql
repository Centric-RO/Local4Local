CREATE TYPE l4l_eu_global.merchant_status AS ENUM (
    'PENDING',
    'APPROVED',
    'REJECTED'
	);

ALTER TABLE l4l_eu_security.merchant
    ADD COLUMN contact_email character varying(256) NOT NULL DEFAULT 'l4l.centric@gmail.com',
    ADD COLUMN status l4l_eu_global.merchant_status DEFAULT 'PENDING';