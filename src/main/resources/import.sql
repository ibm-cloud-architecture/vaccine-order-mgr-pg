INSERT INTO VaccineOrderEntity(id,askingOrganization, deliveryLocation, priority,quantity, status, vaccineType,deliveryDate,creationDate) VALUES (1, 'Spain Government','Madrid',1,200,0,'COVID-19', '2021-02-24','10-Nov-2020 02:02:40');
INSERT INTO VaccineOrderEntity(id,askingOrganization, deliveryLocation, priority,quantity, status, vaccineType,deliveryDate,creationDate) VALUES (2, 'French Government','Paris',1,300,0,'COVID-19', '2021-01-10','15-Nov-2020 02:02:40');
INSERT INTO VaccineOrderEntity(id,askingOrganization, deliveryLocation, priority,quantity, status, vaccineType,deliveryDate,creationDate) VALUES (3, 'Italy Government','Roma',1,400,0,'COVID-19', '2021-01-12','15-Nov-2020 02:02:40');
ALTER SEQUENCE hibernate_sequence RESTART WITH 15;