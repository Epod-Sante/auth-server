create table if not exists patient
(
    id          uuid    not null,
    version     integer not null,
    first_name  varchar not null,
    middle_name varchar not null,
    last_name   varchar not null,
    birthday    date    not null,
    is_active   boolean not null,
    constraint patient_pk
        primary key (id)
);

create table if not exists contact
(
    id            uuid    not null,
    phone         bigint  not null,
    email         varchar not null,
    street        varchar not null,
    street_number integer not null,
    city          varchar not null,
    zip_code      varchar not null,
    province      varchar not null,
    version       integer not null,
    constraint contact_pk
        primary key (id),
    constraint contact_patient_id_fk
        foreign key (id) references patient
);

create table if not exists profession
(
    id         uuid    not null,
    type       varchar not null,
    is_active  boolean not null,
    version    integer not null,
    patient_id uuid    not null,
    constraint profession_pk
        primary key (id),
    constraint profession_patient_id_fk
        foreign key (patient_id) references patient
);

create table if not exists expert
(
    id         uuid    not null,
    first_name varchar not null,
    last_name  varchar not null,
    expert_id  uuid    not null,
    version    integer not null,
    patient_id uuid    not null,
    constraint expert_pk
        primary key (id),
    constraint expert_patient_id_fk
        foreign key (patient_id) references patient
);

create table if not exists medical_file
(
    id      uuid    not null,
    version integer not null,
    code    varchar not null,
    patient    varchar not null,
    constraint medical_file_pk
        primary key (id)
);

create table if not exists blood_pressure
(
    id         uuid             not null,
    value      double precision not null,
    date       date             not null,
    patient_id uuid             not null,
    constraint blood_pressure_pk
        primary key (id),
    constraint blood_pressure_medical_file_id_fk
        foreign key (patient_id) references medical_file
);

create table if not exists condition
(
    id         uuid    not null,
    type       varchar not null,
    patient_id uuid    not null,
    constraint condition_pk
        primary key (id),
    constraint condition_medical_file_id_fk
        foreign key (patient_id) references medical_file
);

create table if not exists exercise
(
    id         uuid    not null,
    type       varchar not null,
    date       date    not null,
    duration   integer not null,
    patient_id uuid    not null,
    constraint exercise_pk
        primary key (id),
    constraint exercise_medical_file_id_fk
        foreign key (patient_id) references medical_file
);

create table if not exists medical_device
(
    id         uuid    not null,
    type       varchar not null,
    patient_id uuid    not null,
    constraint medical_device_pk
        primary key (id),
    constraint medical_device_medical_file_id_fk
        foreign key (patient_id) references medical_file
);

create table if not exists medicament
(
    id         uuid    not null,
    name       varchar not null,
    patient_id uuid    not null,
    constraint medicament_pk
        primary key (id),
    constraint medicament_medical_file_id_fk
        foreign key (patient_id) references medical_file
);

create table if not exists allergy
(
    id         uuid    not null,
    type       varchar not null,
    patient_id uuid    not null,
    constraint allergy_pk
        primary key (id),
    constraint allergy_medical_file_id_fk
        foreign key (patient_id) references medical_file
);