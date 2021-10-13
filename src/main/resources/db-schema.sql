create table language
(
    id   serial primary key,
    name varchar(50) not null
);

insert into language
(name)
values ('ENGLISH'),
       ('GERMAN');
