INSERT INTO "sitools"."USERS" (identifier, firstname, lastname, email, secret) VALUES
('admin', 'firstname', 'lastname', 'admin-sitools@cnes.fr', 'admin');

INSERT INTO "sitools"."GROUPS" (name, description) VALUES
('register', 'Group of registered persons'),
('administrator', 'Group of persons managing the archive system');

INSERT INTO "sitools"."USER_GROUP" (identifier, name) VALUES
('admin', 'administrator');

ALTER USER "sitools" SET PASSWORD "sitools";