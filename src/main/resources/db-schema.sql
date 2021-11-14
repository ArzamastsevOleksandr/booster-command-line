-- todo: primary/foreign key AND index naming documentation

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
    language_id           bigint,

    check (correct_answers_count >= 0)
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

create table vocabulary_entry__context__jt
(
    vocabulary_entry_id bigint       not null,
    context             varchar(255) not null,

    constraint vocabulary_entry__context__jt__pkey
        primary key (vocabulary_entry_id, context),

    constraint vocabulary_entry__context__jt__fkey
        foreign key (vocabulary_entry_id)
            references vocabulary_entry (id)
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

create table vocabulary_entry__tag__jt
(
    vocabulary_entry_id bigint      not null,
    tag                 varchar(50) not null,

    constraint vocabulary_entry_id__tag__pkey
        primary key (vocabulary_entry_id, tag),

    constraint vocabulary_entry_id__fkey
        foreign key (vocabulary_entry_id) references vocabulary_entry (id),

    constraint tag__fkey
        foreign key (tag) references tag (name)
);

create table note__tag__jt
(
    note_id bigint      not null,
    tag     varchar(50) not null,

    constraint note_id__tag__pkey
        primary key (note_id, tag),

    constraint note_id__fkey
        foreign key (note_id) references note (id),

    constraint tag__fkey
        foreign key (tag) references tag (name)
);
