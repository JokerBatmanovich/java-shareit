CREATE TABLE IF NOT EXISTS USERS (
                        USER_ID INT PRIMARY KEY AUTO_INCREMENT,
                        NAME VARCHAR(255) NOT NULL,
                        EMAIL VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS REQUESTS (
                        REQUEST_ID INT PRIMARY KEY AUTO_INCREMENT,
                        DESCRIPTION VARCHAR(255),
                        REQUESTOR_ID INT  NOT NULL,
                        CREATED TIMESTAMP  NOT NULL,
                        CONSTRAINT PK_REQUESTOR_ID
                            FOREIGN KEY (REQUEST_ID)
                                REFERENCES USERS(USER_ID)
);

CREATE TABLE IF NOT EXISTS ITEMS (
                        ITEM_ID INT PRIMARY KEY AUTO_INCREMENT,
                        NAME VARCHAR(100)  NOT NULL,
                        DESCRIPTION VARCHAR(255),
                        AVAILABLE BOOLEAN ,
                        OWNER_ID INT  NOT NULL,
                        REQUEST_ID INT  NOT NULL,
                        CONSTRAINT PK_OWNER_ID
                           FOREIGN KEY (OWNER_ID)
                               REFERENCES USERS(USER_ID)
);

CREATE TABLE IF NOT EXISTS BOOKING (
                        BOOKING_ID INT PRIMARY KEY AUTO_INCREMENT,
                        START TIMESTAMP NOT NULL,
                        END TIMESTAMP NOT NULL,
                        ITEM_ID INT  NOT NULL,
                        BOOKER_ID INT NOT NULL,
                        STATUS VARCHAR(15),
                        CONSTRAINT PK_ITEM_ID
                            FOREIGN KEY (ITEM_ID)
                                REFERENCES ITEMS(ITEM_ID),
                        CONSTRAINT PK_BOOKER_ID
                            FOREIGN KEY (BOOKER_ID)
                                REFERENCES USERS(USER_ID)
);

/*

ALTER TABLE MPA ALTER COLUMN mpa_id RESTART WITH 6;

CREATE TABLE IF NOT EXISTS FILMS (
                        film_id INT PRIMARY KEY AUTO_INCREMENT,
                        name VARCHAR(120) DEFAULT '<no name>',
                        release_date DATE,
                        description VARCHAR(200) DEFAULT '<empty>',
                        mpa_id INT,
                        duration INT,
                        CONSTRAINT fk_film_mpa
                            FOREIGN KEY (mpa_id)
                                REFERENCES MPA(mpa_id)
);

CREATE TABLE IF NOT EXISTS USERS (
                        user_id INT PRIMARY KEY AUTO_INCREMENT,
                        email VARCHAR(100) NOT NULL,
                        login VARCHAR(30) NOT NULL ,
                        name varchar(30),
                        birthday DATE
);

CREATE TABLE IF NOT EXISTS FRIENDSHIP_REQUESTS (
                                      user_from INT NOT NULL,
                                      user_to INT NOT NULL,
                                      CONSTRAINT fk_friendship_user
                                          FOREIGN KEY (user_from)
                                              REFERENCES USERS(user_id),
                                      CONSTRAINT fk_friendship_friend
                                          FOREIGN KEY (user_to)
                                              REFERENCES USERS(user_id)

);

CREATE TABLE IF NOT EXISTS FILMS_GENRES (
                                      film_id INT NOT NULL ,
                                      genre_id INT NOT NULL,
                                      CONSTRAINT fk_film_genre
                                          FOREIGN KEY (film_id)
                                              REFERENCES FILMS(film_id),
                                      CONSTRAINT fk_id_genre
                                          FOREIGN KEY (genre_id)
                                              REFERENCES GENRES(genre_id)
);

CREATE TABLE IF NOT EXISTS LIKES (
                         film_id INT NOT NULL,
                         CONSTRAINT fk_like_film
                             FOREIGN KEY (film_id)
                                 REFERENCES FILMS(film_id),

                         user_id INT NOT NULL,
                         CONSTRAINT fk_like_user
                             FOREIGN KEY (user_id)
                                 REFERENCES USERS(user_id)

);
*/

