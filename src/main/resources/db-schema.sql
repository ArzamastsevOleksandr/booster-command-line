create table language
(
    id   serial primary key,
    name varchar(50) not null
);

create unique index language__name__index
    on language (name);

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

    primary key (vocabulary_entry_id, word_id),

    constraint ve_synonym_jt__ve__fkey
        foreign key (vocabulary_entry_id)
            references vocabulary_entry (id),

    constraint ve_synonym_jt__word__fkey
        foreign key (word_id)
            references word (id)
);

create table vocabulary_entry__antonym__jt
(
    vocabulary_entry_id bigint,
    word_id             bigint,

    primary key (vocabulary_entry_id, word_id),

    constraint ve_antonym_jt__ve__fkey
        foreign key (vocabulary_entry_id)
            references vocabulary_entry (id),

    constraint ve_antonym_jt__word__fkey
        foreign key (word_id)
            references word (id)
);

create table settings
(
    id          serial primary key,
    language_id bigint,

    constraint settings__language__fkey
        foreign key (language_id)
            references language (id)
);

create table note
(
    id      serial primary key,
    content varchar(255) not null
);

create table tag
(
    name varchar(50) primary key
);
