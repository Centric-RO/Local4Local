CREATE SCHEMA l4l_eu_global;

CREATE TABLE l4l_eu_global.category (
    id SERIAL NOT NULL PRIMARY KEY,
    label CHARACTER VARYING(256) NOT NULL
);

INSERT INTO l4l_eu_global.category (id, label)
VALUES
    (0, 'category.foodAndBeverage'),
    (1, 'category.retail'),
    (2, 'category.healthAndWellness'),
    (3, 'category.services'),
    (4, 'category.entertainmentAndLeisure'),
    (5, 'category.specialtyStores'),
    (6, 'category.technologyAndCommunications'),
    (7, 'category.homeImprovement'),
    (8, 'category.other');

ALTER TABLE l4l_eu_security.merchant DROP COLUMN category;

ALTER TABLE l4l_eu_security.merchant ADD COLUMN category_id SERIAL NOT NULL CHECK (category_id >= 0 AND category_id <= 8);

UPDATE l4l_eu_security.merchant SET category_id = 0;

ALTER TABLE l4l_eu_security.merchant ADD CONSTRAINT fk_category_id FOREIGN KEY (category_id) REFERENCES l4l_eu_global.category (id);