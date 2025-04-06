CREATE table TB_USER (
                id INTEGER NOT NULL,
                name varchar(60) NOT NULL,
                email varchar(200),
                access_id INTEGER,
                CONSTRAINT user_pk PRIMARY KEY (id)
);

CREATE SEQUENCE USER_SEQ START WITH 1;

COMMIT;