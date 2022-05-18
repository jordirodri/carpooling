DROP TABLE IF EXISTS CAR;
DROP TABLE IF EXISTS GROUPS;
DROP TABLE IF EXISTS JOURNEY;


CREATE TABLE CAR
(
    uuid integer NOT NULL PRIMARY KEY AUTO_INCREMENT,
    id integer NOT NULL,
    seats integer NOT NULL,
    empty_seats integer NOT NULL,
    waiting boolean NOT NULL DEFAULT true,
    group_ids ARRAY
);

CREATE TABLE GROUPS
(
    uuid integer NOT NULL PRIMARY KEY AUTO_INCREMENT,
    id integer NOT NULL,
    people integer NOT NULL,
    waiting boolean NOT NULL DEFAULT true
);

commit;
