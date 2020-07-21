drop table if exists item_tbl;
create table item_tbl (
    `id` int not null,
    `name` varchar(256) not null,
    `size` decimal(3, 2),
    primary key(id)
);
