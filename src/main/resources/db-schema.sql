create table Language
(
    id   bigint primary key,
    name varchar(50) not null
);

insert into Language
    (id, name)
values (1, 'ENGLISH'),
       (2, 'GERMAN');