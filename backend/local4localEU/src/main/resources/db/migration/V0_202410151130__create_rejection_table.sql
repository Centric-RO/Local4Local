CREATE TABLE l4l_eu_global.merchant_rejection (
    id uuid DEFAULT uuid_generate_v1() PRIMARY KEY,
    reason VARCHAR(1024) NOT NULL,
    merchant_id uuid NOT NULL REFERENCES l4l_eu_security.merchant(id)
);
