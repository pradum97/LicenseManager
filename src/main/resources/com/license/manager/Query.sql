

create table registered_company
(
    company_id      integer primary key auto_increment,
    username        varchar(10)  not null unique,
    full_name       varchar(50)  not null,
    gender          varchar(10)  not null,
    phone           varchar(10)  not null unique,
    email           varchar(100) not null unique,
    company_name    varchar(50),
    password        text,
    registered_date timestamp default current_timestamp
);

create table all_application
(
    id                        integer unique auto_increment,
    application_id            varchar(50) primary key not null,
    application_name          varchar(200)            not null,
    application_type          varchar(50)             not null,
    status                    int       default 1     not null,
    connected_application     int       default 0     not null,
    max_connected_application int       default 1,
    registered_date           timestamp default current_timestamp,
    company_id                integer                 not null,
    foreign key (company_id) references registered_company (company_id)

);

create table application_owner
(
    owner_id       int primary key auto_increment,
    name           varchar(50)  default '-' not null,
    email          varchar(150) default '-' not null,
    phone          varchar(15)  default '-' not null,
    company_id     int                      not null,
    application_id varchar(50)              not null,
    foreign key (company_id) references registered_company (company_id),
    foreign key (application_id) references all_application (application_id)
);

create table serial_key
(
    serial_key_id       integer primary key auto_increment,
    is_active           integer     default 0 not null,
    serial_key          varchar(100) unique   not null,
    license_type        varchar(50)           not null,
    company_id          integer               not null,
    application_id      varchar(50)           not null,
    start_date          varchar(15) default 0 not null,
    expire_date         varchar(15) default 0 not null,
    licence_period_days int                   not null,
    key_quantity        int         default 1 not null,
    quantity_count      int         default 0 not null,
    foreign key (company_id) references registered_company (company_id),
    foreign key (application_id) references all_application (application_id)
);

create table db_backup
(
    backup_id      int primary key auto_increment,
    path           text not null,
    application_id varchar(100)  not null,
    backup_date    varchar(100)
);




