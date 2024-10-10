CREATE SEQUENCE hibernate_sequence START 1;

CREATE TABLE l4l_eu_security.recover_password (
        recover_password_id serial NOT NULL,
	token character varying(64) NOT NULL,
	token_expiration_date timestamp without time zone DEFAULT now() NOT NULL,
	user_id uuid REFERENCES l4l_eu_security.user(id),
        is_active boolean DEFAULT false
);


CREATE OR REPLACE FUNCTION l4l_eu_security.update_is_active_after_rp_add()
RETURNS TRIGGER AS $$
BEGIN
		UPDATE l4l_eu_security.recover_password rp SET is_active=false where rp.user_id = NEW.user_id AND rp.recover_password_id != NEW.recover_password_id;
		RETURN NEW;
END;
$$ language 'plpgsql';


CREATE TRIGGER trigger_after_rp_added AFTER INSERT ON l4l_eu_security.recover_password FOR EACH ROW EXECUTE PROCEDURE l4l_eu_security.update_is_active_after_rp_add();