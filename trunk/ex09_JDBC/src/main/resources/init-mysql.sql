CREATE TABLE bookmark (bookmark_id INT NOT NULL AUTO_INCREMENT, 
    url VARCHAR(255) NOT NULL,  
    title VARCHAR(50) NOT NULL, 
    last_updated TIMESTAMP(6), 
    content TEXT,
    checksum LONG,
    description VARCHAR(255), 
    PRIMARY KEY(bookmark_id));
 