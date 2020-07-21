drop table if exists group_tbl;
create table group_tbl (
    id char(36) not null,
    name varchar(256) not null,
    primary key(id)
);

drop table if exists user_tbl;
create table user_tbl (
    id char(36) not null,
    name varchar(256) not null,
    primary key(id)
 );

drop table if exists group_user_tbl;
create table group_user_tbl (
    group_id char(36) not null,
    user_id char(36) not null,
    primary key(group_id, user_id)
);
create index on group_user_tbl (user_id, group_id);
