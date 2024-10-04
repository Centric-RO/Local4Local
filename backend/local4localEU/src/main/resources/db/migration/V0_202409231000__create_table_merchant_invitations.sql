CREATE TABLE l4l_eu_global.merchant_invitation (
    id uuid DEFAULT uuid_generate_v1() PRIMARY KEY,
	created_date timestamp without time zone DEFAULT now() NOT NULL,
	email character varying(256) NOT NULL,
	message character varying(1024) NOT NULL,
	is_active BOOLEAN DEFAULT true
);