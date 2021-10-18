create table language
(
    id   serial primary key,
    name varchar(50) not null
);

create table language_being_learned
(
    id          serial primary key,
    created_at  timestamp default now(),
    language_id bigint
);

create unique index language_being_learned__language_id__index
    on language_being_learned (language_id);

alter table language_being_learned
    add constraint language_being_learned__language_id__fkey
        foreign key (language_id)
            references language (id);

create table vocabulary
(
    id                        serial primary key,
    name                      varchar(50),
    created_at                timestamp default now(),
    language_being_learned_id bigint,

    foreign key (language_being_learned_id) references language_being_learned,
    unique (name, language_being_learned_id)
);

create table word
(
    id   serial primary key,
    name varchar(50)
);

create table vocabulary_entry
(
    id                    serial primary key,
    created_at            timestamp default now(),
    correct_answers_count smallint  default 0,

    word_id               bigint,
    vocabulary_id         bigint,

    foreign key (word_id) references word (id),
    foreign key (vocabulary_id) references vocabulary (id)
);

create table vocabulary_entry__synonym
(
    vocabulary_entry_id bigint,
    word_id             bigint,

    primary key (vocabulary_entry_id, word_id)
);

create table vocabulary_entry__antonym
(
    vocabulary_entry_id bigint,
    word_id             bigint,

    primary key (vocabulary_entry_id, word_id)
);
