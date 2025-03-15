CREATE table ms_access.TB_ACCESS (
                id INTEGER NOT NULL,
                user_name varchar(25) NOT NULL,
                password varchar(100),
                CONSTRAINT access_pk PRIMARY KEY (id)
);

CREATE SEQUENCE ACCESS_SEQ START WITH 1;

COMMIT;