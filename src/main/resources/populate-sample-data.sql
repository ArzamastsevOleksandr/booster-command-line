insert into language
    (name)
values ('ENGLISH'),
       ('GERMAN');

insert into language_being_learned
    (language_id)
values ((select id from language l where l.name = 'ENGLISH'));

insert into vocabulary
    (name, language_being_learned_id)
values ('DEFAULT', 1);

insert into word
    (name)
values ('language');
