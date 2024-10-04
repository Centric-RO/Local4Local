CREATE TABLE l4l_eu_security.otp_codes (
    id uuid DEFAULT public.uuid_generate_v1() PRIMARY KEY,
    session_id uuid NOT NULL,
    otp_code integer NOT NULL,
    created_date timestamp without time zone DEFAULT now() NOT NULL,
    user_id uuid REFERENCES l4l_eu_security.user(id)
);
