create table oauth_client_details (
                                      client_id VARCHAR(256) PRIMARY KEY,
                                      resource_ids VARCHAR(256),
                                      client_secret VARCHAR(256),
                                      scope VARCHAR(256),
                                      authorized_grant_types VARCHAR(256),
                                      web_server_redirect_uri VARCHAR(256),
                                      authorities VARCHAR(256),
                                      access_token_validity INTEGER,
                                      refresh_token_validity INTEGER,
                                      additional_information VARCHAR(4096),
                                      autoapprove VARCHAR(256)
);

create table oauth_client_token (
                                    token_id VARCHAR(256),
                                    token bytea,
                                    authentication_id VARCHAR(256) PRIMARY KEY,
                                    user_name VARCHAR(256),
                                    client_id VARCHAR(256)
);

create table oauth_access_token (
                                    token_id VARCHAR(256),
                                    token bytea,
                                    authentication_id VARCHAR(256) PRIMARY KEY,
                                    user_name VARCHAR(256),
                                    client_id VARCHAR(256),
                                    authentication bytea,
                                    refresh_token VARCHAR(256)
);

create table oauth_refresh_token (
                                     token_id VARCHAR(256),
                                     token bytea,
                                     authentication bytea
);

create table oauth_code (
                            code VARCHAR(256), authentication bytea
);

create table oauth_approvals (
                                 userId VARCHAR(256),
                                 clientId VARCHAR(256),
                                 scope VARCHAR(256),
                                 status VARCHAR(10),
                                 expiresAt TIMESTAMP,
                                 lastModifiedAt TIMESTAMP
);


-- customized oauth_client_details table
create table ClientDetails (
                               appId VARCHAR(256) PRIMARY KEY,
                               resourceIds VARCHAR(256),
                               appSecret VARCHAR(256),
                               scope VARCHAR(256),
                               grantTypes VARCHAR(256),
                               redirectUrl VARCHAR(256),
                               authorities VARCHAR(256),
                               access_token_validity INTEGER,
                               refresh_token_validity INTEGER,
                               additionalInformation VARCHAR(4096),
                               autoApproveScopes VARCHAR(256)
);

create table if not exists  permission (
                                           id INTEGER,
                                           name VARCHAR(512) default null,
                                           primary key (id)
);

create table if not exists role (
                                    id INTEGER,
                                    name varchar(255) default null,
                                    primary key (id)
);

create table users
(
    id               uuid    not null,
    first_name       varchar,
    midlle_name      varchar,
    last_name        varchar,
    birthday         date,
    profile          varchar not null,
    institution_name varchar,
    institution_code varchar,
    email            varchar not null,
    street           varchar,
    street_number    integer,
    city             varchar,
    postal_code      varchar,
    province         varchar,
    version          integer,
    constraint user_pk
        primary key (id)
);

create table account
(
    id        uuid    not null,
    username  varchar not null,
    password  varchar not null,
    version   integer,
    enabled boolean not null,
    accountNonExpired boolean not null,
    credentialsNonExpired boolean not null,
    accountNonLocked boolean not null,
    constraint account_pk
        primary key (id),
    constraint account_users_id_fk
        foreign key (id) references users
);
create table  if not exists permission_role (
                                                permission_id integer default null,
                                                role_id integer default null,
                                                constraint permission_role_ibfk_1 foreign key (permission_id) references permission (id),
                                                constraint permission_role_ibfk_2 foreign key (role_id) references role (id)
) ;
create table if not exists role_user (
                                         role_id integer default null,
                                         user_id uuid default null,
                                         constraint role_user_ibfk_1 foreign key (role_id) references role (id),
                                         constraint role_user_ibfk_2 foreign key (user_id) references users (id)
) ;

 INSERT INTO PERMISSION (NAME) VALUES
 ('create_profile'),
 ('read_profile'),
 ('update_profile'),
 ('delete_profile');

 INSERT INTO role (NAME) VALUES
		('role_admin'),('role_expert'),('role_searcher');

 INSERT INTO PERMISSION_ROLE (PERMISSION_ID, ROLE_ID) VALUES
     (1,1), /*create-> admin */
     (2,1), /* read admin */
     (3,1), /* update admin */
     (4,1), /* delete admin */
     (2,3),  /* read searcher */
     (3,3);  /* update searcher */

INSERT INTO users (id, first_name, middle_name, last_name, birthday, profile, institution_name, institution_code, email, street, street_number, city, postal_code, province, version)
VALUES ('a12a8e93-4ff8-4f48-9354-cf8494501e9f', 'lahcene', '', 'zinnour', '01-01-2010', 'admin', 'uqtr', '111', 'lacen@uqtr.ca', 'jean tallon', 111, 'tr', 'aze111', 'qc', 0);
INSERT INTO account (id, username, password, version, enabled, accountNonExpired, credentialsNonExpired, accountNonLocked)
VALUES ('a12a8e93-4ff8-4f48-9354-cf8494501e9f', 'lacenu', '{bcrypt}$2y$10$1la3GJ5QduAL7pY/sfp0n.LHWM6pdHoV2R3AufGHYroFqmkfH0Hbe', 0, true, true, true, true);

INSERT INTO oauth_client_details (client_id, client_secret, scope, authorized_grant_types, authorities, access_token_validity)
VALUES ('clientId', '{bcrypt}$2a$10$vCXMWCn7fDZWOcLnIEhmK.74dvK1Eh8ae2WrWlhr2ETPLoxQctN4.', 'read,write', 'password,refresh_token,client_credentials', 'ROLE_CLIENT', 300);
