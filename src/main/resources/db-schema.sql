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

create table vocabulary
(
    id                        serial primary key,
    name                      varchar(50),
    created_at                timestamp default now(),
    language_being_learned_id bigint,

    foreign key (language_being_learned_id) references language_being_learned,
    unique (name, language_being_learned_id)
);

insert into vocabulary
    (name, language_being_learned_id)
values ('DEFAULT', 1);

create table word
(
    id   serial primary key,
    name varchar(50)
);

insert into word
    (name)
values ('language');
