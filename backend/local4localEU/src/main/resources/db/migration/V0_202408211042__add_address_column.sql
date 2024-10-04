-- Add address column
ALTER TABLE l4l_eu_security.merchant
ADD COLUMN address character varying(256) NOT NULL DEFAULT 'Spotex Gersbach DD 65plus Sierenzestrasse 83';

