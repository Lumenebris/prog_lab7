-- SCHEMA: lena_laba7

-- DROP SCHEMA lena_laba7 ;

CREATE SCHEMA lena_laba7
    AUTHORIZATION s265081;

GRANT ALL ON SCHEMA lena_laba7 TO s265081;


-- SEQUENCE: lena_laba7.users_id_seq

-- DROP SEQUENCE lena_laba7.users_id_seq;

CREATE SEQUENCE lena_laba7.users_id_seq
    INCREMENT 1
    START 3
    MINVALUE 1
    MAXVALUE 2147483647
    CACHE 1;

ALTER SEQUENCE lena_laba7.users_id_seq
    OWNER TO s265081;

-- SEQUENCE: lena_laba7.rooms_id_seq

-- DROP SEQUENCE lena_laba7.rooms_id_seq;

CREATE SEQUENCE lena_laba7.rooms_id_seq
    INCREMENT 1
    START 4
    MINVALUE 1
    MAXVALUE 2147483647
    CACHE 1;

ALTER SEQUENCE lena_laba7.rooms_id_seq
    OWNER TO s265081;

-- Table: lena_laba7.users

-- DROP TABLE lena_laba7.users;

CREATE TABLE lena_laba7.users
(
    id integer NOT NULL DEFAULT nextval('lena_laba7.users_id_seq'::regclass),
    login character varying COLLATE pg_catalog."default" NOT NULL,
    password character varying COLLATE pg_catalog."default" NOT NULL,
    CONSTRAINT users_pkey PRIMARY KEY (id),
    CONSTRAINT users_unique_login UNIQUE (login)

)

TABLESPACE pg_default;

ALTER TABLE lena_laba7.users
    OWNER to s265081;

-- Table: lena_laba7.rooms

-- DROP TABLE lena_laba7.rooms;

CREATE TABLE lena_laba7.rooms
(
    id integer NOT NULL DEFAULT nextval('lena_laba7.rooms_id_seq'::regclass),
    user_id integer NOT NULL,
    created timestamp with time zone NOT NULL,
    floor integer NOT NULL,
    "number" integer NOT NULL,
    shape character varying COLLATE pg_catalog."default" NOT NULL,
    wall_material character varying COLLATE pg_catalog."default" NOT NULL,
    x integer NOT NULL,
    y integer NOT NULL,
    CONSTRAINT rooms_pkey PRIMARY KEY (id),
    CONSTRAINT room_unique_shape UNIQUE (shape)
,
    CONSTRAINT fk_rooms_users FOREIGN KEY (user_id)
        REFERENCES lena_laba7.users (id) MATCH SIMPLE
        ON UPDATE NO ACTION
        ON DELETE NO ACTION
        NOT VALID
)

TABLESPACE pg_default;

ALTER TABLE lena_laba7.rooms
    OWNER to s265081;


