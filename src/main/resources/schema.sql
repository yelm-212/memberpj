CREATE TABLE users (
                        member_id bigint not null,
                        username varchar(64) not null,
                        password varchar(256) not null,
                        nickname varchar(100) not null,
                        name varchar(100) not null,
                        phonenumber varchar(256) not null,
                        email varchar(256) not null,
                        create_at datetime not null,
                        role varchar(64) not null,
                        primary key(member_id)
);
