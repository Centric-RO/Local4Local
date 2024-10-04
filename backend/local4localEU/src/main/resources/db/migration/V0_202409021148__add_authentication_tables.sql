-- Create user table
CREATE TABLE l4l_eu_security.user (
    id uuid DEFAULT public.uuid_generate_v1() PRIMARY KEY,
    email character varying(256) NOT NULL,
    password character varying(256) NOT NULL,
    is_active boolean DEFAULT true NOT NULL,
    created_date timestamp without time zone DEFAULT now() NOT NULL
);

-- Create role table
CREATE TABLE l4l_eu_security.role (
    id serial PRIMARY KEY,
    name character varying(256) NOT NULL
);

INSERT INTO l4l_eu_security.role(id,name) VALUES (0,'ROLE_MANAGER');

-- Create user_role table
CREATE TABLE l4l_eu_security.user_role(
    id serial PRIMARY KEY,
    user_id uuid NOT NULL REFERENCES l4l_eu_security.user(id),
    role_id integer NOT NULL REFERENCES l4l_eu_security.role(id)
);

INSERT INTO l4l_eu_security.user_role(user_id, role_id)
  SELECT id, 0 from l4l_eu_security.user;

-- Refresh token table
CREATE TABLE l4l_eu_security.refresh_token (
    id uuid DEFAULT uuid_generate_v1() PRIMARY KEY,
    user_id uuid NOT NULL,
    token VARCHAR(255) NOT NULL,
    expiry_date timestamp without time zone NOT NULL,
    created_date timestamp without time zone DEFAULT now() NOT NULL,
    FOREIGN KEY (user_id) REFERENCES l4l_eu_security.user(id),
    UNIQUE (token)
);

-- Login attempt table
CREATE TABLE l4l_eu_security.login_attempt (
    id uuid DEFAULT uuid_generate_v1() PRIMARY KEY,
    remote_addr character varying(256) NOT NULL,
    created_date timestamp DEFAULT now() NOT NULL,
    failed_count smallint DEFAULT 0 NOT NULL
);

-- Add unique constraint to kvk
ALTER TABLE l4l_eu_security.merchant
ADD CONSTRAINT unique_kvk UNIQUE (kvk);
