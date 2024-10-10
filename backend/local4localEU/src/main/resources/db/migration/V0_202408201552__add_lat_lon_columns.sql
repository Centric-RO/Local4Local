-- If the coordinates column exists, drop it
ALTER TABLE l4l_eu_security.merchant
DROP COLUMN IF EXISTS address;

-- Add new columns for latitude and longitude
ALTER TABLE l4l_eu_security.merchant
ADD COLUMN lat double precision NOT NULL DEFAULT 52.387386,
ADD COLUMN lon double precision NOT NULL DEFAULT 4.646219;

