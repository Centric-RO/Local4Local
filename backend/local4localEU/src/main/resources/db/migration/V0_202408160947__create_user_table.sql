CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE SCHEMA l4l_eu_security;

CREATE TABLE l4l_eu_security.merchant(
    id uuid DEFAULT public.uuid_generate_v1() PRIMARY KEY,
    company_name character varying(256) NOT NULL,
    kvk character varying(8) NOT NULL,
    category character varying(256) NOT NULL,
    address character varying(256) NOT NULL,
    website character varying(256),
    created_date timestamp without time zone DEFAULT now() NOT NULL
);