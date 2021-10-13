create table language
(
    id   serial primary key,
    name varchar(50) not null
);

insert into language
    (name)
values ('ENGLISH'),
       ('GERMAN');

create table language_being_learned
(
    id          serial primary key,
    created_at  timestamp default now(),

    language_id bigint,

    foreign key (language_id) references language
);

insert into language_being_learned
    (language_id)
values ((select id from language l where l.name = 'ENGLISH'));
