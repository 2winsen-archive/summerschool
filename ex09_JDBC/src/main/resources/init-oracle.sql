CREATE TABLE bookmark (bookmark_id NUMERIC(10) NOT NULL, 
    url VARCHAR2(255) NOT NULL,  
    title VARCHAR(50) NOT NULL, 
    last_updated TIMESTAMP(6), 
    content CLOB, 
    checksum LONG,
    description VARCHAR2(255), 
    CONSTRAINT bookmark_pk PRIMARY KEY (bookmark_id));
 
 CREATE SEQUENCE seq_bookmark_id;