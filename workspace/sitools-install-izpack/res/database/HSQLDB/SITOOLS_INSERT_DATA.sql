INSERT INTO "@db_schema@"."USERS" (identifier, firstname, lastname, email, secret) VALUES
('admin', 'firstname', 'lastname', 'admin-sitools@cnes.fr', 'admin');

INSERT INTO "@db_schema@"."GROUPS" (name, description) VALUES
('register', 'Group of registered persons'),
('administrator', 'Group of persons managing the archive system');

INSERT INTO "@db_schema@"."USER_GROUP" (identifier, name) VALUES
('admin', 'administrator');

ALTER USER "@db_user@" SET PASSWORD "@db_pwd@";