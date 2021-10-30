create table language
(
    id   serial primary key,
    name varchar(50) not null
);

create table word
(
    id   serial primary key,
    name varchar(50) not null
);

create table vocabulary_entry
(
    id                    serial primary key,
    created_at            timestamp default now() not null,
    correct_answers_count smallint  default 0     not null,

    definition            varchar(255),
    is_difficult          boolean   default false not null,

    word_id               bigint,
    language_id           bigint
);

alter table vocabulary_entry
    add constraint vocabulary_entry__word_id__fkey
        foreign key (word_id)
            references word (id);

alter table vocabulary_entry
    add constraint vocabulary_entry__language_id__fkey
        foreign key (language_id)
            references language (id);

create unique index vocabulary_entry__word_id__language_id__index
    on vocabulary_entry (word_id, language_id);

create table vocabulary_entry__synonym__jt
(
    vocabulary_entry_id bigint,
    word_id             bigint,

    primary key (vocabulary_entry_id, word_id)
);

create table vocabulary_entry__antonym__jt
(
    vocabulary_entry_id bigint,
    word_id             bigint,

    primary key (vocabulary_entry_id, word_id)
);

create table settings
(
    id          serial primary key,
    language_id bigint
);

alter table settings
    add constraint settings__language_id__fkey
        foreign key (language_id)
            references language (id);
